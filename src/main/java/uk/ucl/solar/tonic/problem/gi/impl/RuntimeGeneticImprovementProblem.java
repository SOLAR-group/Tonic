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

import gin.test.UnitTestResultSet;
import uk.ucl.solar.tonic.problem.gi.GeneticImprovementProblem;
import uk.ucl.solar.tonic.solution.PatchSolution;

import java.io.IOException;
import java.util.Properties;

/**
 * @author Giovani
 */
public class RuntimeGeneticImprovementProblem extends GeneticImprovementProblem {

    public RuntimeGeneticImprovementProblem(String ginPropertiesPath) throws IOException {
        super(ginPropertiesPath);
        this.setNumberOfObjectives(2);
        this.setNumberOfVariables(-1);
        this.setName("Runtime Genetic Improvement Problem");
    }

    public RuntimeGeneticImprovementProblem(Properties ginProperties) throws IOException {
        super(ginProperties);
        this.setNumberOfObjectives(2);
        this.setNumberOfVariables(-1);
        this.setName("Runtime Genetic Improvement Problem");
    }

    @Override
    public PatchSolution evaluate(PatchSolution solution) {
        solution.setObjective(0, solution.getNumberOfVariables());

        UnitTestResultSet results = runPatch(solution.getPatch());
        double fitness = Double.MAX_VALUE;
        if (results.getCleanCompile() && results.allTestsSuccessful()) {
            fitness = (results.totalExecutionTime() / 1000000D);
        }
        solution.setObjective(1, fitness);

        this.fillSolutionAttributes(solution, results);
        return solution;
    }

}
