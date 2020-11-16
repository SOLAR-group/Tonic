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
import gin.edit.Edit;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 *
 * @author Giovani
 */
public class DefaultResultsPrinter {

    protected final File outputDir;
    protected String separator = ",";
    protected String varFileName = "VAR.csv";
    protected String funFileName = "FUN.csv";
    protected String timeFileName = "TIME.csv";

    protected List<? extends Solution<?>> solutionList;
    protected List<Boolean> isObjectiveToBeMinimized;
    protected List<Long> times;

    public DefaultResultsPrinter(File outputDir, List<Solution<Edit>> solutionList, List<Long> times) {
        this.outputDir = outputDir;
        this.solutionList = solutionList;
        this.times = times;
    }

    public DefaultResultsPrinter(File outputDir, List<Solution<Edit>> solutionList, Long time) {
        this.outputDir = outputDir;
        this.solutionList = solutionList;
        this.times = Lists.newArrayList(time);
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

    public List<? extends Solution<?>> getSolutionList() {
        return solutionList;
    }

    public void setSolutionList(List<? extends Solution<?>> solutionList) {
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

    public void print() {
        printObjectivesToFile();
        printVariablesToFile();
        printTimesTofile();
    }

    public void printVariablesToFile() {
        FileWriter writer = null;
        try {
            writer = new FileWriter(FileUtils.getFile(outputDir, varFileName));
            for (Solution<?> solution : solutionList) {
                StringBuilder lineBuilder = new StringBuilder();
                Joiner.on(separator).appendTo(lineBuilder, solution.getVariables());
                lineBuilder.append(System.lineSeparator());
                writer.write(lineBuilder.toString());
            }
        } catch (IOException e) {
            throw new JMetalException("Error writing data ", e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ex) {
                    Logger.getLogger(DefaultResultsPrinter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void printObjectivesToFile() {
        FileWriter writer = null;
        try {
            writer = new FileWriter(FileUtils.getFile(outputDir, funFileName));
            if (!solutionList.isEmpty()) {
                int numberOfObjectives = solutionList.get(0).getNumberOfObjectives();
                if (isObjectiveToBeMinimized != null && numberOfObjectives != isObjectiveToBeMinimized.size()) {
                    throw new JMetalException("The size of list minimizeObjective is not correct: " + isObjectiveToBeMinimized.size());
                }
                for (Solution<?> solution : solutionList) {
                    String line = "";
                    Joiner joiner = Joiner.on(separator);
                    for (int j = 0; j < numberOfObjectives; j++) {
                        if (isObjectiveToBeMinimized == null || isObjectiveToBeMinimized.get(j)) {
                            line = joiner.join(line, solution.getObjective(j));
                        } else {
                            line = joiner.join(line, -1.0 * solution.getObjective(j));
                        }
                    }
                    writer.write(line + System.lineSeparator());
                }
            }
        } catch (IOException e) {
            throw new JMetalException("Error writing data ", e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ex) {
                    Logger.getLogger(DefaultResultsPrinter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void printTimesTofile() {
        FileWriter writer = null;
        try {
            writer = new FileWriter(FileUtils.getFile(outputDir, timeFileName));
            StringBuilder lineBuilder = new StringBuilder();
            Joiner.on(separator).appendTo(lineBuilder, times);
            lineBuilder.append(System.lineSeparator());
            writer.write(lineBuilder.toString());
        } catch (IOException e) {
            throw new JMetalException("Error writing data ", e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ex) {
                    Logger.getLogger(DefaultResultsPrinter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

}
