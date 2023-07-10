package com.susion.rabbit.base

import android.content.Context

/**
 * wangpengcheng.wpc create at 2023/7/5
 * */
interface RabbitOptimizerProtocol {


    companion object {
        //消息优化
        val APP_SPEED = OprimizerInfo(
            "message_upgrade_priority",
            "消息提优"
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