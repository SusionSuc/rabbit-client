package com.susion.rabbit.tracer

/**
 * susionwang at 2019-11-13
 */
object RabbitTracerLog {

    var listener: EventListener? = null

    @JvmStatic
    fun d(msg: String) {
        listener?.requestPrintDebugLog(msg)
    }

    interface EventListener {
        fun requestPrintDebugLog(msg: String)
    }

}