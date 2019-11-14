package com.susion.rabbit.tracer.monitor

import com.susion.rabbit.RabbitLog

/**
 * susionwang at 2019-11-14
 *
 * 应用速度监控:
 * 1. 应用启动速度
 * 2. 页面启动速度、渲染速度
 * 3. 页面平均帧率
 */
class RabbitAppSpeedMonitor {

    private val TAG = javaClass.simpleName


    fun recordApplicationCreateCostTime(time: Long) {
        RabbitLog.d(TAG, "application onCreate cost time : $time ms")
    }


}