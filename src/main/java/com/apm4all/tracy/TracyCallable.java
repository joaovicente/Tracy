package com.apm4all.tracy;
import java.util.concurrent.Callable;

public class TracyCallable<V> implements Callable<V>{
    private TracyThreadContext tracyWorkerContext = null;
    
    public TracyCallable(){
        // TODO: setup context only if parent thread is Tracing
        if (Tracy.isEnabled())    {
            TracyThreadContext parentContext = Tracy.getTracyThreadContext();
            this.tracyWorkerContext = new TracyThreadContext(
                    parentContext.getTaskId(),
                    parentContext.getOptId(),
                    parentContext.getComponent());
        }
    }

    
    public V call() throws Exception {
        if (Tracy.isEnabled(this.tracyWorkerContext))   {
            Tracy.setContext(this.tracyWorkerContext);
        }
        else    {
            Tracy.clearContext();
        }
        return null;
    }
    
    public TracyThreadContext getTracyWorkerContext() {
        return tracyWorkerContext;
    }
}
