package com.susion.rabbit.entities

/**
 * susionwang at 2019-12-02
 */

//解压后的apk文件信息
data class UnZipApkFileInfo(
    var unZipDir: String = "",
    var apkSize: Long = 0,
    val fileMap: HashMap<String, ArrayList<FileInfo>> = HashMap(),
    val proguardClassMap: HashMap<String, String> = HashMap()
) {
    data class FileInfo(
        val name: String,
        @Transient val path: String,
        val size: Long
    )
}