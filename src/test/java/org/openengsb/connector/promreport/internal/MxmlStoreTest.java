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
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openengsb.connector.promreport.internal.mxml.AuditTrailEntry;
import org.openengsb.connector.promreport.internal.mxml.AuditTrailEntry.EventType;
import org.openengsb.connector.promreport.internal.mxml.Eventtypes;
import org.openengsb.connector.promreport.internal.mxml.Process;
import org.openengsb.connector.promreport.internal.mxml.ProcessInstance;
import org.openengsb.connector.promreport.internal.mxml.WorkflowLog;

public abstract class MxmlStoreTest {

    private MxmlStore mxmlStore;
    private WorkflowLog workflow;
    private long pid;
    private String iid;
    
    public abstract MxmlStore getMxmlStore();
    
    public abstract void clearStore() throws Exception;
    
    @Before
    public void setUp() {
        mxmlStore = getMxmlStore();
        workflow = new WorkflowLog();
        
        Process p = new Process();
        pid = 1;
        p.setId(String.valueOf(pid));
        
        ProcessInstance pi = new ProcessInstance();
        iid = "FOO-20";
        pi.setId(iid);
        
        p.getProcessInstance().add(pi);
        workflow.getProcess().add(p);
        
        mxmlStore.persist(workflow);
    }
    
    @After
    public void tearDown() throws Exception {
        clearStore();
    }
    
    @Test
    public void persist_shouldWork() {
        workflow.getProcess().get(0).getProcessInstance().get(0).setId(iid + "NEW");
        mxmlStore.persist(workflow);
        ProcessInstance pi = mxmlStore.read(pid, iid + "NEW");
        assertThat(pi.getId(), is(iid + "NEW"));
    }
    
    @Test
    public void persist_appendPIWithEntry_shouldWork() {
        ProcessInstance newpi = new ProcessInstance();
        newpi.setId("iid");
        newpi.getAuditTrailEntry().add(getEntry());
        workflow.getProcess().get(0).getProcessInstance().add(newpi);
        mxmlStore.persist(workflow);
        ProcessInstance pi = mxmlStore.read(pid, "iid");
        assertThat(pi.getId(), is("iid"));
        assertThat(pi.getAuditTrailEntry().get(0).getWorkflowModelElement(), is("element"));
        assertThat(pi.getAuditTrailEntry().get(0).getEventType().getValue(), is(Eventtypes.START));
    }
    
    @Test
    public void read_shouldReturnNull() {
        assertThat(mxmlStore.read(pid, "unkown"), nullValue());
    }
    
    @Test
    public void read_shouldReturnProcessInstance() {
        ProcessInstance pi = mxmlStore.read(pid, iid);
        assertThat(pi.getId(), is(iid));
    }
    
    private AuditTrailEntry getEntry() {
        AuditTrailEntry ae = new AuditTrailEntry();
        ae.setWorkflowModelElement("element");
        EventType type = new EventType();
        type.setValue(Eventtypes.START);
        ae.setEventType(type);
        return ae;
    }

}
