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
		sb.append("taskId=" + "\"" + this.taskId + "\"" 
			+ ", parentOptId=" + "\"" + this.parentOptId + "\"" 
			+ ", label=" + "\"" + this.label + "\"" 
			+ ", optId=" + "\"" + this.optId + "\"" 
			+ ", msecBefore=" + "\"" + this.msecBefore + "\"" 
			+ ", msecAfter=" + "\"" + this.msecAfter + "\"");
		for (String key : annotations.keySet())	{
			sb.append(", " + key + "=" + "\"" + annotations.get(key) + "\"");
		}
		return sb.toString();
	}

	public void addAnnotation(String key, String value)	{
		annotations.put(key, value);
	}
	
	public void addAnnotations(String... args) {
		if (args.length % 2 != 0) {
			throw new IllegalArgumentException(
					"Tracy.addAnnotation requires an even number of arguments.");
		}
		for (int i=0; i<args.length/2; i++) {
			String key = args[2*i].toString();
			String value = args[2*i + 1].toString();
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
}
