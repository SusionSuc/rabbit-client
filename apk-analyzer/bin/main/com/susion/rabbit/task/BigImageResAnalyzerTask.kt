package com.susion.rabbit.task

import com.google.gson.Gson
import com.susion.rabbit.ApkAnalyzer
import com.susion.rabbit.base.AnalyzerTask
import com.susion.rabbit.entities.UnZipApkFileInfo
import com.susion.rabbit.helper.Utils

/**
 * susionwang at 2019-12-02
 * 过滤出比较大的图片，这些图片建议进行压缩
 */
class BigImageResAnalyzerTask : AnalyzerTask {

    data class OverLimitImageInfo(val name:String, val size:String = "")

    companion object {
        val NAME = "BigImageRes"
    }

    private val checkedImageTypes = arrayOf("png", "jpeg", "jpg", "webp")

    override fun analyze(unZipContext: UnZipApkFileInfo): String {

        val limitImageSize = ApkAnalyzer.globalConfig.maxImageSizeKB

        val overLimitImages =
            unZipContext.fileMap.filter { checkedImageTypes.contains(it.key) }.flatMap { it.value }
                .filter { (it.size / 1024) > limitImageSize }
                .map { OverLimitImageInfo(Utils.getFileRelativeName(unZipContext.unZipDir,it.path), Utils.formatFileSize(it.size)) }

        return Gson().toJson(overLimitImages)
    }

    override fun getResultName() = NAME

}