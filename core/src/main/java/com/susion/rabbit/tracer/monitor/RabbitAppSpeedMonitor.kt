package com.susion.rabbit.tracer.monitor

import android.content.Context
import com.google.gson.Gson
import com.susion.rabbit.RabbitLog
import com.susion.rabbit.db.RabbitDbStorageManager
import com.susion.rabbit.tracer.RabbitTracerEventNotifier
import com.susion.rabbit.tracer.entities.*
import com.susion.rabbit.utils.FileUtils
import java.lang.Exception

/**
 * susionwang at 2019-11-14
 *
 * 应用速度监控:
 * 1. 应用启动速度
 * 2. 页面启动速度、渲染速度
 */
class RabbitAppSpeedMonitor {

    private val TAG = javaClass.simpleName

    private val ASSERT_FILE_NAME = "page_api_list.json"

    private var currentPageName: String = ""
    private val pageApiStatusInfo = HashMap<String, RabbitPageApiInfo>()
    private var confgiInfo = RabbitAppSpeedMonitorConfig()
    private val monitorPageApiSet = HashSet<String>()

    //避免重复统计测试时间
    private var appSpeedCanRecord = false
    private var pageSpeedCanRecord = false

    private var pageSpeedInfo: RabbitPageSpeedInfo = RabbitPageSpeedInfo()
    private var appSpeedInfo: RabbitAppStartSpeedInfo = RabbitAppStartSpeedInfo()

    //第一个对用户有效的页面 【闪屏页 or 首页】
    private var entryActivityName = ""

    //启动页面测试记录
    private var pageSpeedMonitorEnable = false

    fun init(context: Context) {
        loadConfig(context)
        monitorAppStartSpeed()
        RabbitLog.d(TAG, "entryActivityName : $entryActivityName")
    }

    /**
     * 目前定死从 asstets/page_api_list.json 文件中加载
     *
     * 也可以扩展为网络配置
     * */
    private fun loadConfig(context: Context) {
        try {

            val jsonStr = FileUtils.getAssetString(context, ASSERT_FILE_NAME)
            if (jsonStr.isEmpty()) return
            confgiInfo = Gson().fromJson(jsonStr,RabbitAppSpeedMonitorConfig::class.java)

            confgiInfo.pageConfigList.forEach {
                monitorPageApiSet.add(it.pageSimpleName)
            }

            entryActivityName = confgiInfo.homeActivity

        } catch (e: Exception) {
            RabbitLog.d(TAG, "loadConfig failed ${e.message} ")
        }
    }

    /**
     * 一个请求结束
     * */
    fun markRequestFinish(requestUrl: String) {
        val curApiInfo = pageApiStatusInfo[currentPageName] ?: return
        curApiInfo.apiStatusList.forEach {
            if (requestUrl.contains(it.api)) {
                it.isFinish = true
            }
        }
    }

    fun startMonitorPageSpeed() {
        pageSpeedMonitorEnable = true
        monitorActivitySpeed()
    }

    fun stopMonitorPageSpeed() {
        pageSpeedMonitorEnable = false
        RabbitTracerEventNotifier.eventNotifier = RabbitTracerEventNotifier.FakeEventListener()
    }

    private fun monitorActivitySpeed() {
        RabbitTracerEventNotifier.eventNotifier = object : RabbitTracerEventNotifier.TracerEvent {

            override fun applicationCreateTime(attachBaseContextTime: Long, createEndTime: Long) {
                appSpeedCanRecord = true
                appSpeedInfo.createStartTime = attachBaseContextTime
                appSpeedInfo.createEndTime = createEndTime
                appSpeedInfo.fullShowCostTime = 0
                if (entryActivityName.isEmpty()) {
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

            override fun activityDrawFinish(activity: Any, time: Long) {
                saveAppSpeedInfoToLocal(time)
                if (entryActivityName.isNotEmpty()) {
                    saveAppStartInfoToLocal(time, activity.javaClass.simpleName)
                }
            }

            //页面测试信息
            private fun saveAppSpeedInfoToLocal(drawFinishTime: Long) {
                if (!pageSpeedCanRecord) return

                if (pageSpeedInfo.inflateFinishTime == 0L) {
                    pageSpeedInfo.time = System.currentTimeMillis()
                    pageSpeedInfo.inflateFinishTime = drawFinishTime
                    RabbitLog.d(
                        TAG,
                        "$currentPageName page inflateFinishTime ---> cost time : ${drawFinishTime - pageSpeedInfo.createStartTime}"
                    )
                }

                val apiStatus = pageApiStatusInfo[currentPageName]
                if (apiStatus != null) {
                    if (apiStatus.allApiRequestFinish()) {
                        RabbitLog.d(
                            TAG,
                            "$currentPageName page finish all request ---> cost time : ${drawFinishTime - pageSpeedInfo.createStartTime}"
                        )
                        pageSpeedCanRecord = false
                        pageSpeedInfo.fullDrawFinishTime = drawFinishTime
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
            for (apiConfigInfo in confgiInfo.pageConfigList) {
                if (apiConfigInfo.pageSimpleName == acSimpleName) {
                    apiConfigInfo.apiList.forEach { apiUrl ->
                        pageApiInfo.apiStatusList.add(RabbitApiInfo(apiUrl, false))
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

    private fun monitorAppStartSpeed() {
        RabbitTracerEventNotifier.eventNotifier = object : RabbitTracerEventNotifier.TracerEvent {
            override fun applicationCreateTime(attachBaseContextTime: Long, createEndTime: Long) {
                appSpeedCanRecord = true
                appSpeedInfo.createStartTime = attachBaseContextTime
                appSpeedInfo.createEndTime = createEndTime
                if (entryActivityName.isEmpty()) {
                    RabbitDbStorageManager.save(appSpeedInfo)
                }
            }
        }
    }

    //应用测速信息记录
    private fun saveAppStartInfoToLocal(pageDrawFinishTime: Long, pageName: String) {
        if (!appSpeedCanRecord || pageName != entryActivityName) return

        val apiStatus = pageApiStatusInfo[entryActivityName]

        if (apiStatus != null) {
            if (apiStatus.allApiRequestFinish()) {
                appSpeedCanRecord = false
                appSpeedInfo.fullShowCostTime = pageDrawFinishTime - appSpeedInfo.createStartTime
                RabbitDbStorageManager.save(appSpeedInfo)
            }
        }else{
            appSpeedCanRecord = false
            appSpeedInfo.fullShowCostTime = pageDrawFinishTime - appSpeedInfo.createStartTime
            RabbitDbStorageManager.save(appSpeedInfo)
        }
    }

    fun isOpen() = pageSpeedMonitorEnable

}