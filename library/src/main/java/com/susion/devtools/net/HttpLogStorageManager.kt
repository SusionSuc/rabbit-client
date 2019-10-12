package com.susion.devtools.net

import com.susion.devtools.base.DevToolsFileStorageManager
import com.susion.devtools.net.entities.HttpLogInfo
import com.susion.devtools.utils.runOnIoThread
import io.reactivex.disposables.Disposable

/**
 * susionwang at 2019-09-24
 */
object HttpLogStorageManager : DevToolsFileStorageManager() {

    private val TAG = javaClass.simpleName

    private val disposableList = ArrayList<Disposable>()

    fun saveLogInfoToLocal(logInfo: HttpLogInfo) {
        if (!logInfo.isValid()) return
        val dis = runOnIoThread {
            saveObjToLocalFile(logInfo, TYPE_HTTP_LOG)
        }
        disposableList.add(dis)
    }

    fun getAllLogFiles(loadResult: (logInfos: List<HttpLogInfo>) -> Unit) {
        val dis = getAllExceptionFiles(TYPE_HTTP_LOG, HttpLogInfo::class.java, loadResult)
        disposableList.add(dis)
    }

    fun destroy() {
        disposableList.forEach {
            it.dispose()
        }
    }

}