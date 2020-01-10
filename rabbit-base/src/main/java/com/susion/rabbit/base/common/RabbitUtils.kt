package com.susion.rabbit.base.common

import android.app.ActivityManager
import android.content.Context

/**
 * susionwang at 2019-12-13
 * 存放一些通用的工具人
 */
object RabbitUtils {

    fun isMainProcess(context: Context): Boolean {
        return context.packageName == getCurrentProcessName(
            context
        )
    }

    private fun getCurrentProcessName(context: Context): String {
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

        val configList = pkgList

        if (configList.isEmpty()) return true

        configList.forEach {
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

}