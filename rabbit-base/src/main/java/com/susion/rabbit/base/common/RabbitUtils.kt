package com.susion.rabbit.base.common

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Looper
import android.text.TextUtils
import com.susion.rabbit.base.RabbitLog
import com.susion.rabbit.base.TAG_MONITOR
import com.susion.rabbit.base.entities.RabbitAnrInfo
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * susionwang at 2019-12-13
 * 存放一些通用的工具人
 */
object RabbitUtils {

    fun isMainProcess(context: Context?): Boolean {
        return context != null && context.packageName == getCurrentProcessName(context)
    }

    fun getCurrentProcessName(context: Context): String {
        val pid = android.os.Process.myPid()
        var processName = ""
        val manager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (process in manager.runningAppProcesses) {
            if (process.pid == pid) {
                processName = process.processName
            }
        }
        return processName
    }

    fun classInPkgList(className: String, pkgList: List<String>): Boolean {

        if (pkgList.isEmpty()) return true

        pkgList.forEach {
            if (className.contains(it)) {
                return true
            }
        }
        return false
    }

    fun dropPackageName(className: String): String {
        val strSlice = className.split(".")
        if (strSlice.size < 2) return className
        return "${strSlice[strSlice.size - 2]}.${strSlice[strSlice.size - 1]}"
    }

    fun isMainThread(threadId: Long): Boolean {
        return Looper.getMainLooper().thread.id == threadId
    }

    fun getAbiList(): String {
        if (Build.VERSION.SDK_INT >= 21) {
            return TextUtils.join(",", Build.SUPPORTED_ABIS)
        } else {
            val abi = Build.CPU_ABI
            val abi2 = Build.CPU_ABI2
            return if (TextUtils.isEmpty(abi2)) {
                abi
            } else {
                "$abi,$abi2"
            }
        }
    }

}