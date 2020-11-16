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
package uk.ucl.solar.tonic.solution;

import gin.Patch;
import gin.SourceFile;
import gin.edit.Edit;
import java.util.List;
import org.uma.jmetal.solution.AbstractSolution;
import org.uma.jmetal.solution.Solution;

/**
 *
 * @author Giovani
 */
public class PatchSolution extends AbstractSolution<Edit> {

    protected Patch patch;

    /**
     * Constructor
     */
    public PatchSolution(PatchSolution solution) {
        super(0, solution.getNumberOfObjectives(), solution.getNumberOfConstraints());
        patch = solution.getPatch().clone();
    }

    /**
     * Constructor
     */
    public PatchSolution(int numberOfObjectives, SourceFile sourceFile) {
        super(0, numberOfObjectives);
        patch = new Patch(sourceFile);
    }

    /**
     * Constructor
     */
    public PatchSolution(int numberOfObjectives, int numberOfConstraints, SourceFile sourceFile) {
        super(0, numberOfObjectives, numberOfConstraints);
        patch = new Patch(sourceFile);
    }

    @Override
    public Solution<Edit> copy() {
        return new PatchSolution(this);
    }

    @Override
    public int getNumberOfVariables() {
        return patch.getEdits().size();
    }

    @Override
    public List<Edit> getVariables() {
        return patch.getEdits();
    }

    @Override
    public Edit getVariable(int index) {
        return patch.getEdits().get(index);
    }

    @Override
    public void setVariable(int index, Edit value) {
        patch.getEdits().set(index, value);
    }

    public Patch getPatch() {
        return patch;
    }

}
