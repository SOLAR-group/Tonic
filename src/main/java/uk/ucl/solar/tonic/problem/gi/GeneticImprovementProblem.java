/*
 * Copyright 2020 Giovani.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ucl.solar.tonic.problem.gi;

import com.opencsv.CSVReaderHeaderAware;
import gin.Patch;
import gin.SourceFile;
import gin.edit.Edit;
import gin.test.ExternalTestRunner;
import gin.test.InternalTestRunner;
import gin.test.UnitTest;
import gin.test.UnitTestResult;
import gin.test.UnitTestResultSet;
import gin.util.MavenUtils;
import gin.util.Project;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.pmw.tinylog.Logger;
import org.uma.jmetal.problem.AbstractGenericProblem;
import uk.ucl.solar.tonic.base.TargetMethod;
import uk.ucl.solar.tonic.solution.PatchSolution;

/**
 *
 * @author Giovani
 */
public abstract class GeneticImprovementProblem extends AbstractGenericProblem<PatchSolution> {

    /*============== Required  ==============*/
    protected File projectDirectory;
    protected File methodFile;

    /*============== Optional (required only for certain types of projects, ignored otherwise)  ==============*/
    protected String projectName = null;
    protected String classPath = null;
    protected File mavenHome = null;

    /*============== Optional (setup)  ==============*/
    protected File outputFile = new File("./sampler_results.csv");
    protected File timingOutputFile = new File("./sampler_timing.csv");
    protected Long timeoutMS = 10000L;
    protected Integer reps = 1;
    protected Boolean inSubprocess = false;
    protected Boolean eachRepetitionInNewSubprocess = false;
    protected Boolean eachTestInNewSubprocess = false;
    protected Boolean failFast = false;

    /*============== Other  ==============*/
    protected Project project = null;
    private static final String TEST_SEPARATOR = ",";
    private static final String METHOD_SEPARATOR = ".";

    /*============== Structures holding all project data  ==============*/
    protected List<TargetMethod> methodData = new ArrayList<>();
    protected Iterator<TargetMethod> methodIterator;
    protected TargetMethod targetedMethod;
    protected SourceFile targetedSourceFile;

    protected Map<SourceFile, UnitTestResultSet> originalProgramResults;

    protected Set<UnitTest> testData = new LinkedHashSet<>();

    /**
     * allowed edit types for sampling: parsed from editType
     */
    protected List<Class<? extends Edit>> editTypes;

    public GeneticImprovementProblem(String ginPropertiesPath) {
        //TODO get properties from file
        setUp();
    }

    protected void setUp() {
        if (this.classPath == null) {
            this.project = new Project(projectDirectory, projectName);
            if (mavenHome != null) {
                this.project.setMavenHome(mavenHome);
            } else if (this.project.isMavenProject()) {
                // In case it is indeed a Maven project, tries to find maven in
                // the System's evironment variables and set the path to it.
                Logger.info("I'm going to try and find your maven home, but make sure to set mavenHome for maven projects in the future.");
                this.project.setMavenHome(MavenUtils.findMavenHomeFile());
            }
            Logger.info("Calculating classpath..");
            this.classPath = project.classpath();
            Logger.info("Classpath: " + this.classPath);
        }
        this.methodData = processMethodFile();
        this.methodIterator = this.methodData.iterator();
        if (!this.methodIterator.hasNext()) {
            Logger.error("No methods to process.");
            System.exit(0);
        }
        this.originalProgramResults = new HashMap<>();
    }

    public UnitTestResultSet runPatch(Patch patch) {
        if (this.targetedMethod != null && this.targetedSourceFile != null) {
            String className = this.targetedMethod.getClassName();
            List<UnitTest> tests = this.targetedMethod.getGinTests();
            UnitTestResultSet results = testPatch(className, tests, patch);
            if (patch.size() == 0) {
                this.originalProgramResults.computeIfAbsent(this.targetedSourceFile, sourceFile -> results);
            }
            return results;
        } else {
            return null;
        }
    }

    public TargetMethod nextMethod() {
        if (this.methodIterator.hasNext()) {
            this.targetedMethod = this.methodIterator.next();
            this.targetedSourceFile = SourceFile.makeSourceFileForEditTypes(
                    editTypes,
                    this.targetedMethod.getFileSource().getPath(),
                    Collections.singletonList(this.targetedMethod.getMethodName()));
        } else {
            this.targetedMethod = null;
            this.targetedSourceFile = null;
        }
        return this.targetedMethod;
    }

    @Override
    public PatchSolution createSolution() {
        Validate.notNull(this.targetedMethod, "There is no target method. Either the method file is empty, or you forgot to call \"problem.nextMethod()\".");
        return new PatchSolution(this.getNumberOfObjectives(), this.getNumberOfConstraints(), this.targetedSourceFile);
    }

