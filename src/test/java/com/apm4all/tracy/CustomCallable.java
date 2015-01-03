package com.apm4all.tracy;
import com.apm4all.tracy.TracyCallable;

public class CustomCallable extends TracyCallable<String> {
    
    private long waitTime;
     
    public CustomCallable(int timeInMillis){
        this.waitTime=timeInMillis;
    }

    private void someWork() throws InterruptedException {
        Thread.sleep(waitTime);
    }
    
    @Override
    public String call() throws Exception {
        super.call(); // This enables
        Tracy.before(Thread.currentThread().getName());
        someWork(); 
        Tracy.after(Thread.currentThread().getName());
//      System.out.println("CustomCallable.call()" + Tracy.getEvents().toString());
        return Thread.currentThread().getName();
    }
}