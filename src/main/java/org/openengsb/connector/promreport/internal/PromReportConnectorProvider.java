package org.openengsb.connector.promreport.internal;

import org.openengsb.core.api.descriptor.ServiceDescriptor;
import org.openengsb.core.api.descriptor.ServiceDescriptor.Builder;
import org.openengsb.core.common.AbstractConnectorProvider;

public class PromReportConnectorProvider extends AbstractConnectorProvider {

    @Override
    public ServiceDescriptor getDescriptor() {
        Builder builder = ServiceDescriptor.builder(strings);
        builder.id(id);
        builder.name("promReport.name").description("promReport.description");
        return builder.build();
    }

}
