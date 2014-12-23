package com.apm4all.tracy;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
* Allows Tracy usage in an ExecutorService <br>
* TracyFutureTask simply extracts worker TracyThreadContext from the TracyCallable
* and merges it into the parent TracyThreadContext when the parent thread retrieves 
* the worker thread result
*/
public class TracyFutureTask<V> extends FutureTask<V>{
    private TracyCallable<V> callable = null;
    public TracyFutureTask(TracyCallable<V> callable) {
        super(callable);
        this.callable = callable;
    }
   
    private void mergeWorkerTracyTheadContext() {
        Tracy.mergeWorkerContext(callable.getTracyWorkerContext());
    }
    
    /**
     * When called from the parent thread to retrieve callable result,
     * it will merge the worker TracyThreadContext into the parent TracyThreadContext
     */
    @Override
    public V get() throws InterruptedException, ExecutionException {
        V v = super.get();
        this.mergeWorkerTracyTheadContext();
        return v;
    }
    
    /**
     * When called from the parent thread to retrieve callable result,
     * it will merge the worker TracyThreadContext into the parent TracyThreadContext
     */
    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        V v = super.get(timeout, unit);
        this.mergeWorkerTracyTheadContext();
        return v;
    }
}
