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
package uk.ucl.solar.tonic.operator.mutation;

import gin.edit.Edit;
import java.util.List;
import java.util.Random;
import org.uma.jmetal.operator.mutation.MutationOperator;
import uk.ucl.solar.tonic.solution.PatchSolution;

/**
 *
 * @author Giovani
 */
public class RandomPatchMutation implements MutationOperator<PatchSolution> {

    protected double mutationProbability;
    protected Random random;
    protected Long seed;
    protected List<Class<? extends Edit>> editTypes;

    public RandomPatchMutation(double mutationProbability, List<Class<? extends Edit>> editTypes, int seed) {
        this.mutationProbability = mutationProbability;
        this.editTypes = editTypes;
        this.random = new Random(seed);
    }

    public RandomPatchMutation(double mutationProbability, List<Class<? extends Edit>> editTypes) {
        this.mutationProbability = mutationProbability;
        this.editTypes = editTypes;
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

    public List<Class<? extends Edit>> getEditTypes() {
        return editTypes;
    }

    public void setEditTypes(List<Class<? extends Edit>> editTypes) {
        this.editTypes = editTypes;
    }

    @Override
    public double getMutationProbability() {
        return this.mutationProbability;
    }

    public void setMutationProbability(double mutationProbability) {
        this.mutationProbability = mutationProbability;
    }

    @Override
    public PatchSolution execute(PatchSolution source) {
        if (this.random.nextDouble() < this.mutationProbability) {
            source.addRandomEditOfClasses(this.random, this.editTypes);
        }
        return source;
    }

}
