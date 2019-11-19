package com.susion.rabbit.tracer.monitor

import com.susion.rabbit.RabbitLog
import com.susion.rabbit.db.RabbitDbStorageManager
import com.susion.rabbit.tracer.RabbitTracerEventNotifier
import com.susion.rabbit.tracer.entities.RabbitPageSpeedInfo

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
    private var currentPageSpeedInfo: RabbitPageSpeedInfo = RabbitPageSpeedInfo()
    private var pageSpeedMonotorEnable = false

    fun init() {
        monitorActivitySpeed()
    }

    fun startMonitorPageSpeed() {
        pageSpeedMonotorEnable = true
        monitorActivitySpeed()
    }

    fun stopMonitorPageSpeed(){
        pageSpeedMonotorEnable = false
        RabbitTracerEventNotifier.eventNotifier = RabbitTracerEventNotifier.FakeEventListener()
    }

    private fun monitorActivitySpeed() {
        RabbitTracerEventNotifier.eventNotifier = object : RabbitTracerEventNotifier.TracerEvent {

            override fun applicationCreateCostTime(time: Long) {

            }

            override fun activityCreateStart(activity: Any, time: Long) {
                currentPageSpeedInfo = RabbitPageSpeedInfo()
                currentPageSpeedInfo.pageName = activity.javaClass.name
                currentPageSpeedInfo.createStartTime = time
            }

            override fun activityCreateEnd(activity: Any, time: Long) {
                currentPageSpeedInfo.createEndTime = time
            }

            override fun activityDrawFinish(activity: Any, time: Long) {
                currentPageSpeedInfo.drawFinishTime = time
                saveToLocal()
            }

            override fun activityResumeEnd(activity: Any, time: Long) {
                currentPageSpeedInfo.resumeEndTime = time
            }

            //持久化页面测试信息到本地
            private fun saveToLocal() {
                if (!currentPageSpeedInfo.isValid()) return
                currentPageSpeedInfo.time = System.currentTimeMillis()
                RabbitLog.d(TAG, "---> 持久化页面测试信息 : ${currentPageSpeedInfo.pageName}")
                RabbitDbStorageManager.save(currentPageSpeedInfo)
            }

        }
    }

    private fun monitorAppStartSpeed() {
        RabbitTracerEventNotifier.eventNotifier = object : RabbitTracerEventNotifier.TracerEvent {
            override fun applicationCreateCostTime(time: Long) {

            }
        }
    }

    fun isOpen() = pageSpeedMonotorEnable

}