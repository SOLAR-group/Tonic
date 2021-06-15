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
import org.uma.jmetal.solution.AbstractSolution;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * @author Giovani
 */
public class PatchSolution extends AbstractSolution<Edit> {

    protected Patch patch;

    /**
     * Constructor
     */
    protected PatchSolution(PatchSolution solution) {
        super(0, solution.getNumberOfObjectives(), solution.getNumberOfConstraints());
        patch = solution.getPatch().clone();
        for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
            this.setObjective(i, solution.getObjective(i));
        }
        for (int i = 0; i < solution.getNumberOfConstraints(); i++) {
            this.setConstraint(i, solution.getConstraint(i));
        }
        this.attributes = new HashMap<>(solution.attributes);
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
    public PatchSolution copy() {
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

    public SourceFile getSourceFile() {
        return patch.getSourceFile();
    }

    public void add(Edit edit) {
        patch.add(edit);
    }

    public String apply() {
        return patch.apply();
    }

    public void addRandomEditOfClass(Random rng, Class<? extends Edit> allowableEditType) {
        patch.addRandomEditOfClass(rng, allowableEditType);
    }

    public void addRandomEditOfClasses(Random rng, List<Class<? extends Edit>> allowableEditTypes) {
        patch.addRandomEditOfClasses(rng, allowableEditTypes);
    }

    public void addRandomEdit(Random rng, Edit.EditType allowableEditType) {
        patch.addRandomEdit(rng, allowableEditType);
    }

    public void addRandomEdit(Random rng, List<Edit.EditType> allowableEditTypes) {
        patch.addRandomEdit(rng, allowableEditTypes);
    }

    public void writePatchedSourceToFile(String filename) {
        patch.writePatchedSourceToFile(filename);
    }

    public boolean isOnlyLineEdits() {
        return patch.isOnlyLineEdits();
    }

    public boolean isOnlyStatementEdits() {
        return patch.isOnlyStatementEdits();
    }

    public boolean lastApplyWasValid() {
        return patch.lastApplyWasValid();
    }

    public List<Boolean> getEditsInvalidOnLastApply() {
        return patch.getEditsInvalidOnLastApply();
    }

}
