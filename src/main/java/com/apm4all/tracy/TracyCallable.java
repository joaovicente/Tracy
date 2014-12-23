package com.apm4all.tracy;
import java.util.concurrent.Callable;

public abstract class TracyCallable<V> implements Callable<V>{
    private TracyThreadContext tracyWorkerContext = null;
   
    /**
     * A Callable base class used to hold tracyThreadContext for the worker thread <br>
     * When called from the parent thread it will: <br>
     * 1. Gather TracyThreadContext from the parent thread and <br>
     * 2. Create TracyThreadContext to be used within the worker thread 
     * (if parent thread had a non-null TracyThreadContext)
     */
    public TracyCallable(){
        // Setup context only if parent thread is Tracing
        if (Tracy.isEnabled())    {
            TracyThreadContext parentContext = Tracy.getTracyThreadContext();
            this.tracyWorkerContext = new TracyThreadContext(
                    parentContext.getTaskId(),
                    parentContext.getOptId(),
                    parentContext.getComponent());
        }
    }

    /**
     * When called from the worker thread, will inject the worker TracyThreadContext 
     * created in the constructor <br>
     * NOTE: super.call() must called as the first line of the the TracyCallable derived class 
     * call() method 
     */
    public V call() throws Exception {
        if (Tracy.isEnabled(this.tracyWorkerContext))   {
            Tracy.setContext(this.tracyWorkerContext);
        }
        else    {
            Tracy.clearContext();
        }
        return null;
    }
    
    /**
     * Used to collect the worker TracyThreadContext (when it is time to 
     * merge it into the parent TracyThreadContext)
     */
    public TracyThreadContext getTracyWorkerContext() {
        return tracyWorkerContext;
    }
}
