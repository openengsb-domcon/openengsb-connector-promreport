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
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.openengsb.connector.promreport.internal.model.ProcessInstancePointer;
import org.openengsb.connector.promreport.internal.mxml.AuditTrailEntry;
import org.openengsb.connector.promreport.internal.mxml.Process;
import org.openengsb.connector.promreport.internal.mxml.ProcessInstance;

public abstract class MxmlInMemoryStoreTest {

    private MxmlInMemoryStore mxmlStore;
    private AuditTrailEntry atentry;
    private String element;
    private String reportId;
    private long processId;
    
    protected abstract MxmlInMemoryStore getMxmlStore();
    
    @Before
    public void setUp() {
        mxmlStore = getMxmlStore();
        atentry = new AuditTrailEntry();
        element = "foo";
        atentry.setWorkflowModelElement(element);
        reportId = "report";
        processId = 10L;
        mxmlStore.store(reportId, processId, atentry, "part");
        mxmlStore.store("bar", processId, atentry, "part2");
    }
    
    @Test
    public void store_shouldReturnPointer() {
        ProcessInstancePointer pip = mxmlStore.store(reportId, processId, atentry, "bar");
        assertThat(pip.getProcessId(), is(processId));
    }
    
    @Test
    public void store_shouldReturnNull() {
        assertThat(mxmlStore.store(reportId, processId, null, "foo"), nullValue());
        assertThat(mxmlStore.store(null, processId, new AuditTrailEntry(), "bar"), nullValue());
    }
    
    @Test
    public void take_shouldReturnWorkflowContainingEntry() {
        Process p = mxmlStore.take(reportId).getProcess().get(0);
        assertThat(p.getId(), is(String.valueOf(processId)));
        ProcessInstance pi = p.getProcessInstance().get(0);
        assertThat(pi, notNullValue());
        AuditTrailEntry ae = pi.getAuditTrailEntry().get(0);
        assertThat(ae.getWorkflowModelElement(), is(element));        
    }
    
    @Test
    public void take_shouldRemoveMapEntry() {
        assertThat(mxmlStore.take(reportId), notNullValue());
        assertThat(mxmlStore.take("bar"), notNullValue());
        assertThat(mxmlStore.take(reportId), nullValue());
    }
    
    
    
}
