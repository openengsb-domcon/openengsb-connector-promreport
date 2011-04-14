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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openengsb.connector.promreport.internal.model.ProcessInstancePointer;
import org.openengsb.connector.promreport.internal.mxml.WorkflowLog;
import org.openengsb.core.api.AliveState;
import org.openengsb.core.api.Event;
import org.openengsb.domain.report.NoSuchReportException;
import org.openengsb.domain.report.common.ReportStore;
import org.openengsb.domain.report.model.Report;
import org.openengsb.domain.report.model.SimpleReportPart;

public class PromReportServiceTest {

    private PromReportService reportService;
    private ReportStore store;
    private MxmlStore mxmlStore;
    private EventTransformator transformer;

    @Before
    public void setUp() {
        reportService = new PromReportService("test");
        store = mock(ReportStore.class);
        mxmlStore = mock(MxmlStore.class);
        transformer = mock(EventTransformator.class);
        when(transformer.getProcessId(Mockito.any(Event.class))).thenReturn(10L);
        reportService.setStore(store);
        reportService.setMxmlStore(mxmlStore);
        reportService.setTransformer(transformer);
    }

    @Test(expected = NoSuchReportException.class)
    public void generateReport_UnknownId_shouldFail() throws NoSuchReportException {
        reportService.generateReport("foo", "bar", "buz");
    }

    @Test
    public void generateReport_shouldStoreReportAndMxml() throws NoSuchReportException {
        String reportId = reportService.collectData();
        Report report = reportService.generateReport(reportId, "foo", "bar");
        assertThat(report.getName(), is("bar"));
        verify(store).storeReport("foo", report);
        verify(mxmlStore).persist(null);
    }

    @Test(expected = NoSuchReportException.class)
    public void generateReportTwice_shouldFail() throws NoSuchReportException {
        String reportId = reportService.collectData();
        reportService.generateReport(reportId, "foo", "bar");
        reportService.generateReport(reportId, "foo", "bar");
    }

    @Test
    public void getDraft_shouldNotInvokePersistence() throws NoSuchReportException {
        String reportId = reportService.collectData();
        Report report = reportService.getDraft(reportId, "bar");
        assertThat(report.getName(), is("bar"));
        verify(store, Mockito.never()).storeReport(Mockito.eq(reportId), Mockito.any(Report.class));
        verify(mxmlStore, Mockito.never()).persist(Mockito.any(WorkflowLog.class));
    }

    @Test
    public void generateDraftTwice_shouldWork() throws NoSuchReportException {
        String reportId = reportService.collectData();
        reportService.getDraft(reportId, "bar");
        reportService.getDraft(reportId, "bar");
    }

    @Test
    public void addReportPart_shouldWork() throws NoSuchReportException {
        String reportId = reportService.collectData();
        reportService.addReportPart(reportId, new ProcessInstancePointer("bar"));
        Report report = reportService.generateReport(reportId, "buz", "42");
        assertThat(report.getParts().size(), is(1));
        assertThat(report.getParts().get(0).getPartName(), is("bar"));
    }

    @Test(expected = NoSuchReportException.class)
    public void addReportPartWrongReportId_shouldFail() throws NoSuchReportException {
        reportService.addReportPart("wrongReportId", new ProcessInstancePointer("bar"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addReportPartWrongType_shouldFail() throws NoSuchReportException {
        String reportId = reportService.collectData();
        reportService.addReportPart(reportId, new SimpleReportPart("bar", "text/plain", null));
    }

    @Test(expected = NoSuchReportException.class)
    public void processEventWrongReportId_shouldFail() throws NoSuchReportException {
        reportService.processEvent("wrongReportId", new Event());
    }

    @Test
    public void processEvent_shouldWork() throws NoSuchReportException {
        String reportId = reportService.collectData();
        Event testEvent = mock(Event.class);

        reportService.processEvent(reportId, testEvent);
        verify(transformer).getProcessId(testEvent);
        verify(transformer).transform(testEvent);
    }

    @Test
    public void getAliveState_ShouldBeOnline() {
        assertThat(reportService.getAliveState(), is(AliveState.ONLINE));
    }


}
