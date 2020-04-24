package com.susion.rabbit.storage

import android.app.Application
import com.susion.rabbit.base.RabbitLog
import com.susion.rabbit.base.TAG_STORAGE
import com.susion.rabbit.base.config.RabbitDaoProviderConfig
import com.susion.rabbit.base.entities.*
import com.susion.rabbit.base.greendao.DaoMaster
import com.susion.rabbit.base.greendao.DaoSession
import org.greenrobot.greendao.AbstractDao
import org.greenrobot.greendao.Property
import org.greenrobot.greendao.query.WhereCondition

/**
 * susionwang at 2019-10-21
 *
 * 所有rabbit可以持久数据都应该有对应的 Dao
 */
internal class RabbitGreenDaoDbManage(
    val application: Application,
    extraDaoProvider: ArrayList<RabbitDaoProviderConfig> = ArrayList()
) : RabbitStorageProtocol {

    private val DB_NAME = "rabbit-apm"
    private var daoSession: DaoSession?
    private val daoMap = HashMap<String, AbstractDao<Any, Long>>()

    init {
        daoSession = DaoMaster(
            DaoMaster.DevOpenHelper(
                application,
                DB_NAME
            ).writableDb
        ).newSession()
        getAllDao(daoSession!!, extraDaoProvider).forEach {
            daoMap[it.clazz.simpleName] = it.dao
        }
    }

    override fun save(obj: RabbitInfoProtocol) {
        val daoImpl = daoImpl(obj.javaClass) ?: return
        daoImpl.save(obj)
        RabbitLog.d(TAG_STORAGE, "save data $obj")
    }

    override fun <T : RabbitInfoProtocol> update(
        clazz: Class<T>,
        obj: RabbitInfoProtocol,
        id: Long
    ) {
        val daoImpl = daoImpl(obj.javaClass) ?: return
        val storageData = getDataById(clazz, id)
        if (storageData == null) {
            daoImpl.save(obj)
        } else {
            daoImpl.update(obj)
        }
    }

    override fun <T : RabbitInfoProtocol> query(clazz: Class<T>, id: Long): T? {
        return getDataById(clazz, id)
    }

    override fun <T : Any> delete(clazz: Class<T>, id: Long) {
        daoImpl(clazz)?.deleteByKey(id)
    }

    override fun <T : RabbitInfoProtocol> distinct(
        clazz: Class<T>,
        columnName: String
    ): List<String> {
        val dao = daoImpl(clazz) ?: return emptyList()
        val distinctSQL = "SELECT DISTINCT $columnName FROM ${dao.tablename};"
        val cursor = daoSession?.database?.rawQuery(distinctSQL, null) ?: return emptyList()
        val resList = ArrayList<String>()
        while (cursor.moveToNext()) {
            resList.add(cursor.getString(0))
        }
        return resList
    }

    override fun <T : Any> count(clazz: Class<T>): Long {
        return daoImpl(clazz)?.queryBuilder()?.count() ?: 0
    }

    fun <T : RabbitInfoProtocol> delete(clazz: Class<T>, condition: WhereCondition) {
        getDatas(clazz, condition).forEach {
            daoImpl(clazz)?.delete(it)
        }
    }

    private fun <T : RabbitInfoProtocol> getDataById(clazz: Class<T>, id: Long): T? {
        return daoImpl(clazz)?.loadByRowId(id)
    }

    fun <T : RabbitInfoProtocol> getDatas(
        clazz: Class<T>,
        condition: WhereCondition? = null,
        sortField: String = "time",
        count: Int = 0,
        orderDesc: Boolean = false
    ): List<T> {
        val dao = daoImpl(clazz)
        val property = getProperties(dao, sortField)
        val queryBuilder = dao?.queryBuilder()
        if (count != 0) {
            queryBuilder?.limit(count)
        }
        if (orderDesc) {
            queryBuilder?.orderDesc(property)
        } else {
            queryBuilder?.orderAsc(property)
        }
        if (condition != null) {
            queryBuilder?.where(condition)
        }

        return queryBuilder?.build()?.list() ?: emptyList()
    }

    private fun <T : Any> getProperties(
        dao: AbstractDao<T, Long>?,
        field: String
    ): Property? {
        return dao?.properties?.find { it.name == field }
    }

    override fun <T : Any> clear(clazz: Class<T>) {
        daoImpl(clazz)?.deleteAll()
    }

    private fun <T : Any> daoImpl(entitiesClass: Class<T>): AbstractDao<T, Long>? {
        return daoMap[entitiesClass.simpleName] as AbstractDao<T, Long>
    }

    private fun getAllDao(
        daoSession: DaoSession,
        extraDaoProvider: ArrayList<RabbitDaoProviderConfig>
    ): List<RabbitDaoProviderConfig> {
        return ArrayList<RabbitDaoProviderConfig>().apply {
            addAll(extraDaoProvider)
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
                    daoSession.rabbitFPSInfoDao as AbstractDao<Any, Long>
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

            add(
                RabbitDaoProviderConfig(
                    RabbitAppPerformanceInfo::class.java as Class<Any>,
                    daoSession.rabbitAppPerformanceInfoDao as AbstractDao<Any, Long>
                )
            )

            add(
                RabbitDaoProviderConfig(
                    RabbitAnrInfo::class.java as Class<Any>,
                    daoSession.rabbitAnrInfoDao as AbstractDao<Any, Long>
                )
            )
        }
    }

}