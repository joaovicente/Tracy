package com.apm4all.tracy;

public class TracyEvent {
	long msecBefore;
	long msecAfter;
	String label;
	String optId;
	
	public TracyEvent(String label, String optId, long msec) {
		this.label = label;
		this.optId = optId;
		this.msecBefore = msec;
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
}
