package com.susion.rabbit.db

import android.content.Context
import com.susion.rabbit.Rabbit
import com.susion.rabbit.base.entities.RabbitGreenDaoInfo
import com.susion.rabbit.exception.entities.RabbitExceptionInfo
import com.susion.rabbit.greendao.DaoMaster
import com.susion.rabbit.net.entities.RabbitHttpLogInfo
import com.susion.rabbit.performance.entities.RabbitAppStartSpeedInfo
import com.susion.rabbit.performance.entities.RabbitBlockFrameInfo
import com.susion.rabbit.performance.entities.RabbitPageSpeedInfo
import org.greenrobot.greendao.AbstractDao
import org.greenrobot.greendao.Property

/**
 * susionwang at 2019-10-21
 * 所有rabbit可以持久数据都应该有对应的 Dao
 */
internal class RabbitGreenDaoDbManage(val context: Context) {

    private val DB_NAME = "rabbit"
    private val daoMap = HashMap<String, AbstractDao<Any, Long>>()

    fun saveObj(obj: RabbitGreenDaoInfo) {
        daoImpl(obj.javaClass)?.save(obj)
    }

    fun <T : Any> getDatas(clazz: Class<T>, count:Int): List<T>{
        return daoImpl(clazz)?.queryBuilder()?.limit(count)?.list() as List<T>
    }

    fun <T : Any> getAllDataWithDescendingSort(clazz: Class<T>, sortField: String): List<T> {
        val dao = daoImpl(clazz)
        val property = getProperties(dao, sortField)
        return dao?.queryBuilder()?.orderDesc(property)?.build()?.list() as List<T>
    }

    private fun <T : Any> getDataWithDescendingSort(clazz: Class<T>, sortField: String, count: Int): List<T> {
        val dao = daoImpl(clazz)
        val property = getProperties(dao, sortField)
        return dao?.queryBuilder()?.orderDesc(property)?.limit(count)?.build()?.list() as List<T>
    }

    fun <T : Any> getDataCount(clazz: Class<T>): Long {
        return daoImpl(clazz)?.count() ?: 0
    }

    private fun <T : Any> getProperties(dao: AbstractDao<T, Long>?, field: String): Property? {
        return dao?.properties?.find { it.name == field }
    }

    fun <T:Any> clearAllData(clazz: Class<T>) {
        daoImpl(clazz)?.deleteAll()
    }

    private fun <T : Any> daoImpl(entitiesClass: Class<T>): AbstractDao<T, Long>? {
        var dao = daoMap[entitiesClass.simpleName]
        if (dao == null) {
            val daoSession =
                DaoMaster(DaoMaster.DevOpenHelper(context, DB_NAME).writableDb).newSession()
            dao = when (entitiesClass) {
                RabbitExceptionInfo::class.java -> daoSession.rabbitExceptionInfoDao as AbstractDao<Any, Long>
                RabbitHttpLogInfo::class.java -> daoSession.rabbitHttpLogInfoDao as AbstractDao<Any, Long>
                RabbitBlockFrameInfo::class.java ->daoSession.rabbitBlockFrameInfoDao as AbstractDao<Any, Long>
                RabbitPageSpeedInfo::class.java -> daoSession.rabbitPageSpeedInfoDao as AbstractDao<Any, Long>
                RabbitAppStartSpeedInfo::class.java -> daoSession.rabbitAppStartSpeedInfoDao as AbstractDao<Any, Long>
                else -> null
            }

            if (dao == null){
                dao = getDaoFromProvider(entitiesClass, Rabbit.geConfig().daoProvider)
            }

            if (dao != null) {
                daoMap[entitiesClass.simpleName] = dao
            }
        }

        return dao as? AbstractDao<T, Long>
    }

    private fun <T> getDaoFromProvider(entitiesClass: Class<T>, daoProvider: List<RabbitDaoPluginProvider>): AbstractDao<Any, Long>? {
        daoProvider.forEach {
            if (it.clazz.simpleName == entitiesClass.simpleName){
                return  it.dao
            }
        }
        return null
    }

    fun clearOldSessionData() {
        val dataClass = Rabbit.geConfig().storageInOnSessionData
        dataClass.forEach {
            clearAllData(it)
        }
    }

}