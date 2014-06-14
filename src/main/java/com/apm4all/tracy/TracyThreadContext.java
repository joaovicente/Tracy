package com.apm4all.tracy;

public class TracyThreadContext {
	private String taskId;
	private String parentOptId;
	
	public TracyThreadContext(String taskId, String parentOptId) {
		super();
		this.taskId = taskId;
		this.parentOptId = parentOptId;
	}

	public String getTaskId() {
		return taskId;
	}

	public String getParentOptId() {
		return parentOptId;
	}
}
