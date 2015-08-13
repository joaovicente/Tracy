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
    TracyAnnotations annotations;

    public TracyEvent(String taskId, String label, String parentOptId ,String optId, long msec) {
        this.taskId = taskId;
        this.parentOptId = parentOptId;
        this.label = label;
        this.optId = optId;
        this.msecBefore = msec;
        this.annotations = new TracyAnnotations(Tracy.TRACY_FRAME_ESTIMATED_ANNOTATION_COUNT);
    }

    public String toString()	{
        StringBuilder sb = new StringBuilder(Tracy.TRACY_ESTIMATED_FRAME_SIZE);
        sb.append("\"taskId\"=" + "\"" + taskId + "\"" 
                + ", \"parentOptId\"=" + "\"" + parentOptId + "\"" 
                + ", \"label\"=" + "\"" + label + "\"" 
                + ", \"optId\"=" + "\"" + optId + "\"" 
                + ", \"msecBefore\"=" + msecBefore 
                + ", \"msecAfter\"=" + msecAfter
                + ", \"msecElapsed\"=" + msecElapsed);
        annotations.toString();
        return sb.toString();
    }

    public Map<String, Object> toMap() {
    	final int DEFAULT_TRACY_KEY_SIZE = 7;
        Map<String, Object> map = new HashMap<String, Object>(Tracy.TRACY_FRAME_ESTIMATED_ANNOTATION_COUNT 
        		+ DEFAULT_TRACY_KEY_SIZE);
        map.put("taskId", taskId);
        map.put("parentOptId", parentOptId);
        map.put("label", label);
        map.put("optId", optId);
        map.put("msecBefore", Long.toString(msecBefore));
        map.put("msecAfter", Long.toString(msecAfter));
        map.put("msecElapsed", Long.toString(msecElapsed));
        annotations.appendToMap(map);
        return map;
    }
    
    
    public String toJsonString() {
        StringBuilder jsonBuffer = new StringBuilder(Tracy.TRACY_ESTIMATED_FRAME_SIZE);
        jsonBuffer.append("{");
        JsonFormatter.addJsonStringValue(jsonBuffer, "taskId", taskId, true);
        JsonFormatter.addJsonStringValue(jsonBuffer, "parentOptId", parentOptId, false);
        JsonFormatter.addJsonStringValue(jsonBuffer, "label", label, false);
        JsonFormatter.addJsonStringValue(jsonBuffer, "optId", optId, false);
        JsonFormatter.addJsonLongValue(jsonBuffer, "msecBefore", msecBefore, false);
        JsonFormatter.addJsonLongValue(jsonBuffer, "msecAfter", msecAfter, false);
        JsonFormatter.addJsonLongValue(jsonBuffer, "msecElapsed", msecElapsed, false);
        annotations.appendToJsonStringBuilder(jsonBuffer);
        jsonBuffer.append("}");
        return jsonBuffer.toString();
    }

    public void addAnnotation(String key, String value)	{
    	annotations.add(key, value);
    }
    
    public void addAnnotation(String key, int value)	{
    	annotations.add(key, value);
    }
    
    public void addAnnotation(String key, long value)	{
    	annotations.add(key, value);
    }
    
    public Object getAnnotation(String key) {
        return annotations.get(key);
    }

    public void addAnnotations(String... args) {
    	annotations.add(args);
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
