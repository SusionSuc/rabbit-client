package com.susion.rabbit.storage

import com.susion.rabbit.utils.runOnIoThread
import io.reactivex.disposables.Disposable
import io.realm.RealmModel

/**
 * susionwang at 2019-10-12
 * 内容存储的基类
 */
open abstract class RabbitDbStorageManager {

    private val TAG = javaClass.simpleName
    private val disposableList = ArrayList<Disposable>()

    fun <T : RealmModel> getAllExceptionFiles(
        ktClass: Class<T>,
        loadResult: (exceptionList: List<T>) -> Unit
    ) {
        runOnIoThread({
            RabbitRealmHelper.getAllDataWithDescendingSort(ktClass, getDbTimeField())
        },{
            loadResult(it)
        })
    }

    fun saveObjToLocalFile(obj: RealmModel) {
        val dis = runOnIoThread {
            assertMaxFileNumber(obj)
            RabbitRealmHelper.saveObj(obj)
        }
        disposableList.add(dis)
    }

    /**
     * delete out of data http log file to make sure max number is [MAX_FILE_NUMBER]
     * */
    private fun assertMaxFileNumber(obj: RealmModel) {
        val MAX_FILE_NUMBER = getMaxDataNumber()
        val currentCount = RabbitRealmHelper.getDataCount(obj.javaClass)
        if (currentCount < MAX_FILE_NUMBER) return
        RabbitRealmHelper.deleteFirstData(
            obj.javaClass,
            currentCount - MAX_FILE_NUMBER,
            getDbTimeField()
        )
    }

    abstract fun getMaxDataNumber(): Int

    abstract fun getDbTimeField(): String

    fun destroy() {
        disposableList.forEach {
            it.dispose()
        }
    }

}