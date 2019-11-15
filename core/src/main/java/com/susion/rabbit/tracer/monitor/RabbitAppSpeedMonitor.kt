package com.susion.rabbit.tracer.monitor

import android.app.Activity
import android.content.Context
import com.susion.rabbit.Rabbit
import com.susion.rabbit.RabbitLog
import com.susion.rabbit.tracer.RabbitTracerEventNotifier
import com.susion.rabbit.utils.RabbitEmptyActivityLifecycleCallbacks

/**
 * susionwang at 2019-11-14
 *
 * 应用速度监控:
 * 1. 应用启动速度
 * 2. 页面启动速度、渲染速度
 * 3. 页面平均帧率
 */
class RabbitAppSpeedMonitor {

    private val TAG = javaClass.simpleName

    fun init() {

        RabbitTracerEventNotifier.eventNotifier = object : RabbitTracerEventNotifier.TracerEvent {

            override fun applicationCreateCostTime(time: Long) {
                recordApplicationCreateCostTime(time)
            }

            override fun activityCreateStart(activity: Any, time: Long) {
                RabbitLog.d(TAG, "${activity.javaClass.simpleName}  activityCreateStart time : $time ms")
            }

            override fun activityCreateEnd(activity: Any, time: Long) {
                RabbitLog.d(TAG, "${activity.javaClass.simpleName}  activityCreateEnd time : $time ms")
            }

            override fun activityDrawFinish(activity: Any, time: Long) {
                RabbitLog.d(TAG, "${activity.javaClass.simpleName}  activityDrawFinish time : $time ms")
            }

        }

        if (Rabbit.geConfig().traceConfig.homeActivityName.isNotEmpty()) {
            openActivityDrawListener()
        }
    }

    private fun openActivityDrawListener() {

    }

    fun recordApplicationCreateCostTime(time: Long) {
        RabbitLog.d(TAG, "application onCreate cost time : $time ms")
    }

    fun monitorAcSpeed() {
        Rabbit.application?.registerActivityLifecycleCallbacks(object :
            RabbitEmptyActivityLifecycleCallbacks() {
            override fun onActivityResumed(activity: Activity?) {
                super.onActivityResumed(activity)

            }
        })
    }


    fun recordActivityDrawFinish(context: Context, time: Long) {

    }

}