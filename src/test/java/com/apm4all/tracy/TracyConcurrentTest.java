package com.apm4all.tracy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

public class TracyConcurrentTest {

    @Test
    public void testTwoThreadsTracing() {
        List<String> tracyEvents = null;
        Tracy.setContext("someTaskId", "null", "TracyTestConcurrent");
        Tracy.before("delegator");
        
        CustomCallable callable1 = new CustomCallable(1000);
        CustomCallable callable2 = new CustomCallable(2000);

        FutureTask<String> futureTask1 = new TracyFutureTask<String>(callable1);
        FutureTask<String> futureTask2 = new TracyFutureTask<String>(callable2);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.execute(futureTask1);
        executor.execute(futureTask2);

        while (true) {
            try {
                if(futureTask1.isDone() && futureTask2.isDone()){
                    Tracy.after("delegator");
                    System.out.println("Done");
                    Thread.sleep(1000);
                    //shut down executor service
                    executor.shutdown();
                    tracyEvents = Tracy.getEventsAsJson();
                    for (String event : tracyEvents)   {
                        System.out.println(event);
                    }
                    assertEquals(3, tracyEvents.size());
                    Tracy.clearContext();
                    return;
                }

                if(!futureTask1.isDone()){
                    //wait indefinitely for future task to complete
                    System.out.println("FutureTask1 output="+futureTask1.get());
                }

                System.out.println("Waiting for FutureTask2 to complete");
                String s = futureTask2.get(200L, TimeUnit.MILLISECONDS);
                if(s !=null){
                    System.out.println("FutureTask2 output="+s);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }catch(TimeoutException e){
                //do nothing
            }
        }
    }
    
    @Test
    public void testTwoThreadsNotTracing() {
        List<String> tracyEvents = null;
//        Tracy.setContext("someTaskId", "null", "parallel");
//        Tracy.before("delegator");
        
        CustomCallable callable1 = new CustomCallable(1000);
        CustomCallable callable2 = new CustomCallable(2000);

        FutureTask<String> futureTask1 = new TracyFutureTask<String>(callable1);
        FutureTask<String> futureTask2 = new TracyFutureTask<String>(callable2);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.execute(futureTask1);
        executor.execute(futureTask2);

        while (true) {
            try {
                if(futureTask1.isDone() && futureTask2.isDone()){
//                    Tracy.after("delegator");
                    System.out.println("Done");
                    Thread.sleep(1000);
                    //shut down executor service
                    executor.shutdown();
                    tracyEvents = Tracy.getEventsAsJson();
                    for (String event : tracyEvents)   {
                        System.out.println(event);
                    }
                    assertEquals(0, tracyEvents.size());
                    return;
                }

                if(!futureTask1.isDone()){
                    //wait indefinitely for future task to complete
                    System.out.println("FutureTask1 output="+futureTask1.get());
                }

                System.out.println("Waiting for FutureTask2 to complete");
                String s = futureTask2.get(200L, TimeUnit.MILLISECONDS);
                if(s !=null){
                    System.out.println("FutureTask2 output="+s);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }catch(TimeoutException e){
                //do nothing
            }
        }
    }
   
    
    private boolean taskTracingOn(int taskId)    {
        return ( (taskId % 2) == 0 );
    }
    
    @Test
    public void testInterleavedTracing() {
        List<String> tracyEvents = null;
        ExecutorService executor = Executors.newFixedThreadPool(2);
       
        // Expect to produce traces for even task ids 2 and 4, and to not produce them for 1 and 3
        for (int taskId = 1 ; taskId < 5 ; taskId++)   {
            if (taskTracingOn(taskId)) {
                Tracy.setContext(Integer.toString(taskId), "null", "TracyTestConcurrentPerf");
            }
            else    {
                Tracy.clearContext();
            }
            Tracy.before("delegator");
            TracyableArithmeticOperationCallable op1Callable = new TracyableArithmeticOperationCallable(taskId, 2);
            TracyableArithmeticOperationCallable op2Callable = new TracyableArithmeticOperationCallable(taskId, 3);

            FutureTask<Integer> futureTaskOp1 = new TracyFutureTask<Integer>(op1Callable);
            FutureTask<Integer> futureTaskOp2 = new TracyFutureTask<Integer>(op2Callable);

            executor.execute(futureTaskOp1);
            executor.execute(futureTaskOp2);

            while (true) {
                try {
                    if(futureTaskOp1.isDone() && futureTaskOp2.isDone()){
                        Tracy.after("delegator");
                        assertEquals("multiplyBy2", new Integer(taskId*2), (Integer)futureTaskOp1.get());
                        assertEquals("multiplyBy3", new Integer(taskId*3), (Integer)futureTaskOp2.get());
                        tracyEvents = Tracy.getEventsAsJson();
                        for (String event : tracyEvents)   {
                            System.out.println(event.toString());
                        }
                        assertNotNull(tracyEvents);
                        List<Map<String, String>> tracyEventsAsMaps = Tracy.getEventsAsMaps();
                        if (taskTracingOn(taskId)) {
                            assertEquals(3, tracyEvents.size());
                            for (Map<String, String> eventAsMap : tracyEventsAsMaps) {
                                assertEquals(new Integer(taskId).toString(), eventAsMap.get("taskId") );
                            }
                        }
                        else    {
                            assertEquals(0, tracyEvents.size());
                        }
                        break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
