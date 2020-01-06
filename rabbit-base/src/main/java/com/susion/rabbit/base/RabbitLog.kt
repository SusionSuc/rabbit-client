package com.susion.rabbit.base

import android.util.Log

/**
 * susionwang at 2019-10-18
 */

val TAG_STORAGE = "rabbit-storage-log"
val TAG_REPORT = "rabbit-report-log"
val TAG_MONITOR = "rabbit-monitor-log"
val TAG_MONITOR_UI = "rabbit-monitor-ui-log"
val TAG_UI = "rabbit-ui-log"
val COMMON_TAG = "rabbit-log"

object RabbitLog {

    var isEnable: Boolean = true

    fun init(enable: Boolean) {
        isEnable = enable
    }

    fun d(tag: String, logStr: String) {
        if (isEnable) {
            Log.d(tag, logStr)
        }
    }

    fun d(logStr: String) {
        if (isEnable) {
            Log.d(COMMON_TAG, logStr)
        }
    }

    fun e(tag: String, logStr: String) {
        if (isEnable) {
            Log.e(tag, logStr)
        }
    }

    fun e(logStr: String) {
        if (isEnable) {
            Log.e(COMMON_TAG, logStr)
        }
    }

    fun getStackTraceString(e: Throwable) = Log.getStackTraceString(e)

}