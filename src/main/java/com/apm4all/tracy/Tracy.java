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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * A Trace utility to capture capture timing application flow meta data 
 * Tracy is designed to provide a minimalist API for ease of use 
 * @author joao.diogo.vicente@gmail.com
 */
public class Tracy {
	static final String TRACY_DEFAULT_TASK_ID = "NA";
	static final String TRACY_DEFAULT_PARENT_OPT_ID = "NA";
	private final static ThreadLocal <TracyThreadContext> threadContext = new ThreadLocal <TracyThreadContext>();;

	/**
	 * Tracy allows to trace execution flow and later represent it as a Directed Acyclic Graph (DAG)
	 * A Tracy identifier (taskId) starts at the most outbound endpoint exposed by a system<br>
	 * The taskId is to be propagated across components, JVMs and hosts
	 * If the endpoint is outside this JVM/component you should have received a taskId and optId from a client.<br>
	 * @param taskId is a string which allows correlating Tracy events resulting of a endpoint being hit
	 * @param parentOptId is a string identifying the parent operation which invoked some logic on a local component
	 */
	public static void setContext(String taskId, String parentOptId) {
		threadContext.set(new TracyThreadContext(taskId, parentOptId));
	}
	
	 /**
     * Setting context in this manner is highly discouraged.<br>
     * taskId is fundamental to correlate Tracy events.<br>
     * optId is fundamental to place the parent node in the Tracy DAG    
     */	
	public static void setContext() {
		setContext(TracyThreadContext.generateRandomTaskId(), TracyThreadContext.generateRandomOptId());
	}
	
	 /**
     * Call before starting an operation you want to capture elapsed time for.<br>
     * You can nest before() calls if you want to trace both caller and callee methods, 
     * but make sure you call after() for every before().
     * @param label is the name you will see in the trace event report of graph node representation or timeline
     */	
	public static void before(String label) {
		TracyThreadContext ctx = threadContext.get();
		ctx.push(label);
	}
	
	 /**
     * Call after finishing an operation you want to capture elapsed time for.<br>
     * @param label is the name you will see in the trace event report of graph node representation or timeline
     */	
	public static void after(String label) {
		TracyThreadContext ctx = threadContext.get();
		ctx.pop();
	}

	 /**
     * before() and after() will capture timing information and hostname in a TracyEvent.<br>
     * annotate() allows capturing other information you want to see in TracyEvent (e.g. bytesReceived, bytesSent, etc)
     * for the TracyEvent for which you last called before() for
     * @param keyValueSequence is the sequence key,value strings you want on the TracyEvent 
     * (e.g. annotate("bytesSent", "103", "bytesReceived", "15432")
     */	
	public static void annotate(String... keyValueSequence) {
		TracyThreadContext ctx = threadContext.get();
		ctx.annotate(keyValueSequence);
	}
	
	 /**
     * Once all work has been done, and TracyEvents are ready to be collected you can collect them using this method
     * @return list of TracyEvents
     */	
	public static List<TracyEvent> getEvents() {
		TracyThreadContext ctx = threadContext.get();
		return ctx.getPoppedList();
	}
	
	 /**
     * Once all work has been done, and TracyEvents are ready to be collected you can collect them using this method
     * This method differs from getEvents() in the sense that the client does not need to know the structure of 
     * TracyEvents to be able to consume them.
     * @return list of TracyEvent maps
     */	
	public static List<Map<String, String>> getEventsAsMaps() {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		TracyThreadContext ctx = threadContext.get();
		for (TracyEvent event : ctx.getPoppedList())	{
			list.add(event.toMap());
		}
		return list;
	}
	
	public static String getTaskId() {
		TracyThreadContext ctx = threadContext.get();
		return ctx.getTaskId();
	}
	
	public static String getParentOptId() {
		TracyThreadContext ctx = threadContext.get();
		return ctx.getParentOptId();
	}
}
