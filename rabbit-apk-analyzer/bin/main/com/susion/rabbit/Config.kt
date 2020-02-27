package com.susion.rabbit

import com.google.gson.annotations.SerializedName

/**
 * susionwang at 2019-11-29
 */
class Config(
    val apkPath: String = "",
    var maxImageSizeKB: Long = 30,
    val classMappingFilePath: String = "",
    val methodGroup: List<MethodGroupInfo> = ArrayList(),
    val uploadPath:String = ""
) {

    fun isValid(): Boolean {
        return apkPath.isNotEmpty()
    }

    data class MethodGroupInfo(val name: String, @SerializedName("package")val packageName: String)

}