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
package uk.ucl.solar.tonic.print.gi;

import com.google.common.collect.Lists;
import com.opencsv.CSVReader;
import gin.SourceFileTree;
import gin.edit.Edit;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.ucl.solar.tonic.solution.PatchSolution;

/**
 *
 * @author Giovani
 */
public class DefaultGIResultsPrinterTest {

    private final File outputDir = FileUtils.getFile("./unittests/tempdir");
    private DefaultGIResultsPrinter printer;
    private List<PatchSolution> solutions;

    private final static String verySmallExampleSourceFilename = "unittests/Small.java";
    private final static List<Edit.EditType> allowableEditTypesTree = Arrays.asList(Edit.EditType.STATEMENT, Edit.EditType.MODIFY_STATEMENT);

    private SourceFileTree sourceFileTree;

    public DefaultGIResultsPrinterTest() {
    }

    @Before
    public void setUp() {
        printer = new DefaultGIResultsPrinter(outputDir);
        solutions = new ArrayList<>();
        sourceFileTree = new SourceFileTree(verySmallExampleSourceFilename, Collections.emptyList());

        PatchSolution solutionA = new PatchSolution(2, 1, sourceFileTree);
        solutionA.getPatch().addRandomEdit(new Random(1), allowableEditTypesTree);
        solutionA.getPatch().addRandomEdit(new Random(2), allowableEditTypesTree);
        solutionA.getPatch().addRandomEdit(new Random(3), allowableEditTypesTree);
        solutionA.getPatch().addRandomEdit(new Random(4), allowableEditTypesTree);
        solutionA.setObjective(0, 10);
        solutionA.setObjective(1, 1);
        solutionA.setAttribute("NTests", 10);
        solutionA.setAttribute("NPassed", 6);
        solutionA.setAttribute("NFailed", 4);
        solutionA.setAttribute("TimeStamp", 1000L);

        PatchSolution solutionB = new PatchSolution(2, 1, sourceFileTree);
        solutionB.getPatch().addRandomEdit(new Random(5), allowableEditTypesTree);
        solutionB.getPatch().addRandomEdit(new Random(6), allowableEditTypesTree);
        solutionB.getPatch().addRandomEdit(new Random(7), allowableEditTypesTree);
        solutionB.getPatch().addRandomEdit(new Random(8), allowableEditTypesTree);
        solutionB.setObjective(0, 15);
        solutionB.setObjective(1, 0.5);
        solutionB.setAttribute("NTests", 10);
        solutionB.setAttribute("NPassed", 7);
        solutionB.setAttribute("NFailed", 3);
        solutionB.setAttribute("TimeStamp", 2000L);

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
            assertArrayEquals(new String[]{"15.0", "-0.5"}, allLines.get(2));
        }

        try ( Scanner reader = new Scanner(FileUtils.getFile(outputDir.getAbsolutePath(), "VAR.csv"))) {
            String line = reader.nextLine();
            assertEquals("gin.edit.statement.SwapStatement unittests/Small.java:28 <-> unittests/Small.java:6,"
                    + "gin.edit.modifynode.BinaryOperatorReplacement  unittests/Small.java:29 OR -> EQUALS,"
                    + "gin.edit.statement.ReplaceStatement unittests/Small.java:42 -> unittests/Small.java:43,"
                    + "gin.edit.statement.ReplaceStatement unittests/Small.java:12 -> unittests/Small.java:6",
                    line.replace("\\", "/"));
            line = reader.nextLine();
            assertEquals("gin.edit.modifynode.UnaryOperatorReplacement  unittests/Small.java:44 POSTFIX_INCREMENT -> PREFIX_INCREMENT,"
                    + "gin.edit.statement.CopyStatement unittests/Small.java:6 -> unittests/Small.java:42:42,"
                    + "gin.edit.modifynode.BinaryOperatorReplacement  unittests/Small.java:31 LESS -> GREATER_EQUALS,"
                    + "gin.edit.modifynode.BinaryOperatorReplacement  unittests/Small.java:37 GREATER -> LESS",
                    line.replace("\\", "/"));
        }

        try ( CSVReader reader = new CSVReader(new FileReader(FileUtils.getFile(outputDir.getAbsolutePath(), "TIME.csv")))) {
            List<String[]> allLines = reader.readAll();
            assertArrayEquals(new String[]{"FullExecTime"}, allLines.get(0));
            assertArrayEquals(new String[]{"1024"}, allLines.get(1));
        }

