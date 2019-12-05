package com.susion.rabbit.report

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.google.gson.Gson
import com.susion.rabbit.Rabbit
import com.susion.rabbit.RabbitLog
import com.susion.rabbit.db.RabbitDbStorageManager
import com.susion.rabbit.report.entities.RabbitDeviceInfo
import com.susion.rabbit.report.entities.RabbitReportInfo
import com.susion.rabbit.utils.RabbitActivityLifecycleWrapper
import com.susion.rabbit.utils.device.DeviceUtils
import java.lang.ref.WeakReference
import java.util.concurrent.*

/**
 * susionwang at 2019-12-05
 * 数据上报中心
 */
internal object RabbitDataReportCenter {

    private val TAG = "rabbit-data-report"
    var appCurrentActivity: WeakReference<Activity?>? = null    //当前应用正在展示的Activity
    var deviceInfoStr = ""
    private val gson = Gson()
    private val DB_THREAD = ThreadPoolExecutor(5, 10, 10, TimeUnit.SECONDS, LinkedBlockingDeque(),
        ThreadFactory { r ->
            Thread(r, "rabbit_report_db_thread_${System.currentTimeMillis()}")
        }).apply {
        allowCoreThreadTimeOut(true)
    }

    private val REQUEST_THREAD = Executors.newFixedThreadPool(
        1
    ) { r -> Thread(r, "rabbit_report_request_thread") }

    private val dataEmitterTask = RabbitReportDataEmitterTask()
    private val LOAD_POINT_TO_EMITER_QUEUE = 1
    private val mHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                LOAD_POINT_TO_EMITER_QUEUE -> {
                    val loadDataCount = RabbitReportDataEmitterTask.EMITER_QUEUE_MAX_SIZE / 2
                    RabbitDbStorageManager.getDataWithDescendingSort(
                        RabbitReportInfo::class.java,
                        loadDataCount
                    ) {
                        dataEmitterTask.addPointsToEmitterQueue(it)
                        RabbitLog.d(TAG, "load point from db! Emitter Start")
                        startEmitterTask()
                    }
                }
            }
        }
    }

    fun init(application: Application) {
        application.registerActivityLifecycleCallbacks(object :
            RabbitActivityLifecycleWrapper() {
            override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
                appCurrentActivity = WeakReference(activity)
            }
        })
        deviceInfoStr = gson.toJson(getDeviceInfoX(application))
        dataEmitterTask.eventListener = object : RabbitReportDataEmitterTask.EventListener {
            override fun successEmitterPoint(pointInfo: RabbitReportInfo) {
                RabbitDbStorageManager.delete(RabbitReportInfo::class.java, pointInfo.id)
            }

            override fun pointQueueIsEmpty() {
                val currentDbPointCount =
                    RabbitDbStorageManager.dataCount(RabbitReportInfo::class.java)
                RabbitLog.d(TAG, "pointQueueIsEmpty db point : $currentDbPointCount")
                if (currentDbPointCount > 0) {
                    mHandler.sendEmptyMessage(LOAD_POINT_TO_EMITER_QUEUE)
                }
            }
        }
    }

    fun report(info: Any, timeX: Long = System.currentTimeMillis()) {

        if (!Rabbit.getConfig().reportConfig.reportMonitorData) return

        val infoJsonStr = gson.toJson(info)

        val reportInfo = RabbitReportInfo(
            infoJsonStr,
            timeX,
            appCurrentActivity?.get()?.javaClass?.simpleName ?: "",
            deviceInfoStr
        )

        RabbitLog.d(TAG, gson.toJson(reportInfo))

        saveData2Db(reportInfo)

        dataEmitterTask.addPoint(reportInfo)

        startEmitterTask()

    }

    private fun startEmitterTask() {
        if (!dataEmitterTask.isRunning) {
            REQUEST_THREAD.execute {
                dataEmitterTask.emitterPoints()
            }
        }
    }

    /**
     * 保存数据到数据库，防止丢失
     * */
    private fun saveData2Db(reportInfo: RabbitReportInfo) {
        DB_THREAD.execute {
            RabbitDbStorageManager.saveSync(reportInfo, false)
        }
    }

    private fun getDeviceInfoX(application: Application): RabbitDeviceInfo {
        return RabbitDeviceInfo().apply {
            deviceName = DeviceUtils.getDeviceName()
            deviceId = DeviceUtils.getDeviceId(application)
            systemVersion = android.os.Build.VERSION.RELEASE
            appVersionCode = DeviceUtils.getAppVersionCode(application) ?: ""
        }
    }

}