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

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import uk.ucl.solar.tonic.solution.PatchSolution;

/**
 *
 * @author Giovani
 */
public class RuntimeGeneticImprovementProblemTest {

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
    public void createSolution() throws IOException {
        RuntimeGeneticImprovementProblem problem = new RuntimeGeneticImprovementProblem(propertiesObject);
        problem.nextMethod();

        PatchSolution solution = problem.createSolution();
        assertNotNull(solution);
        assertNotNull(problem.getOriginalPatchSolution());
        assertEquals(solution, problem.getOriginalPatchSolution());
        problem.evaluate(solution);

        assertEquals(1, solution.getAttribute("MethodIndex"));
        assertEquals("com.mycompany.app.App.classifyTriangle(int,int,int)", solution.getAttribute("MethodName"));
        assertEquals(0, solution.getAttribute("PatchSize"));
        assertEquals("|", solution.getAttribute("Patch"));
        assertEquals(true, solution.getAttribute("Compiled"));
        assertEquals(4, solution.getAttribute("NTests"));
        assertEquals(true, solution.getAttribute("AllTestsPassed"));
        assertEquals(4, solution.getAttribute("NPassed"));
        assertEquals(0, solution.getAttribute("NFailed"));
        assertTrue((double) solution.getAttribute("TotalExecutionTime(ms)") > 0);
        assertTrue((long) solution.getAttribute("TimeStamp") > 0);

        assertEquals(0.0, solution.getAttribute("Fitness_0"));
        assertEquals(0.0, solution.getAttribute("FitnessImprovement_0"));
        assertEquals(solution.getAttribute("TotalExecutionTime(ms)"), solution.getAttribute("Fitness_1"));
        assertEquals(0.0, solution.getAttribute("FitnessImprovement_1"));

        solution = problem.createSolution();
        assertNotNull(solution);
        assertNotNull(problem.getOriginalPatchSolution());
        assertNotEquals(solution, problem.getOriginalPatchSolution());
        problem.evaluate(solution);

        assertEquals(1, solution.getAttribute("MethodIndex"));
        assertEquals("com.mycompany.app.App.classifyTriangle(int,int,int)", solution.getAttribute("MethodName"));
        assertEquals(1, solution.getAttribute("PatchSize"));
        assertTrue(((String) solution.getAttribute("Patch")).startsWith("| gin.edit.statement.ReplaceStatement"));
        assertEquals(false, solution.getAttribute("Compiled"));
        assertEquals(4, solution.getAttribute("NTests"));
        assertEquals(false, solution.getAttribute("AllTestsPassed"));
        assertEquals(0, solution.getAttribute("NPassed"));
        assertEquals(4, solution.getAttribute("NFailed"));
        assertTrue((double) solution.getAttribute("TotalExecutionTime(ms)") == 0);
        assertTrue((long) solution.getAttribute("TimeStamp") > 0);

        assertEquals(1.0, solution.getAttribute("Fitness_0"));
        assertEquals(-1.0, solution.getAttribute("FitnessImprovement_0"));
        assertEquals(Double.MAX_VALUE, solution.getAttribute("Fitness_1"));
        assertTrue((double) solution.getAttribute("FitnessImprovement_1") == Double.MAX_VALUE * -1);
    }

}
