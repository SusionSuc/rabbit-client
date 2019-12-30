package com.susion.rabbit.monitor

import android.app.Application

/**
 * susionwang at 2019-10-18
 * 整个Rabbit的监控系统
 */
object RabbitMonitor {

    fun init(application: Application, config: Config) {

    }

    class Config(
        var blockStackCollectPeriodNs: Long = STANDARD_FRAME_NS,
        var blockThresholdNs: Long = STANDARD_FRAME_NS * 10,
        var autoOpenMonitors: HashSet<String> = HashSet(),
        var memoryValueCollectPeriodMs: Long = 2000L,
        var fpsCollectThresholdNs: Long = STANDARD_FRAME_NS * 10,
        var fpsReportPeriodS: Long = 10
    ) {
        companion object {
            var STANDARD_FRAME_NS = 16666666L
        }
    }

    interface UiEventListener {
        fun updateUi(type: Int, value: Any)
    }

    interface PageChangeListener {
        fun onPageShow()
    }

}