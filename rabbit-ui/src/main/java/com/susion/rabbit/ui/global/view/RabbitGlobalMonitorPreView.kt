package com.susion.rabbit.ui.global.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import com.susion.rabbit.base.ui.adapter.RabbitAdapterItemView
import com.susion.rabbit.ui.entities.RabbitGlobalModePreInfo
import com.susion.rabbit.ui.monitor.R
import kotlinx.android.synthetic.main.rabbit_view_global_monitor_pre.view.*

/**
 * susionwang at 2020-01-16
 */
class RabbitGlobalMonitorPreView(context: Context) : LinearLayout(context),
    RabbitAdapterItemView<RabbitGlobalModePreInfo> {

    init {
        LayoutInflater.from(context).inflate(R.layout.rabbit_view_global_monitor_pre, this)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        orientation = VERTICAL
    }

    override fun bindData(info: RabbitGlobalModePreInfo, position: Int) {
        info.apply {
            mGlobalMonitorPreViewTvFps.text = avgFps
            mGlobalMonitorPreViewTvMem.text = avgMemory
            mGlobalMonitorPreViewTvApplicationCreate.text = applicationCreateTime
            mGlobalMonitorPreViewTvColdStart.text = appColdStartTime
            mGlobalMonitorPreViewTvPageInflate.text = pageSpeedAvgTime
            mGlobalMonitorPreViewTvBlockCount.text = blockCount
            mGlobalMonitorPreViewTvSlowMethodCount.text = slowMethodCount
            mGlobalMonitorPreViewTvStartTime.text = recordStartTime
            mGlobalMonitorPreViewTvDuration.text = duration
        }
    }

}