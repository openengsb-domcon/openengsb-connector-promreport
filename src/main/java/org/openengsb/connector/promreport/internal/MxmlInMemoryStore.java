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

import org.openengsb.connector.promreport.internal.model.ProcessInstancePointer;
import org.openengsb.connector.promreport.internal.mxml.AuditTrailEntry;
import org.openengsb.connector.promreport.internal.mxml.WorkflowLog;

public interface MxmlInMemoryStore {

    /**
     * Appends the AuditTrailEntry to the process instance of the process with the given processId.
     * Each report manages its own process instances. 
     * Keeps it only in the memory.
     * 
     * @return a pointer to the process instance of which the event belongs to 
     */
    ProcessInstancePointer store(String reportId, long processId, AuditTrailEntry ae, String partName);

    /**
     * Returns and removes it from this store.
     * 
     * @return all stored data of the report collected to a workflow log
     */
    WorkflowLog take(String reportId);

}
