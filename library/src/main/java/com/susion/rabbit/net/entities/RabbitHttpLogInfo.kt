package com.susion.rabbit.net.entities

import io.realm.RealmObject
import io.realm.annotations.RealmClass
import java.io.Serializable

/**
 * susionwang at 2019-09-24
 *
 * http/https response info
 */
@RealmClass(name = "rabbit_http_log")
open class RabbitHttpLogInfo(
    var host: String = "",
    var path: String = "",
    var requestBody: String = "",
    var responseStr: String = "",
    var tookTime: Long = 0L,
    var size: String = "",
    var requestType: String = "get",
    var responseContentType: String = ResponseContentType.GSON,
    var isSuccessRequest: Boolean = true,
    var responseCode: String = "200",
    var requestParamsMapString: String = "",
    var time: Long = System.currentTimeMillis()
) : RealmObject(), Serializable {

    object ResponseContentType {
        val GSON = "gson"
    }

    fun isvalid(): Boolean {
        return host.isNotEmpty() && path.isNotEmpty()
    }

    override fun toString(): String {
        return "$host$path$requestParamsMapString$responseStr$tookTime"
    }

}