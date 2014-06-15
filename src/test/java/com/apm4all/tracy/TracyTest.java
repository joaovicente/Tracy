package com.apm4all.tracy;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class TracyTest {
	static final String TASK_ID = "TID-ab1234-x";
	static final String PARENT_OPT_ID = "1234";
	static final String L1_LABEL_NAME = "Operation";

	@Test
	public void testSetContext_empty() {
		Tracy.setContext();
		assertEquals(Tracy.TRACY_DEFAULT_TASK_ID, Tracy.getTaskId());
		assertEquals(Tracy.TRACY_DEFAULT_PARENT_OPT_ID, Tracy.getParentOptId());
	}

	@Test
	public void testSetContext_full() {
		Tracy.setContext(TASK_ID, PARENT_OPT_ID);
		assertEquals(TASK_ID, Tracy.getTaskId());
		assertEquals(PARENT_OPT_ID, Tracy.getParentOptId());
	}
	
	@Test
	public void testGetEvents() throws InterruptedException	{
		Tracy.setContext(TASK_ID, PARENT_OPT_ID);
		Tracy.before(L1_LABEL_NAME);
		Thread.sleep(100);
		Tracy.after(L1_LABEL_NAME);
		List<TracyEvent> events = Tracy.getEvents();
		assertEquals(1, events.size());
		TracyEvent event = events.get(0);
		assertEquals(TASK_ID, event.getTaskId());
		assertEquals(PARENT_OPT_ID, event.getParentOptId());
		assertEquals(L1_LABEL_NAME, event.getLabel());
	}
}
