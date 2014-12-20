package com.apm4all.tracy;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;


public class TracyFutureTask<V> extends FutureTask<V>{
    private String tracyContext = null;

    public TracyFutureTask(Callable<V> callable) {
        super(callable);
    }
    
    @Override
    protected void set(V v) {
        super.set(v);
        // TODO: Store worker Tracy context
        tracyContext = new Long(System.currentTimeMillis()).toString();
    }
    
    @Override
    public V get() throws InterruptedException, ExecutionException {
        // TODO: Update parent thread Tracy with child Tracy context
        return super.get();
    }
}
