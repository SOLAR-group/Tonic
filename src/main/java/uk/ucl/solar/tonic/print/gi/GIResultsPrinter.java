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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.pmw.tinylog.Logger;
import uk.ucl.solar.tonic.exception.TonicException;
import uk.ucl.solar.tonic.print.DefaultResultsPrinter;
import uk.ucl.solar.tonic.solution.PatchSolution;

/**
 *
 * @author Giovani
 */
public class GIResultsPrinter extends DefaultResultsPrinter<PatchSolution> {

    protected List<String> patchColumnsNames;
    protected String patchFileName = "PATCH.csv";

    public GIResultsPrinter() {
    }

    public GIResultsPrinter(List<String> patchColumnsNames) {
        this.patchColumnsNames = patchColumnsNames;
    }
    
    public GIResultsPrinter(File outputDir, List<String> patchColumnsNames) {
        super(outputDir);
        this.patchColumnsNames = patchColumnsNames;
    }

    public GIResultsPrinter(File outputDir, List<PatchSolution> solutionList, List<String> patchColumnsNames) {
        super(outputDir, solutionList);
        this.patchColumnsNames = patchColumnsNames;
    }

    public GIResultsPrinter(File outputDir, List<PatchSolution> solutionList, List<Long> times, List<String> patchColumnsNames) {
        super(outputDir, solutionList, times);
        this.patchColumnsNames = patchColumnsNames;
    }

    public GIResultsPrinter(File outputDir, List<PatchSolution> solutionList, Long time, List<String> patchColumnsNames) {
        super(outputDir, solutionList, time);
        this.patchColumnsNames = patchColumnsNames;
    }

    public GIResultsPrinter(File outputDir, List<PatchSolution> solutionList, List<Long> times, boolean shouldWriteHeaders, List<String> patchColumnsNames) {
        super(outputDir, solutionList, times, shouldWriteHeaders);
        this.patchColumnsNames = patchColumnsNames;
    }

    public GIResultsPrinter(File outputDir, List<PatchSolution> solutionList, Long time, boolean shouldWriteHeaders, List<String> patchColumnsNames) {
        super(outputDir, solutionList, time, shouldWriteHeaders);
        this.patchColumnsNames = patchColumnsNames;
    }

    public List<String> getPatchColumnsNames() {
        return patchColumnsNames;
    }

    public void setPatchColumnsNames(List<String> patchColumnsNames) {
        this.patchColumnsNames = patchColumnsNames;
    }

    public String getPatchFileName() {
        return patchFileName;
    }

    public void setPatchFileName(String patchFileName) {
        this.patchFileName = patchFileName;
    }

    @Override
    public void print() throws IOException, TonicException {
        super.print();
        this.printPatchesToFile();
    }

    private void printPatchesToFile() throws TonicException, IOException {
        if (solutionList != null && !solutionList.isEmpty()
                && patchColumnsNames != null && !patchColumnsNames.isEmpty()) {
            FileWriter writer = null;
            try {
                writer = new FileWriter(FileUtils.getFile(outputDir, patchFileName));
                this.printHeader(patchColumnsNames, writer);
                for (PatchSolution solution : solutionList) {
                    List<?> attributes = patchColumnsNames.stream()
                            .map(column -> solution.getAttribute(column))
                            .collect(Collectors.toList());
                    this.printLine(attributes, writer);
                }
            } finally {
                this.closeWriter(writer);
            }
        }
    }

}
