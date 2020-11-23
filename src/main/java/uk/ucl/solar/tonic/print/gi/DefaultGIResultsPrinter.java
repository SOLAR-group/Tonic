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
import java.io.File;
import java.util.List;
import org.apache.commons.collections4.ListUtils;
import uk.ucl.solar.tonic.solution.PatchSolution;

/**
 *
 * @author Giovani
 */
public class DefaultGIResultsPrinter extends GIResultsPrinter {

    private static final List<String> PATCH_COLUMNS = ListUtils.unmodifiableList(
            Lists.newArrayList(
                    "MethodName",
                    "MethodIndex",
                    "Patch",
                    "Compiled",
                    "AllTestsPassed",
                    "NTests",
                    "NPassed",
                    "NFailed",
                    "TotalExecutionTime(ms)",
                    "Fitness",
                    "FitnessImprovement",
                    "TimeStamp"));

    public DefaultGIResultsPrinter() {
        super(PATCH_COLUMNS);
    }

    public DefaultGIResultsPrinter(File outputDir) {
        super(outputDir, PATCH_COLUMNS);
    }

    public DefaultGIResultsPrinter(File outputDir, List<PatchSolution> solutionList) {
        super(outputDir, solutionList, PATCH_COLUMNS);
    }

    public DefaultGIResultsPrinter(File outputDir, List<PatchSolution> solutionList, List<Long> times) {
        super(outputDir, solutionList, times, PATCH_COLUMNS);
    }

    public DefaultGIResultsPrinter(File outputDir, List<PatchSolution> solutionList, Long time) {
        super(outputDir, solutionList, time, PATCH_COLUMNS);
    }

    public DefaultGIResultsPrinter(File outputDir, List<PatchSolution> solutionList, List<Long> times, boolean shouldWriteHeaders) {
        super(outputDir, solutionList, times, shouldWriteHeaders, PATCH_COLUMNS);
    }

    public DefaultGIResultsPrinter(File outputDir, List<PatchSolution> solutionList, Long time, boolean shouldWriteHeaders) {
        super(outputDir, solutionList, time, shouldWriteHeaders, PATCH_COLUMNS);
    }

}
