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
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class TracyTest {
	static final String TASK_ID = "TID-ab1234-x";
	static final String PARENT_OPT_ID = "AAAA";
	static final String L1_LABEL_NAME = "L1 Operation";
	static final String L11_LABEL_NAME = "L11 Operation";

	@Test
	public void testSetContext_empty() {
		Tracy.setContext();
		//TODO: There should be no parent optId
//		assertEquals(Tracy.TRACY_DEFAULT_PARENT_OPT_ID, Tracy.getParentOptId());
	}

	@Test
	public void testSetContext_full() {
		Tracy.setContext(TASK_ID, PARENT_OPT_ID);
		assertEquals(TASK_ID, Tracy.getTaskId());
		assertEquals(PARENT_OPT_ID, Tracy.getParentOptId());
	}
	
	@Test
	public void testGetEvents() throws InterruptedException {
		final int MSEC_OPERATION_TIME = 1000;
		final int MSEC_SLEEP_JITTER = 100;
		Tracy.setContext(TASK_ID, PARENT_OPT_ID);
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
	}
	
	@Test
	public void testGetEvents_twoEventsTwoLevelStack() throws InterruptedException {
		Tracy.setContext(TASK_ID, PARENT_OPT_ID);
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
	}
	
	@Test
	public void testGetEventsAsMap_withAnnotations() throws InterruptedException {
		Tracy.setContext(TASK_ID, PARENT_OPT_ID);
		Tracy.before(L1_LABEL_NAME);
		Tracy.annotate("sizeOut", "10", "sizeIn", "2000");
		Thread.sleep(100);
		Tracy.after(L1_LABEL_NAME);
		List<Map<String, String>> events = Tracy.getEventsAsMaps();
		assertEquals(1, events.size());
		Map<String, String> map = events.get(0);
		assertEquals(TASK_ID, map.get("taskId"));
		assertEquals(PARENT_OPT_ID, map.get("parentOptId"));
		assertEquals(L1_LABEL_NAME, map.get("label"));
		assertEquals("10", map.get("sizeOut"));
		assertEquals("2000", map.get("sizeIn"));
	}
}
