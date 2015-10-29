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
import java.util.Arrays;
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

    private int doWork(String operation, int operand1, int operand2) throws InterruptedException  {
        int result = 0;
        Tracy.before("doWork");
        Tracy.annotate("operation",operation);
        Tracy.annotate("operand1", operand1);
        Tracy.annotate("operand2", operand2);
        result = operand1 * operand2;
        Thread.sleep(100); // slow it down a bit
        Tracy.after("doWork");
        return result;
    }

    @Test
    public void testTypicalUsage() throws InterruptedException {
        // In the context of a HTTP endpoint TASK_ID and PARENT_OPT_ID would be passed in as HTTP headers
        // COMPONENT_NAME would be the name you want to give your component (e.g. my-awesome-service)
        Tracy.setContext(TASK_ID, PARENT_OPT_ID, COMPONENT_NAME);

        // Mark beginning of Tracy frame
        Tracy.before("testTypicalUsage");

        // Annotate Tracy frame with result of called method
        // see doWork implementation above to see construction of inner Tracy frame 
        Tracy.annotate("doWorkResult", doWork("multiply", 2, 3));

        // Mark end of Tracy frame
        Tracy.after("testTypicalUsage");

        System.out.println("testTypicalUsage output:");
        // Observe the Tracy frames JSON representation
        List<String> events = Tracy.getEventsAsJson();
        for (String event : events)	{
            System.out.println(event);
        }
        assertEquals(2, events.size());

        // Clear context when done
        Tracy.clearContext();
    }   

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
        final int intValue = Integer.MAX_VALUE;
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
        String jsonEvent = Tracy.getEventsAsJson().get(0);
        assertTrue(jsonEvent.contains("\"" + INT_NAME + "\":" + Integer.toString(intValue)));
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
        String jsonEvent = Tracy.getEventsAsJson().get(0);
        assertTrue(jsonEvent.contains("\"" + LONG_NAME + "\":" + Long.toString(longValue)));
        Tracy.clearContext();
    }
    
    @Test
    public void testGetEvents_floatAnnotation() throws InterruptedException {
        final String FLOAT_NAME = "floatName";
        float floatValue = Float.MAX_VALUE;
        Tracy.setContext(TASK_ID, PARENT_OPT_ID, COMPONENT_NAME);
        Tracy.before(L1_LABEL_NAME);
        Tracy.annotate(FLOAT_NAME, floatValue);
        Tracy.after(L1_LABEL_NAME);
        List<TracyEvent> events = Tracy.getEvents();
        assertEquals(1, events.size());
        TracyEvent event = events.get(0);
        assertEquals(TASK_ID, event.getTaskId());
        assertEquals(PARENT_OPT_ID, event.getParentOptId());
        assertEquals(L1_LABEL_NAME, event.getLabel());
        assertEquals(event.getAnnotation(FLOAT_NAME), new Float(floatValue));
        assertEquals(new Float(floatValue), Tracy.getEventsAsMaps().get(0).get(FLOAT_NAME));
        String jsonEvent = Tracy.getEventsAsJson().get(0);
        assertTrue(jsonEvent.contains("\"" + FLOAT_NAME + "\":" + Float.toString(floatValue)));
        Tracy.clearContext();
    }

    
    @Test
    public void testGetEvents_doubleAnnotation() throws InterruptedException {
        final String DOUBLE_NAME = "doubleName";
        double doubleValue = Double.MAX_VALUE;
        Tracy.setContext(TASK_ID, PARENT_OPT_ID, COMPONENT_NAME);
        Tracy.before(L1_LABEL_NAME);
        Tracy.annotate(DOUBLE_NAME, doubleValue);
        Tracy.after(L1_LABEL_NAME);
        List<TracyEvent> events = Tracy.getEvents();
        assertEquals(1, events.size());
        TracyEvent event = events.get(0);
        assertEquals(TASK_ID, event.getTaskId());
        assertEquals(PARENT_OPT_ID, event.getParentOptId());
        assertEquals(L1_LABEL_NAME, event.getLabel());
        assertEquals(event.getAnnotation(DOUBLE_NAME), new Double(doubleValue));
        assertEquals(new Double(doubleValue), Tracy.getEventsAsMaps().get(0).get(DOUBLE_NAME));
        String jsonEvent = Tracy.getEventsAsJson().get(0);
        assertTrue(jsonEvent.contains("\"" + DOUBLE_NAME + "\":" + Double.toString(doubleValue)));
        Tracy.clearContext();
    }
    
    @Test
    public void testGetEvents_boolAnnotation() throws InterruptedException {
        final String BOOLEAN_NAME = "booleanName";
        boolean booleanValue = true;
        Tracy.setContext(TASK_ID, PARENT_OPT_ID, COMPONENT_NAME);
        Tracy.before(L1_LABEL_NAME);
        Tracy.annotate(BOOLEAN_NAME, booleanValue);
        Tracy.after(L1_LABEL_NAME);
        List<TracyEvent> events = Tracy.getEvents();
        assertEquals(1, events.size());
        TracyEvent event = events.get(0);
        assertEquals(TASK_ID, event.getTaskId());
        assertEquals(PARENT_OPT_ID, event.getParentOptId());
        assertEquals(L1_LABEL_NAME, event.getLabel());
        assertEquals(true, event.getAnnotation(BOOLEAN_NAME));
        assertEquals(new Boolean(booleanValue), Tracy.getEventsAsMaps().get(0).get(BOOLEAN_NAME));
        String jsonEvent = Tracy.getEventsAsJson().get(0);
        assertTrue(jsonEvent.contains("\"" + BOOLEAN_NAME + "\":" + Boolean.toString(booleanValue)));
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

    private String jsonEventWithoutBrackets(
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
        sb.append("\"taskId\":\"" + taskId + "\"");
        sb.append(",\"parentOptId\":\"" + parentOptId + "\"");
        sb.append(",\"label\":\"" + label + "\"");
        sb.append(",\"optId\":\"" + optId + "\"");
        sb.append(",\"msecBefore\":" + msecBefore);
        sb.append(",\"msecAfter\":" + msecAfter);
        sb.append(",\"msecElapsed\":" + msecElapsed);
        sb.append(",\"host\":\"" + host + "\"");
        sb.append(",\"component\":\"" + component + "\"");
        for (String key : annotations.keySet())	{
            sb.append(",\"" + key + "\":\"" + annotations.get(key) + "\"");
        }
        return sb.toString();
    }

    private boolean startsWithBracket(String s)	{
        return "{".equals(String.valueOf(s.charAt(0)));
    }
    private boolean endsWithBracket(String s)	{
        return "}".equals(String.valueOf(s.charAt(s.length()-1)));
    }

    private String removeBrackets(String s)	{
        return s.substring(1, s.length()-1);
    }

    private String sortJsonEvent(String s)	{
        String[] split = s.split(",");
        Arrays.sort(split);
        return Arrays.toString(split);
    }

    @Test
    public void testGetEventsAsJsonString_withAnnotations() throws InterruptedException {
        // TODO: This example should have Int and Long annotations but jsonEventWithoutBrackets will need to be extended
        Tracy.setContext(TASK_ID, PARENT_OPT_ID, COMPONENT_NAME);
        Tracy.before(L1_LABEL_NAME);
        Tracy.annotate("sizeOut", "10", "sizeIn", "2000");
        Thread.sleep(10);
        Tracy.after(L1_LABEL_NAME);
        Tracy.before(L11_LABEL_NAME);
        Thread.sleep(10);
        Tracy.after(L11_LABEL_NAME);

        Map<String, Object> annotations = new HashMap<String, Object>();
        String actualWithoutBrackets = null;
        annotations.put("sizeOut", "10");
        annotations.put("sizeIn", "2000");
        List<String> events = Tracy.getEventsAsJson();
        List<Map<String, Object>> eventsAsMaps = Tracy.getEventsAsMaps();
        assertEquals(2, events.size());

        String jsonEvent1 = jsonEventWithoutBrackets(
                TASK_ID, PARENT_OPT_ID, L1_LABEL_NAME, 
                eventsAsMaps.get(0).get("optId"), 
                eventsAsMaps.get(0).get("msecBefore"), 
                eventsAsMaps.get(0).get("msecAfter"), 
                eventsAsMaps.get(0).get("msecElapsed"), 
                eventsAsMaps.get(0).get("host"), 
                eventsAsMaps.get(0).get("component"), 
                annotations);

        assertTrue(startsWithBracket(events.get(0)));
        assertTrue(endsWithBracket(events.get(0)));
        actualWithoutBrackets = removeBrackets(events.get(0));
        assertEquals(sortJsonEvent(actualWithoutBrackets), sortJsonEvent(jsonEvent1));

        annotations.clear();
        String jsonEvent2 = jsonEventWithoutBrackets(
                TASK_ID, PARENT_OPT_ID, L11_LABEL_NAME, 
                eventsAsMaps.get(1).get("optId"), 
                eventsAsMaps.get(1).get("msecBefore"), 
                eventsAsMaps.get(1).get("msecAfter"), 
                eventsAsMaps.get(1).get("msecElapsed"), 
                eventsAsMaps.get(1).get("host"), 
                eventsAsMaps.get(1).get("component"), 
                annotations);
        assertTrue(startsWithBracket(events.get(1)));
        assertTrue(endsWithBracket(events.get(1)));
        actualWithoutBrackets = removeBrackets(events.get(1));
        assertEquals(sortJsonEvent(actualWithoutBrackets), sortJsonEvent(jsonEvent2));

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

    @Test
    public void testGetHttpResponseBufferAnnotation() {
        final String L1 = "L1";
        final String L2 = "L2";
        final String L3 = "L3";
        final String KEY_STR = "key_str";
        final String KEY_INT = "key_int";
        final String KEY_LONG = "key_long";
        final String VAL_STR = "str_val";
        final int    VAL_INT = Integer.MAX_VALUE;
        final long   VAL_LONG = Long.MAX_VALUE;
        Tracy.setContext(TASK_ID, PARENT_OPT_ID, COMPONENT_NAME);
        Tracy.before(L1);
        Tracy.annotate(KEY_STR, VAL_STR);
        Tracy.annotateOnHttpResponseBuffer(KEY_STR);
        Tracy.before(L2);
        Tracy.annotate(KEY_INT, VAL_INT);
        Tracy.annotateOnHttpResponseBuffer(KEY_INT);
        Tracy.before(L3);
        Tracy.annotate(KEY_LONG, VAL_LONG);
        Tracy.annotateOnHttpResponseBuffer(KEY_LONG);
        Tracy.after(L3);
        Tracy.after(L2);
        Tracy.after(L1);
        String expectedString =
                "key_long,9223372036854775807,key_int,2147483647,key_str,str_val";
        assertEquals(expectedString,Tracy.getHttpResponseBufferAnnotations());
        Tracy.clearContext();
    }

    
    @Test
    public void testGetHttpResponseBufferAnnotation_allStrings() {
        final String L1 = "L1";
        final String L2 = "L2";
        final String KEY_STR1 = "key_str1";
        final String VAL_STR1 = "val_str1";
        final String KEY_STR2 = "key_str2";
        final String VAL_STR2 = "val_str2";
        final String KEY_STR3 = "key_str3";
        final String VAL_STR3 = "val_str3";
        Tracy.setContext(TASK_ID, PARENT_OPT_ID, COMPONENT_NAME);
        Tracy.before(L1);
        Tracy.annotate(KEY_STR1, VAL_STR1);
        Tracy.annotateOnHttpResponseBuffer(KEY_STR1);
        Tracy.annotate(KEY_STR2, VAL_STR2);
        Tracy.annotateOnHttpResponseBuffer(KEY_STR2);
        Tracy.before(L2);
        Tracy.annotate(KEY_STR3, VAL_STR3);
        Tracy.annotateOnHttpResponseBuffer(KEY_STR3);
        Tracy.after(L2);
        Tracy.after(L1);
        String expectedString =
              KEY_STR3 +"," + VAL_STR3 +"," 
              + KEY_STR1 +"," + VAL_STR1 +","
              + KEY_STR2 +"," + VAL_STR2; 
        assertEquals(expectedString,Tracy.getHttpResponseBufferAnnotations());
        Tracy.clearContext();
    }
    
    @Test
    public void testAnnotateOnHttpResponseBuffer_invalidKey() { 
        final String L1 = "L1";
        final String KEY_STR = "key_str";
        Tracy.setContext(TASK_ID, PARENT_OPT_ID, COMPONENT_NAME);
        Tracy.before(L1);
        try{
            // No Tracy.annotate() called
            Tracy.annotateOnHttpResponseBuffer(KEY_STR);
         }
         catch(Exception e){
            fail("annotateOnHttpResponseBuffer(invalidKey) should not have thrown any exception");
         }
        Tracy.after(L1);
        assertEquals("",Tracy.getHttpResponseBufferAnnotations());
        Tracy.clearContext();
    }
    
    
    @Test
    public void testAnnotateOnHttpResponseBuffer_nullKey() { 
        final String L1 = "L1";
        Tracy.setContext(TASK_ID, PARENT_OPT_ID, COMPONENT_NAME);
        Tracy.before(L1);
        try{
            Tracy.annotateOnHttpResponseBuffer(null);
         }
         catch(Exception e){
            fail("annotateOnHttpResponseBuffer(null) should not have thrown any exception");
         }
        Tracy.after(L1);
        assertEquals("",Tracy.getHttpResponseBufferAnnotations());
        Tracy.clearContext();
    }
    
    @Test
    public void testGetHttpResponseBufferAnnotation_empty() {
        Tracy.setContext(TASK_ID, PARENT_OPT_ID, COMPONENT_NAME);
        Tracy.before("L1");
        Tracy.after("L1");
        assertEquals("", Tracy.getHttpResponseBufferAnnotations());
        Tracy.clearContext();
    }

    @Test
    public void testAnnotateFromHttpRequestAnnotations() {
        Tracy.setContext(TASK_ID, PARENT_OPT_ID, COMPONENT_NAME);
        String csvAnnotations = "key1,val1,key2,val2";
        Tracy.before("L1");
        Tracy.annotateFromHttpRequestAnnotations(csvAnnotations);
        Tracy.after("L1");
        List<Map<String, Object>> events = Tracy.getEventsAsMaps();
        Map<String, Object> map = events.get(0);
        assertEquals("val1", map.get("key1"));
        assertEquals("val2", map.get("key2"));
        Tracy.clearContext();
    }

    @Test
    public void testAnnotateFromHttpRequestAnnotations_odd() {
        Tracy.setContext(TASK_ID, PARENT_OPT_ID, COMPONENT_NAME);
        String csvAnnotations = "key1,val1,key2";
        Tracy.before("L1");
        Tracy.annotateFromHttpRequestAnnotations(csvAnnotations);
        Tracy.after("L1");
        List<Map<String, Object>> events = Tracy.getEventsAsMaps();
        Map<String, Object> map = events.get(0);
        assertEquals(null, map.get("key1"));
        assertEquals(null, map.get("key2"));
        Tracy.clearContext();
    }
    
    @Test
    public void testAnnotateFromHttpRequestAnnotations_null() {
        Tracy.setContext(TASK_ID, PARENT_OPT_ID, COMPONENT_NAME);
        String csvAnnotations = null;
        Tracy.before("L1");
        try{
            Tracy.annotateFromHttpRequestAnnotations(csvAnnotations);
         }
         catch(Exception e){
            fail("annotateFromHttpRequestAnnotations(null) should not have thrown any exception");
         }
        Tracy.after("L1");
        Tracy.clearContext();
    }
}
