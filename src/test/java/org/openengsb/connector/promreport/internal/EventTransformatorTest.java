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

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.openengsb.connector.promreport.internal.events.TestDomainEndEvent;
import org.openengsb.connector.promreport.internal.events.TestDomainFailEvent;
import org.openengsb.connector.promreport.internal.events.TestDomainStartEvent;
import org.openengsb.connector.promreport.internal.events.TestDomainSuccessEvent;
import org.openengsb.connector.promreport.internal.events.TestRandomXYZEvent;
import org.openengsb.connector.promreport.internal.mxml.AuditTrailEntry;
import org.openengsb.connector.promreport.internal.mxml.Data;
import org.openengsb.connector.promreport.internal.mxml.Data.Attribute;
import org.openengsb.connector.promreport.internal.mxml.Eventtypes;
import org.openengsb.core.api.Event;

public abstract class EventTransformatorTest {

    private static final Long ID = 10L;
    private static final String NAME = "TEST";
    
    protected abstract EventTransformator getTransformator();
    
    private EventTransformator transformer;
    private TestEvent testEvent;
    
    @Before
    public void setUp() {
        transformer = getTransformator();
        testEvent = new TestEvent(NAME, ID);
    }
    
    @Test
    public void getProcessId_ShouldReturnNull() {
        testEvent.setProcessId(null);
        assertThat(transformer.getProcessId(testEvent), nullValue());
    }
    
    @Test
    public void getProcessId_ShouldReturnValue() {
        assertThat(transformer.getProcessId(testEvent), is(ID));
    }
    
    @Test
    public void transform_ShouldReturnNull() {
        assertThat(transformer.transform(null), nullValue());
    }
    
    @Test
    public void transform_ShouldReturnAuditTrailEntryWithDataOfEvent() {
        testEvent.setStringField("foo");
        testEvent.setIntField(76);
        testEvent.setObjectField(null);
        
        AuditTrailEntry ae = transformer.transform(testEvent);
        assertThat(ae.getWorkflowModelElement(), is(NAME));
        
        List<Attribute> atts = ae.getData().getAttribute();
        assertThat(atts.size(), is(5));
        
        Matcher<Data.Attribute> stringName = hasProperty("name", is("stringField"));
        Matcher<Attribute> stringValue = hasProperty("value", is("foo"));
        @SuppressWarnings("unchecked")
        Matcher<Attribute> stringM = allOf(stringName, stringValue);
        
        Matcher<Attribute> intName = hasProperty("name", is("intField"));
        Matcher<Attribute> intValue = hasProperty("value", is("76"));
        @SuppressWarnings("unchecked")
        Matcher<Attribute> intM = allOf(intName, intValue);
        
        Matcher<Attribute> objName = hasProperty("name", is("objectField"));
        Matcher<Attribute> objValue = hasProperty("value", is("null"));
        @SuppressWarnings("unchecked")
        Matcher<Attribute> objM = allOf(objName, objValue);
        
        Matcher<Attribute> className = hasProperty("name", is("class"));
        Matcher<Attribute> classValue = hasProperty("value", is("class " + testEvent.getClass().getName()));
        @SuppressWarnings("unchecked")
        Matcher<Attribute> classM = allOf(className, classValue);
        
        @SuppressWarnings("unchecked")
        Matcher<Iterable<Attribute>> hasItems = Matchers.hasItems(stringM, intM, objM, classM);
        assertThat(atts, hasItems);
    }
    
    @Test
    public void transform_ShouldGuessAnEventtype() {
        Event e = new TestDomainStartEvent();
        assertThat(transformer.transform(e).getEventType().getValue(), is(Eventtypes.START));
        
        e = new TestDomainSuccessEvent();
        assertThat(transformer.transform(e).getEventType().getValue(), is(Eventtypes.COMPLETE));
        
        e = new TestDomainEndEvent();
        assertThat(transformer.transform(e).getEventType().getValue(), is(Eventtypes.COMPLETE));
        
        e = new TestDomainFailEvent();
        assertThat(transformer.transform(e).getEventType().getValue(), is(Eventtypes.PI_ABORT));
        
        e = new TestRandomXYZEvent();
        assertThat(transformer.transform(e).getEventType().getValue(), is(Eventtypes.UNKNOWN));
        assertThat(transformer.transform(e).getEventType().getUnknowntype(), is("testrandomxyzevent"));
    }    

    private static class TestEvent extends Event {
        private String stringField;
        private int intField;
        private Object objectField;

        public TestEvent(String name, Long processId) {
            super(name, processId);
        }

        public void setStringField(String stringField) {
            this.stringField = stringField;
        }

        public void setIntField(int intField) {
            this.intField = intField;
        }

        public void setObjectField(Object objectField) {
            this.objectField = objectField;
        }

        @SuppressWarnings("unused")
        public String getStringField() {
            return this.stringField;
        }

        @SuppressWarnings("unused")
        public int getIntField() {
            return this.intField;
        }

        @SuppressWarnings("unused")
        public Object getObjectField() {
            return this.objectField;
        }

    }

}
