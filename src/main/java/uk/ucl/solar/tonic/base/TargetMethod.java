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
package uk.ucl.solar.tonic.base;

import gin.test.UnitTest;

import java.io.File;
import java.util.List;
import java.util.Objects;

/**
 * @author Giovani
 */
public class TargetMethod {

    private File source;
    private String className;

    private String methodName;
    private List<UnitTest> tests;

    private Integer methodID;

    public TargetMethod(File source, String className, String methodName, List<UnitTest> tests, Integer methodID) {
        this.source = source;
        this.className = className;
        this.methodName = methodName;
        this.tests = tests;
        this.methodID = methodID;
    }

    public File getFileSource() {
        return source;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<UnitTest> getGinTests() {
        return tests;
    }

    public Integer getMethodID() {
        return methodID;
    }

    @Override
    public String toString() {
        return className + "." + methodName;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.methodID);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TargetMethod other = (TargetMethod) obj;
        if (!Objects.equals(this.methodID, other.methodID)) {
            return false;
        }
        return true;
    }

}
