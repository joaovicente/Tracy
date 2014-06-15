package com.apm4all.tracy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Tracy {
	static final String TRACY_DEFAULT_TASK_ID = "NA";
	static final String TRACY_DEFAULT_PARENT_OPT_ID = "NA";
	//private static ThreadLocal <ArrayList<Annotation>> annotations = new ThreadLocal <ArrayList<Annotation>>();
	private final static ThreadLocal <TracyThreadContext> threadContext = new ThreadLocal <TracyThreadContext>();;
	
	public static void setContext(String taskId, String parentOptId) {
		threadContext.set(new TracyThreadContext(taskId, parentOptId));
	}
	
	 /**
     * Setting context in this manner is highly discouraged.<br>
     * taskId is fundamental to correlate Tracy events.<br>
     * optId is fundamental to place the parent node in the Tracy DAG    
     */	
	public static void setContext() {
		setContext(TRACY_DEFAULT_TASK_ID, TRACY_DEFAULT_PARENT_OPT_ID);
		
	}
	
	public static String getTaskId() {
		TracyThreadContext ctx = threadContext.get();
		return ctx.getTaskId();
	}
	
	public static String getParentOptId() {
		TracyThreadContext ctx = threadContext.get();
		return ctx.getParentOptId();
	}
	
	public static void before(String label) {
		TracyThreadContext ctx = threadContext.get();
		ctx.push(label);
	}
	
	public static void after(String label) {
		TracyThreadContext ctx = threadContext.get();
		ctx.pop();
	}
	
	public static List<TracyEvent> getEvents() {
		TracyThreadContext ctx = threadContext.get();
		return ctx.getPoppedList();
	}
	
	public static List<Map<String, String>> getEventsAsMaps() {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		TracyThreadContext ctx = threadContext.get();
		for (TracyEvent event : ctx.getPoppedList())	{
			list.add(event.toMap());
		}
		return list;
	}
	
	public static void annotate(String... args) {
		TracyThreadContext ctx = threadContext.get();
		ctx.annotate(args);
	}

}
