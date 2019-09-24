package com.susion.devtools.net.entities

/**
 * susionwang at 2019-09-24
 */
class HttpLogInfo(
    var host: String = "",
    var path: String = "",
    var getRequestParams: HashMap<String, String> = HashMap(),
    var responseStr: String = "",
    var tookTime: Long = 0L,
    var size:String = ""
){

    fun isValid():Boolean{
        return host.isNotEmpty() && path.isNotEmpty() && responseStr.isNotEmpty()
    }

    override fun toString(): String {
        return "$host$path$getRequestParams$responseStr$tookTime"
    }

}