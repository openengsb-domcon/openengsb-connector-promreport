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
package org.openengsb.connector.promreport.internal.mxml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "data",
        "workflowModelElement",
        "eventType",
        "timestamp",
        "originator"
        })
@XmlRootElement(name = "AuditTrailEntry")
public class AuditTrailEntry {

    @XmlElement(name = "Data")
    protected Data data;
    @XmlElement(name = "WorkflowModelElement", required = true)
    protected String workflowModelElement;
    @XmlElement(name = "EventType", required = true)
    protected AuditTrailEntry.EventType eventType;
    @XmlElement(name = "Timestamp")
    protected AuditTrailEntry.Timestamp timestamp;
    @XmlElement(name = "Originator")
    protected String originator;

    public Data getData() {
        return data;
    }

    public void setData(Data value) {
        this.data = value;
    }

    public String getWorkflowModelElement() {
        return workflowModelElement;
    }

    public void setWorkflowModelElement(String value) {
        this.workflowModelElement = value;
    }

    public AuditTrailEntry.EventType getEventType() {
        return eventType;
    }

    public void setEventType(AuditTrailEntry.EventType value) {
        this.eventType = value;
    }

    public AuditTrailEntry.Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(AuditTrailEntry.Timestamp value) {
        this.timestamp = value;
    }

    public String getOriginator() {
        return originator;
    }

    public void setOriginator(String value) {
        this.originator = value;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "value"
            })
    public static class EventType {

        @XmlValue
        protected Eventtypes value;
        @XmlAttribute(name = "unknowntype")
        protected String unknowntype;

        public Eventtypes getValue() {
            return value;
        }

        public void setValue(Eventtypes value) {
            this.value = value;
        }

        public String getUnknowntype() {
            return unknowntype;
        }

        public void setUnknowntype(String value) {
            this.unknowntype = value;
        }

    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "value"
            })
    public static class Timestamp {

        @XmlValue
        @XmlSchemaType(name = "dateTime")
        protected XMLGregorianCalendar value;

        public XMLGregorianCalendar getValue() {
            return value;
        }

        public void setValue(XMLGregorianCalendar value) {
            this.value = value;
        }

    }

}
