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

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "eventtypes")
@XmlEnum
public enum Eventtypes {

    @XmlEnumValue("schedule")
    SCHEDULE("schedule"),
    @XmlEnumValue("assign")
    ASSIGN("assign"),
    @XmlEnumValue("withdraw")
    WITHDRAW("withdraw"),
    @XmlEnumValue("reassign")
    REASSIGN("reassign"),
    @XmlEnumValue("start")
    START("start"),
    @XmlEnumValue("suspend")
    SUSPEND("suspend"),
    @XmlEnumValue("resume")
    RESUME("resume"),
    @XmlEnumValue("pi_abort")
    PI_ABORT("pi_abort"),
    @XmlEnumValue("ate_abort")
    ATE_ABORT("ate_abort"),
    @XmlEnumValue("complete")
    COMPLETE("complete"),
    @XmlEnumValue("autoskip")
    AUTOSKIP("autoskip"),
    @XmlEnumValue("manualskip")
    MANUALSKIP("manualskip"),
    @XmlEnumValue("unknown")
    UNKNOWN("unknown");
    private final String value;

    Eventtypes(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Eventtypes fromValue(String v) {
        for (Eventtypes c : Eventtypes.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
