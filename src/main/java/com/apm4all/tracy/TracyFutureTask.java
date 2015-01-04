/*
 * Copyright 2014 Joao Vicente
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
