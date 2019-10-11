package com.susion.devtools.exception

import android.os.Environment
import com.google.gson.Gson
import com.susion.devtools.DevTools
import com.susion.devtools.exception.entities.ExceptionInfo
import com.susion.devtools.utils.FileUtils
import com.susion.devtools.utils.runOnIoThread
import io.reactivex.disposables.Disposable
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * susionwang at 2019-10-10
 * 异常日志
 */
object ExceptionLogStorageManager {

    private val TAG = javaClass.simpleName
    private val FILE_TIME_FORMAT = "yyyy-MM-dd-HH-mm-ss"
    private val MAX_FILE_NUMBER = 50
    private val disposableList = ArrayList<Disposable>()

    fun saveExceptionToLocal(e: Throwable) {
        val dis = runOnIoThread {
            saveToLocalFile(translateThrowableToExceptionInfo(e))
        }
        disposableList.add(dis)
    }

    fun getAllExceptionFiles(loadResult: (exceptionList: List<ExceptionInfo>) -> Unit) {
        val dis = runOnIoThread({
            val exceptionList = ArrayList<ExceptionInfo>()
            val logParentDir = File(getLogDir())
            if (logParentDir.exists()) {
                logParentDir.listFiles().forEach { logFile ->
                    if (logFile != null && logFile.isFile) {
                        val exceptionInfo = readLogInfoFromFile(logFile)
                        if (exceptionInfo != null) {
                            exceptionList.add(exceptionInfo)
                        }
                    }
                }
            }
            exceptionList.sortByDescending { it.time }
            exceptionList
        }, {
            loadResult(it)
        })
        disposableList.add(dis)
    }

    private fun readLogInfoFromFile(logFile: File): ExceptionInfo? {
        FileReader(logFile).use {
            val str = it.readText()
            if (str.isNotEmpty()) {
                return try {
                    Gson().fromJson(str, ExceptionInfo::class.java)
                } catch (e: Exception) {
                    null
                }
            }
        }
        return null
    }

    private fun translateThrowableToExceptionInfo(e: Throwable): ExceptionInfo {
        val exceptionInfo = ExceptionInfo()
        val strWriter = StringWriter()
        e.printStackTrace(PrintWriter(strWriter))
        exceptionInfo.crashTraceStr = strWriter.buffer.toString()
        exceptionInfo.exceptionName = e.javaClass.name
        exceptionInfo.simpleMessage = e.message ?: ""
        exceptionInfo.filePath = File(getLogDir(), getFileName()).absolutePath
        return exceptionInfo
    }

    private fun saveToLocalFile(exceptionInfo: ExceptionInfo) {
        assertMaxFileNumber()
        val savedStr = Gson().toJson(exceptionInfo)
        val file = File(exceptionInfo.filePath)
        if (!FileUtils.createFileByDeleteOldFile(file.absolutePath)) {
            return
        }
        val fileWriter = FileWriter(file)
        fileWriter.use {
            fileWriter.write(savedStr)
        }
    }

    private fun getFileName(): String {
        val timePrefix = SimpleDateFormat(FILE_TIME_FORMAT).format(Date())
        return "${timePrefix}.txt"
    }

    private fun getLogDir(): String {
        val logDir = "${Environment.getExternalStorageDirectory()}/DevTools/AppExceptionLog"
        val appId = DevTools.application?.packageName ?: logDir
        return "${logDir}${appId.replace(".", "")}/"
    }

    /**
     * delete out of data http log file to make sure max number is [MAX_FILE_NUMBER]
     * */
    private fun assertMaxFileNumber() {
        val logDir = File(getLogDir())
        if (logDir.exists()) {
            val sortedFiles = logDir.listFiles() ?: return
            if (sortedFiles.size < MAX_FILE_NUMBER) return
            sortedFiles.sortByDescending { getLogFileTime(it.name) }
            (MAX_FILE_NUMBER - 1 until sortedFiles.size).forEach {
                sortedFiles[it].delete()
            }
        }
    }

    private fun getLogFileTime(fileName: String): Date {
        val time = fileName.substring(0, fileName.indexOf('_'))
        return SimpleDateFormat(FILE_TIME_FORMAT).parse(time)
    }

    fun destroy() {
        disposableList.forEach {
            it.dispose()
        }
    }

}