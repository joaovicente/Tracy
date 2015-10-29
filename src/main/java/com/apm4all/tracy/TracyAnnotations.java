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

import java.util.HashMap;
import java.util.Map;

public class TracyAnnotations {
    Map<String, Object> annotations;
	public TracyAnnotations(int estimatedAnnotationCount)	{
		annotations = new HashMap<String, Object>(estimatedAnnotationCount);
	}
	
	public void add(String key, String value)	{
        annotations.put(key, value);
	}

	public void add(String key, int value)	{
        annotations.put(key, new Integer(value));
	}

	public void add(String key, long value)	{
        annotations.put(key, new Long(value));
	}

	public void add(String key, Object value) {
        annotations.put(key, value);
	}
	
	public void add(String... vars)	{
        if (vars.length % 2 != 0) {
            throw new IllegalArgumentException(
                    "Tracy.addAnnotation requires an even number of arguments.");
        }
        String value = "null";
        for (int i=0; i<vars.length/2; i++) {
            String key = vars[2*i].toString();
            if (null != vars[2*i + 1])	{
            	value = vars[2*i + 1].toString();
            }
            add(key, value);
        }
	}
	
	public Object get(String key)	{
        return annotations.get(key);
	}
	
	public void appendToMap(Map<String, Object> map)	{
        for (String key : annotations.keySet())	{
            map.put(key, annotations.get(key));
        }
	}
	
	public void appendToJsonStringBuilder(StringBuilder jsonBuffer)	{
        for (String key : annotations.keySet())	{
        	Object value = annotations.get(key);
        	if (String.class.isInstance(value))	{
        		JsonFormatter.addJsonStringValue(jsonBuffer, key, (String)value, false);
        	}
        	else if (Integer.class.isInstance(value))	{
        		JsonFormatter.addJsonIntValue(jsonBuffer, key, ((Integer)value).intValue(), false);
        	}
        	else if  (Long.class.isInstance(value)) {
        		JsonFormatter.addJsonLongValue(jsonBuffer, key, ((Long)value).longValue(), false);
        	}
        	else if  (Float.class.isInstance(value)) {
        		JsonFormatter.addJsonFloatValue(jsonBuffer, key, ((Float)value).floatValue(), false);
        	}
        	else if  (Double.class.isInstance(value)) {
        		JsonFormatter.addJsonDoubleValue(jsonBuffer, key, ((Double)value).doubleValue(), false);
        	}
        	else if  (Boolean.class.isInstance(value)) {
        		JsonFormatter.addJsonBooleanValue(jsonBuffer, key, ((Boolean)value).booleanValue(), false);
        	}
        }
	}
	
	public String asJsonStringWithoutBrackets()	{
		String output = "";
		StringBuilder jsonBuffer = new StringBuilder(Tracy.TRACY_ESTIMATED_FRAME_SIZE);
		boolean first = true;
        for (String key : annotations.keySet())	{
        	Object value = annotations.get(key);
        	if (String.class.isInstance(value))	{
        		JsonFormatter.addJsonStringValue(jsonBuffer, key, (String)value, first);
        	}
        	else if (Integer.class.isInstance(value))	{
        		JsonFormatter.addJsonIntValue(jsonBuffer, key, ((Integer)value).intValue(), first);
        	}
        	else if  (Long.class.isInstance(value)) {
        		JsonFormatter.addJsonLongValue(jsonBuffer, key, ((Long)value).longValue(), first);
        	}
        	else if  (Float.class.isInstance(value)) {
        		JsonFormatter.addJsonFloatValue(jsonBuffer, key, ((Float)value).floatValue(), first);
        	}
        	else if  (Double.class.isInstance(value)) {
        		JsonFormatter.addJsonDoubleValue(jsonBuffer, key, ((Double)value).doubleValue(), first);
        	}
        	else if  (Boolean.class.isInstance(value)) {
        		JsonFormatter.addJsonBooleanValue(jsonBuffer, key, ((Boolean)value).booleanValue(), first);
        	}
        	first = false;
        }
        if (jsonBuffer.length() > 0)	{
        	output = jsonBuffer.toString();
        }
		return output;
	}
	
	
	public String asCsvString()	{
		String output = "";
		StringBuilder csvBuffer = new StringBuilder(Tracy.TRACY_ESTIMATED_FRAME_SIZE);
		boolean first = true;
        for (String key : annotations.keySet())	{
            if (true != first)  {
        	    csvBuffer.append(",");
            }
        	Object value = annotations.get(key);
        	csvBuffer.append(key).append(",").append(value);
        	first = false;
        }
        if (csvBuffer.length() > 0)	{
        	output = csvBuffer.toString();
        }
		return output;
	}
	
	public String toString()	{
		StringBuilder sb = new StringBuilder(Tracy.TRACY_ESTIMATED_FRAME_SIZE);
		for (String key : annotations.keySet())	{
			Object value = annotations.get(key);
			if (String.class.isInstance(value))	{
				sb.append(", \"" + key + "\"=" + "\"" + annotations.get(key).toString() + "\"");
			}
			else if (Integer.class.isInstance(value) || Long.class.isInstance(value)) {
				sb.append(", \"" + key + "\"=" + annotations.get(key).toString());
			}
			else if (Long.class.isInstance(value) || Long.class.isInstance(value)) {
				sb.append(", \"" + key + "\"=" + annotations.get(key).toString());
			}
			else if (Float.class.isInstance(value) || Float.class.isInstance(value)) {
				sb.append(", \"" + key + "\"=" + annotations.get(key).toString());
			}
			else if (Double.class.isInstance(value) || Double.class.isInstance(value)) {
				sb.append(", \"" + key + "\"=" + annotations.get(key).toString());
			}
			else if (Boolean.class.isInstance(value) || Boolean.class.isInstance(value)) {
				sb.append(", \"" + key + "\"=" + annotations.get(key).toString());
			}
		}
		return sb.toString();
	}

}
