package com.susion.rabbit.base.entities

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

/**
 * susionwang at 2019-11-21
 *
 * Rabbit  应用测速 配置信息
 */

@Keep
class RabbitAppSpeedMonitorConfig(
    @SerializedName("home_activity") val homeActivity: String = "",
    @SerializedName("page_list") val pageConfigList: List<RabbitPageSpeedConfig> = ArrayList()
)

@Keep
class RabbitPageSpeedConfig(
    @SerializedName("page") val pageSimpleName: String = "",
    @SerializedName("api") val apiList: List<String> = ArrayList()
)

class RabbitApiInfo(
    val api: String = "",
    var isFinish: Boolean = false,
    var costTime:Long = 0
)

class RabbitPageApiInfo(val apiStatusList: ArrayList<RabbitApiInfo> = ArrayList()) {
    fun allApiRequestFinish() = apiStatusList.filter { !it.isFinish }.isNullOrEmpty()
}
