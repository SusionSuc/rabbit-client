package com.susion.rabbit.tracer;

/**
 * susionwang at 2019-11-13
 */
public class AppStartTracer {

    private static Long appStartTime = 0L;
    public static final String CLASS_PATH = "com/susion/rabbit/tracer/AppStartTracer";
    public static final String METHOD_RECORD_APP_START_TIME = "recordAppStartTime";

    public static void recordAppStartTime() {
        appStartTime = System.currentTimeMillis();
        RabbitTracerLog.d("appStartTime : " + appStartTime);
    }

}
