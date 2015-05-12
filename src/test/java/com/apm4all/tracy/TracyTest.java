/*
 * Copyright 2014 Joao Vicente
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.apm4all.tracy;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TracyTest {
    static final String COMPONENT_NAME = "TracyTest";
    static final String TASK_ID = "TID-ab1234-x";
    static final String PARENT_OPT_ID = "AAAA";
    static final String L1_LABEL_NAME = "L1 Operation";
    static final String L11_LABEL_NAME = "L11 Operation";

    @Test
    public void testSetContext_full() {
        Tracy.setContext(TASK_ID, PARENT_OPT_ID, COMPONENT_NAME);
        assertEquals(TASK_ID, Tracy.getTaskId());
        assertEquals(PARENT_OPT_ID, Tracy.getParentOptId());
        Tracy.clearContext();
    }

    @Test
    public void testGetEvents() throws InterruptedException {
        final int MSEC_OPERATION_TIME = 1000;
        final int MSEC_SLEEP_JITTER = 100;
        Tracy.setContext(TASK_ID, PARENT_OPT_ID, COMPONENT_NAME);
        Tracy.before(L1_LABEL_NAME);
        Thread.sleep(MSEC_OPERATION_TIME);
        Tracy.after(L1_LABEL_NAME);
        List<TracyEvent> events = Tracy.getEvents();
        assertEquals(1, events.size());
        TracyEvent event = events.get(0);
        assertEquals(TASK_ID, event.getTaskId());
        assertEquals(PARENT_OPT_ID, event.getParentOptId());
        assertEquals(L1_LABEL_NAME, event.getLabel());
        assertTrue(event.getMsecAfter() > event.getMsecBefore() + MSEC_OPERATION_TIME - MSEC_SLEEP_JITTER);
        assertTrue(event.getMsecAfter() < event.getMsecBefore() + MSEC_OPERATION_TIME + MSEC_SLEEP_JITTER);
        assertTrue(event.getMsecElapsed() > event.getMsecAfter()  - event.getMsecBefore() - MSEC_SLEEP_JITTER);
        assertTrue(event.getMsecElapsed() < event.getMsecAfter()  - event.getMsecBefore() + MSEC_SLEEP_JITTER);
        Tracy.clearContext();
    }
    
    @Test
    public void testGetEvents_intAnnotation() throws InterruptedException {
        final String INT_NAME = "intName";
        int intValue = Integer.MAX_VALUE;
        Tracy.setContext(TASK_ID, PARENT_OPT_ID, COMPONENT_NAME);
        Tracy.before(L1_LABEL_NAME);
        Tracy.annotate(INT_NAME, intValue);
        Tracy.after(L1_LABEL_NAME);
        List<TracyEvent> events = Tracy.getEvents();
        assertEquals(1, events.size());
        TracyEvent event = events.get(0);
        assertEquals(TASK_ID, event.getTaskId());
        assertEquals(PARENT_OPT_ID, event.getParentOptId());
        assertEquals(L1_LABEL_NAME, event.getLabel());
        assertEquals(event.getAnnotation(INT_NAME), new Integer(intValue));
        assertEquals(new Integer(intValue) , Tracy.getEventsAsMaps().get(0).get(INT_NAME));
        Tracy.clearContext();
    }
    
    @Test
    public void testGetEvents_longAnnotation() throws InterruptedException {
        final String LONG_NAME = "longName";
        long longValue = Long.MAX_VALUE;
        Tracy.setContext(TASK_ID, PARENT_OPT_ID, COMPONENT_NAME);
        Tracy.before(L1_LABEL_NAME);
        Tracy.annotate(LONG_NAME, longValue);
        Tracy.after(L1_LABEL_NAME);
        List<TracyEvent> events = Tracy.getEvents();
        assertEquals(1, events.size());
        TracyEvent event = events.get(0);
        assertEquals(TASK_ID, event.getTaskId());
        assertEquals(PARENT_OPT_ID, event.getParentOptId());
        assertEquals(L1_LABEL_NAME, event.getLabel());
        assertEquals(event.getAnnotation(LONG_NAME), new Long(longValue));
        assertEquals(new Long(longValue), Tracy.getEventsAsMaps().get(0).get(LONG_NAME));
        Tracy.clearContext();
    }
    
    @Test
    public void testGetEvents_componentAnnotated() throws InterruptedException {
	final String COMPONENT_NAME = "Component X";
        Tracy.setContext(TASK_ID, PARENT_OPT_ID, COMPONENT_NAME);
        Tracy.before(L1_LABEL_NAME);
        Tracy.after(L1_LABEL_NAME);
        List<TracyEvent> events = Tracy.getEvents();
        assertEquals(1, events.size());
        TracyEvent event = events.get(0);
        assertEquals(TASK_ID, event.getTaskId());
        assertEquals(PARENT_OPT_ID, event.getParentOptId());
        assertEquals(L1_LABEL_NAME, event.getLabel());
        assertEquals(COMPONENT_NAME, Tracy.getEventsAsMaps().get(0).get("component"));
        Tracy.clearContext();
    }
    
    @Test
    public void testGetEvents_twoEventsTwoLevelStack() throws InterruptedException {
        Tracy.setContext(TASK_ID, PARENT_OPT_ID, COMPONENT_NAME);
        Tracy.before(L1_LABEL_NAME);
        Tracy.before(L11_LABEL_NAME);
        Thread.sleep(100);
        Tracy.after(L11_LABEL_NAME);
        Tracy.after(L1_LABEL_NAME);
        List<TracyEvent> events = Tracy.getEvents();
        assertEquals(2, events.size());

        // L1 event will be popped last
        TracyEvent l1Event = events.get(1);
        assertEquals(TASK_ID, l1Event.getTaskId());
        assertEquals(PARENT_OPT_ID, l1Event.getParentOptId());
        assertEquals(L1_LABEL_NAME, l1Event.getLabel());

        // L11 event will be popped first
        TracyEvent l11Event = events.get(0);
        assertEquals(TASK_ID, l11Event.getTaskId());
        assertEquals(l1Event.getOptId(), l11Event.getParentOptId());
        assertEquals(L11_LABEL_NAME, l11Event.getLabel());
        Tracy.clearContext();
    }

    @Test
    public void testGetEventsAsMap_withAnnotations() throws InterruptedException {
        Tracy.setContext(TASK_ID, PARENT_OPT_ID, COMPONENT_NAME);
        Tracy.before(L1_LABEL_NAME);
        Tracy.annotate("sizeOut", "10", "sizeIn", "2000");
        Thread.sleep(100);
        Tracy.after(L1_LABEL_NAME);
        List<Map<String, Object>> events = Tracy.getEventsAsMaps();
        assertEquals(1, events.size());
        Map<String, Object> map = events.get(0);
        assertEquals(TASK_ID, map.get("taskId"));
        assertEquals(PARENT_OPT_ID, map.get("parentOptId"));
        assertEquals(L1_LABEL_NAME, map.get("label"));
        assertEquals("10", map.get("sizeOut"));
        assertEquals("2000", map.get("sizeIn"));
        Tracy.clearContext();
    }
    
    @Test
    public void testGetEventsAsMap_unAfteredErrorL1() throws InterruptedException {
        Tracy.setContext(TASK_ID, PARENT_OPT_ID, COMPONENT_NAME);
        Tracy.before(L1_LABEL_NAME);
        List<Map<String, Object>> events = Tracy.getEventsAsMaps();
        assertEquals(1, events.size());
        Map<String, Object> map = events.get(0);
        assertEquals(L1_LABEL_NAME, map.get("label"));
        assertEquals("unknown", map.get("error"));
        Tracy.clearContext();
    }
    
    @Test
    public void testGetEventsAsMap_unAfteredErrorL11() throws InterruptedException {
        Tracy.setContext(TASK_ID, PARENT_OPT_ID, COMPONENT_NAME);
        Tracy.before(L1_LABEL_NAME);
        Tracy.before(L11_LABEL_NAME);
        Tracy.after(L11_LABEL_NAME);
        List<Map<String, Object>> events = Tracy.getEventsAsMaps();
        assertEquals(2, events.size());
        assertEquals(L11_LABEL_NAME, events.get(0).get("label"));
        assertEquals(null, events.get(0).get("error"));
        assertEquals(L1_LABEL_NAME, events.get(1).get("label"));
        assertEquals("unknown", events.get(1).get("error"));
        Tracy.clearContext();
    }
    
    @Test
    public void testGetEvents_validCustomOptId() throws InterruptedException {
        final String CUSTOM_OPT_ID = "U001"; 
        Tracy.setContext(TASK_ID, PARENT_OPT_ID, COMPONENT_NAME);
        Tracy.before(L1_LABEL_NAME);
        // Reserved range is string representation of 32bit hex range [0000..FFFF]
        Tracy.setOptId(CUSTOM_OPT_ID); 
        Tracy.after(L1_LABEL_NAME);
        List<TracyEvent> events = Tracy.getEvents();
        assertEquals(1, events.size());
        TracyEvent event = events.get(0);
        assertEquals(TASK_ID, event.getTaskId());
        assertEquals(PARENT_OPT_ID, event.getParentOptId());
        assertEquals(L1_LABEL_NAME, event.getLabel());
        assertEquals(CUSTOM_OPT_ID, event.getOptId());
        Tracy.clearContext();
    }
    
    private String jsonEvent(
            Object taskId, 
            Object parentOptId, 
            Object label, 
            Object optId, 
            Object msecBefore, 
            Object msecAfter, 
            Object msecElapsed,
            Object host,
            Object component,
            Map<String, Object> annotations 
            )	
    {
    	StringBuilder sb = new StringBuilder(200);
    	sb.append("{");
    	sb.append("\"taskId\":\"" + taskId + "\"");
    	sb.append(",\"parentOptId\":\"" + parentOptId + "\"");
    	sb.append(",\"label\":\"" + label + "\"");
    	sb.append(",\"optId\":\"" + optId + "\"");
    	sb.append(",\"msecBefore\":" + msecBefore);
    	sb.append(",\"msecAfter\":" + msecAfter);
    	sb.append(",\"msecElapsed\":" + msecElapsed);
    	for (String key : annotations.keySet())	{
    		sb.append(",\"" + key + "\":\"" + annotations.get(key) + "\"");
    	}
    	sb.append(",\"host\":\"" + host + "\"");
    	sb.append(",\"component\":\"" + component + "\"");
    	sb.append("}");
    	return sb.toString();
    }
    
    @Test
    public void testGetEventsAsJsonString_withAnnotations() throws InterruptedException {
        Tracy.setContext(TASK_ID, PARENT_OPT_ID, COMPONENT_NAME);
        Tracy.before(L1_LABEL_NAME);
        Tracy.annotate("sizeOut", "10", "sizeIn", "2000");
        Thread.sleep(10);
        Tracy.after(L1_LABEL_NAME);
        Tracy.before(L11_LABEL_NAME);
        Thread.sleep(10);
        Tracy.after(L11_LABEL_NAME);
        
        Map<String, Object> annotations = new HashMap<String, Object>();
        annotations.put("sizeOut", "10");
        annotations.put("sizeIn", "2000");
        List<String> events = Tracy.getEventsAsJson();
        List<Map<String, Object>> eventsAsMaps = Tracy.getEventsAsMaps();
        assertEquals(2, events.size());
        
        String jsonEvent1 = jsonEvent(
        		TASK_ID, PARENT_OPT_ID, L1_LABEL_NAME, 
        		eventsAsMaps.get(0).get("optId"), 
        		eventsAsMaps.get(0).get("msecBefore"), 
        		eventsAsMaps.get(0).get("msecAfter"), 
        		eventsAsMaps.get(0).get("msecElapsed"), 
        		eventsAsMaps.get(0).get("host"), 
        		eventsAsMaps.get(0).get("component"), 
        		annotations);
        assertEquals(jsonEvent1, events.get(0));
        annotations.clear();
        String jsonEvent2 = jsonEvent(
        		TASK_ID, PARENT_OPT_ID, L11_LABEL_NAME, 
        		eventsAsMaps.get(1).get("optId"), 
        		eventsAsMaps.get(1).get("msecBefore"), 
        		eventsAsMaps.get(1).get("msecAfter"), 
        		eventsAsMaps.get(1).get("msecElapsed"), 
        		eventsAsMaps.get(1).get("host"), 
        		eventsAsMaps.get(0).get("component"), 
        		annotations);
        assertEquals(jsonEvent2, events.get(1));
        Tracy.clearContext();
    }
    
    @Test
    public void testOuterError_twoLevelStack() throws InterruptedException {
        final String CUSTOM_ERROR_MESSAGE = "CustomErrorMessage";
        Tracy.setContext(TASK_ID, PARENT_OPT_ID, COMPONENT_NAME);
        Tracy.before(L1_LABEL_NAME);
        Tracy.before(L11_LABEL_NAME);
        Thread.sleep(100);
        Tracy.outerError(CUSTOM_ERROR_MESSAGE);
        
        List<TracyEvent> events = Tracy.getEvents();
        assertEquals(2, events.size());

        // L1 event will be popped last
        TracyEvent l1Event = events.get(1);
        assertEquals(TASK_ID, l1Event.getTaskId());
        assertEquals(PARENT_OPT_ID, l1Event.getParentOptId());
        assertEquals(L1_LABEL_NAME, l1Event.getLabel());
        assertEquals(L1_LABEL_NAME, l1Event.getLabel());
        assertEquals(CUSTOM_ERROR_MESSAGE, l1Event.getError());

        // L11 event will be popped first
        TracyEvent l11Event = events.get(0);
        assertEquals(TASK_ID, l11Event.getTaskId());
        assertEquals(l1Event.getOptId(), l11Event.getParentOptId());
        assertEquals(L11_LABEL_NAME, l11Event.getLabel());
        assertEquals(CUSTOM_ERROR_MESSAGE, l11Event.getError());
        Tracy.clearContext();
    }

    @Test
    public void testFrameError_twoLevelStack() throws InterruptedException {
        final String CUSTOM_ERROR_MESSAGE = "CustomErrorMessage";
        Tracy.setContext(TASK_ID, PARENT_OPT_ID, COMPONENT_NAME);
        Tracy.before(L1_LABEL_NAME);
        Tracy.before(L11_LABEL_NAME);
        Thread.sleep(100);
        Tracy.frameError(CUSTOM_ERROR_MESSAGE);
        Tracy.after(L1_LABEL_NAME);
        
        List<TracyEvent> events = Tracy.getEvents();
        assertEquals(2, events.size());

        // L1 event will be popped last
        TracyEvent l1Event = events.get(1);
        assertEquals(TASK_ID, l1Event.getTaskId());
        assertEquals(PARENT_OPT_ID, l1Event.getParentOptId());
        assertEquals(L1_LABEL_NAME, l1Event.getLabel());
        assertEquals(L1_LABEL_NAME, l1Event.getLabel());
        assertEquals(null, l1Event.getError());

        // L11 event will be popped first
        TracyEvent l11Event = events.get(0);
        assertEquals(TASK_ID, l11Event.getTaskId());
        assertEquals(l1Event.getOptId(), l11Event.getParentOptId());
        assertEquals(L11_LABEL_NAME, l11Event.getLabel());
        assertEquals(CUSTOM_ERROR_MESSAGE, l11Event.getError());
        Tracy.clearContext();
    }
    
    @Test
    public void testAnnotateBeforeBefore() throws InterruptedException {
        Tracy.setContext(TASK_ID, PARENT_OPT_ID, COMPONENT_NAME);
        try {
            Tracy.annotate("anyKey", "anyValue");
        } catch (Exception e) {
            fail();
        }
    }
    
    @Test
    public void testAnnotateWithNull() throws InterruptedException {
    	final String ANY_KEY = "anyKey";
        List<TracyEvent> events;
        Tracy.setContext(TASK_ID, PARENT_OPT_ID, COMPONENT_NAME);
        Tracy.before("test");
        Tracy.annotate(ANY_KEY, null);
        Tracy.after("test");
        events = Tracy.getEvents();
        assertEquals("null", events.get(0).getAnnotation(ANY_KEY));
    }

    @Test
    public void testSetTaskIdAtTheEnd() throws InterruptedException {
    	final String NEW_TASK_ID = "newTaskId";
        List<TracyEvent> events;
        Tracy.setContext(TASK_ID, PARENT_OPT_ID, COMPONENT_NAME);
        Tracy.before("test");
        Tracy.after("test");
        Tracy.setTaskId(NEW_TASK_ID);
        events = Tracy.getEvents();
        assertEquals(NEW_TASK_ID, events.get(0).getTaskId());
    }
    
    @Test
    public void testGetEventsAsJsonTracySegment() throws JsonParseException, JsonMappingException, IOException	{
    	Tracy.setContext(TASK_ID, PARENT_OPT_ID, COMPONENT_NAME);
        Tracy.before("test1");
        Tracy.after("test1");
        Tracy.before("test2");
        Tracy.after("test2");
        String jsonTracySegment = Tracy.getEventsAsJsonTracySegment();
       
        // using Jackson Tree model to validate (see http://wiki.fasterxml.com/JacksonTreeModel)
        ObjectMapper m = new ObjectMapper();
        JsonNode rootNode = m.readTree(jsonTracySegment);
        assertNotNull(rootNode);
        JsonNode tracySegment = rootNode.path("tracySegment");
        String label1 = tracySegment.path(0).get("label").textValue();
        String label2 = tracySegment.path(1).get("label").textValue();
        assertNull("", tracySegment.path(2).get("label"));
        assertEquals("test1", label1);
        assertEquals("test2", label2);
    }
}
