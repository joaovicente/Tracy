package com.apm4all.tracy;

import static org.junit.Assert.*;

import org.junit.Test;

public class TracyTest {
	static final String TASK_ID = "Id-cdf535035388a0a9552b0000";
	static final String PARENT_OPT_ID = "1234";

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
}
