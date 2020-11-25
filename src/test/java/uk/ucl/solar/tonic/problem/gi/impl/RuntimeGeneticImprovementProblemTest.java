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
package uk.ucl.solar.tonic.problem.gi.impl;

import gin.edit.Edit;
import static org.junit.Assert.*;
import java.io.IOException;
import org.junit.Test;

/**
 *
 * @author Giovani
 */
public class RuntimeGeneticImprovementProblemTest {

    private String propertiesFile = "./src/test/resources/maven-simple/tonic.properties";

    public RuntimeGeneticImprovementProblemTest() {
    }

    @Test
    public void testEverythingParsed() throws IOException {
        RuntimeGeneticImprovementProblem problem = new RuntimeGeneticImprovementProblem(this.propertiesFile);
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
        assertEquals(4, problem.getEditTypes().size());
        assertNotNull(problem.getProject());
        assertEquals(4, problem.getTestData().size());
        assertNull(problem.getOriginalProgramResults());
        assertNull(problem.getTargetedMethod());
        assertNull(problem.getTargetedSourceFile());
        problem.nextMethod();
        assertNotNull(problem.getTargetedMethod());
        assertNotNull(problem.getTargetedSourceFile());
        problem.nextMethod();
        assertNotNull(problem.getTargetedMethod());
        assertNotNull(problem.getTargetedSourceFile());
        problem.nextMethod();
        assertNull(problem.getTargetedMethod());
        assertNull(problem.getTargetedSourceFile());
    }
    
    @Test(expected = NullPointerException.class)
    public void testNullFile() throws IOException {
        RuntimeGeneticImprovementProblem problem = new RuntimeGeneticImprovementProblem(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testBlankFile() throws IOException {
        RuntimeGeneticImprovementProblem problem = new RuntimeGeneticImprovementProblem("");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testPropertiesFileDoesNotExist() throws IOException {
        RuntimeGeneticImprovementProblem problem = new RuntimeGeneticImprovementProblem("./src/test/thisfiledoesnotexist.properties");
    }
    
    @Test(expected = IOException.class)
    public void testPropertiesFileIsDir() throws IOException {
        RuntimeGeneticImprovementProblem problem = new RuntimeGeneticImprovementProblem("./src/test");
    }

}
