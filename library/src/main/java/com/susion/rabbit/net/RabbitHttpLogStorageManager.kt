package com.susion.rabbit.net

import android.util.Log
import com.susion.rabbit.base.RabbitFileStorageManager
import com.susion.rabbit.net.entities.RabbitHttpLogInfo
import com.susion.rabbit.utils.runOnIoThread
import io.reactivex.disposables.Disposable

/**
 * susionwang at 2019-09-24
 */
object RabbitHttpLogStorageManager : RabbitFileStorageManager() {

    private val TAG = javaClass.simpleName

    private val disposableList = ArrayList<Disposable>()

    fun saveLogInfoToLocal(logInfo: RabbitHttpLogInfo) {
        if (!logInfo.isValid()) return
        val dis = runOnIoThread {
            Log.d(TAG, "save log : ${logInfo.path}")
            saveObjToLocalFile(logInfo, TYPE_HTTP_LOG)
        }
        disposableList.add(dis)
    }

    fun getAllLogFiles(loadResult: (logInfos: List<RabbitHttpLogInfo>) -> Unit) {
        val dis = getAllExceptionFiles(TYPE_HTTP_LOG, RabbitHttpLogInfo::class.java, loadResult)
        disposableList.add(dis)
    }

    fun destroy() {
        disposableList.forEach {
            it.dispose()
        }
    }

}