package com.susion.rabbit.tracer;

/**
 * susionwang at 2020-01-02
 */
public class MethodTracer {

    private static long methodStartTime = 0;
    public static final String CLASS_PATH = "com/susion/rabbit/tracer/MethodTracer";
    public static final String METHOD_RECORD_METHOD_START = "recordMethodStart";
    public static final String METHOD_RECORD_METHOD_END = "recordMethodEnd";
    public static final String METHOD_RECORD_METHOD_END_PARAMS = "(Ljava/lang/String;)V";

    public static void recordMethodStart() {
        methodStartTime = System.currentTimeMillis();
    }

    public static void recordMethodEnd(String name) {
        RabbitTracerEventNotifier.methodCostNotifier.methodCost(name, System.currentTimeMillis() - methodStartTime);
    }
}
