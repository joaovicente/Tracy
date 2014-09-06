package com.apm4all.tracy;

public class TracyableFutureRequest {
	private boolean traced;
	Object request;

	public TracyableFutureRequest(boolean traced, Object request) {
		super();
		this.traced = traced;
		this.request = request;
	}
	
	public boolean isTraced()	{
		return(traced);
	}

	public Object getRequest() {
		return request;
	}
}
