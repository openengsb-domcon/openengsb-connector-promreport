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

import java.util.Map;

import org.openengsb.core.api.Connector;
import org.openengsb.core.common.AbstractConnectorInstanceFactory;
import org.openengsb.domain.report.ReportDomainEvents;
import org.openengsb.domain.report.common.ReportStoreFactory;

public class PromReportFactory extends AbstractConnectorInstanceFactory<PromReportService> {

    private ReportStoreFactory storeFactory;
    private MxmlStoreFactory mxmlStoreFactory;
    private EventTransformator eventTransformator;
    
    @SuppressWarnings("unused")
    private ReportDomainEvents reportEvents;

    @Override
    public Connector createNewInstance(String id) {
        PromReportService service = new PromReportService(id);
        service.setStore(storeFactory.createReportStore(id));
        service.setMxmlStore(mxmlStoreFactory.createMxmlStore(id));
        service.setTransformer(eventTransformator);
        return service;
    }

    @Override
    public void doApplyAttributes(PromReportService connector, Map<String, String> attributes) {
        // do nothing - currently no attributes defined
    }

    public void setStoreFactory(ReportStoreFactory storeFactory) {
        this.storeFactory = storeFactory;
    }

    public void setMxmlStoreFactory(MxmlStoreFactory mxmlStoreFactory) {
        this.mxmlStoreFactory = mxmlStoreFactory;
    }

    public void setEventTransformator(EventTransformator eventTransformator) {
        this.eventTransformator = eventTransformator;
    }

    public void setReportEvents(ReportDomainEvents reportEvents) {
        this.reportEvents = reportEvents;
    }

}
