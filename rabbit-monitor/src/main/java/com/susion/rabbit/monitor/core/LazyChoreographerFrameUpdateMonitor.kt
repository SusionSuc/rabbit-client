package com.susion.rabbit.monitor.core

import android.view.Choreographer
import com.susion.rabbit.base.RabbitLog
import com.susion.rabbit.base.TAG_MONITOR
import com.susion.rabbit.monitor.utils.RabbitReflectHelper
import java.lang.reflect.Method

/**
 *  hook [Choreographer], 监控 doFrame() 回调。
 *
 * 向[Choreographer]中插入3种callback, 监控一帧不同类型的事件运行时间
 */
internal open class LazyChoreographerFrameUpdateMonitor {

    //主线程消息循环
    private val mainThreadLooperMonitor by lazy {
        UIThreadLooperMonitor()
    }
    private val looperListener = object :
        UIThreadLooperMonitor.LooperHandleEventListener {
        override fun onMessageLooperStartHandleMessage() {
            startMonitorChoreographerDoFrame()
        }

        override fun onMessageLooperStopHandleMessage() {
            endMonitorChoreographerDoFrame()
        }
    }

    // Choreographer's callback
    val CALLBACK_INPUT = 0  //input event
    val CALLBACK_ANIMATION = 1 // animation event
    val CALLBACK_TRAVERSAL = 2  //traversal event
    val CALLBACK_COMMIT = 3  //traversal event

    // Choreographer's field or method
    private var addTraversalQueue: Method? = null
    private var addInputQueue: Method? = null
    private var addAnimationQueue: Method? = null
    private var commitQueque: Method? = null
    private var choreographer: Choreographer? = null
    private var callbackQueues: Array<Any>? = null
    private var callbackQueueLock: Any? = null
    private val METHOD_ADD_CALLBACK = "addCallbackLocked"

    // Choreographer中每种callback执行的时间
    private val frameListeners = ArrayList<FrameUpdateListener>()
    private var inputEventCostTimeNs = 0L
    private var animationEventCostTimeNs = 0L
    private var traversalEventCostTimeNs = 0L
    private var actualExecuteDoFrame = false
    private var insertMonitorRunnable = false

    private var oneFrameStartTime = 0L
    private var oneFrameEndTime = 0L
    private var startMonitorDoFrame = false

    fun init() {
        choreographer = Choreographer.getInstance()
        callbackQueueLock = RabbitReflectHelper.reflectField<Any>(choreographer, "mLock")
        callbackQueues =
            RabbitReflectHelper.reflectField<Array<Any>>(choreographer, "mCallbackQueues")

        if (callbackQueues != null) {
            addInputQueue = RabbitReflectHelper.reflectMethod(
                callbackQueues!![CALLBACK_INPUT],
                METHOD_ADD_CALLBACK,
                Long::class.java,
                Any::class.java,
                Any::class.java
            )
            addAnimationQueue = RabbitReflectHelper.reflectMethod(
                callbackQueues!![CALLBACK_ANIMATION],
                METHOD_ADD_CALLBACK,
                Long::class.java,
                Any::class.java,
                Any::class.java
            )
            addTraversalQueue = RabbitReflectHelper.reflectMethod(
                callbackQueues!![CALLBACK_TRAVERSAL],
                METHOD_ADD_CALLBACK,
                Long::class.java,
                Any::class.java,
                Any::class.java
            )
            commitQueque = RabbitReflectHelper.reflectMethod(
                callbackQueues!![CALLBACK_TRAVERSAL],
                METHOD_ADD_CALLBACK,
                Long::class.java,
                Any::class.java,
                Any::class.java
            )
        }
    }

    fun startMonitor(){
        mainThreadLooperMonitor.enable = true
        mainThreadLooperMonitor.addLooperHandleEventListener(looperListener)
    }

    fun stopMonitor(){
        mainThreadLooperMonitor.enable = false
        mainThreadLooperMonitor.removeLooperHandleEventListener(looperListener)
    }

    fun startMonitorChoreographerDoFrame() {
        startMonitorDoFrame = true
        actualExecuteDoFrame = false
        inputEventCostTimeNs = 0
        animationEventCostTimeNs = 0
        traversalEventCostTimeNs = 0
        oneFrameStartTime = System.nanoTime()
        insertCallbackToInputQueue()
    }

    /**
     * 真正计算一帧时间的采集条件 : 主线程消息循环 && 执行了 Choreographer.doFrame()
     *
     * actualExecuteDoFrame : 一次消息处理可能并没有执行渲染事件
     * */
    fun endMonitorChoreographerDoFrame() {
        if (!startMonitorDoFrame || !actualExecuteDoFrame) return

        startMonitorDoFrame = false
        oneFrameEndTime = System.nanoTime()
        traversalEventCostTimeNs = System.nanoTime() - traversalEventCostTimeNs

        val oneFrameCostNs = oneFrameEndTime - oneFrameStartTime
        val inputCost = inputEventCostTimeNs
        val animationCost = animationEventCostTimeNs
        val traversalCost = traversalEventCostTimeNs

        frameListeners.forEach {
            it.doFrame(
                oneFrameCostNs,
                inputCost,
                animationCost,
                traversalCost
            )
        }
    }

    /**
     * 这里添加的callback， 如果不执行 Choreographer.doFrame(), 是不会被执行的。
     * */
    private fun insertCallbackToInputQueue() {
        //已经插过一遍 [时间监控消息] 了， 不继续插入
        if (insertMonitorRunnable) {
            return
        }
        insertMonitorRunnable = true
        addCallbackToQueue(CALLBACK_INPUT, Runnable {
            insertMonitorRunnable = false
            actualExecuteDoFrame = true
            inputEventCostTimeNs = System.nanoTime()
        })

        addCallbackToQueue(CALLBACK_ANIMATION, Runnable {
            inputEventCostTimeNs = System.nanoTime() - inputEventCostTimeNs
            animationEventCostTimeNs = System.nanoTime()
        })

        addCallbackToQueue(CALLBACK_TRAVERSAL, Runnable {
            animationEventCostTimeNs = System.nanoTime() - animationEventCostTimeNs
            traversalEventCostTimeNs = System.nanoTime()
        })
    }

    /**
     * 把事件插入到队列的最前面
     * */
    @Synchronized
    private fun addCallbackToQueue(type: Int, callback: Runnable, isHead:Boolean = false) {
        if (callbackQueueLock == null || callbackQueues == null) return
        try {
            synchronized(callbackQueueLock!!) {
                var method: Method? = null

                when (type) {
                    CALLBACK_INPUT -> method = addInputQueue
                    CALLBACK_ANIMATION -> method = addAnimationQueue
                    CALLBACK_TRAVERSAL -> method = addTraversalQueue
                    CALLBACK_COMMIT -> method = commitQueque
                }

                method?.invoke(callbackQueues!![type], -1, callback, null)
            }
        } catch (e: Exception) {
            RabbitLog.e(TAG_MONITOR, e.toString())
        }
    }

    fun addCallbackToCommitQueue(callback: Runnable){
        addCallbackToQueue(CALLBACK_COMMIT, callback)
    }

    fun addFrameUpdateListener(listener: FrameUpdateListener) {
        frameListeners.add(listener)
    }

    fun removeFrameUpdateListener(listener: FrameUpdateListener) {
        frameListeners.remove(listener)
    }

    fun getCurrentListenerSize() = frameListeners.size

    interface FrameUpdateListener {
        fun doFrame(
            frameCostNs: Long,
            inputCostNs: Long,
            animationCostNs: Long,
            traversalCostNs: Long
        )
    }

}