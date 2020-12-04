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

import gin.SourceFileTree;
import gin.edit.Edit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.Before;
import uk.ucl.solar.tonic.solution.PatchSolution;

/**
 *
 * @author Giovani
 */
public class RandomPatchMutationTest {

    private final static String verySmallExampleSourceFilename = "./unittests/Small.java";
    private final static List<Edit.EditType> allowableEditTypesTree = Arrays.asList(Edit.EditType.STATEMENT, Edit.EditType.MODIFY_STATEMENT);

    private SourceFileTree sourceFileTree;
    private PatchSolution solution;

    public RandomPatchMutationTest() {
    }

    @Before
    public void setUp() {
        sourceFileTree = new SourceFileTree(verySmallExampleSourceFilename, Collections.emptyList());
        solution = new PatchSolution(2, 1, sourceFileTree);
        Random random = new Random(1234);
        solution.getPatch().addRandomEdit(random, allowableEditTypesTree);
        solution.getPatch().addRandomEdit(random, allowableEditTypesTree);
        solution.getPatch().addRandomEdit(random, allowableEditTypesTree);
        solution.getPatch().addRandomEdit(random, allowableEditTypesTree);
    }

    @Test
    public void testSuccessfullExecute() {
        RandomPatchMutation operator = new RandomPatchMutation(1.0, Edit.getEditClassesOfTypes(allowableEditTypesTree));
        int numberOfVariablesBefore = solution.getNumberOfVariables();
        PatchSolution solution = operator.execute(this.solution);
        
        assertEquals(numberOfVariablesBefore + 1, solution.getNumberOfVariables());
    }
    
    @Test
    public void testNoPorbability() {
        RandomPatchMutation operator = new RandomPatchMutation(0.0, Edit.getEditClassesOfTypes(allowableEditTypesTree));
        int numberOfVariablesBefore = solution.getNumberOfVariables();
        PatchSolution solution = operator.execute(this.solution);
        
        assertEquals(numberOfVariablesBefore, solution.getNumberOfVariables());
    }

}
