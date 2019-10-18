package com.susion.rabbit.base

import android.os.Environment
import com.google.gson.Gson
import com.susion.rabbit.Rabbit
import com.susion.rabbit.utils.FileUtils
import com.susion.rabbit.utils.runOnIoThread
import io.reactivex.disposables.Disposable
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

/**
 * susionwang at 2019-10-12
 * 文件存储功能的基类
 */
open class RabbitFileStorageManager {

    companion object {
        const val TYPE_EXCEPTION = "exception"
        const val TYPE_HTTP_LOG = "httplog"
    }

    private val TAG = javaClass.simpleName
    private val FILE_TIME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"

    fun <T : RabbitFileBaseInfo> getAllExceptionFiles(
        fileType: String,
        ktClass: Class<T>,
        loadResult: (exceptionList: List<T>) -> Unit
    ): Disposable {
        return runOnIoThread({
            val exceptionList = ArrayList<T>()
            val logParentDir = File(getLogDirByFileType(fileType))
            if (logParentDir.exists()) {
                logParentDir.listFiles().forEach { logFile ->
                    if (logFile != null && logFile.isFile) {
                        val exceptionInfo = readObjectFormFile(logFile, ktClass)
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
    }

    private fun <T : Any> readObjectFormFile(logFile: File, ktClass: Class<T>): T? {
        FileReader(logFile).use {
            val str = it.readText()
            if (str.isNotEmpty()) {
                return try {
                    Gson().fromJson(str, ktClass)
                } catch (e: Exception) {
                    null
                }
            }
        }
        return null
    }

    fun saveObjToLocalFile(obj: Any, fileType: String) {
        assertMaxFileNumber(fileType)
        val savedStr = Gson().toJson(obj)
        val file = File(getLogDirByFileType(fileType), getFileName(fileType))
        if (!FileUtils.createFileByDeleteOldFile(file.absolutePath)) {
            return
        }
        val fileWriter = FileWriter(file)
        fileWriter.use {
            fileWriter.write(savedStr)
        }
    }

    fun getLogDirByFileType(fileType: String): String {
        val logDir = "${Environment.getExternalStorageDirectory()}/Rabbit/$fileType"
        val appId = Rabbit.application?.packageName ?: logDir
        return "${logDir}_${appId.replace(".", "")}/"
    }

    /**
     * delete out of data http log file to make sure max number is [MAX_FILE_NUMBER]
     * */
    private fun assertMaxFileNumber(fileType: String) {
        val MAX_FILE_NUMBER = getMaxFileNumber(fileType)
        val logDir = File(getLogDirByFileType(fileType))
        if (logDir.exists()) {
            val sortedFiles = logDir.listFiles() ?: return
            if (sortedFiles.size < MAX_FILE_NUMBER) return
            sortedFiles.sortByDescending { getLogFileTime(it.name) }
            (MAX_FILE_NUMBER - 1 until sortedFiles.size).forEach {
                sortedFiles[it].delete()
            }
        }
    }

    private fun getMaxFileNumber(fileType: String): Int {
        return when (fileType) {
            TYPE_EXCEPTION -> 50
            TYPE_HTTP_LOG -> 100
            else -> 20
        }
    }

    private fun getLogFileTime(fileName: String): Date {
        val time = fileName.substring(fileName.indexOf('_')+1, fileName.indexOf('.'))
        try {
            return  SimpleDateFormat(FILE_TIME_FORMAT).parse(time)
        }catch (e:java.lang.Exception){
            return Date()
        }
    }

    private fun getFileName(fileType: String): String {
        val timePrefix = SimpleDateFormat(FILE_TIME_FORMAT).format(Date())
        return "${fileType}_${timePrefix}.txt"
    }

    fun getFilePath(fileType: String): String =
        File(getLogDirByFileType(fileType), getFileName(fileType)).absolutePath

}