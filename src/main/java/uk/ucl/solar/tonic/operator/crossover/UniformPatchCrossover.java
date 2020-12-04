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
package uk.ucl.solar.tonic.operator.crossover;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.Validate;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import uk.ucl.solar.tonic.solution.PatchSolution;

/**
 *
 * @author Giovani
 */
public class UniformPatchCrossover implements CrossoverOperator<PatchSolution> {

    private double crossoverProbability;
    private Random random;
    private Long seed;

    public UniformPatchCrossover(double crossoverProbability, int seed) {
        this.crossoverProbability = crossoverProbability;
        this.random = new Random(seed);
    }

    public UniformPatchCrossover(double crossoverProbability) {
        this.crossoverProbability = crossoverProbability;
        this.random = new Random();
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

    @Override
    public double getCrossoverProbability() {
        return crossoverProbability;
    }

    @Override
    public int getNumberOfRequiredParents() {
        return 2;
    }

    @Override
    public int getNumberOfGeneratedChildren() {
        return 2;
    }

    @Override
    public List<PatchSolution> execute(List<PatchSolution> solutions) {
        Validate.notNull(solutions);
        Validate.isTrue(solutions.size() >= 2, "There must be at least two parents instead of " + solutions.size());

        return doCrossover(this.crossoverProbability, solutions.get(0), solutions.get(1));
    }

    private List<PatchSolution> doCrossover(double probability, PatchSolution parent1, PatchSolution parent2) {
        List<PatchSolution> offspring = new ArrayList<>(2);
        offspring.add((PatchSolution) parent1.copy());
        offspring.add((PatchSolution) parent2.copy());

        if (this.random.nextDouble() < probability) {
            for (int variableIndex = 0; variableIndex < parent1.getNumberOfVariables() && variableIndex < parent2.getNumberOfVariables(); variableIndex++) {
                if (this.random.nextDouble() < 0.5) {
                    offspring
                            .get(0)
                            .setVariable(variableIndex, parent2.getVariable(variableIndex));
                    offspring
                            .get(0)
                            .setVariable(variableIndex, parent1.getVariable(variableIndex));
                }
            }
        }
        return offspring;
    }

}
