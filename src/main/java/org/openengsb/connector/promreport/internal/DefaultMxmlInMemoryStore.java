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

import java.util.HashMap;
import java.util.Map;

import org.openengsb.connector.promreport.internal.model.ProcessInstancePointer;
import org.openengsb.connector.promreport.internal.mxml.AuditTrailEntry;
import org.openengsb.connector.promreport.internal.mxml.Process;
import org.openengsb.connector.promreport.internal.mxml.ProcessInstance;
import org.openengsb.connector.promreport.internal.mxml.WorkflowLog;

public class DefaultMxmlInMemoryStore implements MxmlInMemoryStore {

    Map<String, WorkflowLog> workflowMap = new HashMap<String, WorkflowLog>();
    
    @Override
    public ProcessInstancePointer store(String reportId, long processId, AuditTrailEntry ae, String partName) {
        if (reportId == null) {
            return null;
        }
        if (ae == null) {
            return null;
        }
        ProcessInstance pi = getProcessInstance(reportId, processId);
        pi.getAuditTrailEntry().add(ae);
        ProcessInstancePointer pipointer = new ProcessInstancePointer(partName);
        pipointer.setProcessId(processId);
        pipointer.setProcessInstanceId(pi.getId());
        return pipointer;
    }
    
    private ProcessInstance getProcessInstance(String reportId, long processId) {
        Process p = getProcess(reportId, processId);
        for (ProcessInstance pi : p.getProcessInstance()) {
            if (reportId.equals(pi.getId())) {
                return pi;
            }
        }
        ProcessInstance pi = new ProcessInstance();
        pi.setId(reportId);
        p.getProcessInstance().add(pi);
        return pi;
    }

    private Process getProcess(String reportId, long processId) {
        WorkflowLog workflow = getWorkflowLog(reportId);
        for (Process p : workflow.getProcess()) {
            if (String.valueOf(processId).equals(p.getId())) {
                return p;
            }
        }
        Process p = new Process();
        p.setId(String.valueOf(processId));
        workflow.getProcess().add(p);
        return p;
    }

    private WorkflowLog getWorkflowLog(String reportId) {
        WorkflowLog workflow = workflowMap.get(reportId);
        if (workflow == null) {
            workflow = new WorkflowLog();
            workflowMap.put(reportId, workflow);
        }
        return workflow;
    }

    @Override
    public WorkflowLog take(String reportId) {
        WorkflowLog workflow =  workflowMap.get(reportId);
        workflowMap.remove(reportId);
        return workflow;
    }
}
