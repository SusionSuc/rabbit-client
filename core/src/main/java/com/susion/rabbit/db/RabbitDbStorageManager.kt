package com.susion.rabbit.db

import com.susion.rabbit.Rabbit
import com.susion.rabbit.base.entities.RabbitGreenDaoInfo
import com.susion.rabbit.utils.runOnIoThread
import com.susion.rabbit.utils.runOnIoThreadWithData
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

    fun <T : RabbitGreenDaoInfo> getAll(
        ktClass: Class<T>,
        loadResult: (exceptionList: List<T>) -> Unit
    ) {
        runOnIoThreadWithData({
            greenDaoDbManage.getAllDataWithDescendingSort(ktClass, "time")
        }, {
            loadResult(it)
        })
    }

    fun save(obj: RabbitGreenDaoInfo) {
        val dis = runOnIoThread({
            greenDaoDbManage.saveObj(obj)
        })
        disposableList.add(dis)
    }

    fun saveSync(obj: RabbitGreenDaoInfo) {
        greenDaoDbManage.saveObj(obj)
    }

    fun <T : Any> clearAllData(clazz: Class<T>) {
        runOnIoThread({ greenDaoDbManage.clearAllData(clazz) })
    }

    fun destroy() {
        disposableList.forEach {
            it.dispose()
        }
    }

    fun clearOldSessionData() {
        greenDaoDbManage.clearOldSessionData()
    }

}