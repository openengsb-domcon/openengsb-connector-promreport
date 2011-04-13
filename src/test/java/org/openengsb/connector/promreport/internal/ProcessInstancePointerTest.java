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

import org.junit.Test;
import org.openengsb.connector.promreport.internal.model.ProcessInstancePointer;


public class ProcessInstancePointerTest {

    @Test
    public void getBytes_shouldReturnByteRepresentation() {
        String content = "processId=1\ninstanceId=100\n";        
        ProcessInstancePointer part = new ProcessInstancePointer("repPart");
        part.setProcessId(1L);
        part.setProcessInstanceId("100");
        assertThat(part.getContent(), is(content.getBytes()));
    }
    
}
