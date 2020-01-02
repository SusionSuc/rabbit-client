package com.susion.rabbit.tracer;

/**
 * susionwang at 2019-11-13
 *
 * 应用启动统计
 *
 */
public class AppStartTracer {

    public static long appAttachBaseContextTime = 0;
    public static final String CLASS_PATH = "com/susion/rabbit/tracer/AppStartTracer";
    public static final String METHOD_RECORD_APPLICATION_CREATE_START = "recordApplicationCreateStart";
    public static final String METHOD_RECORD_APPLICATION_CREATE_END = "recordApplicationCreateEnd";

    public static void recordApplicationCreateStart() {
        appAttachBaseContextTime =System.currentTimeMillis();
    }

    public static void recordApplicationCreateEnd() {
        RabbitTracerEventNotifier.appSpeedNotifier.applicationCreateTime(appAttachBaseContextTime, System.currentTimeMillis());
    }

}
