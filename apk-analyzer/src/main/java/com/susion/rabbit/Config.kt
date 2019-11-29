package com.susion.rabbit

/**
 * susionwang at 2019-11-29
 */
class Config (val apkPath:String = "",
              var unzipApkPath:String = ""){

    fun isValid():Boolean{
        return apkPath.isNotEmpty()
    }

}