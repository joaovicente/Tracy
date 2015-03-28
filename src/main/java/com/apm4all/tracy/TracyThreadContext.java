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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.Random;

public class TracyThreadContext {
    private static Random r = new Random();
    private static String hostname = null;
    private static String component = null;

    private String taskId;
    private String parentOptId;
    private Stack<TracyEvent> stack;
    private List<TracyEvent> poppedList;
    //TODO: Develop TracyEvent log reporter 

    public TracyThreadContext(String taskId, String parentOptId) {
        super();
        resolveHostname();
        this.taskId = taskId;
        this.parentOptId = parentOptId;
        stack = new Stack<TracyEvent>();
        poppedList = new ArrayList<TracyEvent>();
    }
    
    /**
     * Creates Thread context using taskId, parentOptId and componentName
     * @param taskId
     * @param parentOptId
     * @param componentName
     */  
    public TracyThreadContext(String taskId, String parentOptId, String componentName) {
        super();
        resolveHostname();
        this.taskId = taskId;
        this.parentOptId = parentOptId;
        if (component == null || component.equals(componentName) == false) {
            component = componentName;
        }
        stack = new Stack<TracyEvent>();
        poppedList = new ArrayList<TracyEvent>();
    }

    static void resolveHostname() {
        try {
            if (hostname == null) {
                hostname = InetAddress.getLocalHost().getHostName();
            }
        } catch (UnknownHostException e) {
            hostname = "unknown";
        }
    }

    static int randomNumber(int upperLimit)	{
        return r.nextInt(upperLimit);
    }

    //TODO: OptId must be unique within all taskId TracyEvents. Must use a better mechanism to avoid collisions. (e.g. using IP address, threadId, milliseconds as input)
    static String generateRandomOptId()	{
        return Integer.toHexString(randomNumber(0xFFFF)).toUpperCase();
    }

    static String generateRandomTaskId()	{
        return Integer.toHexString(randomNumber(0x7FFFFFFF)).toUpperCase();
    }

    public String getTaskId() {
        return taskId;
    }
    
    public void setTaskId(String taskId) {
        this.taskId = taskId;
        for (TracyEvent event : poppedList)	{
        	event.setTaskId(taskId);
        }
    }

    public String getParentOptId() {
        return parentOptId;
    }

    public String getOptId() {
        TracyEvent event = stack.peek();
        return event.optId;
    }
    
    public void setOptId(String optId) {
        TracyEvent event = stack.peek();
        event.setOptId(optId);
    }

    public String getComponent() {
        return component;
    }

    public void push(String label) {
        long msec = System.currentTimeMillis();
        String eventParentOptId;
        // Event parent OptId will be lower stack event,
        if (stack.size() > 0)	{
            eventParentOptId = stack.peek().getOptId();
        }
        else	{
            // or context parent in case this is first stack level
            eventParentOptId = this.parentOptId;
        }
        // Generate random optId (must be unique per taskid event set)
        String optId = generateRandomOptId();
        // Create new TracyEvent
        TracyEvent event = new TracyEvent(this.taskId, label, eventParentOptId, optId, msec);
        event.addAnnotation("host", hostname);
        if (component != null)	{
            event.addAnnotation("component", component);
        }
        stack.add(event);
    }

    public void pop() {
        if (stack.isEmpty() == false)   {
            TracyEvent event = stack.pop();
            event.setMsecAfter(System.currentTimeMillis());
            poppedList.add(event);
        }
    }
    
    public void forcePop() {
        forcePop("unknown");
    }
    
    public void forcePop(String error) {
        TracyEvent event = stack.pop();
        event.setMsecAfter(System.currentTimeMillis());
        event.addAnnotation("error", error);
        poppedList.add(event);
    }

    public List<TracyEvent> getPoppedList() {
	while(stack.isEmpty() == false)	{
	    forcePop();
	}
        return poppedList;
    }
    
    public void popAllWithError(String error) {
        while(stack.isEmpty() == false)	{
            forcePop(error);
        }
    }
    
    public void popFrameWithError(String error) {
        if (stack.isEmpty() == false)	{
            forcePop(error);
        }
    }
    
    
    public void setPoppedList(List<TracyEvent> poppedList) {
        this.poppedList = poppedList;
    }

    public void annotate(String... args) {
        if (stack.isEmpty() == false)   {
            stack.peek().addAnnotations(args);
        }
    }

    public void mergeChildContext(TracyThreadContext ctx) {
    	if (null != ctx)	{
    		for (TracyEvent event : ctx.getPoppedList())	{
    			poppedList.add(event);
    		}
    	}
    }


}
