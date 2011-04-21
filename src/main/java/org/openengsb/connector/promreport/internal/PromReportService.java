/**
 * Copyright 2010 OpenEngSB Division, Vienna University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openengsb.connector.promreport.internal;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.openengsb.connector.promreport.internal.model.ProcessInstancePointer;
import org.openengsb.core.api.AliveState;
import org.openengsb.core.api.Event;
import org.openengsb.domain.report.NoSuchReportException;
import org.openengsb.domain.report.common.AbstractReportDomain;
import org.openengsb.domain.report.model.Report;
import org.openengsb.domain.report.model.ReportPart;

public class PromReportService extends AbstractReportDomain {

    private Set<String> activeReportIds = new HashSet<String>();

    private ReportPartStore partStore = new InMemoryReportPartStore();
    private MxmlInMemoryStore mxmlTemp = new DefaultMxmlInMemoryStore();
    
    private MxmlStore mxmlStore;
    private EventTransformator transformer;

    public PromReportService(String id) {
        super(id);
    }

    public void setMxmlStore(MxmlStore mxmlStore) {
        this.mxmlStore = mxmlStore;
    }

    public void setTransformer(EventTransformator transformer) {
        this.transformer = transformer;
    }

    public MxmlStore getMxmlStore() {
        return mxmlStore;
    }

    public EventTransformator getTransformer() {
        return transformer;
    }

    @Override
    public Report generateReport(String reportId, String category, String reportName) throws NoSuchReportException {
        checkId(reportId);
        Report report = doGenerateReport(reportName, reportId);
        mxmlStore.persist(mxmlTemp.take(reportId));
        activeReportIds.remove(reportId);
        partStore.clearParts(reportId);
        storeReport(category, report);
        return report;
    }

    @Override
    public Report getDraft(String reportId, String draftName) throws NoSuchReportException {
        checkId(reportId);
        return doGenerateReport(draftName, reportId);
    }

    @Override
    public String collectData() {
        String reportId = UUID.randomUUID().toString();
        activeReportIds.add(reportId);
        return reportId;
    }

    @Override
    public void addReportPart(String reportId, ReportPart reportPart) throws NoSuchReportException {
        if (!(reportPart instanceof ProcessInstancePointer)) {
            throw new IllegalArgumentException("Prom report service supports only ReportParts of type "
                + "ProcessInstancePointer");
        }
        checkId(reportId);
        partStore.storePart(reportId, reportPart);
    }

    @Override
    public void processEvent(String reportId, Event event) throws NoSuchReportException {
        checkId(reportId);
        Long processId = transformer.getProcessId(event);
        if (processId == null) {
            return; //ignore events without Id
        }
        ReportPart reportPart = mxmlTemp.store(reportId, processId, transformer.transform(event), 
            generatePartName(event));
        partStore.storePart(reportId, reportPart);
    }

    @Override
    public AliveState getAliveState() {
        return AliveState.ONLINE;
    }

    private void checkId(String reportId) throws NoSuchReportException {
        if (activeReportIds.contains(reportId)) {
            return;
        }
        throw new NoSuchReportException("Currently no report is generated for reportId: " + reportId);
    }

    private Report doGenerateReport(String reportName, String reportId) {
        List<ReportPart> parts = partStore.getParts(reportId);
        Report report = new Report(reportName);
        report.setParts(parts);
        return report;
    }

    private String generatePartName(Event e) {
        return e.getClass().getName() + " - " + new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date());
    }
}
