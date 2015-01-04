package com.apm4all.tracy;
import java.util.concurrent.Callable;

public class CustomCallable implements Callable<String> {
    
    private long waitTime;
     
    public CustomCallable(int timeInMillis){
        this.waitTime=timeInMillis;
    }

    private void someWork() throws InterruptedException {
        Thread.sleep(waitTime);
    }
    
    public String call() throws Exception {
        Tracy.before(Thread.currentThread().getName());
        someWork(); 
        Tracy.after(Thread.currentThread().getName());
//      System.out.println("CustomCallable.call()" + Tracy.getEvents().toString());
        return Thread.currentThread().getName();
    }
}