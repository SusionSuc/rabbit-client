package com.susion.devtools.net

import android.os.Environment
import android.util.Log
import com.google.gson.Gson
import com.susion.devtools.net.entities.HttpLogInfo
import com.susion.devtools.utils.FileUtils
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File
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
    private val LOG_DIR = "${Environment.getExternalStorageDirectory()}/DevToolsNetLog/"
    private val disposableList = ArrayList<Disposable>()
    private val FILE_TIME_FORMAT = "yyyy-MM-dd-HH-mm-ss"

    fun saveLogInfoToLocal(logInfo: HttpLogInfo) {
        runOnIoThread {
            saveToLocalFile(logInfo)
        }
    }

    private fun saveToLocalFile(logInfo: HttpLogInfo) {
        assertMaxFileNumber()
        val savedStr = Gson().toJson(logInfo)
        val file = File(LOG_DIR, getFileName(logInfo))
        if (!FileUtils.createFileByDeleteOldFile(file.absolutePath)){
            return
        }
        val fileWriter = FileWriter(file)
        fileWriter.use {
            fileWriter.write(savedStr)
        }
        Log.d(TAG, "文件保存成功 : ${file.absolutePath}")
    }

    private fun assertMaxFileNumber() {
        val logDir = File(LOG_DIR)
        if (logDir.exists()){
            val sortedFiles = logDir.listFiles().sortBy {
                getLogFileTime(it.name)
            }
        }

    }

    private fun runOnIoThread(block: () -> Unit) {
        val saveDis = Observable.create<Boolean> {
            block()
            it.onNext(true)
            it.onComplete()
        }.subscribeOn(Schedulers.io()).subscribe()
        disposableList.add(saveDis)
    }

    private fun md5(str: String): String {
        val digest = MessageDigest.getInstance("MD5")
        val result = digest.digest(str.toByteArray())
        println("result${result.size}")
        return toHex(result)
    }

    private fun toHex(byteArray: ByteArray): String {
        val result = with(StringBuilder()) {
            byteArray.forEach {
                val hex = it.toInt() and (0xFF)
                val hexStr = Integer.toHexString(hex)
                if (hexStr.length == 1) {
                    this.append("0").append(hexStr)
                } else {
                    this.append(hexStr)
                }
            }
            this.toString()
        }
        //转成16进制后是32字节
        return result
    }

    private fun getFileName(logInfo: HttpLogInfo): String {
        val timePrifix = SimpleDateFormat(FILE_TIME_FORMAT).format(Date())
        return "${timePrifix}_${md5(logInfo.toString())}.txt"
    }

    private fun getLogFileTime(fileName:String):Date{
        val time = fileName.substring(0, fileName.indexOf('_'))
        return SimpleDateFormat(FILE_TIME_FORMAT).parse(time)
    }

    fun destroy() {
        disposableList.forEach {
            it.dispose()
        }
    }

}