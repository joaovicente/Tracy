package com.apm4all.tracy;
import com.apm4all.tracy.TracyCallable;

//import java.util.concurrent.Callable;

//public class MyCallable implements Callable<String> {
public class CustomCallable extends TracyCallable<String> {
    
    private long waitTime;
     
    public CustomCallable(int timeInMillis){
        this.waitTime=timeInMillis;
    }
    @Override
    public String call() throws Exception {
        super.call();
        Thread.sleep(waitTime);
        //return the thread name executing this callable task
        return Thread.currentThread().getName();
    }
}