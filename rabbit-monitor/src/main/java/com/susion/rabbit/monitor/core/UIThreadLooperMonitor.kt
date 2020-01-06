package com.susion.rabbit.monitor.core

import android.os.Looper
import android.util.Printer
import androidx.annotation.UiThread
import com.susion.rabbit.monitor.utils.RabbitReflectHelper

/**
 * susionwang at 2019-10-17
 * 监控主线程消息处理
 */
internal class UIThreadLooperMonitor {

    private var mHandleEventListeners = ArrayList<LooperHandleEventListener>()
    private var mHookedPrinter: Printer? = null
    private var mOriginPrinter: Printer? = null
    var enable = false

    init {
        mOriginPrinter = RabbitReflectHelper.reflectField<Printer>(Looper.getMainLooper(), "mLogging")

        mHookedPrinter = Printer { x ->

            mOriginPrinter?.println(x)

            if (!enable) return@Printer

            val dispatch = x[0] == '>' || x[0] == '<'

            if (dispatch) {
                notifyListenerLooperProcessMsg(x[0] == '>')
            }
        }

        Looper.getMainLooper().setMessageLogging(mHookedPrinter)
    }

    fun addLooperHandleEventListener(listener: LooperHandleEventListener) {
        mHandleEventListeners.add(listener)
    }

    fun removeLooperHandleEventListener(listener: LooperHandleEventListener) {
        mHandleEventListeners.remove(listener)
    }

    @UiThread
    private fun notifyListenerLooperProcessMsg(start: Boolean) {
        mHandleEventListeners.forEach {
            if (start) {
                it.onMessageLooperStartHandleMessage()
            } else {
                it.onMessageLooperStopHandleMessage()
            }
        }
    }

    interface LooperHandleEventListener {
        fun onMessageLooperStartHandleMessage()
        fun onMessageLooperStopHandleMessage()
    }

}