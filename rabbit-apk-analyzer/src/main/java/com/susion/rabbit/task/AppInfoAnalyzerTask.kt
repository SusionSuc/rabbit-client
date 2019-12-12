package com.susion.rabbit.task

import com.google.gson.Gson
import com.susion.rabbit.base.AnalyzerTask
import com.susion.rabbit.entities.UnZipApkFileInfo
import com.susion.rabbit.helper.ManifestParser
import com.susion.rabbit.helper.Utils
import java.io.File

/**
 * susionwang at 2019-11-29
 */
class AppInfoAnalyzerTask : AnalyzerTask {

    companion object {
        val NAME = "AppInfo"
    }

    val MANIFEST_FILE_NAME = "AndroidManifest.xml"
    val ARSC_FILE_NAME = "resources.arsc"

    override fun analyze(apkInfo: UnZipApkFileInfo): String {

        val appInfo = AppInfo()

        appInfo.appSize = Utils.formatFileSize(apkInfo.apkSize)

        val manifestParser = ManifestParser(
            File(apkInfo.unZipDir, MANIFEST_FILE_NAME),
            File(apkInfo.unZipDir, ARSC_FILE_NAME)
        )
        val manifestTag = manifestParser.parse()

        appInfo.versionCode = manifestTag.get("android:versionCode")?.asString ?: ""
        appInfo.versionName = manifestTag.get("android:versionName")?.asString ?: ""

//        if (manifestTag.has("application")) {
//            val applicationTag = manifestTag.getAsJsonObject("application")
//            appInfo.appName = applicationTag.get("android:label")?.asString ?: ""
//        }

        return Gson().toJson(appInfo)
    }

    override fun getResultName() = NAME

    class AppInfo(
        var versionCode: String = "",
        var versionName: String = "",
        var appSize: String = ""
    )

}