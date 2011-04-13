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
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.mockito.Mockito;
import org.openengsb.domain.report.common.ReportStore;
import org.openengsb.domain.report.common.ReportStoreFactory;

public class PromReportFactoryTest {
    
    @Test
    public void testCreatePromReportService() throws Exception {
        ReportStoreFactory storeFactory = mock(ReportStoreFactory.class);
        ReportStore store = mock(ReportStore.class);
        when(storeFactory.createReportStore(Mockito.anyString())).thenReturn(store);
        
        MxmlStoreFactory mxmlstoreFactory = mock(MxmlStoreFactory.class);
        MxmlStore mxmlStore = mock(MxmlStore.class);
        Mockito.when(mxmlstoreFactory.createMxmlStore(Mockito.anyString())).thenReturn(mxmlStore);
        
        EventTransformator transformer = mock(EventTransformator.class);
        
        PromReportFactory factory = new PromReportFactory(storeFactory, mxmlstoreFactory, transformer);

        Map<String, String> attributes = new HashMap<String, String>();
        PromReportService reportService = factory.createServiceInstance("id", attributes);

        assertThat(reportService, notNullValue());
        assertThat(reportService.getStore(), notNullValue());
        assertThat(reportService.getMxmlStore(), notNullValue());
        assertThat(reportService.getTransformer(), notNullValue());
        assertThat(reportService.getInstanceId(), is("id"));
    }

}
