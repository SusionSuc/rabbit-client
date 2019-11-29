package com.susion.rabbit.task

import com.google.gson.Gson
import com.susion.rabbit.Config
import com.susion.rabbit.base.AnalyzerTask
import com.susion.rabbit.model.task.util.ManifestParser
import com.susion.rabbit.utils.Utils
import java.io.File

/**
 * susionwang at 2019-11-29
 */
class AppInfoAnalyzerTask : AnalyzerTask {

    val MANIFEST_FILE_NAME = "AndroidManifest.xml"
    val ARSC_FILE_NAME = "resources.arsc"
    private val appInfo = AppInfo()

    override fun analyze(config: Config): String {
        appInfo.appSize = Utils.formatImageSize(File(config.apkPath).length())

        val manifestParser = ManifestParser(File(config.unzipApkPath, MANIFEST_FILE_NAME), File(config.unzipApkPath, ARSC_FILE_NAME))
        val manifestTag = manifestParser.parse()

        appInfo.versionCode = manifestTag.get("android:versionCode")?.asString ?: ""
        appInfo.versionName = manifestTag.get("android:versionName")?.asString ?: ""

//        if (manifestTag.has("application")) {
//            val applicationTag = manifestTag.getAsJsonObject("application")
//            appInfo.appName = applicationTag.get("android:label")?.asString ?: ""
//        }

        return Gson().toJson(appInfo)
    }

    override fun getResultName() = "AppInfo"

    class AppInfo(
        var appName: String = "",
        var versionCode: String = "",
        var versionName: String = "",
        var appSize: String = ""
    )

}