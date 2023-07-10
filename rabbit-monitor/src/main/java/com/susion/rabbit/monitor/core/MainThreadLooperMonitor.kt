package com.susion.rabbit.monitor.core

import android.os.Build
import android.os.Looper
import android.os.Message
import android.util.Printer
import androidx.annotation.UiThread
import com.susion.rabbit.free.android.LooperMessageObserver
import com.susion.rabbit.free.android.LooperUtil
import com.susion.rabbit.monitor.utils.RabbitReflectHelper

/**
 * susionwang at 2019-10-17
 * 监控主线程消息处理
 */
object MainThreadLooperMonitor {

    private var mHandleEventListeners = ArrayList<MainLooperMessageDispatchListener>()
    private var mHookedPrinter: Printer? = null
    private var mOriginPrinter: Printer? = null
    @Volatile
    private var isInit = false

    fun init() {
        if (isInit) {
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            LooperUtil.setObserver(object : LooperMessageObserver{
                override fun messageDispatchStarting(): Any {
                    notifyListenerLooperProcessMsg(true, "")
                    return "main_thread_looper_monitor"
                }

                override fun messageDispatched(token: Any?, msg: Message?) {
                    notifyListenerLooperProcessMsg(false, msg.toString())
                }

                override fun dispatchingThrewException(
                    token: Any?,
                    msg: Message?,
                    exception: Exception?
                ) {

                }
            })
        } else {
            mOriginPrinter = RabbitReflectHelper.reflectField<Printer>(Looper.getMainLooper(), "mLogging")

            mHookedPrinter = Printer { msgStr ->

                mOriginPrinter?.println(msgStr)

                val dispatch = msgStr[0] == '>' || msgStr[0] == '<'

                if (dispatch) {
                    notifyListenerLooperProcessMsg(msgStr[0] == '>', msgStr)
                }
            }

            Looper.getMainLooper().setMessageLogging(mHookedPrinter)
        }

        isInit = true
    }

    fun addMessageDispatchListener(listener: MainLooperMessageDispatchListener) {
        mHandleEventListeners.add(listener)
    }

    fun removeMessageDispatchListener(listener: MainLooperMessageDispatchListener) {
        mHandleEventListeners.remove(listener)
    }

    @UiThread
    private fun notifyListenerLooperProcessMsg(start: Boolean, msgStr:String) {
        mHandleEventListeners.forEach {
            if (start) {
                it.onMessageLooperStartHandleMessage(msgStr)
            } else {
                it.onMessageLooperStopHandleMessage(msgStr)
            }
        }
    }

    interface MainLooperMessageDispatchListener {
        fun onMessageLooperStartHandleMessage(msgStr: String)
        fun onMessageLooperStopHandleMessage(msgStr: String)
    }

}