/**
 * Licensed to the Austrian Association for Software Tool Integration (AASTI)
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. The AASTI licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openengsb.connector.promreport.internal;

import java.util.ArrayList;
import java.util.List;

import org.openengsb.core.api.model.OpenEngSBModelEntry;
import org.openengsb.domain.report.model.Report;
import org.openengsb.domain.report.model.ReportPart;

public class TestReport implements Report {
    private String name;
    private List<ReportPart> parts;

    public TestReport(String name) {
        this.name = name;
        this.parts = new ArrayList<ReportPart>();
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setParts(List<ReportPart> parts) {
        this.parts = parts;
    }

    @Override
    public List<ReportPart> getParts() {
        return parts;
    }
    
    @Override
    public void addOpenEngSBModelEntry(OpenEngSBModelEntry arg0) {
    }

    @Override
    public List<OpenEngSBModelEntry> getOpenEngSBModelEntries() {
        return null;
    }

    @Override
    public void removeOpenEngSBModelEntry(String arg0) {
    }

}
