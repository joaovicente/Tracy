package com.apm4all.tracy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.databene.contiperf.PerfTest;
import org.databene.contiperf.Required;
import org.databene.contiperf.junit.ContiPerfRule;
import org.databene.contiperf.timer.RandomTimer;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

public class TracyTestConcurrentPerf {
    ExecutorService executor = Executors.newFixedThreadPool(30);
    Random r = new Random();
    private static final Logger logger = (Logger) LoggerFactory.getLogger(TracyTestFuturePerf.class);
    private static boolean logToFile = true;
    @Rule
    public ContiPerfRule i = new ContiPerfRule();

    private int randomInput()    {
        // Random number below 10000
        return r.nextInt(9999);
    }
    
    @Before
    public void setup()  {
        
    }
    
    @After
    public void tearDown()  {
        //shut down executor service
        executor.shutdown();
        
    }
    
    @PerfTest(threads=10, duration=5000, rampUp = 1000, timer = RandomTimer.class, timerParams = { 1, 10 })
    @Required(percentile99 = 1)
    @Test
    public void testTwoThreadsTracing() {
        List<String> tracyEvents = null;
        int randomInt = randomInput();
        Tracy.setContext(Integer.toString(randomInt), "null", "TracyTestConcurrentPerf");
        Tracy.before("delegator");
       
        TracyableArithmeticOperationCallable op1Callable = new TracyableArithmeticOperationCallable(randomInt, 2);
        TracyableArithmeticOperationCallable op2Callable = new TracyableArithmeticOperationCallable(randomInt, 3);

        FutureTask<Integer> futureTaskOp1 = new TracyFutureTask<Integer>(op1Callable);
        FutureTask<Integer> futureTaskOp2 = new TracyFutureTask<Integer>(op2Callable);

        executor.execute(futureTaskOp1);
        executor.execute(futureTaskOp2);

        while (true) {
            try {
                if(futureTaskOp1.isDone() && futureTaskOp2.isDone()){
                    Tracy.after("delegator");
                    assertEquals("multiplyBy2", new Integer(randomInt*2), (Integer)futureTaskOp1.get());
                    assertEquals("multiplyBy3", new Integer(randomInt*3), (Integer)futureTaskOp2.get());
                    tracyEvents = Tracy.getEventsAsJson();
                    if (logToFile)  {
                        for (String event : tracyEvents)   {
                            logger.info(event.toString());
                        }
                    }
                    assertNotNull(tracyEvents);
                    assertEquals(3, tracyEvents.size());
                    return;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    @PerfTest(threads=10, duration=5000, rampUp = 1000, timer = RandomTimer.class, timerParams = { 1, 10 })
    @Required(percentile99 = 1)
    @Test
    public void testTwoThreadsNotTracing() {
        int randomInt = randomInput();
       
        NonTracyableArithmeticOperationCallable op1Callable = new NonTracyableArithmeticOperationCallable(randomInt, 2);
        NonTracyableArithmeticOperationCallable op2Callable = new NonTracyableArithmeticOperationCallable(randomInt, 3);

        FutureTask<Integer> futureTaskOp1 = new FutureTask<Integer>(op1Callable);
        FutureTask<Integer> futureTaskOp2 = new FutureTask<Integer>(op2Callable);

        executor.execute(futureTaskOp1);
        executor.execute(futureTaskOp2);

        while (true) {
            try {
                if(futureTaskOp1.isDone() && futureTaskOp2.isDone()){
                    assertEquals("multiplyBy2", new Integer(randomInt*2), (Integer)futureTaskOp1.get());
                    assertEquals("multiplyBy3", new Integer(randomInt*3), (Integer)futureTaskOp2.get());
                    return;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
   
    private boolean taskTracingOn(int taskId)    {
        return ( (taskId % 2) == 0 );
    }
    
    
    @PerfTest(threads=10, duration=5000, rampUp = 1000, timer = RandomTimer.class, timerParams = { 1, 10 })
    @Required(percentile99 = 1)
    @Test
    public void testTwoThreadsSampledTracing() {
        boolean thourough = false;
        List<String> tracyEvents = null;
        int randomInt = randomInput();
        if (taskTracingOn(randomInt))   {
            Tracy.setContext(Integer.toString(randomInt), "null", "TracyTestConcurrentPerf");
        }
        else {
            Tracy.clearContext();
        }
        Tracy.before("delegator");
       
        TracyableArithmeticOperationCallable op1Callable = new TracyableArithmeticOperationCallable(randomInt, 2);
        TracyableArithmeticOperationCallable op2Callable = new TracyableArithmeticOperationCallable(randomInt, 3);

        FutureTask<Integer> futureTaskOp1 = new TracyFutureTask<Integer>(op1Callable);
        FutureTask<Integer> futureTaskOp2 = new TracyFutureTask<Integer>(op2Callable);

        executor.execute(futureTaskOp1);
        executor.execute(futureTaskOp2);

        while (true) {
            try {
                if(futureTaskOp1.isDone() && futureTaskOp2.isDone()){
                    Tracy.after("delegator");
                    assertEquals("multiplyBy2", new Integer(randomInt*2), (Integer)futureTaskOp1.get());
                    assertEquals("multiplyBy3", new Integer(randomInt*3), (Integer)futureTaskOp2.get());
                    tracyEvents = Tracy.getEventsAsJson();
                    if (logToFile)  {
                        for (String event : tracyEvents)   {
                            logger.info(event.toString());
                        }
                    }
                    if (thourough ) {
                        List<Map<String, String>> tracyEventsAsMaps = Tracy.getEventsAsMaps();
                        for (Map<String, String> eventAsMap : tracyEventsAsMaps) {
                            assertEquals(new Integer(randomInt).toString(), eventAsMap.get("taskId") );
                        }
                    }
                    
                    assertNotNull(tracyEvents);
                    if (taskTracingOn(randomInt))   {
                        assertEquals(3, tracyEvents.size());
                    }
                    else
                    {
                        assertEquals(0, tracyEvents.size());
                    }
                    return;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}
