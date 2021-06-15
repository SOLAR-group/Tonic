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

import gin.SourceFileTree;
import gin.edit.Edit;
import org.junit.Before;
import org.junit.Test;
import uk.ucl.solar.tonic.solution.PatchSolution;

import java.util.*;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * @author Giovani
 */
public class UniformPatchCrossoverTest {

    private final static String verySmallExampleSourceFilename = "./unittests/Small.java";
    private final static List<Edit.EditType> allowableEditTypesTree = Arrays.asList(Edit.EditType.STATEMENT, Edit.EditType.MODIFY_STATEMENT);

    private SourceFileTree sourceFileTree;

    public UniformPatchCrossoverTest() {
    }

    @Before
    public void setUp() {
        sourceFileTree = new SourceFileTree(verySmallExampleSourceFilename, Collections.emptyList());
    }

    @Test
    public void testNoProbability() {
        Random random = new Random(1234);

        PatchSolution solution = new PatchSolution(2, 1, sourceFileTree);
        solution.getPatch().addRandomEdit(random, allowableEditTypesTree);
        solution.getPatch().addRandomEdit(random, allowableEditTypesTree);
        solution.getPatch().addRandomEdit(random, allowableEditTypesTree);
        solution.getPatch().addRandomEdit(random, allowableEditTypesTree);

        PatchSolution solution2 = new PatchSolution(2, 1, sourceFileTree);
        solution2.getPatch().addRandomEdit(random, allowableEditTypesTree);
        solution2.getPatch().addRandomEdit(random, allowableEditTypesTree);
        solution2.getPatch().addRandomEdit(random, allowableEditTypesTree);
        solution2.getPatch().addRandomEdit(random, allowableEditTypesTree);

        List<PatchSolution> solutions = new ArrayList<>();
        solutions.add(solution);
        solutions.add(solution2);

        UniformPatchCrossover operator = new UniformPatchCrossover(0);
        List<PatchSolution> offspring = operator.execute(solutions);

        assertEquals(2, offspring.size());
        assertEquals(4, offspring.get(0).getNumberOfVariables());
        assertEquals(4, offspring.get(1).getNumberOfVariables());

        assertArrayEquals(solution.getVariables().toArray(), offspring.get(0).getVariables().toArray());
        assertArrayEquals(solution2.getVariables().toArray(), offspring.get(1).getVariables().toArray());
    }

    @Test
    public void testSuccessfull() {
        Random random = new Random(1234);

        PatchSolution solution = new PatchSolution(2, 1, sourceFileTree);
        solution.getPatch().addRandomEdit(random, allowableEditTypesTree);
        solution.getPatch().addRandomEdit(random, allowableEditTypesTree);
        solution.getPatch().addRandomEdit(random, allowableEditTypesTree);
        solution.getPatch().addRandomEdit(random, allowableEditTypesTree);

        PatchSolution solution2 = new PatchSolution(2, 1, sourceFileTree);
        solution2.getPatch().addRandomEdit(random, allowableEditTypesTree);
        solution2.getPatch().addRandomEdit(random, allowableEditTypesTree);
        solution2.getPatch().addRandomEdit(random, allowableEditTypesTree);
        solution2.getPatch().addRandomEdit(random, allowableEditTypesTree);

        List<PatchSolution> solutions = new ArrayList<>();
        solutions.add(solution);
        solutions.add(solution2);

        UniformPatchCrossover operator = new UniformPatchCrossover(1.0);
        operator.random = new Random() {
            int count = 0;

            @Override
            public double nextDouble() {
                return count++ % 2;
            }
        };
        List<PatchSolution> offspring = operator.execute(solutions);

        assertEquals(2, offspring.size());
        assertEquals(4, offspring.get(0).getNumberOfVariables());
        assertEquals(4, offspring.get(1).getNumberOfVariables());

        assertEquals(solution.getVariables().get(0), offspring.get(0).getVariables().get(0));
        assertEquals(solution2.getVariables().get(1), offspring.get(0).getVariables().get(1));
        assertEquals(solution.getVariables().get(2), offspring.get(0).getVariables().get(2));
        assertEquals(solution2.getVariables().get(3), offspring.get(0).getVariables().get(3));

        assertEquals(solution2.getVariables().get(0), offspring.get(1).getVariables().get(0));
        assertEquals(solution.getVariables().get(1), offspring.get(1).getVariables().get(1));
        assertEquals(solution2.getVariables().get(2), offspring.get(1).getVariables().get(2));
        assertEquals(solution.getVariables().get(3), offspring.get(1).getVariables().get(3));

    }

    @Test
    public void testSuccessfullDifferentSizes() {
        Random random = new Random(1234);

        PatchSolution solution = new PatchSolution(2, 1, sourceFileTree);
        solution.getPatch().addRandomEdit(random, allowableEditTypesTree);
        solution.getPatch().addRandomEdit(random, allowableEditTypesTree);

        PatchSolution solution2 = new PatchSolution(2, 1, sourceFileTree);
        solution2.getPatch().addRandomEdit(random, allowableEditTypesTree);
        solution2.getPatch().addRandomEdit(random, allowableEditTypesTree);
        solution2.getPatch().addRandomEdit(random, allowableEditTypesTree);
        solution2.getPatch().addRandomEdit(random, allowableEditTypesTree);

        List<PatchSolution> solutions = new ArrayList<>();
        solutions.add(solution);
        solutions.add(solution2);

        UniformPatchCrossover operator = new UniformPatchCrossover(1.0);
        operator.random = new Random() {
            int count = 0;

            @Override
            public double nextDouble() {
                return count++ % 2;
            }
        };
        List<PatchSolution> offspring = operator.execute(solutions);

        assertEquals(2, offspring.size());
        assertEquals(2, offspring.get(0).getNumberOfVariables());
        assertEquals(4, offspring.get(1).getNumberOfVariables());

        assertEquals(solution.getVariables().get(0), offspring.get(0).getVariables().get(0));
        assertEquals(solution2.getVariables().get(1), offspring.get(0).getVariables().get(1));

        assertEquals(solution2.getVariables().get(0), offspring.get(1).getVariables().get(0));
        assertEquals(solution.getVariables().get(1), offspring.get(1).getVariables().get(1));
        assertEquals(solution2.getVariables().get(2), offspring.get(1).getVariables().get(2));
        assertEquals(solution2.getVariables().get(3), offspring.get(1).getVariables().get(3));
    }

}
