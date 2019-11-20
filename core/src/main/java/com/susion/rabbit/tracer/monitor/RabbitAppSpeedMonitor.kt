package com.susion.rabbit.tracer.monitor

import com.susion.rabbit.Rabbit
import com.susion.rabbit.RabbitLog
import com.susion.rabbit.db.RabbitDbStorageManager
import com.susion.rabbit.tracer.RabbitTracerEventNotifier
import com.susion.rabbit.tracer.entities.RabbitAppStartSpeedInfo
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

    //避免重复统计测试时间
    private var appSpeedCanRecord = false
    private var pageSpeedCanRecord = false

    private var pageSpeedInfo: RabbitPageSpeedInfo = RabbitPageSpeedInfo()
    private var appSpeedInfo:RabbitAppStartSpeedInfo = RabbitAppStartSpeedInfo()

    //第一个对用户有效的页面 【闪屏页 or 首页】
    private var entryActivityName = ""

    //启动页面测试记录
    private var pageSpeedMonitorEnable = false

    fun init() {
        monitorActivitySpeed()
        entryActivityName = Rabbit.geConfig().traceConfig.homeActivityName
    }

    fun startMonitorPageSpeed() {
        pageSpeedMonitorEnable = true
        monitorActivitySpeed()
    }

    fun stopMonitorPageSpeed(){
        pageSpeedMonitorEnable = false
        RabbitTracerEventNotifier.eventNotifier = RabbitTracerEventNotifier.FakeEventListener()
    }

    private fun monitorActivitySpeed() {
        RabbitTracerEventNotifier.eventNotifier = object : RabbitTracerEventNotifier.TracerEvent {

            override fun applicationCreateTime(attachBaseContextTime: Long, createEndTime: Long) {
                appSpeedCanRecord = true
                appSpeedInfo.createStartTime = attachBaseContextTime
                appSpeedInfo.createEndTime = createEndTime
            }

            override fun activityCreateStart(activity: Any, time: Long) {
                pageSpeedCanRecord =true
                pageSpeedInfo = RabbitPageSpeedInfo()
                pageSpeedInfo.pageName = activity.javaClass.name
                pageSpeedInfo.createStartTime = time
            }

            override fun activityCreateEnd(activity: Any, time: Long) {
                pageSpeedInfo.createEndTime = time
            }

            override fun activityResumeEnd(activity: Any, time: Long) {
                pageSpeedInfo.resumeEndTime = time
            }

            override fun activityDrawFinish(activity: Any, time: Long) {
                saveAppSpeedInfoToLocal(time)
                saveAppStartInfoToLocal(time,activity.javaClass.name)
            }

            //页面测试信息
            private fun saveAppSpeedInfoToLocal(drawFinishTime:Long) {
                if (!pageSpeedCanRecord) return
                RabbitLog.d(TAG, "---> save activity speed info : ${pageSpeedInfo.pageName}")
                pageSpeedInfo.drawFinishTime = drawFinishTime
                pageSpeedCanRecord = false
                pageSpeedInfo.time = System.currentTimeMillis()
                RabbitDbStorageManager.save(pageSpeedInfo)
            }
        }
    }

    private fun monitorAppStartSpeed() {
        RabbitTracerEventNotifier.eventNotifier = object : RabbitTracerEventNotifier.TracerEvent {
            override fun applicationCreateTime(attachBaseContextTime: Long, createEndTime: Long) {
                appSpeedInfo.createStartTime = attachBaseContextTime
                appSpeedInfo.createEndTime = createEndTime
                saveAppStartInfoToLocal(0, "")
            }
        }
    }

    //应用测速信息记录
    private fun saveAppStartInfoToLocal(pageDrawFinishTime:Long, pageName:String){
        if (!appSpeedCanRecord) return
        RabbitLog.d(TAG, "---> save application speed info  : ${pageSpeedInfo.pageName}")
        appSpeedCanRecord = false
        if (pageName == entryActivityName){
            appSpeedInfo.fullShowCostTime = pageDrawFinishTime - appSpeedInfo.createStartTime
        }
        RabbitDbStorageManager.save(appSpeedInfo)
    }

    fun isOpen() = pageSpeedMonitorEnable

}