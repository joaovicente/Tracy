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
    Map<String, String> annotations;

    public TracyEvent(String taskId, String label, String parentOptId ,String optId, long msec) {
        this.taskId = taskId;
        this.parentOptId = parentOptId;
        this.label = label;
        this.optId = optId;
        this.msecBefore = msec;
        this.annotations = new HashMap<String, String>(5);
    }

    public String toString()	{
        StringBuilder sb = new StringBuilder();
        sb.append("\"taskId\"=" + "\"" + taskId + "\"" 
                + ", \"parentOptId\"=" + "\"" + parentOptId + "\"" 
                + ", \"label\"=" + "\"" + label + "\"" 
                + ", \"optId\"=" + "\"" + optId + "\"" 
                + ", \"msecBefore\"=" + "\"" + msecBefore + "\"" 
                + ", \"msecAfter\"=" + "\"" + msecAfter + "\""
                + ", \"msecElapsed\"=" + "\"" + msecElapsed + "\"");
        for (String key : annotations.keySet())	{
            sb.append(", \"" + key + "\"=" + "\"" + annotations.get(key) + "\"");
        }
        return sb.toString();
    }

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<String, String>(10);
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
    
    private void addJsonElement(StringBuilder sb, String key, String value, boolean first)	{
       if (false == first)	{
           sb.append(",");
        }
        sb.append("\""+key+"\":\""+value+"\"");
    }
    
    public String toJsonString() {
        StringBuilder jsonBuffer = new StringBuilder(200);
        jsonBuffer.append("{");
        addJsonElement(jsonBuffer, "taskId", taskId, true);
        addJsonElement(jsonBuffer, "parentOptId", parentOptId, false);
        addJsonElement(jsonBuffer, "label", label, false);
        addJsonElement(jsonBuffer, "optId", optId, false);
        addJsonElement(jsonBuffer, "msecBefore", Long.toString(msecBefore), false);
        addJsonElement(jsonBuffer, "msecAfter", Long.toString(msecAfter), false);
        addJsonElement(jsonBuffer, "msecElapsed", Long.toString(msecElapsed), false);
        for (String key : annotations.keySet())	{
        	addJsonElement(jsonBuffer, key, annotations.get(key), false);
        }
        jsonBuffer.append("}");
        return jsonBuffer.toString();
    }

    public void addAnnotation(String key, String value)	{
        annotations.put(key, value);
    }
    
    public String getAnnotation(String key) {
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
    
    public String getError()  {
        return this.annotations.get("error");
    }
}