    /*============== methods for running tests  ==============*/
    protected UnitTestResultSet testEmptyPatch(String targetClass, Collection<UnitTest> tests, SourceFile sourceFile) {
        Logger.debug("Testing the empty patch..");

        UnitTestResultSet resultSet = null;

        if (!inSubprocess && !eachRepetitionInNewSubprocess && !eachTestInNewSubprocess) {
            resultSet = testPatchInternally(targetClass, new ArrayList<>(tests), new Patch(sourceFile));
        } else {
            resultSet = testPatchInSubprocess(targetClass, new ArrayList<>(tests), new Patch(sourceFile));
        }

        if (!resultSet.allTestsSuccessful()) {
            if (!resultSet.getCleanCompile()) {
                Logger.error("Original code failed to compile");
            } else {
                Logger.error("Original code failed to pass unit tests");
                Logger.error("Valid: " + resultSet.getValidPatch());
                Logger.error("Compiled: " + resultSet.getCleanCompile());
                Logger.error("Failed results follow: ");
                List<UnitTestResult> failingTests = resultSet.getResults().stream()
                        .filter(res -> !res.getPassed())
                        .collect(Collectors.toList());
                for (UnitTestResult failedResult : failingTests) {
                    Logger.error(failedResult);
                }
            }
        } else {
            Logger.debug("Successfully passed all tests on the unmodified code.");
        }
        return resultSet;
    }

    protected UnitTestResultSet testPatch(String targetClass, List<UnitTest> tests, Patch patch) {
        Logger.debug("Testing patch: " + patch);

        UnitTestResultSet resultSet = null;

        if (!inSubprocess && !eachRepetitionInNewSubprocess && !eachTestInNewSubprocess) {
            resultSet = testPatchInternally(targetClass, tests, patch);
        } else {
            resultSet = testPatchInSubprocess(targetClass, tests, patch);
        }

        return resultSet;

    }

    private UnitTestResultSet testPatchInternally(String targetClass, List<UnitTest> tests, Patch patch) {
        InternalTestRunner testRunner = new InternalTestRunner(targetClass, classPath, tests, failFast);
        return testRunner.runTests(patch, reps);
    }

    private UnitTestResultSet testPatchInSubprocess(String targetClass, List<UnitTest> tests, Patch patch) {
        ExternalTestRunner testRunner = new ExternalTestRunner(targetClass, classPath, tests, eachRepetitionInNewSubprocess, eachTestInNewSubprocess, failFast);

        UnitTestResultSet results = null;
        try {
            results = testRunner.runTests(patch, reps);
        } catch (IOException e) {
            Logger.error(e);
            System.exit(-1);
        } catch (InterruptedException e) {
            Logger.error(e);
            System.exit(-1);
        }
        return results;
    }

    // only Tests and Method fields are required, be careful thus if supplying files with multiple projects, this is not yet handled
    private List<TargetMethod> processMethodFile() {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(methodFile);
            CSVReaderHeaderAware reader = new CSVReaderHeaderAware(fileReader);
            Map<String, String> data = reader.readMap();
            if ((!data.containsKey("Method")) || (!data.containsKey("Tests"))) {
                throw new ParseException("Both \"Method\" and \"Tests\" fields are required in the method file.", 0);
            }

            List<TargetMethod> methods = new ArrayList<>();

            Integer idx = 0;

            while (data != null) {

                String[] tests = data.get("Tests").split(TEST_SEPARATOR);
                List<UnitTest> ginTests = new ArrayList();
                for (String test : tests) {
                    UnitTest ginTest = null;
                    ginTest = UnitTest.fromString(test);
                    ginTest.setTimeoutMS(timeoutMS);
                    ginTests.add(ginTest);
                    testData.add(ginTest);
                }

                String method = data.get("Method");

                String className = StringUtils.substringBefore(method, "("); // method arguments can have dots, so need to get data without arguments first
                className = StringUtils.substringBeforeLast(className, METHOD_SEPARATOR);

                File source = (project != null) ? project.findSourceFile(className) : findSourceFile(className);
                if ((source == null) || (!source.isFile())) {
                    throw new FileNotFoundException("Cannot find source for class: " + className);
                }

                // now using fully qualified names...
                //String methodName = StringUtils.substringAfterLast(method, className + METHOD_SEPARATOR);
                idx++;
                Integer methodID = (data.containsKey("MethodIndex")) ? Integer.valueOf(data.get("MethodIndex")) : idx;

                TargetMethod targetMethod = new TargetMethod(source, className, method, ginTests, methodID);

                if (methods.contains(targetMethod)) {
                    throw new ParseException("Duplicate method IDs in the input file.", 0);
                }
                methods.add(targetMethod);

                data = reader.readMap();
            }
            reader.close();

            return methods;

        } catch (ParseException e) {
            Logger.error(e.getMessage());
            Logger.trace(e);
        } catch (FileNotFoundException e) {
            Logger.error(e.getMessage());
            Logger.trace(e);
        } catch (IOException e) {
            Logger.error("Error reading method file: " + methodFile);
            Logger.trace(e);
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException ex) {
                    Logger.error("Error closing method file: " + methodFile);
                    Logger.trace(ex);
                }
            }
        }
        return new ArrayList<>();

    }

    // used for non-maven and non-gradle projects only
    private File findSourceFile(String className) {
        String pathToSource = className.replace(".", File.separator) + ".java";
        String filename;
        File moduleDir;
        if (className.contains(".")) {
            filename = StringUtils.substringAfterLast(pathToSource, File.separator);
            moduleDir = new File(projectDirectory, StringUtils.substringBeforeLast(pathToSource, File.separator));
        } else {
            filename = pathToSource;
            moduleDir = projectDirectory;
        }
        if (!moduleDir.isDirectory()) {
            return null;
        }
        File[] files = moduleDir.listFiles((dir, name) -> name.equals(filename));
        if (files.length == 0) {
            return null;
        }
        if (files.length > 1) {
            Logger.error("Two files found with the same name in: " + projectDirectory);
            return null;
        }
        return files[0];

    }

}
