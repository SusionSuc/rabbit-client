package com.susion.rabbit.storage

import android.content.Context
import com.susion.rabbit.base.RabbitLog
import com.susion.rabbit.base.TAG_STORAGE
import com.susion.rabbit.base.config.RabbitDaoProviderConfig
import com.susion.rabbit.base.greendao.DaoSession
import org.greenrobot.greendao.AbstractDao
import org.greenrobot.greendao.Property
import org.greenrobot.greendao.query.WhereCondition

/**
 * susionwang at 2019-10-21
 * 所有rabbit可以持久数据都应该有对应的 Dao
 */
internal class RabbitGreenDaoDbManage(val context: Context, val daoSession: DaoSession? = null) {

    private val MAX_DATA_COUNT = 5000 // 不允许存太多数据
    private val daoMap = HashMap<String, AbstractDao<Any, Long>>()

    fun saveObj(obj: Any) {
        val daoImpl = daoImpl(obj.javaClass) ?: return
        val currentTotalCount = daoImpl.queryBuilder().count()
        if (currentTotalCount > MAX_DATA_COUNT) {
            daoImpl.deleteAll()
        }
        daoImpl.save(obj)
        RabbitLog.d(TAG_STORAGE, "save data $obj")
    }

    fun <T : Any> updateOrCreate(clazz: Class<T>, obj: Any, id: Long) {
        val daoImpl = daoImpl(obj.javaClass) ?: return
        val storageData = getDataById(clazz, id)
        if (storageData == null) {
            daoImpl.save(obj)
        } else {
            daoImpl.update(obj)
        }
    }

    fun <T : Any> getDataById(clazz: Class<T>, id: Long): T? {
        return daoImpl(clazz)?.loadByRowId(id) ?: null
    }

    fun <T : Any> deleteById(clazz: Class<T>, id: Long) {
        daoImpl(clazz)?.deleteByKey(id)
    }

    fun <T : Any> delete(clazz: Class<T>, condition: WhereCondition) {
        getDatas(clazz, condition).forEach {
            daoImpl(clazz)?.delete(it)
        }
    }

    fun <T : Any> distinct(clazz: Class<T>, columnName: String): List<String> {
        val dao = daoImpl(clazz) ?: return emptyList()
        val distinctSQL = "SELECT DISTINCT $columnName FROM ${dao.tablename};"
        val cursor = daoSession?.database?.rawQuery(distinctSQL, null) ?: return emptyList()
        val resList = ArrayList<String>()
        while (cursor.moveToNext()) {
            resList.add(cursor.getString(0))
        }
        return resList
    }

    fun <T : Any> allDataCount(clazz: Class<T>): Long {
        return daoImpl(clazz)?.queryBuilder()?.count() ?: 0
    }

    fun <T : Any> getDatas(
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
        return queryBuilder?.build()?.list() as List<T>
    }

    fun <T : Any> getDataCount(clazz: Class<T>): Long {
        return daoImpl(clazz)?.count() ?: 0
    }

    private fun <T : Any> getProperties(dao: AbstractDao<T, Long>?, field: String): Property? {
        return dao?.properties?.find { it.name == field }
    }

    fun <T : Any> clearAllData(clazz: Class<T>) {
        daoImpl(clazz)?.deleteAll()
    }

    private fun <T : Any> daoImpl(entitiesClass: Class<T>): AbstractDao<T, Long>? {
        var dao = daoMap[entitiesClass.simpleName]
        if (dao == null) {
            dao = getDaoFromProvider(entitiesClass, RabbitStorage.mConfig.daoProvider)

            if (dao != null) {
                daoMap[entitiesClass.simpleName] = dao
            }
        }

        return dao as? AbstractDao<T, Long>
    }

    private fun <T> getDaoFromProvider(
        entitiesClass: Class<T>,
        daoProvider: List<RabbitDaoProviderConfig>
    ): AbstractDao<Any, Long>? {
        daoProvider.forEach {
            if (it.clazz.simpleName == entitiesClass.simpleName) {
                return it.dao
            }
        }
        return null
    }

    fun clearOldSessionData() {
        val dataClass = RabbitStorage.mConfig.storageInOnSessionData
        dataClass.forEach {
            clearAllData(it)
        }
    }

}