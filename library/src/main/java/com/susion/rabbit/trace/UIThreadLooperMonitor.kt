package com.susion.rabbit.trace

import android.os.Looper
import android.util.Printer
import com.susion.rabbit.reflect.RabbitReflectHelper

/**
 * susionwang at 2019-10-17
 * 监控主线程消息处理
 */
object UIThreadLooperMonitor {

    private val TAG = javaClass.simpleName
    private var mHandleEventListener = ArrayList<LooperHandleEventListener>()
    private var mHookedPrinter: Printer? = null
    private var mOriginPrinter:Printer? = null

    fun startMonitorLooper() {
        if (mHookedPrinter != null) {
            return
        }

        mOriginPrinter = RabbitReflectHelper.getObjectField<Printer>(Looper.getMainLooper(), "mLogging")

        mHookedPrinter = Printer { x ->
            mOriginPrinter?.println(x)

            val dispatch = x[0] == '>' || x[0] == '<'

            if (dispatch) {
                notifyListenerLooperProcessMsg(x[0] == '>')
            }
        }

        Looper.getMainLooper().setMessageLogging(mHookedPrinter)
    }

    fun stopMonitorLooper(){
        if (mOriginPrinter != null){
            Looper.getMainLooper().setMessageLogging(mOriginPrinter)
        }
        mHookedPrinter = null
    }

    fun addLooperHandleEventListener(listener: LooperHandleEventListener){
        mHandleEventListener.add(listener)
    }

    fun removeLooperHandleEventListener(listener: LooperHandleEventListener){
        mHandleEventListener.remove(listener)
    }

    private fun notifyListenerLooperProcessMsg(start: Boolean) {
        mHandleEventListener.forEach {
            if (start){
                it.onStartHandleEvent()
            }else{
                it.onEndHandleEvent()
            }
        }
    }

    interface LooperHandleEventListener {
        fun onStartHandleEvent()
        fun onEndHandleEvent()
    }

}