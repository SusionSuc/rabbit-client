package com.susion.rabbit.base.common

import android.content.Context
import android.os.Looper
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

/**
 * susionwang at 2019-09-24
 */

fun rabbitTimeFormat(time: Long?): String {
    if (time == null) return ""
    return SimpleDateFormat("MM/dd HH:mm:ss:SSS").format(Date(time))
}

fun rabbitSimpleTimeFormat(time: Long?): String {
    if (time == null) return ""
    return SimpleDateFormat("HH:mm:ss").format(Date(time))
}

fun toastInThread(msg: String, context: Context?) {
    if (context == null) return
    object : Thread("DevTools_Toast_Thread") {
        override fun run() {
            Looper.prepare()
            try {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            } catch (e: OutOfMemoryError) {
            }
            Looper.loop()
        }
    }.start()
}