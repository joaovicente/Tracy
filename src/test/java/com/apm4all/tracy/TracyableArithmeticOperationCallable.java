package com.apm4all.tracy;
import java.util.concurrent.Callable;

public class TracyableArithmeticOperationCallable implements Callable<Integer> {
    static String TRACY_LABEL = "ArithmeticOperation.someWork()";
    private int input1;
    private int input2;
    private int output;
     
    public TracyableArithmeticOperationCallable(int input1, int input2){
        this.input1=input1;
        this.input2=input2;
    }

    private void someWork() throws InterruptedException {
        output = input1 * input2;
        Tracy.annotate("Result", output);;
    }
    
    public Integer call() throws Exception {
        Tracy.before(TRACY_LABEL);
        someWork(); 
        Tracy.after(TRACY_LABEL);
        return new Integer(output);
    }
}