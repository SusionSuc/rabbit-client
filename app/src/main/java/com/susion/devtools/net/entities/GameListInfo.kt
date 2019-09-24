package com.susion.devtools.net.entities

import com.google.gson.annotations.SerializedName

/**
 * susionwang at 2019-09-24
 */

data class GameInfoListWrapper(
    val `data`: GameListInfo,
    val message: String,
    val retcode: Int
)

data class GameListInfo(
    val list: MutableList<GameInfo>
)

class GameInfo(
    @SerializedName("id", alternate = arrayOf("game_id")) var gameId: String = "",
    @SerializedName("name") var name: String = "",
    @SerializedName("en_name") val enName: String = "",
    @SerializedName("app_icon") val appIcon: String = "",
    @SerializedName("community_link", alternate = arrayOf("app_path")) var communityLink: String = "",
    @SerializedName("search_trend_word") var seatchTrandWord: String = "",
    @SerializedName("level_image") var levelImage: String = "",
    @SerializedName("level_text_color") var levelTextColor: String = "",
    @SerializedName("topic_num") val topicNum:Int = 0
)