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

    private String propertiesFile = "./src/test/resources/maven-simple/tonic.properties";
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
    }

}
