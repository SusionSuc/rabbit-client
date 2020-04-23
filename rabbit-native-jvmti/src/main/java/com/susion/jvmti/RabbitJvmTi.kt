package com.susion.jvmti

import android.content.Context
import android.os.Build
import android.os.Debug
import com.susion.rabbit.base.RabbitLog
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

/**
 * susionwang at 2020-04-23
 */

object RabbitJvmTi {

    private val TAG = "RabbitJvmTi"
    private val RABBIT_JVMTI_AGENT = "rabbit-jvmti"

    fun init(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val classLoader = context.classLoader
                val findLibrary =
                    ClassLoader::class.java.getDeclaredMethod("findLibrary", String::class.java)
                val jvmtiAgentLibPath =
                    findLibrary.invoke(classLoader, RABBIT_JVMTI_AGENT) as String
                RabbitLog.d(TAG, "jvmtiagentlibpath: $jvmtiAgentLibPath")
                val filesDir = context.filesDir
                val jvmtiLibDir = File(filesDir, "jvmti")
                if (!jvmtiLibDir.exists()) {
                    jvmtiLibDir.mkdirs()
                }

                val agentLibSo = File(jvmtiLibDir, "agent.so")
                if (agentLibSo.exists()) {
                    agentLibSo.delete()
                }
                Files.copy(
                    Paths.get(File(jvmtiAgentLibPath).absolutePath),
                    Paths.get(agentLibSo.absolutePath)
                )

                RabbitLog.d(TAG, agentLibSo.absolutePath + "," + context.packageCodePath)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    Debug.attachJvmtiAgent(agentLibSo.absolutePath, null, classLoader)
                } else {
                    try {
                        val vmDebugClazz = Class.forName("dalvik.system.VMDebug")
                        val attachAgentMethod =
                            vmDebugClazz.getMethod("attachAgent", String::class.java)
                        attachAgentMethod.isAccessible = true
                        attachAgentMethod.invoke(null, agentLibSo.absolutePath)
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }

                }
                System.loadLibrary(RABBIT_JVMTI_AGENT)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

}