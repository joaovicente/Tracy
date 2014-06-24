package com.apm4all.tracy;

public class TracyableData {
	private Object data;
	private TracyThreadContext tracyThreadContext;

	public TracyThreadContext getTracyThreadContext() {
		return tracyThreadContext;
	}

	public void setTracyThreadContext(TracyThreadContext tracyThreadContext) {
		this.tracyThreadContext = tracyThreadContext;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

}
