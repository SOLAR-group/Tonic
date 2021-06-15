/*
 * Copyright 2020 giova.
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
package uk.ucl.solar.tonic;

import gin.edit.Edit;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import uk.ucl.solar.tonic.operator.crossover.UniformPatchCrossover;
import uk.ucl.solar.tonic.operator.mutation.RandomPatchMutation;
import uk.ucl.solar.tonic.problem.gi.impl.RuntimeGeneticImprovementProblem;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author Giovani
 */
public class Experiment {

    public static void main(String[] args) throws IOException {
        List<Edit.EditType> allowableEditTypesTree = Arrays.asList(Edit.EditType.STATEMENT, Edit.EditType.MODIFY_STATEMENT);
        String propertiesFile = "./unittests/maven-simple/tonic.properties";
        final RuntimeGeneticImprovementProblem runtimeGeneticImprovementProblem = new RuntimeGeneticImprovementProblem(propertiesFile);
        NSGAII nsgaii = new NSGAII(runtimeGeneticImprovementProblem,
                6000,
                100,
                100,
                100,
                new UniformPatchCrossover(1.0),
                new RandomPatchMutation(0.8, Edit.getEditClassesOfTypes(allowableEditTypesTree)),
                new BinaryTournamentSelection<>(),
                new SequentialSolutionListEvaluator<>());
        runtimeGeneticImprovementProblem.nextMethod();
        nsgaii.run();
        nsgaii.getResult();
    }

}
