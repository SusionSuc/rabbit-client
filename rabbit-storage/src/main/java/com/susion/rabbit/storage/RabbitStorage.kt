package com.susion.rabbit.storage

import android.app.Application
import com.susion.rabbit.base.entities.*
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
        mConfig.storageInOnSessionData.addAll(ArrayList<Class<Any>>().apply {
            add(RabbitHttpLogInfo::class.java as Class<Any>)
            add(RabbitBlockFrameInfo::class.java as Class<Any>)
            add(RabbitMemoryInfo::class.java as Class<Any>)
        })
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
                    RabbitBlockFrameInfo::class.java as Class<Any>,
                    daoSession.rabbitBlockFrameInfoDao as AbstractDao<Any, Long>
                )
            )
            add(
                RabbitDaoPluginProvider(
                    RabbitHttpLogInfo::class.java as Class<Any>,
                    daoSession.rabbitHttpLogInfoDao as AbstractDao<Any, Long>
                )
            )
            add(
                RabbitDaoPluginProvider(
                    RabbitAppStartSpeedInfo::class.java as Class<Any>,
                    daoSession.rabbitAppStartSpeedInfoDao as AbstractDao<Any, Long>
                )
            )
            add(
                RabbitDaoPluginProvider(
                    RabbitPageSpeedInfo::class.java as Class<Any>,
                    daoSession.rabbitPageSpeedInfoDao as AbstractDao<Any, Long>
                )
            )
            add(
                RabbitDaoPluginProvider(
                    RabbitMemoryInfo::class.java as Class<Any>,
                    daoSession.rabbitMemoryInfoDao as AbstractDao<Any, Long>
                )
            )
            add(
                RabbitDaoPluginProvider(
                    RabbitExceptionInfo::class.java as Class<Any>,
                    daoSession.rabbitExceptionInfoDao as AbstractDao<Any, Long>
                )
            )
            add(
                RabbitDaoPluginProvider(
                    RabbitReportInfo::class.java as Class<Any>,
                    daoSession.rabbitReportInfoDao as AbstractDao<Any, Long>
                )
            )

            add(
                RabbitDaoPluginProvider(
                    RabbitFPSInfo::class.java as Class<Any>,
                    daoSession.rabbitReportInfoDao as AbstractDao<Any, Long>
                )
            )

            add(
                RabbitDaoPluginProvider(
                    RabbitSlowMethodInfo::class.java as Class<Any>,
                    daoSession.rabbitSlowMethodInfoDao as AbstractDao<Any, Long>
                )
            )
        }
        return daoProvider
    }

    class Config(
        var daoProvider: ArrayList<RabbitDaoPluginProvider> = ArrayList(),
        var storageInOnSessionData: ArrayList<Class<Any>> = ArrayList()
    )

    interface EventListener {
        fun onStorageData(obj: Any)
    }

}