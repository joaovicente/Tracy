package com.apm4all.tracy;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
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
	
	private Future<String> futureIt(final int i)	{
		Callable<String> worker = null;
		System.out.println("Executing future");
		//TODO: Create ctx = Tracy.createFutureTheadContext();
		worker = new Callable<String>() {
			public String call() throws Exception {
				System.out.println("Executing future (call) " + i);
				String out = Thread.currentThread().getName() + " " + Integer.toString(i);
				return out;
			}
		};
		return executor.submit(worker);
	}
	
	@Test
	public void testFutureTrace() {
		//FIXME: Future test blocking
		final int NUM_FUTURES = 2;
		ArrayList<Future<String>> futuresList = new ArrayList<Future<String>>();
		int i;
		try {
			for (i=0; i<NUM_FUTURES ; i++)	{
				System.out.println("Calling future " +i);
				futuresList.add(futureIt(i));
			}
//			Thread.sleep(1000);
			
			for (Future<String> future : futuresList)	{
				System.out.println("Polling future");
				String out =  future.get();
				System.out.println("Got future " + out);
                //TODO: Create Tracy.mergeFutureThreadContext(ctx);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
