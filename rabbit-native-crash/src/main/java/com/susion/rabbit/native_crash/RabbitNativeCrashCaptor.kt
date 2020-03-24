package com.susion.rabbit.native_crash

import com.susion.rabbit.base.RabbitLog
import java.lang.Exception

/**
 * susionwang at 2020-03-24
 */
class RabbitNativeCrashCaptor {

    private val TAG = javaClass.simpleName

    fun init() {
        System.loadLibrary("rabbit-crash")
        try {
            val nativeString = nativeInitCaptor("1.0")
            RabbitLog.d(TAG, nativeString)
        } catch (e: Exception) {
            RabbitLog.d(TAG, "调用native方法出错!")
        }
    }

    external fun nativeInitCaptor(version: String): String

}