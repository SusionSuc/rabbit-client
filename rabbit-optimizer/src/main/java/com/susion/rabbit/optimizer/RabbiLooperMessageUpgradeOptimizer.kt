package com.susion.rabbit.optimizer

import android.content.Context
import android.os.Message
import com.susion.rabbit.base.RabbitLog
import com.susion.rabbit.base.RabbitOptimizerProtocol
import com.susion.rabbit.base.core.MainThreadEnv
import com.susion.rabbit.base.core.MainThreadLooperMonitor

/**
 * wangpengcheng.wpc create at 2023/7/5
 * */
class RabbiLooperMessageUpgradeOptimizer(override var isOpen: Boolean = false) : RabbitOptimizerProtocol {

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

    override fun getOptimizerInfo(): RabbitOptimizerProtocol.OprimizerInfo {
        return RabbitOptimizerProtocol.MESSAGE_PRIORITY_UPGRADE
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