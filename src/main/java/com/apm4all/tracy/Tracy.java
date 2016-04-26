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
    // Internal
    static final String TRACY_DEFAULT_PARENT_OPT_ID = "null";
    static final String TRACY_DEFAULT_COMPONENT_NAME = "null";
    static final int TRACY_FRAME_ESTIMATED_ANNOTATION_COUNT = 30;
    static final int TRACY_HTTP_HEADER_ESTIMATED_ANNOTATION_COUNT = 10;
    static final int TRACY_ESTIMATED_FRAME_SIZE = 200;
    static List<String> EMPTY_STRING_LIST = new ArrayList<String>();
    static List<TracyEvent> EMPTY_TRACY_EVENT_LIST = new ArrayList<TracyEvent>(); 
    static List<Map<String, Object>> EMPTY_LIST_OF_MAPS = new ArrayList<Map<String, Object>>();
    private final static ThreadLocal <TracyThreadContext> threadContext = new ThreadLocal <TracyThreadContext>();

    /**
     * Tracy allows to trace execution flow and later represent it as a Directed Acyclic Graph (DAG)
     * A Tracy identifier (taskId) starts at the most outbound endpoint exposed by a system<br>
     * The taskId is to be propagated across components, JVMs and hosts
     * If the endpoint is outside this JVM/component you should have received a taskId and optId from a client.<br>
     * @param taskId is a string which allows correlating Tracy events resulting of a endpoint being hit
     * @param parentOptId is a string identifying the parent operation which invoked some logic on a local component
     * @param componentName is a string identifying the name of the component the tracy belongs to
     */
    public static void setContext(String taskId, String parentOptId, String componentName) {
        if (null == taskId) {
            taskId = TracyThreadContext.generateRandomTaskId();
        }
        if (null == parentOptId)    {
            parentOptId = TRACY_DEFAULT_PARENT_OPT_ID;
        }
        if (null == componentName)  {
            componentName = TRACY_DEFAULT_COMPONENT_NAME;
        }
        threadContext.set(new TracyThreadContext(taskId, parentOptId, componentName));
    }    
    
    /**
     * Attaches existing context to ThreadLocal 
     */	
    protected static void setContext(TracyThreadContext ctx) {
        threadContext.set(ctx);
    }

    /**
     * Clearing context ensures there is no residual context sticking to a recycled thread.<br>
     */	
    public static void clearContext() {
        threadContext.set(null);
    }
    
    /**
     * Clearing context ensures there is no residual context sticking to a recycled thread.<br>
     * Currently just calls clearContext but may change in future so creating a distinction in the API<br>
     */	
    public static void clearWorkerContext() {
        clearContext();
    }

    /**
     * Call before starting an operation you want to capture elapsed time for.<br>
     * You can nest before() calls if you want to trace both caller and callee methods, 
     * but make sure you call after() for every before().
     * @param label is the name you will see in the trace event report of graph node representation or timeline
     */	
    public static void before(String label) {
        TracyThreadContext ctx = threadContext.get();
        if (isValidContext(ctx))    {
            ctx.push(label);
        }
    }

    /**
     * Call after finishing an operation you want to capture elapsed time for.<br>
     * @param label is the name you will see in the trace event report of graph node representation or timeline
     */	
    public static void after(String label) {
        TracyThreadContext ctx = threadContext.get();
        if (isValidContext(ctx))   {
            ctx.pop();
        }
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
        if (isValidContext(ctx)) {
            ctx.annotate(keyValueSequence);
        }
    }
    
    /**
     * Annotate an integer value
     */	
    public static void annotate(String intName, int intValue) {
        TracyThreadContext ctx = threadContext.get();
        if (isValidContext(ctx)) {
        	ctx.annotate(intName, intValue);
        }
    }

    /**
     * Annotate a long value
     */	
    public static void annotate(String longName, long longValue) {
        TracyThreadContext ctx = threadContext.get();
        if (isValidContext(ctx)) {
            ctx.annotate(longName, longValue);
        }
    }
    
    /**
     * Annotate a float value
     */	
    public static void annotate(String floatName, float floatValue) {
        TracyThreadContext ctx = threadContext.get();
        if (isValidContext(ctx)) {
            ctx.annotate(floatName, floatValue);
        }
    }
    
    
    /**
     * Annotate a double value
     */	
    public static void annotate(String doubleName, double doubleValue) {
        TracyThreadContext ctx = threadContext.get();
        if (isValidContext(ctx)) {
            ctx.annotate(doubleName, doubleValue);
        }
    }
    
    /**
     * Annotate a boolean value
     */	
    public static void annotate(String booleanName, boolean booleanValue) {
        TracyThreadContext ctx = threadContext.get();
        if (isValidContext(ctx)) {
            ctx.annotate(booleanName, booleanValue);
        }
    }
    
    
    /**
    * Facilitate annotating annotations received from the Client in the X-Tracy-Annotations HTTP header <br>
    * Currently only supporting string annotations in CSV format. Example usage below:
     * e.g.
     * <code><pre>
     * public void doGet(HttpServletRequest request, HttpServletResponse response)   {
     * throws ServletException, IOException
     *     ...
     *     String httpRequestTracyAnnotations = request.getHeader(Tracy.HTTP_HEADER_X_TRACY_ANNOTATIONS);
     *     Tracy.annotateFromHttpRequestAnnotations(httpRequestTracyAnnotations)
     * }
     * </pre></code>
    * @param csvAnnotations contains the annotations in CSV format e.g. key1,val1,key2,val2<br>
    *  
    */ 
	public static void annotateFromHttpRequestAnnotations(String csvAnnotations) {
	    if (null != csvAnnotations)  {
	        String[] split = csvAnnotations.split(",");
	        if (split.length % 2 == 0) {
	            String value = "null";
	            for (int i=0; i<split.length/2; i++) {
	                String key = split[2*i].toString();
	                if (null != split[2*i + 1])	{
	                    value = split[2*i + 1].toString();
	                }
	                Tracy.annotate(key, value);
	            }
	        }
	    }
	}

    /**
     * This method is used to capture annotations which should be sent back to the HTTP client 
     * HttpResponse annotations are created by this method and retrieved using {@link #getHttpResponseBufferAnnotations}
     * when the HTTP response header is to be returned as shown in example below<br>
     * <code><pre>
     *   ...
     *   Tracy.annotate("key1", "val1");
     *   annotateOnHttpResponseBuffer("key1"); 
     * </pre></code>
     * annotateOnHttpResponseBuffer(key) must be called after a (Tracy frame) Tracy.annotation(key, value) as it will 
     * retrieve the value from the recently created Tracy.annotation.<br>
     * annotateOnHttpResponseBuffer(key) can be called from any point in the Tracy frame stack. Tracy will store them
     * at the topmost level of the thread context to be easily accessible using {@link #getHttpResponseBufferAnnotations} method
     *   
     * @param key defines the recently used key used in Tracy.annotate(key, val) which is to be sent back in the HTTP response header
     */	
    public static void annotateOnHttpResponseBuffer(String key)	{
        TracyThreadContext ctx = threadContext.get();
        if (isValidContext(ctx)) {
            ctx.annotateOnHttpResponseBuffer(key);
        }
    }
    
    /**
     * Retrieves all annotations previously created with {@link #annotateOnHttpResponseBuffers} 
     * to send to Client in the X-Tracy-Annotations header.<br>
     * e.g.
     * <code><pre>
     * public void doGet(HttpServletRequest request, HttpServletResponse response)   {
     * throws ServletException, IOException
     *     ...
     *     String httpResponseAnnotations = getHttpResponseBufferAnnotations();
     *     if (httpResponseAnnotations)  {
     *       response.addHeader(Tracy.HTTP_HEADER_X_TRACY_ANNOTATIONS, httpResponseAnnotations);
     *     }
     * }
     * </pre></code>
     * @return a string containing annotations set using annotateOnHttpResponseBuffer() in CSV format
     */	
    public static String getHttpResponseBufferAnnotations()	{
    	String annotations = "";
        TracyThreadContext ctx = threadContext.get();
        if (isValidContext(ctx)) {
            annotations = ctx.getHttpResponseBufferAnnotations();
        }
        return annotations;
    }
    
    /**
     * Once all work has been done, and TracyEvents are ready to be collected you can collect them using this method
     * @return list of TracyEvents
     */	
    public static List<TracyEvent> getEvents() {
        TracyThreadContext ctx = threadContext.get();
        List<TracyEvent> events = EMPTY_TRACY_EVENT_LIST;
        if (isValidContext(ctx)) {
            events = ctx.getPoppedList();
        }
        return events;
    }

    /**
     * Once all work has been done, and TracyEvents are ready to be collected you can collect them using this method
     * This method differs from getEvents() in the sense that the client does not need to know the structure of 
     * TracyEvents to be able to consume them.
     * @return list of TracyEvent maps
     */	
    public static List<Map<String, Object>> getEventsAsMaps() {
        List<Map<String, Object>> list = EMPTY_LIST_OF_MAPS; 
        TracyThreadContext ctx = threadContext.get();
        if (isValidContext(ctx)) {
            list = new ArrayList<Map<String, Object>>(20);
            for (TracyEvent event : ctx.getPoppedList())	{
        	list.add(event.toMap());
            }
        }
        return list;
    }

    
    /**
     * Gets List of Tracy events in JSON format
     * @return list of Tracy JSONified events
     */	
    public static List<String> getEventsAsJson() {
        List<String> list = Tracy.EMPTY_STRING_LIST;
        TracyThreadContext ctx = threadContext.get();
        if (isValidContext(ctx)) {
            list = new ArrayList<String>(20);
            for (TracyEvent event : ctx.getPoppedList())	{
                list.add(event.toJsonString());
            }
        }
        return list;
	}
    
    /**
     * Collects JSON Tracy Events array packaged as a value of tracySegment
     * @return JSON object containing Tracy array string
     */	
    public static String getEventsAsJsonTracySegment()	{
    	// Assuming max 8 frames per segment. Typical Tracy JSON frame is ~250 
    	// ( 250 * 8 = 2000). Rounding up to 2048
    	final int TRACY_SEGMENT_CHAR_SIZE = 2048;
    	String jsonArrayString = null;
    	int frameCounter = 0;
        TracyThreadContext ctx = threadContext.get();
        if (isValidContext(ctx) && ctx.getPoppedList().size()>0) {
        	StringBuilder sb = new StringBuilder(TRACY_SEGMENT_CHAR_SIZE);
        	sb.append("{\"tracySegment\":[");
        	for (TracyEvent event : ctx.getPoppedList())	{
        		if (frameCounter > 0)	{
        			sb.append(",");
        		}
        		sb.append(event.toJsonString());
        		frameCounter++;
        	}
        	sb.append("]}");
        	jsonArrayString = sb.toString();
        }
        return jsonArrayString;
    }

	
    /**
     * Gets the Tracy taskId
     * @return Tracy taskId
     */	
    public static String getTaskId() {
        TracyThreadContext ctx = threadContext.get();
        return ctx.getTaskId();
    }
    
    /**
     * taskId is normally set at Tracy.setContext(...) and never changed after<br>
     * taskId is: <br>
     * Either provided by the transport mechanism (HTTP header).<br>
     * Or created by the local JVM, if the local JVM is the entry Task entry point<br>
     * setTaskId() allows the user to change the taskId after all Tracy has been gathered, for example, 
     * in case the JVM is a client who wants to use a trasactionId of a server as the taskId<br>
     * WARNING: It is assumed that after serTaskId() is called no more before/after calls are made.
     */	
    public static void setTaskId(String taskId) {
        TracyThreadContext ctx = threadContext.get();
        if (isValidContext(ctx))    {
        	ctx.setTaskId(taskId);
        }
    }
    
    public static final TracyThreadContext getTracyThreadContext() {
        return threadContext.get();
    }
    
    public static String getParentOptId() {
        TracyThreadContext ctx = threadContext.get();
        return ctx.getParentOptId();
    }
   
    /**
     * Allows user to set a custom optId (usually automatically created)
     * This method can be safely called any time between a 'before' and 'after'
     * 
     * @param customOptId is the String which will be recorded as Tracy optId. Best practice is to use a 4 character string outside of 32bit hex pattern [0000..1111]
     * e.g. setOptId("U001")
     */
    public static void setOptId(String customOptId) {
        TracyThreadContext ctx = threadContext.get();
        if (isValidContext(ctx))    {
            ctx.setOptId(customOptId);
        }
    }
    
    /**
     * Creates Tracy worker thread context to be bound to the worker thread
     * The context returned contains only parentage information
     * Note: This needs to be called from the requester thread
     * @return Context to be bound to the worker thread
     */
    public static TracyThreadContext createWorkerTheadContext() {
        TracyThreadContext currentCtx = threadContext.get();
        TracyThreadContext workerCtx = null;
        if (isValidContext(currentCtx))  {
            workerCtx = new TracyThreadContext(
                currentCtx.getTaskId(), currentCtx.getOptId(), null);
        }
        return workerCtx;
    }

    /**
     * Attaches parentage context created by createWorkerThread()<br>
     * Once this context is attached before() after() will create TracyEvents
     * within the worker thread.
     * @return Context to be bound to the worker thread
     */
    public static void setWorkerContext(TracyThreadContext ctx) {
        threadContext.set(ctx);
    }

    /**
     * When called from the worker thread, will return the worker TracyThreadContext 
     * It is the responsibility of the caller to propagate this context using 
     * TracyableData.setTracyContext()
     * Note: This is to be called from the worker thread
     * @return worker TracyThreadContext 
     */
    public static TracyThreadContext getWorkerContext() {
        return threadContext.get();
    }

    /**
     * When called from the requester thread, will merge worker TracyThreadContext into the 
     * requester TracyThreadContext
     * @return worker TracyThreadContext 
     */
    public static void mergeWorkerContext(TracyThreadContext workerTracyThreadContext) {
        TracyThreadContext ctx = threadContext.get();
        if (isValidContext(ctx)) {
            ctx.mergeChildContext(workerTracyThreadContext);
        }
    }
    
    private static boolean isValidContext(TracyThreadContext ctx) {
        return (null != ctx);
    }

    public static boolean isEnabled() {
        TracyThreadContext ctx = threadContext.get();
        return(isValidContext(ctx));
    }
   
    public static int frameDepth() {
        int depth = 0;
        TracyThreadContext ctx = threadContext.get();
        if (isValidContext(ctx)) {
            depth = ctx.frameDepth();
        }
        return depth;
    }
    
    public static boolean isEnabled(TracyThreadContext ctx) {
        return(isValidContext(ctx));
    }

    /**
     * In case where an exception is thrown Tracy.frameError(errorString) can be used to 
     * unwind all Tracy stack frames with a particular user error message.
     * This is useful when one wants to signal an custom error for a task when one does not
     * know (or does not care) how deep the the Tracy stack is.
     */
    public static void outerError(String error) {
        TracyThreadContext ctx = threadContext.get();
        if (isValidContext(ctx)) {
            ctx.popAllWithError(error);
        }
    }

    /**
     * In case where an exception is thrown Tracy.frameError(errorString) can be used to 
     * close Tracy stack frame with a user error message while still flow to continue.<br>
     * This can be useful when in a retry exception where the task may be recoverable.<br>
     * If the current frame is not the only Tracy stack frame the next frame will resume normally.
     * In case the user wants to raise an error all the way up to the inner Tracy stack frame, 
     * then outerError(errorString) should be used instead  
     */
    public static void frameError(String error) {
        TracyThreadContext ctx = threadContext.get();
        if (isValidContext(ctx)) {
            ctx.popFrameWithError(error);
        }
    }
    
    /**
     * In case where an error occurs but the user does not want to raise an exception 
     * and the user takes responsibility of ensuring Tracy.after is guaranteed to 
     * be called then frameErrorWithoutPoping can be called to simply create an "error"
     * annotation
     */
    public static void frameErrorWithoutPopping(String error) {
        TracyThreadContext ctx = threadContext.get();
        if (isValidContext(ctx)) {
            ctx.annotateFrameError(error);
        }
    }
}
