package com.susion.rabbit.storage

import android.app.Application
import com.susion.rabbit.base.greendao.DaoMaster
import org.greenrobot.greendao.AbstractDao

/**
 * susionwang at 2019-12-12
 */
object RabbitStorage {

    private val DB_NAME = "rabbit-apm"
    var mConfig = Config()
    var application: Application? = null
    var eventListener: EventListener? = null

    fun init(application_: Application, config: Config) {
        application = application_
        mConfig = config
        mConfig.daoProvider.addAll(getFixedDaoProvider())
        RabbitDbStorageManager.clearOldSessionData()
    }

    private fun getFixedDaoProvider(): List<RabbitDaoPluginProvider> {
        val daoSession =
            com.susion.rabbit.base.greendao.DaoMaster(
                DaoMaster.DevOpenHelper(
                    application,
                    DB_NAME
                ).writableDb
            ).newSession()
        val daoProvider = ArrayList<RabbitDaoPluginProvider>().apply {
            add(
                RabbitDaoPluginProvider(
                    com.susion.rabbit.base.entities.RabbitBlockFrameInfo::class.java as Class<Any>,
                    daoSession.rabbitBlockFrameInfoDao as AbstractDao<Any, Long>
                )
            )
            add(
                RabbitDaoPluginProvider(
                    com.susion.rabbit.base.entities.RabbitHttpLogInfo::class.java as Class<Any>,
                    daoSession.rabbitHttpLogInfoDao as AbstractDao<Any, Long>
                )
            )
            add(
                RabbitDaoPluginProvider(
                    com.susion.rabbit.base.entities.RabbitAppStartSpeedInfo::class.java as Class<Any>,
                    daoSession.rabbitAppStartSpeedInfoDao as AbstractDao<Any, Long>
                )
            )
            add(
                RabbitDaoPluginProvider(
                    com.susion.rabbit.base.entities.RabbitPageSpeedInfo::class.java as Class<Any>,
                    daoSession.rabbitPageSpeedInfoDao as AbstractDao<Any, Long>
                )
            )
            add(
                RabbitDaoPluginProvider(
                    com.susion.rabbit.base.entities.RabbitMemoryInfo::class.java as Class<Any>,
                    daoSession.rabbitMemoryInfoDao as AbstractDao<Any, Long>
                )
            )
            add(
                RabbitDaoPluginProvider(
                    com.susion.rabbit.base.entities.RabbitExceptionInfo::class.java as Class<Any>,
                    daoSession.rabbitExceptionInfoDao as AbstractDao<Any, Long>
                )
            )
            add(
                RabbitDaoPluginProvider(
                    com.susion.rabbit.base.entities.RabbitReportInfo::class.java as Class<Any>,
                    daoSession.rabbitReportInfoDao as AbstractDao<Any, Long>
                )
            )

            add(
                RabbitDaoPluginProvider(
                    com.susion.rabbit.base.entities.RabbitFPSInfo::class.java as Class<Any>,
                    daoSession.rabbitReportInfoDao as AbstractDao<Any, Long>
                )
            )
        }
        return daoProvider
    }

    class Config(
        var daoProvider: ArrayList<RabbitDaoPluginProvider> = ArrayList(),
        var storageInOnSessionData: List<Class<Any>> = ArrayList()
    )

    interface EventListener {
        fun onStorageData(obj: Any)
    }

}