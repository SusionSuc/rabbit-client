package com.susion.devtools.net

import android.os.Environment
import android.util.Log
import com.google.gson.Gson
import com.susion.devtools.DevTools
import com.susion.devtools.net.entities.HttpLogInfo
import com.susion.devtools.utils.FileUtils
import com.susion.devtools.utils.runOnIoThread
import io.reactivex.disposables.Disposable
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * susionwang at 2019-09-24
 */
object HttpLogStorageManager {

    private val TAG = javaClass.simpleName

    private val MAX_FILE_NUMBER = 30
    private val disposableList = ArrayList<Disposable>()
    private val FILE_TIME_FORMAT = "yyyy-MM-dd-HH-mm-ss"

    fun saveLogInfoToLocal(logInfo: HttpLogInfo) {
        if (!logInfo.isValid())return
        val dis = runOnIoThread {
            saveToLocalFile(logInfo)
        }
        disposableList.add(dis)
    }

    fun getAllLogFiles(loadResult: (logInfos: List<HttpLogInfo>) -> Unit) {
        val dis = runOnIoThread({
            val logList = ArrayList<HttpLogInfo>()
            val logParentDir = File(getLogDir())
            if (logParentDir.exists()) {
                logParentDir.listFiles().forEach { logFile ->
                    if (logFile != null && logFile.isFile) {
                        val logInfo = readLogInfoFromFile(logFile)
                        if (logInfo != null) {
                            logList.add(logInfo)
                        }
                    }
                }
            }
            logList.sortByDescending { it.time }
            logList
        }, {
            loadResult(it)
        })
        disposableList.add(dis)
    }

    private fun readLogInfoFromFile(logFile: File): HttpLogInfo? {
        FileReader(logFile).use {
            val str = it.readText()
            if (str.isNotEmpty()) {
                return try {
                    Gson().fromJson(str, HttpLogInfo::class.java)
                } catch (e: Exception) {
                    null
                }
            }
        }
        return null
    }

    private fun saveToLocalFile(logInfo: HttpLogInfo) {
        assertMaxFileNumber()
        val savedStr = Gson().toJson(logInfo)
        val file = File(getLogDir(), getFileName(logInfo))
        if (!FileUtils.createFileByDeleteOldFile(file.absolutePath)) {
            return
        }
        val fileWriter = FileWriter(file)
        fileWriter.use {
            fileWriter.write(savedStr)
        }
        Log.d(TAG, "文件保存成功 : ${file.absolutePath}")
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

    private fun md5(str: String): String {
        val digest = MessageDigest.getInstance("MD5")
        val result = digest.digest(str.toByteArray())
        println("result${result.size}")
        return toHex(result)
    }

    private fun toHex(byteArray: ByteArray): String {
        return with(StringBuilder()) {
            byteArray.forEach {
                val hex = it.toInt() and (0xFF)
                val hexStr = Integer.toHexString(hex)
                if (hexStr.length == 1) {
                    append("0").append(hexStr)
                } else {
                    append(hexStr)
                }
            }
            toString()
        }
    }

    private fun getFileName(logInfo: HttpLogInfo): String {
        val timePrefix = SimpleDateFormat(FILE_TIME_FORMAT).format(Date())
        return "${timePrefix}_${md5(logInfo.toString())}.txt"
    }

    private fun getLogFileTime(fileName: String): Date {
        val time = fileName.substring(0, fileName.indexOf('_'))
        return SimpleDateFormat(FILE_TIME_FORMAT).parse(time)
    }

    private fun getLogDir():String{
        val logDir =  "${Environment.getExternalStorageDirectory()}/DevToolsNetLog/"
        val appId = DevTools.application?.packageName?: logDir
        return "${logDir}${appId.replace(".","")}/"
    }

    fun destroy() {
        disposableList.forEach {
            it.dispose()
        }
    }

}