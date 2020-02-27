package com.susion.rabbit.monitor.instance

import android.app.Activity
import android.content.Context
import com.google.gson.Gson
import com.susion.rabbit.base.RabbitLog
import com.susion.rabbit.monitor.RabbitMonitor
import com.susion.rabbit.base.RabbitMonitorProtocol
import com.susion.rabbit.base.TAG_MONITOR
import com.susion.rabbit.base.entities.*
import com.susion.rabbit.storage.RabbitDbStorageManager
import com.susion.rabbit.tracer.RabbitTracerEventNotifier

/**
 * susionwang at 2019-11-14
 * 应用速度监控:
 * 1. 应用启动速度
 * 2. 页面启动速度、渲染速度
 */
internal class RabbitAppSpeedMonitor(override var isOpen: Boolean = false) :
    RabbitMonitorProtocol {

    private var currentPageName: String = ""
    private val pageApiStatusInfo = HashMap<String, RabbitPageApiInfo>()
    private var configInfo = RabbitAppSpeedMonitorConfig()
    private val monitorPageApiSet = HashSet<String>()
    private val apiSet = HashSet<String>()

    //避免重复统计测试时间
    private var appSpeedCanRecord = false
    private var pageSpeedCanRecord = false

    private var pageSpeedInfo: RabbitPageSpeedInfo = RabbitPageSpeedInfo()
    private var appSpeedInfo: RabbitAppStartSpeedInfo = RabbitAppStartSpeedInfo()

    //第一个对用户有效的页面 【闪屏页 or 首页】
    private var entryActivityName = ""

    override fun open(context: Context) {
        isOpen = true
        configMonitorList(RabbitMonitor.mConfig.monitorSpeedList)
        if (entryActivityName.isNotEmpty()){
            RabbitLog.d(TAG_MONITOR, "app speed init with page list! entryActivityName :$entryActivityName")
            monitorActivitySpeed()
        }else{
            RabbitLog.d(TAG_MONITOR, "app speed init with null")
            monitorApplicationStart()
        }
        RabbitLog.d(TAG_MONITOR, "entryActivityName : $entryActivityName")
    }

    override fun close() {
        isOpen = false
        RabbitTracerEventNotifier.appSpeedNotifier = RabbitTracerEventNotifier.FakeEventListener()
    }

    override fun getMonitorInfo() = RabbitMonitorProtocol.APP_SPEED

    /**
     * 可以从外部设置
     * */
    fun configMonitorList(speedConfig: RabbitAppSpeedMonitorConfig) {
        monitorPageApiSet.clear()
        apiSet.clear()

        speedConfig.pageConfigList.forEach {
            monitorPageApiSet.add(it.pageSimpleName)
            it.apiList.forEach { simpleUrl ->
                apiSet.add(simpleUrl)
            }
        }
        configInfo = speedConfig
        entryActivityName = speedConfig.homeActivity
    }

    /**
     * 一个请求结束
     * */
    fun markRequestFinish(requestUrl: String, costTime: Long = 0) {
        val curApiInfo = pageApiStatusInfo[currentPageName] ?: return
        curApiInfo.apiStatusList.forEach {
            if (requestUrl.contains(it.api)) {
                it.isFinish = true
                it.costTime = costTime
            }
        }
    }

    private fun monitorActivitySpeed() {
        RabbitTracerEventNotifier.appSpeedNotifier =
            object : RabbitTracerEventNotifier.TracerEvent {
                override fun applicationCreateTime(
                    attachBaseContextTime: Long,
                    createEndTime: Long
                ) {
                    appSpeedCanRecord = true
                    appSpeedInfo.time = System.currentTimeMillis()
                    appSpeedInfo.createStartTime = attachBaseContextTime
                    appSpeedInfo.createEndTime = createEndTime
                    appSpeedInfo.fullShowCostTime = 0
                    RabbitLog.d(TAG_MONITOR, " --> applicationCreateTime  ${createEndTime - attachBaseContextTime}")
                    if (entryActivityName.isEmpty()) {
                        appSpeedCanRecord = false
                        RabbitDbStorageManager.save(appSpeedInfo)
                    }
                }

                override fun activityCreateStart(activity: Any, time: Long) {
                    currentPageName = activity.javaClass.simpleName
                    pageSpeedCanRecord = true
                    pageSpeedInfo = RabbitPageSpeedInfo()
                    pageSpeedInfo.pageName = activity.javaClass.name
                    pageSpeedInfo.createStartTime = time
                    resetPageApiRequestStatus(activity.javaClass.simpleName)
                }

                override fun activityCreateEnd(activity: Any, time: Long) {
                    pageSpeedInfo.createEndTime = time
                }

                override fun activityResumeEnd(activity: Any, time: Long) {
                    pageSpeedInfo.resumeEndTime = time
                }

                override fun activityDrawFinish(activitySimpleName: String, time: Long) {
                    RabbitLog.d(TAG_MONITOR, "$activitySimpleName -> activityDrawFinish")
                    savePageSpeedInfoToLocal(time)
                    if (entryActivityName.isNotEmpty() && appSpeedCanRecord) {
                        saveApplicationStartInfoToLocal(time, activitySimpleName)
                    }
                }

                //保存页面测速信息
                private fun savePageSpeedInfoToLocal(drawFinishTime: Long) {
                    if (!pageSpeedCanRecord) return

                    if (pageSpeedInfo.inflateFinishTime == 0L) {
                        pageSpeedInfo.time = System.currentTimeMillis()
                        pageSpeedInfo.inflateFinishTime = drawFinishTime
                        RabbitLog.d(
                            TAG_MONITOR,
                            "$currentPageName page inflateFinishTime ---> cost time : ${drawFinishTime - pageSpeedInfo.createStartTime}"
                        )
                    }

                    val apiStatus = pageApiStatusInfo[currentPageName]
                    if (apiStatus != null) {
                        if (apiStatus.allApiRequestFinish()) {
                            RabbitLog.d(
                                TAG_MONITOR,
                                "$currentPageName page finish all request ---> cost time : ${drawFinishTime - pageSpeedInfo.createStartTime}"
                            )
                            pageSpeedCanRecord = false
                            pageSpeedInfo.fullDrawFinishTime = drawFinishTime
                            pageSpeedInfo.apiRequestCostString = Gson().toJson(apiStatus).toString()
                            RabbitDbStorageManager.save(pageSpeedInfo)
                        }
                    } else {
                        pageSpeedCanRecord = false
                        pageSpeedInfo.fullDrawFinishTime = drawFinishTime
                        RabbitDbStorageManager.save(pageSpeedInfo)
                    }
                }
            }

    }

    private fun resetPageApiRequestStatus(acSimpleName: String) {
        if (!monitorPageApiSet.contains(acSimpleName)) return
        var pageApiInfo = pageApiStatusInfo[acSimpleName]
        if (pageApiInfo == null) {
            pageApiInfo = RabbitPageApiInfo()
            for (apiConfigInfo in configInfo.pageConfigList) {
                if (apiConfigInfo.pageSimpleName == acSimpleName) {
                    apiConfigInfo.apiList.forEach { apiUrl ->
                        pageApiInfo.apiStatusList.add(
                            RabbitApiInfo(
                                apiUrl,
                                false
                            )
                        )
                    }
                    break
                }
            }
            pageApiStatusInfo[acSimpleName] = pageApiInfo
        } else {
            pageApiInfo.apiStatusList.forEach {
                it.isFinish = false
            }
        }
    }

    private fun monitorApplicationStart() {
        RabbitTracerEventNotifier.appSpeedNotifier =
            object : RabbitTracerEventNotifier.TracerEvent {
                override fun applicationCreateTime(
                    attachBaseContextTime: Long,
                    createEndTime: Long
                ) {
                    appSpeedInfo.time = System.currentTimeMillis()
                    appSpeedInfo.createStartTime = attachBaseContextTime
                    appSpeedInfo.createEndTime = createEndTime
                    RabbitDbStorageManager.save(appSpeedInfo)
                    RabbitLog.d(TAG_MONITOR, "monitorApplicationStart")
                }
            }
    }

    //应用启动测速信息记录
    private fun saveApplicationStartInfoToLocal(pageDrawFinishTime: Long, pageName: String) {
        if (!appSpeedCanRecord || pageName != entryActivityName) return

        val apiStatus = pageApiStatusInfo[entryActivityName]
        if (apiStatus != null) {
            if (apiStatus.allApiRequestFinish()) {
                appSpeedCanRecord = false
                appSpeedInfo.fullShowCostTime = pageDrawFinishTime - appSpeedInfo.createStartTime
                RabbitDbStorageManager.save(appSpeedInfo)
                RabbitLog.d(TAG_MONITOR, "saveApplicationStartInfoToLocal")
            }
        } else {
            appSpeedCanRecord = false
            appSpeedInfo.fullShowCostTime = pageDrawFinishTime - appSpeedInfo.createStartTime
            RabbitDbStorageManager.save(appSpeedInfo)
            RabbitLog.d(TAG_MONITOR, "saveApplicationStartInfoToLocal")
        }
    }

    //是否监控这个请求
    fun monitorRequest(requestUrl: String): Boolean {
        for (api in apiSet) {
            if (requestUrl.contains(api)) {
                return true
            }
        }
        return false
    }

}