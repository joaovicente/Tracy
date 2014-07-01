package com.apm4all.tracy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.databene.contiperf.PerfTest;
import org.databene.contiperf.Required;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

public class TracyTestFuturePerf {
    private static final int NTHREDS = 10;
    private static final Logger logger = (Logger) LoggerFactory.getLogger(TracyTestFuturePerf.class);
    private	ExecutorService executor = Executors.newFixedThreadPool(NTHREDS);
    private static boolean logToFile = false;

    @Rule
    public ContiPerfRule i = new ContiPerfRule();

    private Future<TracyableData> futureIt(final int i)	{
        Callable<TracyableData> worker = null;
        final TracyThreadContext ctx = Tracy.createWorkerTheadContext();
        worker = new Callable<TracyableData>() {
            public TracyableData call() throws Exception {
                TracyableData td = new TracyableData();
                Tracy.setWorkerContext(ctx);
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

    
    @PerfTest(threads=1, duration=5000, rampUp = 100)
    @Required(average = 1, percentile99=1, max = 50)
    @Test
    public void testFutureTracePerf() throws InterruptedException {
        final int NUM_FUTURES = 8;
        ArrayList<Future<TracyableData>> futuresList = new ArrayList<Future<TracyableData>>();
        int i;
        Tracy.setContext();
        Tracy.before("Requestor");
        try {
            for (i=0; i<NUM_FUTURES ; i++)  {
                futuresList.add(futureIt(i));
            }

            for (Future<TracyableData> future : futuresList)    {
                TracyableData out =  future.get();
                @SuppressWarnings("unused")
                String str = (String) out.getData();
                Tracy.mergeWorkerContext(out.getTracyThreadContext());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Tracy.after("Requestor");
        List<TracyEvent> events = Tracy.getEvents();
        if (logToFile)  {
            for (TracyEvent event : events)	{
                logger.info(event.toString());
            }
        }
    }
    
    
    @PerfTest(threads=1, duration=5000, rampUp = 100)
    @Required(average = 1, percentile99=1, max = 50)
    @Test
    public void testFutureTracePerf_disabled() throws InterruptedException {
        final int NUM_FUTURES = 8;
        ArrayList<Future<TracyableData>> futuresList = new ArrayList<Future<TracyableData>>();
        int i;
        // Intentionally don't setup context
        // Tracy.setContext(); 
        Tracy.before("Requestor");
        try {
            for (i=0; i<NUM_FUTURES ; i++)  {
                futuresList.add(futureIt(i));
            }

            for (Future<TracyableData> future : futuresList)    {
                TracyableData out =  future.get();
                @SuppressWarnings("unused")
                String str = (String) out.getData();
                Tracy.mergeWorkerContext(out.getTracyThreadContext());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Tracy.after("Requestor");
        List<TracyEvent> events = Tracy.getEvents();
        if (logToFile)  {
            for (TracyEvent event : events)	{
                logger.info(event.toString());
            }
        }
    }
}
