package com.susion.rabbit.trace.frame

import android.os.SystemClock
import android.util.Log
import android.view.Choreographer
import com.susion.rabbit.RabbitLog
import com.susion.rabbit.reflect.RabbitReflectHelper
import com.susion.rabbit.trace.core.UIThreadLooperMonitor
import com.susion.rabbit.utils.RabbitTimeUtils
import java.lang.reflect.Method

/**
 * susionwang at 2019-10-18
 *
 * hook [Choreographer], 监控 doFrame() 回调。
 *
 * 向[Choreographer]中插入3种callback, 监控一帧不同类型的事件运行时间
 */
class FrameTracer : UIThreadLooperMonitor.LooperHandleEventListener {

    private val TAG = javaClass.simpleName

    /**
     * callback type copy from  Choreographer
     * */

    // Callback type: Input callback.  Runs first.
    val CALLBACK_INPUT = 0

    // Callback type: Animation callback.  Runs before traversals.
    val CALLBACK_ANIMATION = 1

    //Callback type: Traversal callback.  Handles layout and draw.  Runs
    //after all other asynchronous messages have been handled.
    val CALLBACK_TRAVERSAL = 2

    // Choreographer's field or method
    private var addTraversalQueue: Method? = null
    private var addInputQueue: Method? = null
    private var addAnimationQueue: Method? = null
    private var choreographer: Choreographer? = null
    private var callbackQueues: Array<Any>? = null
    private var callbackQueueLock: Any? = null
    private val METHOD_ADD_CALLBACK = "addCallbackLocked"

    // Choreographer中每种callback执行的时间
    private val queueCallbackCost = longArrayOf(0,0,0)
    private val frameListeners = ArrayList<FrameUpdateListener>()

    private var oneFrameStartTime = 0L
    private var oneFrameEndTime = 0L

    fun init(){
        choreographer = Choreographer.getInstance()
        callbackQueueLock = RabbitReflectHelper.reflectField<Any>(choreographer, "mLock")
        callbackQueues = RabbitReflectHelper.reflectField<Array<Any>>(choreographer, "mCallbackQueues")

        if (callbackQueues != null){
            addInputQueue = RabbitReflectHelper.reflectMethod(callbackQueues!![CALLBACK_INPUT],
                METHOD_ADD_CALLBACK,
                Long::class.java,
                Any::class.java,
                Any::class.java
            )
            addAnimationQueue =  RabbitReflectHelper.reflectMethod(
                callbackQueues!![CALLBACK_ANIMATION],
                METHOD_ADD_CALLBACK,
                Long::class.java,
                Any::class.java,
                Any::class.java
            )
            addTraversalQueue =  RabbitReflectHelper.reflectMethod(
                callbackQueues!![CALLBACK_TRAVERSAL],
                METHOD_ADD_CALLBACK,
                Long::class.java,
                Any::class.java,
                Any::class.java
            )
        }
    }

    //主线程在处理消息的时候，才做 doFrame 监控
    override fun onStartHandleEvent() {
        startMonitorOneFrame()
    }

    override fun onEndHandleEvent() {
        endMonitorOneFrame()
    }

    private fun startMonitorOneFrame() {
        oneFrameStartTime = SystemClock.uptimeMillis()
        insertCallbackToInputQueue()
    }

    private fun endMonitorOneFrame() {
        queueCallbackCost[CALLBACK_TRAVERSAL] = System.nanoTime() - queueCallbackCost[CALLBACK_TRAVERSAL]

        oneFrameEndTime = SystemClock.uptimeMillis()

        val oneFrameCostMs = oneFrameEndTime - oneFrameStartTime

        //错误的数据
        if (oneFrameCostMs > 1000) return

        val inputCost =  queueCallbackCost[CALLBACK_INPUT]
        val animationCost =queueCallbackCost[CALLBACK_ANIMATION]
        val traversalCost =  queueCallbackCost[CALLBACK_TRAVERSAL]

        RabbitLog.d(TAG, "oneFrameCost: ${oneFrameEndTime - oneFrameStartTime}; inputCost : $inputCost ; animationCost: $animationCost ; traversalCost: $traversalCost")
        frameListeners.forEach {
            it.doFrame(oneFrameStartTime, oneFrameEndTime, inputCost, animationCost, traversalCost)
        }
    }

    private fun insertCallbackToInputQueue() {
        addCallbackToHead(CALLBACK_INPUT, Runnable {
            queueCallbackCost[CALLBACK_INPUT] = System.nanoTime()

            addCallbackToHead(CALLBACK_ANIMATION, Runnable {
                queueCallbackCost[CALLBACK_INPUT] = System.nanoTime() - queueCallbackCost[CALLBACK_INPUT]
                queueCallbackCost[CALLBACK_ANIMATION] = System.nanoTime()
            })

            addCallbackToHead(CALLBACK_TRAVERSAL, Runnable {
                queueCallbackCost[CALLBACK_ANIMATION] = System.nanoTime() - queueCallbackCost[CALLBACK_ANIMATION]
                queueCallbackCost[CALLBACK_TRAVERSAL] = System.nanoTime()
            })
        })
    }

    /**
     * 把事件插入到队列的最前面
     * */
    @Synchronized
    private fun addCallbackToHead(type: Int, callback: Runnable) {
        if (callbackQueueLock == null || callbackQueues == null)return
        try {
            synchronized(callbackQueueLock!!) {
                var method: Method? = null

                when (type) {
                    CALLBACK_INPUT -> method = addInputQueue
                    CALLBACK_ANIMATION -> method = addAnimationQueue
                    CALLBACK_TRAVERSAL -> method = addTraversalQueue
                }

                method?.invoke(callbackQueues!![type],  -1, callback, null)
            }
        } catch (e: Exception) {
            RabbitLog.e(TAG, e.toString())
        }
    }

    fun addFrameUpdateListener(listener: FrameUpdateListener){
        frameListeners.add(listener)
    }

    fun removeFrameUpdateListener(listener: FrameUpdateListener){
        frameListeners.remove(listener)
    }

    fun containFrameUpdateListener(listener: FrameUpdateListener):Boolean{
        return frameListeners.contains(listener)
    }

    interface FrameUpdateListener{
        fun doFrame(startMs:Long, endMs:Long, inputCostNs:Long, animationCostNs:Long, traversalCostNs:Long)
    }

}