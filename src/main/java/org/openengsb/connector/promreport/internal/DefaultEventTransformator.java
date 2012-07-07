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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.openengsb.connector.promreport.internal.mxml.AuditTrailEntry;
import org.openengsb.connector.promreport.internal.mxml.AuditTrailEntry.EventType;
import org.openengsb.connector.promreport.internal.mxml.Data;
import org.openengsb.connector.promreport.internal.mxml.Eventtypes;
import org.openengsb.core.api.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultEventTransformator implements EventTransformator {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultEventTransformator.class);
    
    private DatatypeFactory dataTypeFactory;
    
    public DefaultEventTransformator() {
        initFactory();
    }
    
    private void initFactory() {
        try {
            dataTypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            LOGGER.warn("error during creating of DataTypeFactory. Continue without setting timestamp.", e);
        }
    }

    @Override
    public Long getProcessId(Event e) {
        if (e == null) {
            return null;
        }
        return e.getProcessId();
    }

    @Override
    public AuditTrailEntry transform(Event e) {
        if (e == null) {
            return null;
        }
        
        AuditTrailEntry aent = new AuditTrailEntry();
        
        Data data = new Data();
        for (Data.Attribute a : getData(e)) {
            data.getAttribute().add(a);
        }
        aent.setData(data);
        
        if (e.getName() == null) {
            aent.setWorkflowModelElement("unknown");
        } else {
            aent.setWorkflowModelElement(e.getName());
        }
        
        aent.setEventType(guessEventType(e));

        if (dataTypeFactory != null) {
            AuditTrailEntry.Timestamp timestamp = new AuditTrailEntry.Timestamp();
            timestamp.setValue(getCalendar());
            aent.setTimestamp(timestamp);
        }
        
        return aent;
    }
    
    private List<Data.Attribute> getData(Event event) {
        List<Data.Attribute> data = new ArrayList<Data.Attribute>();
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(event.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor p : propertyDescriptors) {
                if (!p.getName().equals("processId") && !p.getName().equals("name") && !p.getName().equals("type")) {
                    try {
                        Data.Attribute att = new Data.Attribute();
                        att.setName(p.getName());
                        Method readMethod = p.getReadMethod();
                        if (readMethod != null) {
                            att.setValue(String.valueOf(readMethod.invoke(event)));
                        }                        
                        data.add(att);
                    } catch (IllegalAccessException e) {
                        LOGGER.warn("An attribute couldn't be read", e);
                    } catch (InvocationTargetException e) {
                        LOGGER.warn("Underlying Method throws an exception.", e);
                    }
                }
            }
        } catch (IntrospectionException e) {
            LOGGER.warn("An exception occurs during introspection. No attribute was set.", e);
        }
        return data;
    }
    
    private EventType guessEventType(Event e) {
        String classname = e.getClass().getSimpleName().toLowerCase();
        
        EventType type = new EventType();
        if (classname.contains("start")) {
            type.setValue(Eventtypes.START);
        } else if (classname.contains("end")) {
            type.setValue(Eventtypes.COMPLETE);
        } else if (classname.contains("success")) {
            type.setValue(Eventtypes.COMPLETE);
        } else if (classname.contains("fail")) {
            type.setValue(Eventtypes.PI_ABORT);
        } else {
            type.setValue(Eventtypes.UNKNOWN);
            type.setUnknowntype(classname);
        }
        return type;
    }

    private XMLGregorianCalendar getCalendar() {
        if (dataTypeFactory == null) {
            return null;
        }
        return dataTypeFactory.newXMLGregorianCalendar(new GregorianCalendar());
    }

}
