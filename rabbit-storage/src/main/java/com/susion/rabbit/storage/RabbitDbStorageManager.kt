package com.susion.rabbit.storage

import com.susion.rabbit.base.common.RabbitAsync
import com.susion.rabbit.base.entities.RabbitInfoProtocol
import com.susion.rabbit.base.greendao.DaoSession
import io.reactivex.disposables.Disposable
import org.greenrobot.greendao.Property
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * susionwang at 2019-10-12
 * 内容存储的基类
 * 封装数据层, 数据库异步操作
 * 可以直接使用它进行数据库操作
 */
object RabbitDbStorageManager {

    private val disposableList = ArrayList<Disposable>()
    private val DB_THREAD = ThreadPoolExecutor(5, 10, 10, TimeUnit.SECONDS, LinkedBlockingDeque(),
        ThreadFactory { r ->
            Thread(r, "rabbit_db_manager_thread${System.currentTimeMillis()}")
        }).apply {
        allowCoreThreadTimeOut(true)
    }
    var daoSession: DaoSession? = null
    private val greenDaoDbManage by lazy {
        RabbitGreenDaoDbManage(RabbitStorage.application!!, daoSession)
    }

    /**
     * 查询数据集合
     *
     * 默认按 time 字段 降序排序。 FIX: 有点hardcode
     * */
    fun <T : RabbitInfoProtocol> getAll(
        ktClass: Class<T>,
        condition: Pair<Property, String>? = null,
        sortField: String = RabbitInfoProtocol.PROPERTITY_TIME,
        count: Int = 0,
        orderDesc: Boolean = false,
        loadResult: (exceptionList: List<T>) -> Unit
    ) {
        RabbitAsync.asyncRunWithResult({
            greenDaoDbManage.getDatas(
                ktClass,
                condition?.first?.eq(condition.second),
                sortField,
                count,
                orderDesc
            )
        }, DB_THREAD, {
            loadResult(it)
        })
    }

    fun <T : RabbitInfoProtocol> getAllSync(
        ktClass: Class<T>,
        condition: Pair<Property, String>? = null,
        sortField: String = RabbitInfoProtocol.PROPERTITY_TIME,
        count: Int = 0,
        orderDesc: Boolean = false
    ): List<T> {
        return greenDaoDbManage.getDatas(
            ktClass,
            condition?.first?.eq(condition.second),
            sortField,
            count,
            orderDesc
        )
    }

    fun save(obj: RabbitInfoProtocol) {
        val dis = RabbitAsync.asyncRun({
            greenDaoDbManage.saveObj(obj)
        }, DB_THREAD, {
            RabbitStorage.notifyEventListenerNewDataSave(obj)
        })
        disposableList.add(dis)
    }

    fun saveSync(obj: RabbitInfoProtocol) {
        greenDaoDbManage.saveObj(obj)
        RabbitStorage.notifyEventListenerNewDataSave(obj)
    }

    fun <T : RabbitInfoProtocol> getObjSync(clazz: Class<T>, id: Long): T? {
        return greenDaoDbManage.getObjSync(clazz, id)
    }

    fun <T : Any> clearAllData(clazz: Class<T>) {
        RabbitAsync.asyncRun({ greenDaoDbManage.clearAllData(clazz) }, DB_THREAD)
    }

    fun <T : Any> delete(clazz: Class<T>, id: Long) {
        RabbitAsync.asyncRun({
            greenDaoDbManage.deleteById(clazz, id)
        }, DB_THREAD)
    }

    fun <T : RabbitInfoProtocol> delete(clazz: Class<T>, condition: Pair<Property, String>) {
        RabbitAsync.asyncRun({
            greenDaoDbManage.delete(clazz, condition.first.eq(condition.second))
        }, DB_THREAD)
    }

    fun <T : Any> dataCount(clazz: Class<T>): Long {
        return greenDaoDbManage.allDataCount(clazz)
    }

    fun <T : RabbitInfoProtocol> distinct(
        clazz: Class<T>,
        columnName: String,
        loadResult: (exceptionList: List<String>) -> Unit
    ) {
        RabbitAsync.asyncRunWithResult({
            greenDaoDbManage.distinct(clazz, columnName)
        }, {
            loadResult(it)
        })
    }

    fun <T : RabbitInfoProtocol> updateOrCreate(
        clazz: Class<T>,
        obj: RabbitInfoProtocol,
        id: Long
    ) {
        RabbitAsync.asyncRun({
            greenDaoDbManage.updateOrCreate(clazz, obj, id)
        }, DB_THREAD)
    }

    fun destroy() {
        disposableList.forEach {
            it.dispose()
        }
    }

    fun clearOldSessionData() {
        RabbitAsync.asyncRun({
            greenDaoDbManage.clearOldSessionData()
        }, DB_THREAD)
    }

}