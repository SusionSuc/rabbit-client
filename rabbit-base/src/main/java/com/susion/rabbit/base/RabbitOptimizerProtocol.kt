package com.susion.rabbit.base

import android.content.Context

/**
 * wangpengcheng.wpc create at 2023/7/5
 * */
interface RabbitOptimizerProtocol {


    companion object {
        //消息优化
        val MESSAGE_PRIORITY_UPGRADE = OprimizerInfo(
            "message_priority_upgrade",
            "消息优先级提升"
        )
    }


    class OprimizerInfo(
        val name: String,
        val znName: String,
        val showInExternal: Boolean = true,
        val dataCanClear: Boolean = true
    )

    fun open(context: Context)

    fun close()

    fun getOptimizerInfo(): OprimizerInfo

    var isOpen: Boolean

}