package com.apm4all.tracy;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;

public class TracyTestFuture {
    private static final int NTHREDS = 10;
    private	ExecutorService executor = Executors.newFixedThreadPool(NTHREDS);

    //TODO: Create TracyableFuture interface providing getData() and getTracyThreadContext()

    private Future<TracyableFutureResponse> futureIt(final TracyableFutureRequest request)	{
        Callable<TracyableFutureResponse> worker = null;
//        System.out.println("Executing future");
        // Creates Tracy worker thread context to be bound to the worker thread
        final TracyThreadContext ctx = Tracy.createWorkerTheadContext();
        worker = new Callable<TracyableFutureResponse>() {
            public TracyableFutureResponse call() throws Exception {
                TracyableFutureResponse td = new TracyableFutureResponse();
                // Binds context to worker thread so static Tracy calls can be made (e.g. before() after())
                if (request.isTraced())	{
                    Tracy.setWorkerContext(ctx);
                }
//                System.out.println("Executing future (call) ");
                Thread.sleep(((Integer)(request.getData()))*100);
                Tracy.before("Worker-" + request.getData().toString());
                String out = request.getData().toString();
                Tracy.after("Worker-" + request.getData().toString());
                td.setData(out);
                td.setTracyThreadContext(Tracy.getWorkerContext());
                return td;
            }
        };
        return executor.submit(worker);
    }

    @Test
    public void testFutureTrace() throws InterruptedException {
        final int NUM_FUTURES = 2;
        ArrayList<Future<TracyableFutureResponse>> futuresList = new ArrayList<Future<TracyableFutureResponse>>();
        int i;
        
        Tracy.setContext();
        Tracy.before("Requestor");
        try {
            for (i=0; i<NUM_FUTURES ; i++)	{
//                System.out.println("Calling future " +i);
                futuresList.add(futureIt(new TracyableFutureRequest(true, new Integer(i))));
            }

            i=0;
            for (Future<TracyableFutureResponse> future : futuresList)	{
//                System.out.println("Polling future");
                TracyableFutureResponse out =  future.get();
                String str = (String) out.getData();
                assertEquals(Integer.toString(i), str);
//                System.out.println("Got future " + str);
                // Merge worker trace into the task handler thread Tracy context
                Tracy.mergeWorkerContext(out.getTracyThreadContext());
                i++;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Tracy.after("Requestor");
        List<TracyEvent> events = Tracy.getEvents();
        assertEquals(3, events.size());
        assertEquals("Worker-0", events.get(0).getLabel());
        assertEquals("Worker-1", events.get(1).getLabel());
        assertEquals("Requestor", events.get(2).getLabel());
    }
    
    @Test
    public void testFutureTrace_disabled() throws InterruptedException {
        final int NUM_FUTURES = 2;
        ArrayList<Future<TracyableFutureResponse>> futuresList = new ArrayList<Future<TracyableFutureResponse>>();
        int i;
       
//        Tracy.setContext();
        Tracy.before("Requestor");
        try {
            for (i=0; i<NUM_FUTURES ; i++)	{
//                System.out.println("Calling future " +i);
                futuresList.add(futureIt(new TracyableFutureRequest(false, new Integer(i))));
            }

            i=0;
            for (Future<TracyableFutureResponse> future : futuresList)	{
//                System.out.println("Polling future");
                TracyableFutureResponse out =  future.get();
                String str = (String) out.getData();
                assertEquals(Integer.toString(i), str);
//                System.out.println("Got future " + str);
                // Merge worker trace into the task handler thread Tracy context
                Tracy.mergeWorkerContext(out.getTracyThreadContext());
                i++;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Tracy.after("Requestor");
        assertEquals(null, Tracy.getEvents());
    }
}
