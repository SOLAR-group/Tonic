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

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.Validate;
import org.pmw.tinylog.Logger;
import org.uma.jmetal.solution.Solution;
import uk.ucl.solar.tonic.exception.TonicException;

/**
 *
 * @author Giovani
 */
public class DefaultResultsPrinter<S extends Solution> implements Serializable {

    protected final File outputDir;
    protected String separator = ",";
    protected String varFileName = "VAR.csv";
    protected String funFileName = "FUN.csv";
    protected String timeFileName = "TIME.csv";
    protected boolean shouldPrintHeaders = true;

    protected List<S> solutionList;

    protected List<String> objectiveNames;
    protected List<Boolean> isObjectiveToBeMinimized;

    protected List<String> timeNames;
    protected List<Long> times;

    public DefaultResultsPrinter(File outputDir) {
        this.outputDir = outputDir;
    }

    public DefaultResultsPrinter(File outputDir, List<S> solutionList) {
        this.outputDir = outputDir;
        this.solutionList = solutionList;
    }

    public DefaultResultsPrinter(File outputDir, List<S> solutionList, List<Long> times) {
        this.outputDir = outputDir;
        this.solutionList = solutionList;
        this.times = times;
    }

    public DefaultResultsPrinter(File outputDir, List<S> solutionList, Long time) {
        this.outputDir = outputDir;
        this.solutionList = solutionList;
        this.times = Lists.newArrayList(time);
    }

    public DefaultResultsPrinter(File outputDir, List<S> solutionList, List<Long> times, boolean shouldWriteHeaders) {
        this.outputDir = outputDir;
        this.solutionList = solutionList;
        this.times = times;
        this.shouldPrintHeaders = shouldWriteHeaders;
    }

    public DefaultResultsPrinter(File outputDir, List<S> solutionList, Long time, boolean shouldWriteHeaders) {
        this.outputDir = outputDir;
        this.solutionList = solutionList;
        this.times = Lists.newArrayList(time);
        this.shouldPrintHeaders = shouldWriteHeaders;
    }

    public boolean getShouldWriteHeaders() {
        return shouldPrintHeaders;
    }

    public void setShouldPrintHeaders(boolean shouldPrintHeaders) {
        this.shouldPrintHeaders = shouldPrintHeaders;
    }

    public String getVarFileName() {
        return varFileName;
    }

    public void setVarFileName(String varFileName) {
        this.varFileName = varFileName;
    }

    public String getFunFileName() {
        return funFileName;
    }

    public void setFunFileName(String funFileName) {
        this.funFileName = funFileName;
    }

    public String getTimeFileName() {
        return timeFileName;
    }

    public void setTimeFileName(String timeFileName) {
        this.timeFileName = timeFileName;
    }

    public List<S> getSolutionList() {
        return solutionList;
    }

    public void setSolutionList(List<S> solutionList) {
        this.solutionList = solutionList;
    }

    public List<Boolean> getIsObjectiveToBeMinimized() {
        return isObjectiveToBeMinimized;
    }

    public void setIsObjectiveToBeMinimized(List<Boolean> isObjectiveToBeMinimized) {
        this.isObjectiveToBeMinimized = isObjectiveToBeMinimized;
    }

    public List<Long> getTimes() {
        return times;
    }

    public void setTimes(List<Long> times) {
        this.times = times;
    }

    public void setTime(Long time) {
        this.times = Lists.newArrayList(time);
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public List<String> getObjectiveNames() {
        return objectiveNames;
    }

    public void setObjectiveNames(List<String> objectiveNames) {
        this.objectiveNames = objectiveNames;
    }

    public List<String> getTimeNames() {
        return timeNames;
    }

    public void setTimeNames(List<String> timeNames) {
        this.timeNames = timeNames;
    }

    public void print() throws IOException, TonicException {
        this.createOutputDir();
        this.printObjectivesToFile();
        this.printVariablesToFile();
        this.printTimesTofile();
    }

    protected void createOutputDir() throws IOException {
        Validate.notNull(outputDir);
        try {
            Files.createDirectories(outputDir.toPath(), null);
        } catch (IOException ex) {
            Logger.error(ex, "Error creating output directory for the results: " + outputDir.getAbsolutePath());
            throw ex;
        }
    }

    protected void printLine(List<?> elements, FileWriter writer) throws IOException {
        StringBuilder lineBuilder = new StringBuilder();
        Joiner.on(separator).appendTo(lineBuilder, elements);
        this.printLine(lineBuilder.toString(), writer);
    }

    protected void printLine(String line, FileWriter writer) throws IOException {
        try {
            writer.write(line);
            writer.write(System.lineSeparator());
        } catch (IOException ex) {
            Logger.error(ex, "Error writing data to file.");
            throw ex;
        }
    }

    protected void printHeader(List<String> columns, FileWriter writer) throws IOException {
        if (shouldPrintHeaders) {
            this.printLine(columns, writer);
        }
    }

    protected void printVariablesToFile() throws IOException {
        if (solutionList != null && !solutionList.isEmpty()) {
            FileWriter writer = null;
            try {
                writer = new FileWriter(FileUtils.getFile(outputDir, varFileName));
                for (Solution<?> solution : solutionList) {
                    printLine(solution.getVariables(), writer);
                }
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException ex) {
                        Logger.error(ex, "Error closing file stream.");
                    }
                }
            }
        }
    }

    protected void printObjectivesToFile() throws IOException, TonicException {
        if (solutionList != null && !solutionList.isEmpty()) {
            FileWriter writer = null;
            try {
                writer = new FileWriter(FileUtils.getFile(outputDir, funFileName));
                int numberOfObjectives = solutionList.get(0).getNumberOfObjectives();
                if (isObjectiveToBeMinimized != null && isObjectiveToBeMinimized.size() != numberOfObjectives) {
                    throw new TonicException("The size of list minimizeObjective is not correct: " + isObjectiveToBeMinimized.size());
                }
                if (objectiveNames != null && !objectiveNames.isEmpty()) {
                    if (objectiveNames.size() != numberOfObjectives) {
                        throw new TonicException("The size of list objectiveNames is not correct: " + objectiveNames.size());
                    }
                    this.printHeader(objectiveNames, writer);
                }
                for (Solution<?> solution : solutionList) {
                    List<String> objectives = new ArrayList<>(numberOfObjectives);
                    for (int j = 0; j < numberOfObjectives; j++) {
                        if (isObjectiveToBeMinimized == null || isObjectiveToBeMinimized.get(j)) {
                            objectives.add(String.valueOf(solution.getObjective(j)));
                        } else {
                            objectives.add(String.valueOf(-1.0 * solution.getObjective(j)));
                        }
                    }
                    this.printLine(objectives, writer);
                }
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException ex) {
                        Logger.error(ex, "Error closing file stream.");
                    }
                }
            }
        }
    }

    protected void printTimesTofile() throws IOException, TonicException {
        if (times != null && !times.isEmpty()) {
            FileWriter writer = null;
            try {
                writer = new FileWriter(FileUtils.getFile(outputDir, timeFileName));
                if (timeNames != null && !timeNames.isEmpty()) {
                    if (timeNames.size() != times.size()) {
                        throw new TonicException("The size of list timeNames is not correct: " + timeNames.size());
                    }
                    this.printHeader(timeNames, writer);
                }
                printLine(times, writer);
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException ex) {
                        Logger.error(ex, "Error closing file stream.");
                    }
                }
            }
        }
    }

}
