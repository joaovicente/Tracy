package com.apm4all.tracy;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class TracyAnnotationsTest {
	final String KEY_STR = "key_str";
	final String VAL_STR = "str_val";
	final String KEY_INT = "key_int";
	final int    VAL_INT = Integer.MAX_VALUE;
	final long   VAL_LONG = Long.MAX_VALUE;
	final String KEY_LONG = "key_long";
	final String KEY_STR1 = "key1";
	final String KEY_STR2 = "key2";
	final String VAL_STR1 = "str_val1";
	final String VAL_STR2 = "str_val2";
	TracyAnnotations annotations;
	
	@Before
	public void setUp() {
		annotations = new TracyAnnotations(30); 
	}
	
	@Test
	public void testAddStringString() {
		annotations.add(KEY_STR, VAL_STR);
		assertEquals(VAL_STR, annotations.get(KEY_STR));
	}

	@Test
	public void testAddStringInt() {
		annotations.add(KEY_INT, VAL_INT);
		assertEquals(VAL_INT, annotations.get(KEY_INT));
	}

	@Test
	public void testAddStringLong() {
		annotations.add(KEY_LONG, VAL_LONG);
		assertEquals(VAL_LONG, annotations.get(KEY_LONG));
	}

	@Test
	public void testAddStringArray() {
		annotations.add(KEY_STR1, VAL_STR1, KEY_STR2, VAL_STR2);
		assertEquals(VAL_STR1, annotations.get(KEY_STR1));
		assertEquals(VAL_STR2, annotations.get(KEY_STR2));
	}

	@Test
	public void testGet_noAnnotation() {
		assertEquals(null, annotations.get(KEY_STR));
	}

	@Test
	public void testAppendToMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		annotations.add(KEY_STR, VAL_STR);
		annotations.add(KEY_INT, VAL_INT);
		annotations.add(KEY_LONG, VAL_LONG);
		annotations.appendToMap(map);
		assertEquals(3, map.size());
		assertEquals(VAL_STR, annotations.get(KEY_STR));
		assertEquals(VAL_INT, annotations.get(KEY_INT));
		assertEquals(VAL_LONG, annotations.get(KEY_LONG));
	}

	private String expectedAnnotationsAsString(String separator, String prepend, boolean addSpace)	{
		StringBuilder expectedString = new StringBuilder(Tracy.TRACY_ESTIMATED_FRAME_SIZE);
		expectedString.append(prepend);
		if (addSpace)	{
			expectedString.append(" ");
		}
		// LONG
		expectedString.append("\"").append(KEY_LONG);
		expectedString.append("\"");
		expectedString.append(separator);
		expectedString.append(VAL_LONG);
		expectedString.append(","); // COMMA
		if (addSpace)	{
			expectedString.append(" ");
		}
			
		// INT
		expectedString.append("\"").append(KEY_INT);
		expectedString.append("\"");
		expectedString.append(separator);
		expectedString.append(VAL_INT);
		expectedString.append(","); // COMMA
		if (addSpace)	{
			expectedString.append(" ");
		}
		// STR
		expectedString.append("\"").append(KEY_STR);
		expectedString.append("\"");
		expectedString.append(separator);
		expectedString.append("\"").append(VAL_STR).append("\"");
		return expectedString.toString();
	}
	
	
	@Test
	public void testAppendToJsonStringBuilder() {
		StringBuilder sb = new StringBuilder(Tracy.TRACY_ESTIMATED_FRAME_SIZE);
		annotations.add(KEY_STR, VAL_STR);
		annotations.add(KEY_INT, VAL_INT);
		annotations.add(KEY_LONG, VAL_LONG);
		annotations.appendToJsonStringBuilder(sb);
		assertEquals(expectedAnnotationsAsString(":",",", false), sb.toString());
	}

	@Test
	public void testAsJsonStringWithoutBrackets() {
		annotations.add(KEY_STR, VAL_STR);
		annotations.add(KEY_INT, VAL_INT);
		annotations.add(KEY_LONG, VAL_LONG);
		assertEquals(expectedAnnotationsAsString(":","",false), annotations.asJsonStringWithoutBrackets());
	}

	@Test
	public void testToString() {
		annotations.add(KEY_STR, VAL_STR);
		annotations.add(KEY_INT, VAL_INT);
		annotations.add(KEY_LONG, VAL_LONG);
		assertEquals(expectedAnnotationsAsString("=",",",true), annotations.toString());
	}

}
