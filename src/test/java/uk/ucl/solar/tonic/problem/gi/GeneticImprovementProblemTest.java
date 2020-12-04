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

import gin.edit.Edit;
import gin.test.UnitTestResultSet;
import gin.util.MavenUtils;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Assume;
import org.junit.Before;
import uk.ucl.solar.tonic.problem.gi.impl.RuntimeGeneticImprovementProblem;
import uk.ucl.solar.tonic.solution.PatchSolution;

/**
 *
 * @author Giovani
 */
public class GeneticImprovementProblemTest {

    private String propertiesFile = "./unittests/maven-simple/tonic.properties";
    private Properties propertiesObject;

    @Before
    public void setUp() throws IOException {
        this.propertiesObject = new Properties();
        try (FileReader reader = new FileReader(this.propertiesFile)) {
            propertiesObject.load(reader);
        }
    }

    @Test
    public void testEverythingParsed() throws IOException {
        String mavenHome = MavenUtils.findMavenHomePath();
        Assume.assumeTrue(FileUtils.getFile(mavenHome, "bin/mvn").exists()
                || FileUtils.getFile(mavenHome, "mvn").exists()
                || FileUtils.getFile(mavenHome, "bin/mvn.cmd").exists()
                || FileUtils.getFile(mavenHome, "mvn.cmd").exists());
        RuntimeGeneticImprovementProblem problem = new RuntimeGeneticImprovementProblem(this.propertiesObject);
        assertTrue(problem.getProjectDirectory().exists());
        assertTrue(problem.getMethodFile().exists());
        assertEquals(2, problem.getMethodData().size());
        assertEquals("maven-simple", problem.getProjectName());
        assertNotNull(problem.getClassPath());
        assertFalse(problem.getClassPath().isBlank());
        assertNotNull(problem.getMavenHome());
        assertTrue(problem.getMavenHome().exists());
        assertEquals(10000L, problem.getTimeoutMS().longValue());
        assertEquals(2, problem.getReps().intValue());
        assertTrue(problem.isInSubprocess());
        assertTrue(problem.isEachRepetitionInNewSubprocess());
        assertFalse(problem.isEachTestInNewSubprocess());
        assertFalse(problem.isFailFast());
        assertEquals(Edit.EditType.STATEMENT.toString(), problem.getEditType());
        assertEquals(1, problem.getSeed().longValue());
        assertNotNull(problem.getRandom());
        assertEquals(4, problem.getEditTypes().size());
        assertNotNull(problem.getProject());
        assertEquals(4, problem.getTestData().size());
        assertNull(problem.getOriginalPatchSolution());
        assertNull(problem.getOriginalProgramResults());
        assertNull(problem.getTargetedMethod());
        assertNull(problem.getTargetedSourceFile());
        problem.setOriginalProgramResults(new UnitTestResultSet(null, true, new ArrayList<>(), true, true, null));
        problem.nextMethod();
        assertNotNull(problem.getTargetedMethod());
        assertNotNull(problem.getTargetedSourceFile());
        assertNull(problem.getOriginalPatchSolution());
        assertNull(problem.getOriginalProgramResults());
        problem.nextMethod();
        assertNotNull(problem.getTargetedMethod());
        assertNotNull(problem.getTargetedSourceFile());
        problem.nextMethod();
        assertNull(problem.getTargetedMethod());
        assertNull(problem.getTargetedSourceFile());
    }

    @Test(expected = NullPointerException.class)
    public void testNullFile() throws IOException {
        String nullString = null;
        RuntimeGeneticImprovementProblem problem = new RuntimeGeneticImprovementProblem(nullString);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBlankFile() throws IOException {
        RuntimeGeneticImprovementProblem problem = new RuntimeGeneticImprovementProblem("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPropertiesFileDoesNotExist() throws IOException {
        RuntimeGeneticImprovementProblem problem = new RuntimeGeneticImprovementProblem("./unittests/thisFileDoesNotExist.properties");
    }

    @Test(expected = IOException.class)
    public void testPropertiesFileIsDir() throws IOException {
        RuntimeGeneticImprovementProblem problem = new RuntimeGeneticImprovementProblem("./unittests");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProjectDirectoryDoesNotExist() throws IOException {
        this.propertiesObject.setProperty("projectDirectory", "./unittests/thisDirDoesNotExist");
        RuntimeGeneticImprovementProblem problem = new RuntimeGeneticImprovementProblem(propertiesObject);
    }

    @Test(expected = NullPointerException.class)
    public void testProjectDirectoryNull() throws IOException {
        this.propertiesObject.setProperty("projectDirectory", null);
        RuntimeGeneticImprovementProblem problem = new RuntimeGeneticImprovementProblem(propertiesObject);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMethodFileDoesNotExist() throws IOException {
        this.propertiesObject.setProperty("methodFile", "./unittests/thisFileDoesNotExist.csv");
        RuntimeGeneticImprovementProblem problem = new RuntimeGeneticImprovementProblem(propertiesObject);
    }

    @Test(expected = NullPointerException.class)
    public void testMethodFileNull() throws IOException {
        this.propertiesObject.setProperty("methodFile", null);
        RuntimeGeneticImprovementProblem problem = new RuntimeGeneticImprovementProblem(propertiesObject);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidTimeout() throws IOException {
        this.propertiesObject.setProperty("timeoutMS", "0");
        RuntimeGeneticImprovementProblem problem = new RuntimeGeneticImprovementProblem(propertiesObject);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidReps() throws IOException {
        this.propertiesObject.setProperty("reps", "0");
        RuntimeGeneticImprovementProblem problem = new RuntimeGeneticImprovementProblem(propertiesObject);
    }

    @Test
    public void testNullEdits() throws IOException {
        this.propertiesObject.remove("editType");
        RuntimeGeneticImprovementProblem problem = new RuntimeGeneticImprovementProblem(propertiesObject);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidEdits() throws IOException {
        this.propertiesObject.setProperty("editType", "NOT_A_VALID_EDIT");
        RuntimeGeneticImprovementProblem problem = new RuntimeGeneticImprovementProblem(propertiesObject);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidEditsEmpty() throws IOException {
        this.propertiesObject.setProperty("editType", "");
        RuntimeGeneticImprovementProblem problem = new RuntimeGeneticImprovementProblem(propertiesObject);
    }

    @Test
    public void createSolution() throws IOException {
        RuntimeGeneticImprovementProblem problem = new RuntimeGeneticImprovementProblem(propertiesObject);
        problem.nextMethod();

        PatchSolution solution = problem.createSolution();
        assertNotNull(solution);
        assertNotNull(problem.getOriginalPatchSolution());
        assertEquals(solution, problem.getOriginalPatchSolution());

        solution = problem.createSolution();
        assertNotNull(solution);
        assertNotEquals(solution, problem.getOriginalPatchSolution());
        assertEquals(1, solution.getNumberOfVariables());
    }

}
