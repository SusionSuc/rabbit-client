package com.susion.rabbit.task

import com.google.gson.Gson
import com.susion.rabbit.base.AnalyzerTask
import com.susion.rabbit.entities.UnZipApkFileInfo
import com.susion.rabbit.helper.Util
import com.susion.rabbit.helper.Utils
import java.io.File
import java.security.MessageDigest

/**
 * susionwang at 2019-12-02
 */
class DuplicatedFileAnalyzerTask : AnalyzerTask {

    data class SameMd5Files(val files: ArrayList<String> = ArrayList(), val fileSize: String, var md5Value:String)

    override fun getResultName() = "DuplicatedFile"

    override fun analyze(unZipContext: UnZipApkFileInfo): String {

        val md5Map = HashMap<String, SameMd5Files>()

        unZipContext.fileMap.flatMap { it.value }.forEach {
            val md5Value = getMD5(File(it.path))

            var md5FileInfo = md5Map[md5Value]
            if (md5FileInfo == null) {
                md5FileInfo = SameMd5Files(fileSize = Utils.formatFileSize(it.size), md5Value = md5Value)
                md5Map[md5Value] = md5FileInfo
            }

            md5FileInfo.files.add(Utils.getFileRelativeName(unZipContext.unZipDir, it.path))
        }

        return Gson().toJson(md5Map.filter { it.value.files.size > 1 }.map { it.value })
    }

    private fun getMD5(file: File): String {
        val msgDigest = MessageDigest.getInstance("MD5")
        return Util.byteArrayToHex(msgDigest.digest(file.readBytes()))
    }

}