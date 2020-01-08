package com.susion.rabbit.tracer;

import java.util.ArrayList;
import java.util.List;

/**
 * susionwang at 2020-01-07
 * 加载IO调用
 */
public class RabbitScanIoOpHelper {

    public static boolean hasLoad = false;
    public static final String CLASS_PATH = "com/susion/rabbit/tracer/RabbitScanIoOpHelper";
    public static List<String> ioMethods = new ArrayList<>();
    public static final String METHOD_ADD_IO_METHOD = "addIoCall";
    public static final String METHOD_INJECT_IO_CALL = "dynamicInjectIoCall";
    public static final String METHOD_ADD_PKG_IO_PARAMS = "(Ljava/lang/String;)V";

    public static List<String> loadIoCallToDb() {
        ioMethods.clear();
        dynamicInjectIoCall();
        hasLoad = true;
        return ioMethods;
    }

    public static void dynamicInjectIoCall(){

    }

    public static void addIoCall(String method) {
        ioMethods.add(method);
    }

}
