package com.susion.rabbit.trace.core

import android.os.Looper
import android.util.Printer
import com.susion.rabbit.reflect.RabbitReflectHelper

/**
 * susionwang at 2019-10-17
 * 监控主线程消息处理
 */
object UIThreadLooperMonitor {

    private val TAG = javaClass.simpleName
    private var mHandleEventListeners = ArrayList<LooperHandleEventListener>()
    private var mHookedPrinter: Printer? = null
    private var mOriginPrinter: Printer? = null
    private var enableStatus = false

    fun init(){
        mOriginPrinter = RabbitReflectHelper.reflectField<Printer>(Looper.getMainLooper(), "mLogging")

        mHookedPrinter = Printer { x ->

            mOriginPrinter?.println(x)

            if (!enableStatus) return@Printer

            val dispatch = x[0] == '>' || x[0] == '<'

            if (dispatch) {
                notifyListenerLooperProcessMsg(x[0] == '>')
            }
        }

        Looper.getMainLooper().setMessageLogging(mHookedPrinter)
    }

    fun setEnableStatus(enable:Boolean){
        enableStatus = enable
    }

    fun isEnable() = enableStatus

    fun addLooperHandleEventListener(listener: LooperHandleEventListener) {
        mHandleEventListeners.add(listener)
    }

    fun removeLooperHandleEventListener(listener: LooperHandleEventListener) {
        mHandleEventListeners.remove(listener)
    }

    fun listenerSize() = mHandleEventListeners.size

    private fun notifyListenerLooperProcessMsg(start: Boolean) {
        mHandleEventListeners.forEach {
            if (start) {
                it.onStartHandleEvent()
            } else {
                it.onEndHandleEvent()
            }
        }
    }

    fun containEventListener(listener: LooperHandleEventListener): Boolean {
        return mHandleEventListeners.contains(listener)
    }

    interface LooperHandleEventListener {
        fun onStartHandleEvent()
        fun onEndHandleEvent()
    }

}