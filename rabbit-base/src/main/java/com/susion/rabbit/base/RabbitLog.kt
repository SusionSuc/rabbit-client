package com.susion.rabbit.base

import android.util.Log

/**
 * susionwang at 2019-10-18
 */
object RabbitLog {

    private val DEFAULT_TAG = "rabbit"
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
            Log.d(DEFAULT_TAG, logStr)
        }
    }

    fun e(tag: String, logStr: String) {
        if (isEnable) {
            Log.e(tag, logStr)
        }
    }

    fun e(logStr: String) {
        if (isEnable) {
            Log.e(DEFAULT_TAG, logStr)
        }
    }
}