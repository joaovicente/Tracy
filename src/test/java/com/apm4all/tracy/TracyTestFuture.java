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
	static final String TASK_ID = "TID-ab1234-x";
	static final String PARENT_OPT_ID = "AAAA";
	static final String L1_LABEL_NAME = "L1 Operation";
	static final String L11_LABEL_NAME = "L11 Operation";
	private	ExecutorService executor = Executors.newFixedThreadPool(NTHREDS);

	//TODO: Create TracyableFuture interface providing getData() and getTracyThreadContext()
	
	private Future<TracyableData> futureIt(final int i)	{
		Callable<TracyableData> worker = null;
		System.out.println("Executing future");
		// Creates Tracy worker thread context to be bound to the worker thread
		final TracyThreadContext ctx = Tracy.createWorkerTheadContext();
		worker = new Callable<TracyableData>() {
			public TracyableData call() throws Exception {
				TracyableData td = new TracyableData();
				// Binds context to worker thread so static Tracy calls can be made (e.g. before() after())
				Tracy.setWorkerContext(ctx);
				System.out.println("Executing future (call) ");
				Tracy.before("Worker-" + Integer.toString(i));
				String out = Thread.currentThread().getName();
				Tracy.after("Worker-" + Integer.toString(i));
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
		ArrayList<Future<TracyableData>> futuresList = new ArrayList<Future<TracyableData>>();
		int i;
		Tracy.setContext();
		Tracy.before("Requestor");
		try {
			for (i=0; i<NUM_FUTURES ; i++)	{
				System.out.println("Calling future " +i);
				futuresList.add(futureIt(i));
			}
//			Thread.sleep(1000);
			
			for (Future<TracyableData> future : futuresList)	{
				System.out.println("Polling future");
				TracyableData out =  future.get();
				String str = (String) out.getData();
				System.out.println("Got future " + str);
                // Merge worker trace into the task handler thread Tracy context
				Tracy.mergeWorkerContext(out.getTracyThreadContext());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		Tracy.after("Requestor");
		List<TracyEvent> events = Tracy.getEvents();
		assertEquals(3, events.size());
	}
	
}
