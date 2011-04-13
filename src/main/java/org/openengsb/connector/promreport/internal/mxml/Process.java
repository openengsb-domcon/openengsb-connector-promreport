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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "data",
        "processInstance"
        })
@XmlRootElement(name = "Process")
public class Process {

    @XmlElement(name = "Data")
    protected Data data;
    @XmlElement(name = "ProcessInstance")
    protected List<ProcessInstance> processInstance;
    @XmlAttribute(name = "id", required = true)
    protected String id;
    @XmlAttribute(name = "description")
    protected String description;

    public Data getData() {
        return data;
    }

    public void setData(Data value) {
        this.data = value;
    }

    public List<ProcessInstance> getProcessInstance() {
        if (processInstance == null) {
            processInstance = new ArrayList<ProcessInstance>();
        }
        return this.processInstance;
    }

    public String getId() {
        return id;
    }

    public void setId(String value) {
        this.id = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

}
