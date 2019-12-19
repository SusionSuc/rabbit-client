package com.susion.rabbit.storage

import com.susion.rabbit.base.common.RabbitAsync
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
 */
object RabbitDbStorageManager {

    private val TAG = javaClass.simpleName
    private val disposableList = ArrayList<Disposable>()
    private val DB_THREAD = ThreadPoolExecutor(5, 10, 10, TimeUnit.SECONDS, LinkedBlockingDeque(),
        ThreadFactory { r ->
            Thread(r, "rabbit_db_manager_thread${System.currentTimeMillis()}")
        }).apply {
        allowCoreThreadTimeOut(true)
    }
    private val greenDaoDbManage by lazy {
        RabbitGreenDaoDbManage(RabbitStorage.application!!)
    }

    /**
     * 查询数据集合
     *
     * 默认按 time 字段 降序排序。 FIX: 有点hardcode
     * */
    fun <T : Any> getAll(
        ktClass: Class<T>,
        condition: Pair<Property, String>? = null,
        sortField: String = "time",
        count: Int = 0,
        orderDesc: Boolean = true,
        loadResult: (exceptionList: List<T>) -> Unit
    ) {
        com.susion.rabbit.base.common.RabbitAsync.asyncRunWithResult({
            greenDaoDbManage.getDatasWithDescendingSort(
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

    fun save(obj: Any) {
        val dis = com.susion.rabbit.base.common.RabbitAsync.asyncRun({
            greenDaoDbManage.saveObj(obj)
        }, DB_THREAD)

        disposableList.add(dis)
        RabbitStorage.eventListener?.onStorageData(obj)
    }

    fun saveSync(obj: Any) {
        greenDaoDbManage.saveObj(obj)
        RabbitStorage.eventListener?.onStorageData(obj)
    }

    fun <T : Any> clearAllData(clazz: Class<T>) {
        com.susion.rabbit.base.common.RabbitAsync.asyncRun({ greenDaoDbManage.clearAllData(clazz) }, DB_THREAD)
    }

    fun <T : Any> delete(clazz: Class<T>, id: Long) {
        com.susion.rabbit.base.common.RabbitAsync.asyncRun({
            greenDaoDbManage.deleteById(clazz, id)
        }, DB_THREAD)
    }

    fun <T : Any> delete(clazz: Class<T>, condition: Pair<Property, String>) {
        com.susion.rabbit.base.common.RabbitAsync.asyncRun({
            greenDaoDbManage.delete(clazz, condition.first.eq(condition.second))
        }, DB_THREAD)
    }

    fun <T : Any> dataCount(clazz: Class<T>): Long {
        return greenDaoDbManage.allDataCount(clazz)
    }

    fun destroy() {
        disposableList.forEach {
            it.dispose()
        }
    }

    fun clearOldSessionData() {
        com.susion.rabbit.base.common.RabbitAsync.asyncRun({
            greenDaoDbManage.clearOldSessionData()
        }, DB_THREAD)
    }

}