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
import gin.test.UnitTestResultSet;
import gin.util.MavenUtils;
import gin.util.Project;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import org.apache.commons.io.FileUtils;
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
    protected Long timeoutMS = 10000L;
    protected Integer reps = 1;
    protected Boolean inSubprocess = false;
    protected Boolean eachRepetitionInNewSubprocess = false;
    protected Boolean eachTestInNewSubprocess = false;
    protected Boolean failFast = false;
    protected String editType = Edit.EditType.STATEMENT.toString();
    protected Random random = new Random();
    protected Long seed;

    /*============== Other  ==============*/
    protected Project project = null;
    private static final String TEST_SEPARATOR = ",";
    private static final String METHOD_SEPARATOR = ".";
    protected Set<UnitTest> testData = new LinkedHashSet<>();

    /*============== Structures holding all project data  ==============*/
    protected List<TargetMethod> methodData = new ArrayList<>();
    protected Iterator<TargetMethod> methodIterator;
    protected TargetMethod targetedMethod;
    protected SourceFile targetedSourceFile;
    protected PatchSolution originalPatchSolution;
    protected UnitTestResultSet originalProgramResults;
    /**
     * allowed edit types for sampling: parsed from editType
     */
    protected List<Class<? extends Edit>> editTypes;

    public GeneticImprovementProblem(String ginPropertiesPath) throws IOException {
        this.editTypes = Edit.parseEditClassesFromString(this.editType);
        Validate.notBlank(ginPropertiesPath, "Properties file path cannot be null or empty.");
        File propertiesFile = FileUtils.getFile(ginPropertiesPath);
        loadProperties(propertiesFile);
        setUp();
    }

    public GeneticImprovementProblem(Properties ginProperties) throws IOException {
        this.editTypes = Edit.parseEditClassesFromString(this.editType);
        readProperties(ginProperties);
        setUp();
    }

    public File getProjectDirectory() {
        return projectDirectory;
    }

    public void setProjectDirectory(File projectDirectory) {
        Validate.notNull(projectDirectory, "Project's directory cannot be null.");
        Validate.isTrue(projectDirectory.exists(), "I could not find the Project's directory.");
        this.projectDirectory = projectDirectory;
    }

    public void setProjectDirectory(String projectDirectory) {
        Validate.notBlank(projectDirectory, "The property 'projectDirectory' cannot be null or empty.");
        this.setProjectDirectory(FileUtils.getFile(projectDirectory));
    }

    public File getMethodFile() {
        return methodFile;
    }

    public void setMethodFile(File methodFile) {
        Validate.notNull(methodFile, "Method file cannot be null.");
        Validate.isTrue(methodFile.exists(), "I could not find the Method File.");
        this.methodFile = methodFile;
    }

    public void setMethodFile(String methodFile) {
        Validate.notBlank(methodFile, "The property 'methodFile' cannot be null or empty.");
        this.setMethodFile(FileUtils.getFile(methodFile));
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getClassPath() {
        return classPath;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    public File getMavenHome() {
        return mavenHome;
    }

    public void setMavenHome(File mavenHome) {
        if (mavenHome != null) {
            Validate.isTrue(mavenHome.exists(), "I could not find the Maven Home.");
        }
        this.mavenHome = mavenHome;
    }

    public void setMavenHome(String mavenHome) {
        if (mavenHome == null) {
            this.mavenHome = null;
        } else {
            this.setMavenHome(FileUtils.getFile(mavenHome));
        }
    }

    public Long getTimeoutMS() {
        return timeoutMS;
    }

    public void setTimeoutMS(Long timeoutMS) {
        Validate.inclusiveBetween(1L, Long.MAX_VALUE, timeoutMS.longValue());
        this.timeoutMS = timeoutMS;
    }

    public Integer getReps() {
        return reps;
    }

    public void setReps(Integer reps) {
        Validate.inclusiveBetween(1, Long.MAX_VALUE, reps);
        this.reps = reps;
    }

    public Boolean isInSubprocess() {
        return inSubprocess;
    }

    public void setInSubprocess(Boolean inSubprocess) {
        this.inSubprocess = inSubprocess;
    }

    public Boolean isEachRepetitionInNewSubprocess() {
        return eachRepetitionInNewSubprocess;
    }

    public void setEachRepetitionInNewSubprocess(Boolean eachRepetitionInNewSubprocess) {
        this.eachRepetitionInNewSubprocess = eachRepetitionInNewSubprocess;
    }

    public Boolean isEachTestInNewSubprocess() {
        return eachTestInNewSubprocess;
    }

    public void setEachTestInNewSubprocess(Boolean eachTestInNewSubprocess) {
        this.eachTestInNewSubprocess = eachTestInNewSubprocess;
    }

    public Boolean isFailFast() {
        return failFast;
    }

    public void setFailFast(Boolean failFast) {
        this.failFast = failFast;
    }

    public String getEditType() {
        return editType;
    }

    public void setEditType(String editType) {
        this.editType = editType;
        Validate.notBlank(this.editType, "Edit type cannot be null or blank.");
        Validate.isTrue(Edit.EditType.valueOf(this.editType) != null, "Invalid edit type. Available types are: " + Arrays.toString(Edit.EditType.values()) + ".");
        this.editTypes = Edit.parseEditClassesFromString(this.editType);
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Set<UnitTest> getTestData() {
        return testData;
    }

    public void setTestData(Set<UnitTest> testData) {
        this.testData = testData;
    }

    public UnitTestResultSet getOriginalProgramResults() {
        return originalProgramResults;
    }

    public void setOriginalProgramResults(UnitTestResultSet originalProgramResults) {
        this.originalProgramResults = originalProgramResults;
    }

    public List<TargetMethod> getMethodData() {
        return methodData;
    }

    public TargetMethod getTargetedMethod() {
        return targetedMethod;
    }

    public SourceFile getTargetedSourceFile() {
        return targetedSourceFile;
    }

    public List<Class<? extends Edit>> getEditTypes() {
        return editTypes;
    }

    public PatchSolution getOriginalPatchSolution() {
        return originalPatchSolution;
    }

    public Long getSeed() {
        return seed;
    }

    public void setSeed(Long seed) {
        this.seed = seed;
        if (this.random == null) {
            this.random = new Random(seed);
        } else {
            this.random.setSeed(seed);
        }
    }

    public Random getRandom() {
        return random;
    }

    // Coupled by Time.. is there a way of dettaching load and validate?
    protected final void loadProperties(File propertiesFile) throws IOException {
        Validate.notNull(propertiesFile, "Properties file cannot be null.");
        Validate.isTrue(propertiesFile.exists(), "I could not find Gin's properties file.");
        Properties properties = new Properties();
        try (FileReader reader = new FileReader(propertiesFile)) {
            properties.load(reader);
        } catch (IOException ex) {
            Logger.error(ex, "Could not load properties file.");
            throw ex;
        }
        this.readProperties(properties);
    }

    protected final void readProperties(Properties properties) throws IOException {
        Validate.notNull(properties, "Properties cannot be null");
        String property;

        property = properties.getProperty("projectDirectory");
        this.setProjectDirectory(property);

        property = properties.getProperty("methodFile");
        this.setMethodFile(property);

        property = properties.getProperty("projectName");
        this.setProjectName(property);

        property = properties.getProperty("classPath");
        this.setClassPath(property);

        if (properties.containsKey("mavenHome")) {
            property = properties.getProperty("mavenHome");
            this.setMavenHome(property);
        }

        if (properties.containsKey("timeoutMS")) {
            property = properties.getProperty("timeoutMS");
            long timeoutMS = Long.valueOf(property);
            this.setTimeoutMS(timeoutMS);
        }

        if (properties.containsKey("reps")) {
            property = properties.getProperty("reps");
            this.setReps(Integer.valueOf(property));
        }

        if (properties.containsKey("inSubprocess")) {
            property = properties.getProperty("inSubprocess");
            this.setInSubprocess(Boolean.valueOf(property));
        }

        if (properties.containsKey("eachRepetitionInNewSubprocess")) {
            property = properties.getProperty("eachRepetitionInNewSubprocess");
            this.setEachRepetitionInNewSubprocess(Boolean.valueOf(property));
        }

        if (properties.containsKey("eachTestInNewSubprocess")) {
            property = properties.getProperty("eachTestInNewSubprocess");
            this.setEachTestInNewSubprocess(Boolean.valueOf(property));
        }

        if (properties.containsKey("failFast")) {
            property = properties.getProperty("failFast");
            this.setFailFast(Boolean.valueOf(property));
        }

        if (properties.containsKey("editType")) {
            property = properties.getProperty("editType");
            this.setEditType(property);
        }

        if (properties.containsKey("seed")) {
            property = properties.getProperty("seed");
            long seed = Long.valueOf(property);
            this.setSeed(seed);
        }
    }

    protected final void setUp() {
        if (this.classPath == null) {
            this.setProject(new Project(this.projectDirectory, this.projectName));
            if (this.mavenHome != null) {
                this.project.setMavenHome(this.mavenHome);
            } else if (this.project.isMavenProject()) {
                // In case it is indeed a Maven project, tries to find maven in
                // the System's evironment variables and set the path to it.
                Logger.info("I'm going to try and find your maven home, but make sure to set mavenHome for maven projects in the future.");
                final File mavenHome = MavenUtils.findMavenHomeFile();
                this.setMavenHome(mavenHome);
                this.project.setMavenHome(mavenHome);
            }
            Logger.info("Calculating classpath..");
            this.setClassPath(this.project.classpath());
            Logger.info("Classpath: " + this.classPath);
        }
        this.methodData = processMethodFile();
        this.methodIterator = this.methodData.iterator();
        Validate.isTrue(this.methodIterator.hasNext(), "No method to improve.");
    }

    public UnitTestResultSet runPatch(Patch patch) {
        // If there is a method to improve
        if (this.targetedMethod != null && this.targetedSourceFile != null) {
            String className = this.targetedMethod.getClassName();
            List<UnitTest> tests = this.targetedMethod.getGinTests();
            // Test the patch
            UnitTestResultSet results = testPatch(className, tests, patch);
            // If the patch is empty (i.e. the original program) and it is the
            // first time it is being executed
            if (patch.size() == 0 && this.originalProgramResults == null) {
                // Save the original program execution's information
                this.setOriginalProgramResults(results);
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
                    this.editTypes,
                    this.targetedMethod.getFileSource().getPath(),
                    Collections.singletonList(this.targetedMethod.getMethodName()));
        } else {
            this.targetedMethod = null;
            this.targetedSourceFile = null;
        }
        this.originalPatchSolution = null;
        this.originalProgramResults = null;
        return this.targetedMethod;
    }

    @Override
    public PatchSolution createSolution() {
        Validate.notNull(this.targetedMethod, "There is no target method. Either the method file is empty, or you forgot to call \"problem.nextMethod()\".");
        PatchSolution patchSolution = new PatchSolution(this.getNumberOfObjectives(), this.getNumberOfConstraints(), this.getTargetedSourceFile());
        // Always creates an empty patch first
        if (this.originalPatchSolution == null) {
            this.originalPatchSolution = patchSolution;
            // Otherwise, creates a solution with a random edit
        } else {
            patchSolution.addRandomEditOfClasses(this.random, this.editTypes);
        }
        return patchSolution;
    }

    protected UnitTestResultSet testPatch(String targetClass, List<UnitTest> tests, Patch patch) {
        Logger.debug("Testing patch: " + patch);
        UnitTestResultSet resultSet = null;
        if (!this.inSubprocess && !this.eachRepetitionInNewSubprocess && !this.eachTestInNewSubprocess) {
            resultSet = testPatchInternally(targetClass, tests, patch);
        } else {
            resultSet = testPatchInSubprocess(targetClass, tests, patch);
        }
        return resultSet;
    }

    private UnitTestResultSet testPatchInternally(String targetClass, List<UnitTest> tests, Patch patch) {
        InternalTestRunner testRunner = new InternalTestRunner(targetClass, this.classPath, tests, this.failFast);
        return testRunner.runTests(patch, this.reps);
    }

    private UnitTestResultSet testPatchInSubprocess(String targetClass, List<UnitTest> tests, Patch patch) {
        ExternalTestRunner testRunner = new ExternalTestRunner(targetClass, this.classPath, tests, this.eachRepetitionInNewSubprocess, this.eachTestInNewSubprocess, this.failFast);

        UnitTestResultSet results = null;
        try {
            results = testRunner.runTests(patch, this.reps);
        } catch (IOException | InterruptedException e) {
            Logger.error(e);
            System.exit(-1);
        }
        return results;
    }

    // only Tests and Method fields are required, be careful thus if supplying files with multiple projects, this is not yet handled
    private List<TargetMethod> processMethodFile() {
        try (FileReader fileReader = new FileReader(this.methodFile)) {
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
                    ginTest.setTimeoutMS(this.timeoutMS);
                    ginTests.add(ginTest);
                    this.testData.add(ginTest);
                }

                String method = data.get("Method");

                String className = StringUtils.substringBefore(method, "("); // method arguments can have dots, so need to get data without arguments first
                className = StringUtils.substringBeforeLast(className, METHOD_SEPARATOR);

                File source = (this.project != null) ? this.project.findSourceFile(className) : findSourceFile(className);
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

        } catch (ParseException | IOException e) {
            Logger.error(e.getMessage());
            Logger.trace(e);
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
            moduleDir = new File(this.projectDirectory, StringUtils.substringBeforeLast(pathToSource, File.separator));
        } else {
            filename = pathToSource;
            moduleDir = this.projectDirectory;
        }
        if (!moduleDir.isDirectory()) {
            return null;
        }
        File[] files = moduleDir.listFiles((dir, name) -> name.equals(filename));
        if (files.length == 0) {
            return null;
        }
        if (files.length > 1) {
            Logger.error("Two files found with the same name in: " + this.projectDirectory);
            return null;
        }
        return files[0];
    }

    protected void fillSolutionAttributes(PatchSolution solution, UnitTestResultSet results) {
        int nTests = results.getResults().size();
        int nPassed = (int) results.getResults().stream()
                .filter(test -> test.getPassed())
                .count();
        int nFailed = nTests - nPassed;

        solution.setAttribute("MethodIndex", this.getTargetedMethod().getMethodID());
        solution.setAttribute("MethodName", this.getTargetedMethod().getMethodName());
        solution.setAttribute("PatchSize", solution.getNumberOfVariables());
        solution.setAttribute("Patch", solution.getPatch().toString());
        solution.setAttribute("Compiled", results.getCleanCompile());
        solution.setAttribute("NTests", nTests);
        solution.setAttribute("AllTestsPassed", results.allTestsSuccessful());
        solution.setAttribute("NPassed", nPassed);
        solution.setAttribute("NFailed", nFailed);
        solution.setAttribute("TotalExecutionTime(ms)", (double) results.totalExecutionTime() / 1000000);
        solution.setAttribute("TimeStamp", System.currentTimeMillis());

        for (int i = 0; i < this.getNumberOfObjectives(); i++) {
            solution.setAttribute("Fitness_" + i, solution.getObjective(i));
            solution.setAttribute("FitnessImprovement_" + i, this.originalPatchSolution.getObjective(i) - solution.getObjective(i));
        }
    }

}
