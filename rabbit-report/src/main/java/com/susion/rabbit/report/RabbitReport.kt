package com.susion.rabbit.report

import android.app.Application
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.google.gson.Gson
import com.susion.rabbit.base.RabbitLog
import com.susion.rabbit.base.entities.RabbitDeviceInfo
import com.susion.rabbit.storage.RabbitDbStorageManager
import java.util.concurrent.Executors

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
    lateinit var application: Application
    var mConfig: ReportConfig = ReportConfig()
    private var deviceInfoStr = ""
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
                    RabbitDbStorageManager.getAll(
                        com.susion.rabbit.base.entities.RabbitReportInfo::class.java,
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

    fun init(app: Application, config: ReportConfig) {
        application = app
        mConfig = config

        config.notReportDataFormat.add(com.susion.rabbit.base.entities.RabbitReportInfo::class.java)

        com.susion.rabbit.base.common.RabbitAsync.asyncRun({
            deviceInfoStr = Gson().toJson(getDeviceInfo(application))
            RabbitLog.d(TAG, "device info : $deviceInfoStr")
        })

        dataEmitterTask.eventListener = object : RabbitReportDataEmitterTask.EventListener {
            override fun successEmitterPoint(pointInfo: com.susion.rabbit.base.entities.RabbitReportInfo) {
                RabbitDbStorageManager.delete(
                    com.susion.rabbit.base.entities.RabbitReportInfo::class.java,
                    condition = Pair(com.susion.rabbit.base.greendao.RabbitReportInfoDao.Properties.Time, pointInfo.time.toString())
                )
            }

            override fun pointQueueIsEmpty() {
                val currentDbPointCount =
                    RabbitDbStorageManager.dataCount(com.susion.rabbit.base.entities.RabbitReportInfo::class.java)
                RabbitLog.d(TAG, "pointQueueIsEmpty db point : $currentDbPointCount")
                if (currentDbPointCount > 0) {
                    mHandler.sendEmptyMessage(LOAD_POINT_TO_EMITER_QUEUE)
                }
            }
        }
    }

    /**
     * @param useTime  应用使用的总时长
     * */
    fun report(info: Any, useTime: Long = 0) {

        if (!mConfig.reportMonitorData) return

        if (mConfig.notReportDataFormat.contains(info.javaClass)) return

        val reportInfo = RabbitReportTransformCenter.createReportInfo(info, useTime)

        RabbitLog.d(TAG, "report  ${reportInfo.type} data  use time : $useTime")

        RabbitDbStorageManager.save(reportInfo)

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

    private fun getDeviceInfo(application: Application): RabbitDeviceInfo {
        val roomInfo = com.susion.rabbit.base.common.RomUtils.getRomInfo()
        val supportCpuAbi = StringBuilder()
        Build.SUPPORTED_ABIS.forEachIndexed { index, abi ->
            if (index != 0) {
                supportCpuAbi.append("/")
            }
            supportCpuAbi.append(abi)
        }
        return RabbitDeviceInfo().apply {
            deviceName = com.susion.rabbit.base.common.DeviceUtils.getDeviceName()
            deviceId = com.susion.rabbit.base.common.DeviceUtils.getDeviceId(application)
            systemVersion = Build.VERSION.RELEASE
            memorySize = com.susion.rabbit.base.common.DeviceUtils.getMemorySize(application)
            rom = "${roomInfo.name}/${roomInfo.version}"
            appVersionCode = "${com.susion.rabbit.base.common.DeviceUtils.getAppVersionCode(application)}"
            isRoot = com.susion.rabbit.base.common.DeviceUtils.isDeviceRooted()
            supportAbi = supportCpuAbi.toString()
            manufacturer = Build.MANUFACTURER
        }
    }

    fun getDeviceInfoStr() = deviceInfoStr

}