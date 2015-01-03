package com.apm4all.tracy;

import static org.junit.Assert.assertEquals;
import java.util.List;
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
}
