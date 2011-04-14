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

import org.openengsb.core.api.ServiceInstanceFactory;
import org.openengsb.core.api.descriptor.ServiceDescriptor;
import org.openengsb.core.api.descriptor.ServiceDescriptor.Builder;
import org.openengsb.core.api.validation.MultipleAttributeValidationResult;
import org.openengsb.core.api.validation.MultipleAttributeValidationResultImpl;
import org.openengsb.domain.report.ReportDomain;
import org.openengsb.domain.report.common.ReportStoreFactory;

public class PromReportFactory implements ServiceInstanceFactory<ReportDomain, PromReportService> {

    private final ReportStoreFactory storeFactory;
    private final MxmlStoreFactory mxmlStoreFactory;
    private EventTransformator eventTransformator;

    public PromReportFactory(ReportStoreFactory storeFactory, MxmlStoreFactory mxmlStoreFactory,
            EventTransformator eventTransformator) {
        this.storeFactory = storeFactory;
        this.mxmlStoreFactory = mxmlStoreFactory;
        this.eventTransformator = eventTransformator;
    }

    @Override
    public ServiceDescriptor getDescriptor(Builder builder) {
        builder.name("promReport.name").description("promReport.description");
        return builder.build();
    }

    @Override
    public void updateServiceInstance(PromReportService instance, Map<String, String> attributes) {
        setAttributes(instance, attributes);
    }

    @Override
    public PromReportService createServiceInstance(String id, Map<String, String> attributes) {
        PromReportService service = new PromReportService(id);
        service.setStore(storeFactory.createReportStore(id));
        service.setMxmlStore(mxmlStoreFactory.createMxmlStore(id));
        service.setTransformer(eventTransformator);
        setAttributes(service, attributes);
        return service;
    }

    private void setAttributes(PromReportService service, Map<String, String> attributes) {
        // do nothing - currently no attributes defined
    }

    @Override
    public MultipleAttributeValidationResult updateValidation(PromReportService instance,
            Map<String, String> attributes) {
        return new MultipleAttributeValidationResultImpl(true, new HashMap<String, String>());
    }

    @Override
    public MultipleAttributeValidationResult createValidation(String id, Map<String, String> attributes) {
        return new MultipleAttributeValidationResultImpl(true, new HashMap<String, String>());
    }

}
