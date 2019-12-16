package com.susion.rabbit

import com.google.gson.Gson
import com.susion.rabbit.base.AnalyzerTask
import com.susion.rabbit.helper.FileUtil
import com.susion.rabbit.task.*
import java.io.File
import java.lang.StringBuilder
import kotlin.system.exitProcess

/**
 * susionwang at 2019-11-29
 */
object ApkAnalyzer {

    private val analyzerTasks = ArrayList<AnalyzerTask>().apply {
        add(AppInfoAnalyzerTask())
        add(BigImageResAnalyzerTask())
        add(ApkComposeAnalyzerTask())
        add(DuplicatedFileAnalyzerTask())
        add(MethodCountAnalyzerTask())
    }

    var globalConfig: Config = Config()

    @JvmStatic
    fun main(args: Array<String>) {

        if (args.isEmpty()) {
            errorExit("请输入配置文件Path")
        }

        globalConfig = loadConfig(args[0]) ?: Config("")

        if (!globalConfig.isValid()) {
            errorExit("配置文件错误!")
        }

        val unzipResult = UnzipApkTask().unzipApk(globalConfig.apkPath)

        val jsonStr = StringBuilder()
        jsonStr.append("{")
        analyzerTasks.forEachIndexed { index, analyzerTask ->
            jsonStr.append("\"${analyzerTask.getResultName()}\":")
            jsonStr.append(analyzerTask.analyze(unzipResult))
            if (index != analyzerTasks.size - 1) {
                jsonStr.append(",")
            }
        }
        jsonStr.append("}")

        writeResult(jsonStr.toString(), File(unzipResult.unZipDir).parentFile)

    }

    private fun loadConfig(filePath: String): Config? {
        try {
            val configStr = FileUtil.getStringFromFile(File(filePath))
            return Gson().fromJson(configStr, Config::class.java)
        } catch (e: Exception) {
        }
        return null
    }

    @JvmStatic
    fun errorExit(errMsg: String) {
        print(errMsg)
        exitProcess(0)
    }

    private fun writeResult(resultStr: String, targetDir: File) {
        if (!targetDir.exists()) return
        val resultFile = File(targetDir, "apk-analyzer-result.json")

        if (resultFile.exists()) {
            resultFile.delete()
        }

        resultFile.writeText(resultStr)
    }

}