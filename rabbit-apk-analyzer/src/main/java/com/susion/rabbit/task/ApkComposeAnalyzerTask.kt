package com.susion.rabbit.task

import com.google.gson.Gson
import com.susion.rabbit.base.AnalyzerTask
import com.susion.rabbit.entities.UnZipApkFileInfo
import com.susion.rabbit.helper.Utils

/**
 * susionwang at 2019-12-02
 *
 * 分析Apk的组成
 */
class ApkComposeAnalyzerTask : AnalyzerTask {

    data class ApkFileCompose(val type: String, @Transient var totalSize: Long, var totalSizeStr:String = "")

    override fun analyze(unZipContext: UnZipApkFileInfo): String {

        val composeList = ArrayList<ApkFileCompose>()

        unZipContext.fileMap.forEach { (type, fileList) ->
            val fileCompose = ApkFileCompose(type, 0)
            fileList.forEach {
                fileCompose.totalSize += it.size
            }
            composeList.add(fileCompose)
        }

        val resultList = composeList.sortedByDescending {
            it.totalSizeStr = Utils.formatFileSize(it.totalSize)
            it.totalSize
        }.filter { (it.totalSize / 1024) > 10 && it.type.isNotEmpty()}

        return Gson().toJson(resultList)

    }

    override fun getResultName() = "ApkCompose"

}