package com.susion.rabbit.storage

import android.app.Application
import com.susion.rabbit.base.RabbitMonitorProtocol
import com.susion.rabbit.base.entities.*
import com.susion.rabbit.base.greendao.DaoMaster
import com.susion.rabbit.base.config.RabbitDaoProviderConfig
import com.susion.rabbit.base.config.RabbitStorageConfig
import org.greenrobot.greendao.AbstractDao

/**
 * susionwang at 2019-12-12
 */
object RabbitStorage {

    private val DB_NAME = "rabbit-apm"
    var mConfig = RabbitStorageConfig()
    var application: Application? = null
    var eventListener: EventListener? = null

    fun init(application_: Application, config: RabbitStorageConfig) {
        application = application_
        mConfig = config
        mConfig.daoProvider.addAll(getFixedDaoProvider())
        mConfig.storageInOnSessionData.addAll(ArrayList<Class<Any>>().apply {
            add(RabbitHttpLogInfo::class.java as Class<Any>)
            add(RabbitMemoryInfo::class.java as Class<Any>)
        })
        RabbitDbStorageManager.clearOldSessionData()
    }

    private fun getFixedDaoProvider(): List<RabbitDaoProviderConfig> {
        val daoSession =
            DaoMaster(DaoMaster.DevOpenHelper(application, DB_NAME).writableDb).newSession()

        val daoProvider = ArrayList<RabbitDaoProviderConfig>().apply {
            add(
                RabbitDaoProviderConfig(
                    RabbitBlockFrameInfo::class.java as Class<Any>,
                    daoSession.rabbitBlockFrameInfoDao as AbstractDao<Any, Long>
                )
            )
            add(
                RabbitDaoProviderConfig(
                    RabbitHttpLogInfo::class.java as Class<Any>,
                    daoSession.rabbitHttpLogInfoDao as AbstractDao<Any, Long>
                )
            )
            add(
                RabbitDaoProviderConfig(
                    RabbitAppStartSpeedInfo::class.java as Class<Any>,
                    daoSession.rabbitAppStartSpeedInfoDao as AbstractDao<Any, Long>
                )
            )
            add(
                RabbitDaoProviderConfig(
                    RabbitPageSpeedInfo::class.java as Class<Any>,
                    daoSession.rabbitPageSpeedInfoDao as AbstractDao<Any, Long>
                )
            )
            add(
                RabbitDaoProviderConfig(
                    RabbitMemoryInfo::class.java as Class<Any>,
                    daoSession.rabbitMemoryInfoDao as AbstractDao<Any, Long>
                )
            )
            add(
                RabbitDaoProviderConfig(
                    RabbitExceptionInfo::class.java as Class<Any>,
                    daoSession.rabbitExceptionInfoDao as AbstractDao<Any, Long>
                )
            )
            add(
                RabbitDaoProviderConfig(
                    RabbitReportInfo::class.java as Class<Any>,
                    daoSession.rabbitReportInfoDao as AbstractDao<Any, Long>
                )
            )

            add(
                RabbitDaoProviderConfig(
                    RabbitFPSInfo::class.java as Class<Any>,
                    daoSession.rabbitReportInfoDao as AbstractDao<Any, Long>
                )
            )

            add(
                RabbitDaoProviderConfig(
                    RabbitSlowMethodInfo::class.java as Class<Any>,
                    daoSession.rabbitSlowMethodInfoDao as AbstractDao<Any, Long>
                )
            )

            add(
                RabbitDaoProviderConfig(
                    RabbitIoCallInfo::class.java as Class<Any>,
                    daoSession.rabbitIoCallInfoDao as AbstractDao<Any, Long>
                )
            )
        }
        return daoProvider
    }

    interface EventListener {
        fun onStorageData(obj: Any)
    }

    fun clearDataByMonitorName(monitorName: String) {
        when (monitorName) {
            RabbitMonitorProtocol.APP_SPEED.name -> {
                RabbitDbStorageManager.clearAllData(RabbitAppStartSpeedInfo::class.java)
                RabbitDbStorageManager.clearAllData(RabbitPageSpeedInfo::class.java)
            }

            RabbitMonitorProtocol.EXCEPTION.name -> {
                RabbitDbStorageManager.clearAllData(RabbitExceptionInfo::class.java)
            }

            RabbitMonitorProtocol.MEMORY.name -> {
                RabbitDbStorageManager.clearAllData(RabbitMemoryInfo::class.java)
            }

            RabbitMonitorProtocol.METHOD_TRACE.name -> {
                RabbitDbStorageManager.clearAllData(RabbitSlowMethodInfo::class.java)
            }

            RabbitMonitorProtocol.NET.name -> {
                RabbitDbStorageManager.clearAllData(RabbitHttpLogInfo::class.java)
            }
        }
    }

}