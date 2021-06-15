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
import gin.SourceFileTree;
import gin.edit.Edit;
import gin.edit.statement.CopyStatement;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * @author Giovani
 */
public class PatchSolutionTest {

    private final static String verySmallExampleSourceFilename = "./unittests/Small.java";
    private final static List<Edit.EditType> allowableEditTypesTree = Arrays.asList(Edit.EditType.STATEMENT, Edit.EditType.MODIFY_STATEMENT);

    private SourceFileTree sourceFileTree;
    private PatchSolution solution;

    public PatchSolutionTest() {
    }

    @Before
    public void setUp() {
        sourceFileTree = new SourceFileTree(verySmallExampleSourceFilename, Collections.emptyList());
        solution = new PatchSolution(2, 1, sourceFileTree);
        solution.getPatch().addRandomEdit(new Random(1234), allowableEditTypesTree);
        solution.getPatch().addRandomEdit(new Random(1234), allowableEditTypesTree);
        solution.getPatch().addRandomEdit(new Random(1234), allowableEditTypesTree);
        solution.getPatch().addRandomEdit(new Random(1234), allowableEditTypesTree);
        solution.setObjective(0, 1);
        solution.setObjective(1, 0);
        solution.setConstraint(0, 10);
        solution.setAttribute("NTests", 10);
        solution.setAttribute("NPassed", 6);
        solution.setAttribute("NFailed", 4);
    }

    @Test
    public void testCopy() {
        PatchSolution secondSolution = solution.copy();
        assertFalse(solution == secondSolution);
        assertEquals(solution, secondSolution);
        assertNotEquals(solution.getPatch(), secondSolution.getPatch());
        assertFalse(solution.getVariables() == secondSolution.getVariables());
        assertEquals(solution.getVariables(), secondSolution.getVariables());
        assertFalse(solution.getObjectives() == secondSolution.getObjectives());
        assertArrayEquals(solution.getObjectives(), secondSolution.getObjectives(), 0.01);
        assertFalse(solution.getConstraints() == secondSolution.getConstraints());
        assertArrayEquals(solution.getConstraints(), secondSolution.getConstraints(), 0.01);
        assertFalse(solution.getAttributes() == secondSolution.getAttributes());
        assertEquals(solution.getAttributes(), secondSolution.getAttributes());
    }

    @Test
    public void testGetNumberOfVariables() {
        assertEquals(4, solution.getNumberOfVariables());
        solution.getPatch().remove(0);
        solution.getPatch().remove(0);
        solution.getPatch().remove(0);
        assertEquals(1, solution.getNumberOfVariables());
    }

    @Test
    public void testGetVariables() {
        assertNotNull(solution.getVariables());
        assertEquals(4, solution.getVariables().size());
        solution.getVariables().remove(0);
        solution.getVariables().remove(0);
        solution.getVariables().remove(0);
        assertEquals(1, solution.getNumberOfVariables());
    }

    @Test
    public void testGetVariable() {
        assertNotNull(solution.getVariable(0));
        assertTrue(solution.getVariable(0) instanceof Edit);
    }

    @Test
    public void testSetVariable() {
        CopyStatement copyStatement = new CopyStatement(sourceFileTree, new Random(1234));
        solution.setVariable(0, copyStatement);
        assertNotNull(solution.getVariable(0));
        assertEquals(copyStatement, solution.getVariable(0));
    }

    @Test
    public void testGetPatch() {
        assertNotNull(solution.getPatch());
        assertTrue(solution.getPatch() instanceof Patch);
    }

}
