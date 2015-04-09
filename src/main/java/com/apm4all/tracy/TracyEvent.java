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

public class TracyEvent {
    String taskId;
    String parentOptId;
    String label;
    String optId;
    long msecBefore;
    long msecAfter;
    long msecElapsed;
    Map<String, Object> annotations;

    public TracyEvent(String taskId, String label, String parentOptId ,String optId, long msec) {
        this.taskId = taskId;
        this.parentOptId = parentOptId;
        this.label = label;
        this.optId = optId;
        this.msecBefore = msec;
        this.annotations = new HashMap<String, Object>(5);
    }

    public String toString()	{
        StringBuilder sb = new StringBuilder();
        sb.append("\"taskId\"=" + "\"" + taskId + "\"" 
                + ", \"parentOptId\"=" + "\"" + parentOptId + "\"" 
                + ", \"label\"=" + "\"" + label + "\"" 
                + ", \"optId\"=" + "\"" + optId + "\"" 
                + ", \"msecBefore\"=" + msecBefore 
                + ", \"msecAfter\"=" + msecAfter
                + ", \"msecElapsed\"=" + msecElapsed);
        for (String key : annotations.keySet())	{
        	Object value = annotations.get(key);
        	if (String.class.isInstance(value))	{
        		sb.append(", \"" + key + "\"=" + "\"" + annotations.get(key).toString() + "\"");
        	}
        	else if (Integer.class.isInstance(value) || Long.class.isInstance(value)) {
        		sb.append(", \"" + key + "\"=" + "\"" + annotations.get(key).toString() + "\"");
        	}
        }
        return sb.toString();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<String, Object>(10);
        map.put("taskId", taskId);
        map.put("parentOptId", parentOptId);
        map.put("label", label);
        map.put("optId", optId);
        map.put("msecBefore", Long.toString(msecBefore));
        map.put("msecAfter", Long.toString(msecAfter));
        map.put("msecElapsed", Long.toString(msecElapsed));
        for (String key : annotations.keySet())	{
            map.put(key, annotations.get(key));
        }
        return map;
    }
    
    private void addJsonStringValue(StringBuilder sb, String key, String value, boolean first)	{
       if (false == first)	{
           sb.append(",");
        }
        sb.append("\""+key+"\":\""+value+"\"");
    }
    
    private void addJsonLongValue(StringBuilder sb, String key, long value, boolean first)	{
       if (false == first)	{
           sb.append(",");
        }
        sb.append("\""+key+"\":"+value);
    }

    private void addJsonIntValue(StringBuilder sb, String key, int value, boolean first)	{
       if (false == first)	{
           sb.append(",");
        }
        sb.append("\""+key+"\":"+value);
    }
    
    public String toJsonString() {
        StringBuilder jsonBuffer = new StringBuilder(200);
        jsonBuffer.append("{");
        addJsonStringValue(jsonBuffer, "taskId", taskId, true);
        addJsonStringValue(jsonBuffer, "parentOptId", parentOptId, false);
        addJsonStringValue(jsonBuffer, "label", label, false);
        addJsonStringValue(jsonBuffer, "optId", optId, false);
        addJsonLongValue(jsonBuffer, "msecBefore", msecBefore, false);
        addJsonLongValue(jsonBuffer, "msecAfter", msecAfter, false);
        addJsonLongValue(jsonBuffer, "msecElapsed", msecElapsed, false);
        for (String key : annotations.keySet())	{
        	Object value = annotations.get(key);
        	if (String.class.isInstance(value))	{
        		addJsonStringValue(jsonBuffer, key, (String)value, false);
        	}
        	else if (Integer.class.isInstance(value))	{
        		addJsonIntValue(jsonBuffer, key, ((Integer)value).intValue(), false);
        	}
        	else if  (Long.class.isInstance(value)) {
        		addJsonLongValue(jsonBuffer, key, ((Long)value).longValue(), false);
        	}
        }
        jsonBuffer.append("}");
        return jsonBuffer.toString();
    }

    public void addAnnotation(String key, String value)	{
        annotations.put(key, value);
    }
    
    public void addAnnotation(String key, int value)	{
        annotations.put(key, new Integer(value));
    }
    
    public void addAnnotation(String key, long value)	{
        annotations.put(key, new Long(value));
    }
    
    public Object getAnnotation(String key) {
        return annotations.get(key);
    }

    public void addAnnotations(String... args) {
        if (args.length % 2 != 0) {
            throw new IllegalArgumentException(
                    "Tracy.addAnnotation requires an even number of arguments.");
        }
        String value = "null";
        for (int i=0; i<args.length/2; i++) {
            String key = args[2*i].toString();
            if (null != args[2*i + 1])	{
            	value = args[2*i + 1].toString();
            }
            addAnnotation(key, value);
        }
    }

    public long getMsecBefore() {
        return msecBefore;
    }

    public void setMsecBefore(long msecBefore) {
        this.msecBefore = msecBefore;
    }

    public long getMsecAfter() {
        return msecAfter;
    }

    public void setMsecAfter(long msecAfter) {
        this.msecAfter = msecAfter;
        this.msecElapsed = msecAfter - msecBefore;
    }

    public long getMsecElapsed() {
        return msecElapsed;
    }

    public void setMsecElapsed(long msecElapsed) {
        this.msecElapsed = msecElapsed;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getOptId() {
        return optId;
    }

    public void setOptId(String optId) {
        this.optId = optId;
    }

    public String getParentOptId() {
        return parentOptId;
    }

    public void setParentOptId(String parentOptId) {
        this.parentOptId = parentOptId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
    
    public Object getError()  {
        return this.annotations.get("error");
    }
}
