package com.apm4all.tracy;
import java.util.concurrent.Callable;

public class NonTracyableArithmeticOperationCallable implements Callable<Integer> {
    private int input1;
    private int input2;
    private int output;
     
    public NonTracyableArithmeticOperationCallable(int input1, int input2){
        this.input1=input1;
        this.input2=input2;
    }

    private void someWork() throws InterruptedException {
        output = input1 * input2;
    }
    
    public Integer call() throws Exception {
        someWork(); 
        return new Integer(output);
    }
}