        try ( CSVReader reader = new CSVReader(new FileReader(FileUtils.getFile(outputDir.getAbsolutePath(), "PATCH.csv")))) {
            List<String[]> allLines = reader.readAll();
            assertArrayEquals(new String[]{"MethodName", "MethodIndex", "Patch", "Compiled", "AllTestsPassed", "NTests", "NPassed", "NFailed", "TotalExecutionTime(ms)", "Fitness", "FitnessImprovement", "TimeStamp"}, allLines.get(0));
            assertArrayEquals(new String[]{"", "", "", "", "", "10", "6", "4", "", "", "", "1000"}, allLines.get(1));
            assertArrayEquals(new String[]{"", "", "", "", "", "10", "7", "3", "", "", "", "2000"}, allLines.get(2));
        }
    }

    @Test
    public void testPrintWithoutHeaders() throws Exception {
        printer.setShouldPrintHeaders(false);
        printer.print();
        try ( CSVReader reader = new CSVReader(new FileReader(FileUtils.getFile(outputDir.getAbsolutePath(), "FUN.csv")))) {
            List<String[]> allLines = reader.readAll();
            assertArrayEquals(new String[]{"10.0", "-1.0"}, allLines.get(0));
            assertArrayEquals(new String[]{"15.0", "-0.5"}, allLines.get(1));
        }

        try ( Scanner reader = new Scanner(FileUtils.getFile(outputDir.getAbsolutePath(), "VAR.csv"))) {
            String line = reader.nextLine();
            assertEquals("gin.edit.statement.SwapStatement unittests/Small.java:28 <-> unittests/Small.java:6,"
                    + "gin.edit.modifynode.BinaryOperatorReplacement  unittests/Small.java:29 OR -> EQUALS,"
                    + "gin.edit.statement.ReplaceStatement unittests/Small.java:42 -> unittests/Small.java:43,"
                    + "gin.edit.statement.ReplaceStatement unittests/Small.java:12 -> unittests/Small.java:6",
                    line.replace("\\", "/"));
            line = reader.nextLine();
            assertEquals("gin.edit.modifynode.UnaryOperatorReplacement  unittests/Small.java:44 POSTFIX_INCREMENT -> PREFIX_INCREMENT,"
                    + "gin.edit.statement.CopyStatement unittests/Small.java:6 -> unittests/Small.java:42:42,"
                    + "gin.edit.modifynode.BinaryOperatorReplacement  unittests/Small.java:31 LESS -> GREATER_EQUALS,"
                    + "gin.edit.modifynode.BinaryOperatorReplacement  unittests/Small.java:37 GREATER -> LESS",
                    line.replace("\\", "/"));
        }

        try ( CSVReader reader = new CSVReader(new FileReader(FileUtils.getFile(outputDir.getAbsolutePath(), "TIME.csv")))) {
            List<String[]> allLines = reader.readAll();
            assertArrayEquals(new String[]{"1024"}, allLines.get(0));
        }

        try ( CSVReader reader = new CSVReader(new FileReader(FileUtils.getFile(outputDir.getAbsolutePath(), "PATCH.csv")))) {
            List<String[]> allLines = reader.readAll();
            assertArrayEquals(new String[]{"", "", "", "", "", "10", "6", "4", "", "", "", "1000"}, allLines.get(0));
            assertArrayEquals(new String[]{"", "", "", "", "", "10", "7", "3", "", "", "", "2000"}, allLines.get(1));
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
            assertArrayEquals(new String[]{"15.0", "-0.5"}, allLines.get(1));
        }

        try ( Scanner reader = new Scanner(FileUtils.getFile(outputDir.getAbsolutePath(), "VAR.csv"))) {
            String line = reader.nextLine();
            assertEquals("gin.edit.statement.SwapStatement unittests/Small.java:28 <-> unittests/Small.java:6,"
                    + "gin.edit.modifynode.BinaryOperatorReplacement  unittests/Small.java:29 OR -> EQUALS,"
                    + "gin.edit.statement.ReplaceStatement unittests/Small.java:42 -> unittests/Small.java:43,"
                    + "gin.edit.statement.ReplaceStatement unittests/Small.java:12 -> unittests/Small.java:6",
                    line.replace("\\", "/"));
            line = reader.nextLine();
            assertEquals("gin.edit.modifynode.UnaryOperatorReplacement  unittests/Small.java:44 POSTFIX_INCREMENT -> PREFIX_INCREMENT,"
                    + "gin.edit.statement.CopyStatement unittests/Small.java:6 -> unittests/Small.java:42:42,"
                    + "gin.edit.modifynode.BinaryOperatorReplacement  unittests/Small.java:31 LESS -> GREATER_EQUALS,"
                    + "gin.edit.modifynode.BinaryOperatorReplacement  unittests/Small.java:37 GREATER -> LESS",
                    line.replace("\\", "/"));
        }

        try ( CSVReader reader = new CSVReader(new FileReader(FileUtils.getFile(outputDir.getAbsolutePath(), "TIME.csv")))) {
            List<String[]> allLines = reader.readAll();
            assertArrayEquals(new String[]{"1024"}, allLines.get(0));
        }

        try ( CSVReader reader = new CSVReader(new FileReader(FileUtils.getFile(outputDir.getAbsolutePath(), "PATCH.csv")))) {
            List<String[]> allLines = reader.readAll();
            assertArrayEquals(new String[]{"MethodName", "MethodIndex", "Patch", "Compiled", "AllTestsPassed", "NTests", "NPassed", "NFailed", "TotalExecutionTime(ms)", "Fitness", "FitnessImprovement", "TimeStamp"}, allLines.get(0));
            assertArrayEquals(new String[]{"", "", "", "", "", "10", "6", "4", "", "", "", "1000"}, allLines.get(1));
            assertArrayEquals(new String[]{"", "", "", "", "", "10", "7", "3", "", "", "", "2000"}, allLines.get(2));
        }
    }
    
    @Test
    public void testShouldPrintNothing() throws Exception {
        printer.setTimes(new ArrayList<>());
        printer.setSolutionList(new ArrayList<>());
        printer.print();
        assertFalse(FileUtils.getFile(outputDir.getAbsolutePath(), "PATCH.csv").exists());
    }
    
    @Test
    public void testShouldPrintNothing2() throws Exception {
        printer.setTimes(new ArrayList<>());
        printer.setPatchColumnsNames(new ArrayList<>());
        printer.print();
        assertFalse(FileUtils.getFile(outputDir.getAbsolutePath(), "PATCH.csv").exists());
    }

}
