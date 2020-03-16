package com.susion.rabbit.monitor.instance

import android.content.Context
import android.os.FileObserver
import com.susion.rabbit.base.RabbitLog
import com.susion.rabbit.base.RabbitMonitorProtocol
import com.susion.rabbit.base.TAG_MONITOR
import com.susion.rabbit.base.common.RabbitAsync
import com.susion.rabbit.base.common.RabbitUtils
import com.susion.rabbit.monitor.RabbitMonitor
import java.io.BufferedReader
import java.io.FileReader
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * susionwang at 2020-03-16
 * 监控 /data/anr/trace.txt 文件
 */
class RabbitAnrMonitor(override var isOpen: Boolean = false) : RabbitMonitorProtocol {

    private var lastTime: Long = 0
    private val anrTimeoutMs = (15 * 1000).toLong()
    private val patProcessName = Pattern.compile("^Cmd\\sline:\\s+(.*)$")
    private val patPidTime = Pattern.compile("^-----\\spid\\s(\\d+)\\sat\\s(.*)\\s-----$")
    private val traceFileObserver by lazy {
        object : FileObserver("data/anr/", CLOSE_WRITE) {
            override fun onEvent(event: Int, path: String?) {
                if (path.isNullOrEmpty()) return

                val filepath = "/data/anr/$path"
                if (filepath.contains("trace")) {
                    RabbitAsync.asyncRunWithResult({
                        handleAnrFile(filepath)
                    }, { anrStr ->
                        RabbitLog.d(TAG_MONITOR, "monitor anr! -> $anrStr")
                    })
                }
            }
        }
    }
    private val pid by lazy {
        android.os.Process.myPid()
    }
    private val processName: String by lazy {
        RabbitUtils.getCurrentProcessName(RabbitMonitor.application)
    }

    override fun open(context: Context) {
        isOpen = true
    }

    override fun close() {
        isOpen = false
        traceFileObserver.stopWatching()
    }

    private fun handleAnrFile(filepath: String): String {

        val anrTime = System.currentTimeMillis()

        //check ANR time interval
        if (anrTime - lastTime < anrTimeoutMs) {
            return ""
        }

        //captured ANR
        lastTime = anrTime

        // "\n\n----- pid %d at %04d-%02d-%02d %02d:%02d:%02d -----\n"
        // "Cmd line: %s\n"
        // "......"
        // "----- end %d -----\n"

        var br: BufferedReader? = null
        var line: String?
        var matcher: Matcher
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val sb = StringBuilder()
        var found = false

        try {
            br = BufferedReader(FileReader(filepath))
            line = br.readLine()
            while (!line.isNullOrEmpty()) {

                if (!found && line.startsWith("----- pid ")) {

                    //check current line for PID and log time
                    matcher = patPidTime.matcher(line)
                    if (!matcher.find() || matcher.groupCount() != 2) {
                        continue
                    }
                    val sPid = matcher.group(1)
                    val sLogTime = matcher.group(2)
                    if (sPid == null || sLogTime == null) {
                        continue
                    }
                    if (pid != Integer.parseInt(sPid)) {
                        continue //check PID
                    }
                    val dLogTime = dateFormat.parse(sLogTime) ?: continue
                    val logTime = dLogTime.time

                    if (Math.abs(logTime - anrTime) > anrTimeoutMs) {
                        continue //check log time
                    }

                    //check next line for process name
                    line = br.readLine()
                    if (line == null) {
                        break
                    }
                    matcher = patProcessName.matcher(line)
                    if (!matcher.find() || matcher.groupCount() != 1) {
                        continue
                    }
                    val pName = matcher.group(1)
                    if (pName == null || pName != this.processName) {
                        continue //check process name
                    }

                    found = true

                    sb.append(line).append('\n')
                    sb.append("Mode: Watching /data/anr/*\n")

                    continue
                }

                if (found) {
                    if (line.startsWith("----- end ")) {
                        break
                    } else {
                        sb.append(line).append('\n')
                    }
                }

                line = br.readLine()
            }
            return sb.toString()
        } catch (ignored: Exception) {
            return ""
        } finally {
            if (br != null) {
                try {
                    br.close()
                } catch (ignored: Exception) {
                }

            }
        }
    }

    override fun getMonitorInfo() = RabbitMonitorProtocol.ANR

}