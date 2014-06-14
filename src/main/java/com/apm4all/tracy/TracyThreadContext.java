package com.apm4all.tracy;

public class TracyThreadContext {
	private String taskId;
	private String parentOptId;
	//TODO: Stack of TracyEvent(s)
	//TODO: List of popped TracyEvent(s)
	//TODO: Capture hostname
	//TODO: Operation name to be captured as label of Tracy.Before() and TracyAfter
	//TODO: Component name would be useful to gather (1 for the whole context)
	//TODO: Consider mechanism to relay TaskId and parentOptId to child worker threads as well as getting worker thread events back to main thread 
	
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
