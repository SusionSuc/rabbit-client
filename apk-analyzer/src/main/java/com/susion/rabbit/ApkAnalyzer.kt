package com.susion.rabbit

import com.google.gson.Gson
import com.susion.rabbit.base.AnalyzerTask
import com.susion.rabbit.model.utils.FileUtil
import com.susion.rabbit.task.AppInfoAnalyzerTask
import java.io.File
import kotlin.system.exitProcess

/**
 * susionwang at 2019-11-29
 */
object ApkAnalyzer {

    private val analyzerTasks = ArrayList<AnalyzerTask>().apply {
        add(AppInfoAnalyzerTask())
    }
    private var config: Config = Config()

    @JvmStatic
    fun main(args: Array<String>) {

        val args = arrayOf("/Users/susionwang/Desktop/ApkAnalyzer.json")

        if (args.isEmpty()) {
            errorExit("请输入配置文件Path")
        }

        config = loadConfig(args[0]) ?: Config("")

        if (!config.isValid()) {
            errorExit("配置文件错误!")
        }

        val unzipResult = UnzipApkTask().unzipApk(config.apkPath)
        config.unzipApkPath = unzipResult.unzipPath
        analyzerTasks.forEach {
            val resultStr = it.analyze(config)
            print("analyze result : ${it.getResultName()} : $resultStr")
        }

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

}