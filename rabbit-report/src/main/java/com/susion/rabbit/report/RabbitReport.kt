package com.susion.rabbit.report

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.google.gson.Gson
import com.susion.rabbit.RabbitLog
import com.susion.rabbit.common.DeviceUtils
import com.susion.rabbit.common.RabbitActivityLifecycleWrapper
import com.susion.rabbit.entities.RabbitDeviceInfo
import com.susion.rabbit.entities.RabbitReportInfo
import com.susion.rabbit.greendao.RabbitPageSpeedInfoDao
import com.susion.rabbit.greendao.RabbitReportInfoDao
import com.susion.rabbit.storage.RabbitDbStorageManager
import java.lang.ref.WeakReference
import java.util.concurrent.*

/**
 * susionwang at 2019-12-05
 * 数据上报
 */
object RabbitReport {

    /**
     * @property reportMonitorData 是否发送数据
     * @property reportPath 数据上报的地址
     * */
    class ReportConfig(
        var reportMonitorData: Boolean = false,
        var reportPath: String = "http://127.0.0.1:8000/apmdb/upload-log",
        var notReportDataFormat: HashSet<Class<*>> = HashSet()
    )

    private val TAG = javaClass.simpleName
    var application: Application? = null
    var mConfig: ReportConfig = ReportConfig()
    private var appCurrentActivity: WeakReference<Activity?>? = null    //当前应用正在展示的Activity
    private var deviceInfoStr = ""
    private val gson = Gson()
    private val REQUEST_THREAD = Executors.newFixedThreadPool(
        1
    ) { r -> Thread(r, "rabbit_report_request_thread") }

    private val dataEmitterTask = ReportDataEmitterTask()
    private val LOAD_POINT_TO_EMITER_QUEUE = 1
    private val mHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                LOAD_POINT_TO_EMITER_QUEUE -> {
                    val loadDataCount = ReportDataEmitterTask.EMITER_QUEUE_MAX_SIZE / 2
                    RabbitDbStorageManager.getAll(
                        RabbitReportInfo::class.java,
                        count = loadDataCount,
                        sortField = "time",
                        loadResult = {
                            dataEmitterTask.addPointsToEmitterQueue(it)
                            RabbitLog.d(TAG, "load point from db! Emitter Start")
                            startEmitterTask()
                        })
                }
            }
        }
    }

    fun init(application_: Application, config: ReportConfig) {
        application = application_
        mConfig = config
        application_.registerActivityLifecycleCallbacks(object : RabbitActivityLifecycleWrapper() {
            override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
                appCurrentActivity = WeakReference(activity)
            }
        })
        deviceInfoStr = gson.toJson(getDeviceInfoX(application_))
        dataEmitterTask.eventListener = object : ReportDataEmitterTask.EventListener {
            override fun successEmitterPoint(pointInfo: RabbitReportInfo) {
                RabbitDbStorageManager.delete(
                    RabbitReportInfo::class.java,
                    condition = Pair(RabbitReportInfoDao.Properties.Time, pointInfo.time.toString())
                )
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

    fun report(info: Any) {

        if (!mConfig.reportMonitorData) return

        if (mConfig.notReportDataFormat.contains(info.javaClass)) return

        val dataType = info.javaClass.simpleName
        val infoJsonStr = gson.toJson(info)

        val reportInfo = RabbitReportInfo(
            infoJsonStr,
            System.currentTimeMillis(),
            appCurrentActivity?.get()?.javaClass?.simpleName ?: "",
            deviceInfoStr,
            dataType
        )

        RabbitLog.d(TAG, "data type : $dataType report ${gson.toJson(reportInfo)}")

        RabbitDbStorageManager.save(reportInfo, false)

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

    private fun getDeviceInfoX(application: Application): RabbitDeviceInfo {
        return RabbitDeviceInfo().apply {
            deviceName = DeviceUtils.getDeviceName()
            deviceId = DeviceUtils.getDeviceId(application)
            systemVersion = android.os.Build.VERSION.RELEASE
            appVersionCode = DeviceUtils.getAppVersionCode(application) ?: ""
        }
    }

}