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

	//TODO: Create ctx = Tracy.createFutureTheadContext();
	//TODO: Create TracyableFuture interface providing getData() and getTracyThreadContext()
	//TODO: Create Tracy.mergeFutureThreadContext(ctx);
	
	private Future<Long> retrieve()	{
		Callable<Long> worker = null;
		System.out.println("Executing future");
		worker = new Callable<Long>() {
			public Long call() throws Exception {
				System.out.println("Executing future (call)");
				Long someInteger = System.currentTimeMillis();
				return someInteger;
			}
		};
		return executor.submit(worker);
	}
	
	@Test
	public void testFutureTrace() {
		//FIXME: Future test blocking
		final int NUM_FUTURES = 2;
		ArrayList<Future<Long>> futuresList = new ArrayList<Future<Long>>();
		int i;
		try {
			for (i=0; i<NUM_FUTURES ; i++)	{
				System.out.println("Calling future");
				futuresList.add(retrieve());
			}
			Thread.sleep(1000);
			
			for (Future<Long> future : futuresList)	{
				System.out.println("Polling future");
				Long l =  future.get();
				System.out.println("Got " + l);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
