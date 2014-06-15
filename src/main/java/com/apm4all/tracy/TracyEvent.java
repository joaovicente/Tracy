package com.apm4all.tracy;

public class TracyEvent {
	String taskId;
	String parentOptId;
	String label;
	String optId;
	long msecBefore;
	long msecAfter;
	
	public TracyEvent(String taskId, String label, String parentOptId ,String optId, long msec) {
		this.taskId = taskId;
		this.parentOptId = parentOptId;
		this.label = label;
		this.optId = optId;
		this.msecBefore = msec;
	}
	
	public String toString()	{
		return "taskId=" + "\"" + this.taskId + "\"" 
			+ ", parentOptId=" + "\"" + this.parentOptId + "\"" 
			+ ", label=" + "\"" + this.label + "\"" 
			+ ", optId=" + "\"" + this.optId + "\"" 
			+ ", msecBefore=" + "\"" + this.msecBefore + "\"" 
			+ ", msecAfter=" + "\"" + this.msecAfter + "\"";
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
