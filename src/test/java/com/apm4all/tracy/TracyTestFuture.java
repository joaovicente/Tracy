package com.apm4all.tracy;

import static org.junit.Assert.*;

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
	private ExecutorService executorService = Executors.newFixedThreadPool(NTHREDS);

	//TODO: Create ctx = Tracy.createFutureTheadContext();
	//TODO: Create TracyableFuture interface providing getData() and getTracyThreadContext()
	//TODO: Create Tracy.mergeFutureThreadContext(ctx);
	
	private Future<Long> retrieve()	{
		Callable<Long> task = null;
		task = new Callable<Long>() {
			
			public Long call() throws Exception {
				Long someInteger = System.currentTimeMillis();
				return someInteger;
			}
		};
		return executorService.submit(task);
	}
	
	@Test
	public void testFutureTrace() {
		//FIXME: Future test blocking
//		final int NUM_FUTURES = 2;
//		int i;
//		try {
//			CompletionService<Long> completionService = 
//			    	new ExecutorCompletionService<Long>(executorService);
//			
//			for (i=0; i<NUM_FUTURES ; i++)	{
//				Future<Long> longFuture = retrieve();
//			}
//			
//			for (i=0; i<NUM_FUTURES ; i++)	{
//				Long Long = completionService.take().get();
//			}
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
	}
	
}
