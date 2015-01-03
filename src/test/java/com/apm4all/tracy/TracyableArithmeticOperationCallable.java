package com.apm4all.tracy;
import com.apm4all.tracy.TracyCallable;

public class TracyableArithmeticOperationCallable extends TracyCallable<Integer> {
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
    
    @Override
    public Integer call() throws Exception {
        super.call(); // Inject caller thread context info into worker thread
        Tracy.before(TRACY_LABEL);
        someWork(); 
        Tracy.after(TRACY_LABEL);
        return new Integer(output);
    }
}