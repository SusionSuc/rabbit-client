package com.susion.rabbit.monitor.utils

import com.susion.rabbit.base.RabbitLog
import com.susion.rabbit.base.TAG_MONITOR
import com.susion.rabbit.base.entities.RabbitAnrInfo
import com.susion.rabbit.monitor.RabbitMonitor
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * susionwang at 2020-01-06
 */
object RabbitMonitorUtils {

    /*
     * "\n\n----- pid %d at %04d-%02d-%02d %02d:%02d:%02d -----\n"
     * "Cmd line: %s\n"
     * "......"
     * "----- end %d -----\n"
    * */
    fun fillSimpleAnrInfo(anrInfo: RabbitAnrInfo, file: File) {

        val patPidTime = Pattern.compile("^-----\\spid\\s(\\d+)\\sat\\s(.*)\\s-----$")
        val processId = android.os.Process.myPid()
        val br = BufferedReader(FileReader(file))
        var line: String
        var matcher: Matcher
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

        anrInfo.invalid = false
        anrInfo.filePath = file.absolutePath
        anrInfo.pageName = RabbitMonitor.getCurrentPage()

        RabbitLog.d(
            TAG_MONITOR,
            "start parse file .. process id -> $processId ; file path -> ${anrInfo.filePath}"
        )

        br.use {
            while (true) {
                line = br.readLine() ?: break
                if (line.startsWith("----- pid ")) {
                    //1. 获取进程pid和日志时间
                    matcher = patPidTime.matcher(line)
                    if (!matcher.find() || matcher.groupCount() != 2) {
                        continue
                    }
                    val sPid = matcher.group(1)
                    val sLogTime = matcher.group(2)
                    if (sPid == null || sLogTime == null) {
                        continue
                    }
                    val dLogTime = dateFormat.parse(sLogTime) ?: continue
                    anrInfo.time = dLogTime.time
                    RabbitLog.d(TAG_MONITOR, "anrInfo time : $dLogTime ; processId : $sPid")

                    if (processId != Integer.parseInt(sPid)) { //不是自己进程的anr
                        RabbitLog.d(
                            TAG_MONITOR,
                            "anr invalid! happen in process : $sPid ; current pid : $processId"
                        )
                        break
                    }
                }
            }
        }
    }

    fun getAnrStack(anr: RabbitAnrInfo): String {
        var line: String
        val patPidTime = Pattern.compile("^-----\\spid\\s(\\d+)\\sat\\s(.*)\\s-----$")
        var matcher: Matcher
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val anrSB = StringBuilder()
        var invalidPidAnr = false
        BufferedReader(FileReader(File(anr.filePath))).use { br ->
            while (true) {
                line = br.readLine() ?: break
                RabbitLog.d(TAG_MONITOR, line)
                if (!invalidPidAnr && line.startsWith("----- pid ")) {
                    //1. 获取进程pid和日志时间
                    matcher = patPidTime.matcher(line)
                    if (!matcher.find() || matcher.groupCount() != 2) {
                        continue
                    }
                    val sLogTime = matcher.group(2)
                    val dLogTime = dateFormat.parse(sLogTime) ?: continue
                    if (dLogTime.time != anr.time.toLong()) {
                        return ""
                    }
                    RabbitLog.d(TAG_MONITOR, "匹配到anr信息! ---> time : $dLogTime")
                    invalidPidAnr = true
                }

                anrSB.append(line)
                anrSB.append("\n")
            }
        }

        return anrSB.toString()
    }


}