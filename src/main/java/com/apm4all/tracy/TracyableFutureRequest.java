package com.apm4all.tracy;

public class TracyableFutureRequest {
	private boolean traced;
	private Object data;

	public TracyableFutureRequest(boolean traced, Object data) {
		super();
		this.traced = traced;
		this.data = data;
	}
	
	public boolean isTraced()	{
		return(traced);
	}

	public Object getData() {
		return data;
	}
}
