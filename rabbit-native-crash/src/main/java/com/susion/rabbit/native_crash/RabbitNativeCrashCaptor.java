package com.susion.rabbit.native_crash;

import com.susion.rabbit.base.RabbitLog;

import static com.susion.rabbit.base.RabbitLogKt.TAG_NATIVE;

/**
 * susionwang at 2020-03-27
 */
public class RabbitNativeCrashCaptor {

    public void init() {
        System.loadLibrary("rabbit-crash");
        try {
            String nativeString = nativeInitCaptor("1.0");
            RabbitLog.d(TAG_NATIVE, nativeString);
        } catch (Exception e) {
            RabbitLog.d(TAG_NATIVE, "调用native方法出错!");
        }
    }

    public static void onCaptureNativeCrash() {
        RabbitLog.d(TAG_NATIVE, "接收到Native回调!");

    }

    native String nativeInitCaptor(String version);

}
