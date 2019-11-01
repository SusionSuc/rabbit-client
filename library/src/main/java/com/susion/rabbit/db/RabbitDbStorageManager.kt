package com.susion.rabbit.db

import com.susion.rabbit.Rabbit
import com.susion.rabbit.base.entities.RabbitGreenDaoInfo
import com.susion.rabbit.utils.runOnIoThread
import io.reactivex.disposables.Disposable

/**
 * susionwang at 2019-10-12
 * 内容存储的基类
 */

object RabbitDbStorageManager {

    private val TAG = javaClass.simpleName
    private val disposableList = ArrayList<Disposable>()
    private val greenDaoDbManage by lazy {
        RabbitGreenDaoDbManage(Rabbit.application!!)
    }
    private val SESSION_MAX_DATA_NUMBER = 200

    fun <T : RabbitGreenDaoInfo> getAll(
        ktClass: Class<T>,
        loadResult: (exceptionList: List<T>) -> Unit
    ) {
        runOnIoThread({
            greenDaoDbManage.getAllDataWithDescendingSort(ktClass, "time")
        }, {
            loadResult(it)
        })
    }

    fun save(obj: RabbitGreenDaoInfo) {
        val dis = runOnIoThread {
            assertMaxFileNumber(obj)
            greenDaoDbManage.saveObj(obj)
        }
        disposableList.add(dis)
    }

    fun <T : Any> clearAllData(clazz: Class<T>) {
        runOnIoThread {
            greenDaoDbManage.clearAllData(clazz)
        }
    }

    /**
     * delete out of data http log file to make sure max number is [MAX_FILE_NUMBER]
     * */
    private fun assertMaxFileNumber(obj: RabbitGreenDaoInfo) {
        val sortField = obj.sortField
        val MAX_FILE_NUMBER = SESSION_MAX_DATA_NUMBER
        val currentCount = greenDaoDbManage.getDataCount(obj.javaClass).toInt()
        if (currentCount < MAX_FILE_NUMBER) return
        greenDaoDbManage.deleteOldDataBySortedField(
            obj.javaClass,
            currentCount - MAX_FILE_NUMBER,
            sortField
        )
    }

    fun destroy() {
        disposableList.forEach {
            it.dispose()
        }
    }

}