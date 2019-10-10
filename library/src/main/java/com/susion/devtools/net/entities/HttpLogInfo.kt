package com.susion.devtools.net.entities

import java.io.Serializable

/**
 * susionwang at 2019-09-24
 *
 * http/https response info
 */
class HttpLogInfo(
    var host: String = "",
    var path: String = "",
    var requestParams: HashMap<String, String> = HashMap(),
    var requestBody:String = "",
    var responseStr: String = "",
    var tookTime: Long = 0L,
    var size: String = "",
    var requestType: String = RequestType.GET,
    var responseContentType: String = ResponseContentType.GSON,
    var time:Long = System.currentTimeMillis(),
    var isSuccessRequest:Boolean = true,
    var responseCode:String = "200"
) : Serializable {

    object RequestType {
        val GET = "get"
        val POST = "post"
        fun isGet(type:String):Boolean = type == GET || type == GET.toLowerCase()
    }

    object ResponseContentType {
        val GSON = "gson"
    }

    fun isValid(): Boolean {
        return host.isNotEmpty() && path.isNotEmpty()
    }

    override fun toString(): String {
        return "$host$path$requestParams$responseStr$tookTime"
    }
}