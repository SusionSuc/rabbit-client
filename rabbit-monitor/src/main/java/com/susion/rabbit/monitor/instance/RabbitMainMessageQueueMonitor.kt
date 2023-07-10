package com.susion.rabbit.monitor.instance

import android.content.Context
import android.os.Message
import com.susion.rabbit.base.RabbitLog
import com.susion.rabbit.base.RabbitMonitorProtocol
import com.susion.rabbit.base.core.MainThreadEnv
import com.susion.rabbit.base.core.MainThreadLooperMonitor


/**
 * wangpengcheng.wpc create at 2023/6/30
 * example for:
 * 1. activity pause 消息提优
 * 2. 同步屏障消息提优，加速异步消息处理，比如doFrame
 * */
class RabbitMainMessageQueueMonitor(override var isOpen: Boolean = false) : RabbitMonitorProtocol {

    private val LIFECYCLE_MSG_WHAT = 159
    private val TARGET_PAUSE = 4
    private val TARGET_RESUME = 3

    private val TAG = this.javaClass.simpleName
    private var startTime = 0L
    private val mainThreadEnv = MainThreadEnv()

    private val msgDispatchListener by lazy {
        object : MainThreadLooperMonitor.MainLooperMessageDispatchListener {
            override fun onMessageLooperStartHandleMessage(msgStr: String) {
                startTime = System.currentTimeMillis()
            }

            override fun onMessageLooperStopHandleMessage(msgStr: String) {
                movePauseMsgToQueueFront()
//                RabbitLog.d(TAG, mainThreadEnv.messageQueueListStr())
            }
        }
    }

    override fun open(context: Context) {
        MainThreadLooperMonitor.addMessageDispatchListener(msgDispatchListener)
        mainThreadEnv.init()
        RabbitLog.d(TAG, "USE VSYNC : ${mainThreadEnv.useVsync()}")
    }

    override fun close() {
        MainThreadLooperMonitor.removeMessageDispatchListener(msgDispatchListener)
    }

    override fun getMonitorInfo(): RabbitMonitorProtocol.MonitorInfo {
        return RabbitMonitorProtocol.MESSAGE_QUEUE
    }

    /**
     * 将Pause消息移动到队列头部
     * */
    private fun movePauseMsgToQueueFront() {
        try {
            var message = mainThreadEnv.msgQueueFirstMessage
            while (message != null) {
                if (message.what == LIFECYCLE_MSG_WHAT) {
                    val obj = message.obj ?: continue
                    val targetState = mainThreadEnv.getLifecycleTargetState(obj)
                    if (targetState == TARGET_PAUSE || targetState == TARGET_RESUME) {
                        sendLifecycleMsgToQueueFront(message, targetState)
                    }
                }
                message = mainThreadEnv.getNextMessage(message)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun sendLifecycleMsgToQueueFront(message: Message, targetState: Int) {
        val newMessage = Message.obtain(message)
        mainThreadEnv.activityThreadHandler.removeMessages(message.what)
        mainThreadEnv.activityThreadHandler.sendMessageAtFrontOfQueue(newMessage)
        RabbitLog.d(TAG, "lifecycle target $targetState movePauseMsgToQueueFront success!")
    }

    /**
     * do frame 丢帧检测
     * */
    private class DoFrameWatchThread(name: String, val maxDropFrame: Int) : Thread(name) {

        private var mMaxDropFrame = 0.0
        private var mCheckInterval: Long = 0

        @Volatile
        private var isStop = false

        override fun run() {
            while (!isStop) {
                try {
                    sleep(mCheckInterval)
                    checkMayNR()
//                    checkMayDropFrame()
                } catch (e: Exception) {
                    isStop = true
                }
            }
        }

        private fun checkMayNR() {
//            val current = System.nanoTime()
//            if (mNRCheckStartTime > 0 && mNRTimeConfig > 0 && current - mNRCheckStartTime > mNRTimeConfig) {
//                if (!mHavePendingRNCheck) {
//                    mHavePendingRNCheck = true
//                    // 主线程handler处于长时间不响应的状态
//                    val msg: Message = Message.obtain(getMainHandler(), mNRNotify)
//                    getMainHandler().sendMessageAtFrontOfQueue(msg)
//                    mLogger.e(com.bytedance.fwatchdog.FWatchDog.TAG, "ooooooo! discover a ANR!")
//                }
//            }
        }


    }

}