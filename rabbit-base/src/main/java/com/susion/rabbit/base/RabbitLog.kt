package com.susion.rabbit.base

import android.util.Log

/**
 * susionwang at 2019-10-18
 */

const val TAG_STORAGE = "rabbit-storage-log"
const val TAG_REPORT = "rabbit-report-log"
const val TAG_MONITOR = "rabbit-monitor-log"
const val TAG_MONITOR_UI = "rabbit-monitor-ui-log"
const val TAG_UI = "rabbit-ui-log"
const val TAG_COMMON = "rabbit-log"
const val TAG_NATIVE = "rabbit-native"

object RabbitLog {

    var isEnable: Boolean = true

    @JvmStatic
    fun d(tag: String, logStr: String) {
        if (isEnable) {
            Log.d(tag, logStr)
        }
    }

    @JvmStatic
    fun d(tag: String, suffix: String, logStr: String) {
        if (isEnable) {
            Log.d("$tag$suffix", logStr)
        }
    }

    @JvmStatic
    fun d(logStr: String) {
        if (isEnable) {
            Log.d(TAG_COMMON, logStr)
        }
    }

    @JvmStatic
    fun e(tag: String, logStr: String) {
        if (isEnable) {
            Log.e(tag, logStr)
        }
    }

    @JvmStatic
    fun e(logStr: String) {
        if (isEnable) {
            Log.e(TAG_COMMON, logStr)
        }
    }

    fun getStackTraceString(e: Throwable): String = Log.getStackTraceString(e)

}