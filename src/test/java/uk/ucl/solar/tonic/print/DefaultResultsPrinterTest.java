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
package uk.ucl.solar.tonic.print;

import static org.junit.Assert.*;
import com.google.common.collect.Lists;
import com.opencsv.CSVReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.solution.binarysolution.impl.DefaultBinarySolution;
import uk.ucl.solar.tonic.exception.TonicException;

/**
 *
 * @author Giovani
 */
public class DefaultResultsPrinterTest {

    private final File outputDir = FileUtils.getFile("./unittests/tempdir");
    private DefaultResultsPrinter printer;
    private List<Solution> solutions;

    public DefaultResultsPrinterTest() {
    }

    @Before
    public void setUp() {
        printer = new DefaultResultsPrinter(outputDir);
        solutions = new ArrayList<>();

        BinarySolution solutionA = new DefaultBinarySolution(Arrays.asList(5), 2);
        solutionA.getVariable(0).set(0);
        solutionA.getVariable(0).clear(1);
        solutionA.getVariable(0).set(2);
        solutionA.getVariable(0).clear(3);
        solutionA.getVariable(0).set(4);
        solutionA.setObjective(0, 10);
        solutionA.setObjective(1, 1);

        BinarySolution solutionB = new DefaultBinarySolution(Arrays.asList(5), 2);
        solutionB.getVariable(0).clear(0);
        solutionB.getVariable(0).set(1);
        solutionB.getVariable(0).clear(2);
        solutionB.getVariable(0).set(3);
        solutionB.getVariable(0).clear(4);
        solutionB.setObjective(0, 11);
        solutionB.setObjective(1, 10);

        solutions.add(solutionA);
        solutions.add(solutionB);
        printer.setSolutionList(solutions);

        printer.setTime(1024L);
        printer.setIsObjectiveToBeMinimized(Lists.newArrayList(true, false));
        printer.setShouldPrintHeaders(true);
        printer.setTimeNames(Lists.newArrayList("FullExecTime"));
        printer.setObjectiveNames(Lists.newArrayList("Objective1", "Objective2"));
    }

    @After
    public void tearDown() {
        FileUtils.deleteQuietly(outputDir);
    }

    @Test
    public void testPrint() throws Exception {
        printer.print();
        try ( CSVReader reader = new CSVReader(new FileReader(FileUtils.getFile(outputDir.getAbsolutePath(), "FUN.csv")))) {
            List<String[]> allLines = reader.readAll();
            assertArrayEquals(new String[]{"Objective1", "Objective2"}, allLines.get(0));
            assertArrayEquals(new String[]{"10.0", "-1.0"}, allLines.get(1));
            assertArrayEquals(new String[]{"11.0", "-10.0"}, allLines.get(2));
        }

        try ( CSVReader reader = new CSVReader(new FileReader(FileUtils.getFile(outputDir.getAbsolutePath(), "VAR.csv")))) {
            List<String[]> allLines = reader.readAll();
            assertArrayEquals(new String[]{"10101"}, allLines.get(0));
            assertArrayEquals(new String[]{"01010"}, allLines.get(1));
        }

        try ( CSVReader reader = new CSVReader(new FileReader(FileUtils.getFile(outputDir.getAbsolutePath(), "TIME.csv")))) {
            List<String[]> allLines = reader.readAll();
            assertArrayEquals(new String[]{"FullExecTime"}, allLines.get(0));
            assertArrayEquals(new String[]{"1024"}, allLines.get(1));
        }
    }

    @Test
    public void testPrintWithoutHeaders() throws Exception {
        printer.setShouldPrintHeaders(false);
        printer.print();
        try ( CSVReader reader = new CSVReader(new FileReader(FileUtils.getFile(outputDir.getAbsolutePath(), "FUN.csv")))) {
            List<String[]> allLines = reader.readAll();
            assertArrayEquals(new String[]{"10.0", "-1.0"}, allLines.get(0));
            assertArrayEquals(new String[]{"11.0", "-10.0"}, allLines.get(1));
        }

        try ( CSVReader reader = new CSVReader(new FileReader(FileUtils.getFile(outputDir.getAbsolutePath(), "VAR.csv")))) {
            List<String[]> allLines = reader.readAll();
            assertArrayEquals(new String[]{"10101"}, allLines.get(0));
            assertArrayEquals(new String[]{"01010"}, allLines.get(1));
        }

        try ( CSVReader reader = new CSVReader(new FileReader(FileUtils.getFile(outputDir.getAbsolutePath(), "TIME.csv")))) {
            List<String[]> allLines = reader.readAll();
            assertArrayEquals(new String[]{"1024"}, allLines.get(0));
        }
    }
    
    @Test
    public void testPrintWithoutHeaders2() throws Exception {
        printer.setShouldPrintHeaders(true);
        printer.setObjectiveNames(null);
        printer.setTimeNames(null);
        printer.print();
        try ( CSVReader reader = new CSVReader(new FileReader(FileUtils.getFile(outputDir.getAbsolutePath(), "FUN.csv")))) {
            List<String[]> allLines = reader.readAll();
            assertArrayEquals(new String[]{"10.0", "-1.0"}, allLines.get(0));
            assertArrayEquals(new String[]{"11.0", "-10.0"}, allLines.get(1));
        }

        try ( CSVReader reader = new CSVReader(new FileReader(FileUtils.getFile(outputDir.getAbsolutePath(), "VAR.csv")))) {
            List<String[]> allLines = reader.readAll();
            assertArrayEquals(new String[]{"10101"}, allLines.get(0));
            assertArrayEquals(new String[]{"01010"}, allLines.get(1));
        }

        try ( CSVReader reader = new CSVReader(new FileReader(FileUtils.getFile(outputDir.getAbsolutePath(), "TIME.csv")))) {
            List<String[]> allLines = reader.readAll();
            assertArrayEquals(new String[]{"1024"}, allLines.get(0));
        }
    }
    
    @Test
    public void testShouldPrintNothing() throws Exception {
        printer.setTimes(new ArrayList<>());
        printer.setSolutionList(new ArrayList<>());
        printer.print();
        assertFalse(FileUtils.getFile(outputDir.getAbsolutePath(), "FUN.csv").exists());
        assertFalse(FileUtils.getFile(outputDir.getAbsolutePath(), "VAR.csv").exists());
        assertFalse(FileUtils.getFile(outputDir.getAbsolutePath(), "TIME.csv").exists());
    }

    @Test(expected = TonicException.class)
    public void testPrintShouldFailDueToDifferentSizesOfNames() throws Exception {
        printer.setObjectiveNames(new ArrayList<>());
        printer.print();
    }

    @Test(expected = TonicException.class)
    public void testPrintShouldFailDueToDifferentSizesOfNames2() throws Exception {
        printer.setTimeNames(new ArrayList<>());
        printer.print();
    }

    @Test(expected = TonicException.class)
    public void testPrintShouldFailDueToDifferentSizesOfObjectivesBooleans() throws Exception {
        printer.setIsObjectiveToBeMinimized(new ArrayList<>());
        printer.print();
    }

    @Test(expected = NullPointerException.class)
    public void testCreateOutputDir() throws Exception {
        printer.setOutputDir(null);
        printer.createOutputDir();
    }

}
