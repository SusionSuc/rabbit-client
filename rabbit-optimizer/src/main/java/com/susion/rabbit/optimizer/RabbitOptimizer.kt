package com.susion.rabbit.optimizer

import com.susion.rabbit.base.RabbitMonitorProtocol
import com.susion.rabbit.base.RabbitOptimizerProtocol

/**
 * wangpengcheng.wpc create at 2023/7/10
 * */
object RabbitOptimizer {

    private val optimizerMap = LinkedHashMap<String, RabbitOptimizerProtocol>()

    init {
        optimizerMap.apply {
            put(RabbitOptimizerProtocol.MESSAGE_PRIORITY_UPGRADE.name, RabbiLooperMessageUpgradeOptimizer())
        }
    }

    fun getOptimizerList()= ArrayList<RabbitOptimizerProtocol>().apply {
        addAll(optimizerMap.values)
    }

}