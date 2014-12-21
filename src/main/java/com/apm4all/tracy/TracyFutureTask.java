package com.apm4all.tracy;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TracyFutureTask<V> extends FutureTask<V>{
    private TracyCallable<V> callable = null;
    public TracyFutureTask(TracyCallable<V> callable) {
        super(callable);
        this.callable = callable;
    }
   
    @Override
    public void done() {
        // Update parent thread Tracy with child Tracy context
//        System.out.println("TracyFutureTask.get() " + callable.getTracyWorkerContext().getPoppedList().toString());
//        System.out.println("TracyFutureTask.get() called from " + Thread.currentThread().getName());
//        Tracy.mergeWorkerContext(callable.getTracyWorkerContext());
    }
    
    @Override
    public V get() throws InterruptedException, ExecutionException {
        V v = super.get();
        // Update parent thread Tracy with child Tracy context
        System.out.println("TracyFutureTask.get() " + callable.getTracyWorkerContext().getPoppedList().toString());
        System.out.println("TracyFutureTask.get() called from " + Thread.currentThread().getName());
        Tracy.mergeWorkerContext(callable.getTracyWorkerContext());
        return v;
    }
    
    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        V v = super.get(timeout, unit);
        // Update parent thread Tracy with child Tracy context
        System.out.println("TracyFutureTask.get() " + callable.getTracyWorkerContext().getPoppedList().toString());
        System.out.println("TracyFutureTask.get() called from " + Thread.currentThread().getName());
        Tracy.mergeWorkerContext(callable.getTracyWorkerContext());
        return v;
    }
}
