package com.susion.rabbit.native_crash;

import android.os.Build;

import com.susion.rabbit.base.RabbitLog;

import static com.susion.rabbit.base.RabbitLogKt.TAG_NATIVE;

/**
 * susionwang at 2020-03-27
 */
public class RabbitNativeCrashCaptor {

    public void init() {
        if (Build.VERSION.SDK_INT < 21) {
            RabbitLog.d(TAG_NATIVE, "current system version not support capture native crash!");
            return;
        }

        System.loadLibrary("rabbit-crash");
        try {
            String nativeString = nativeInitCaptor("1.0");
            RabbitLog.d(TAG_NATIVE, "native init success ! -> " + nativeString);
        } catch (Exception e) {
            RabbitLog.d(TAG_NATIVE, "调用native方法出错!");
        }
    }

    public static void onCaptureNativeCrash() {
        RabbitLog.d(TAG_NATIVE, "receive native crash callback in java thread ! -> current thread : " + Thread.currentThread().getName());
    }

    native String nativeInitCaptor(String version);

}
