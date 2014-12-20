package com.apm4all.tracy;
import java.util.concurrent.Callable;

public class TracyCallable<V> implements Callable<V>{
    boolean tracyOn = false;
    String taskId;
    String parentOptId;
    String component;
    
    public TracyCallable(){
        // TODO: setup context only if parent thread is Tracing
        this.tracyOn = true;
        this.taskId = Thread.currentThread().getName() + "-" 
                +  Long.toString(System.currentTimeMillis());
        this.parentOptId= Thread.currentThread().getName() + "-" 
                +  Long.toString(System.currentTimeMillis());
        this.component = "whateverComponent";
    }
    
    public V call() throws Exception {
        if (this.tracyOn)   {
            // TODO: Create new worker thread context 
            // Tracy.setContext(this.taskId, this.parentOptId, this.component)
        }
        else    {
            // TODO: Ensure worker does not have Tracy context
        }
        return null;
    }
}
