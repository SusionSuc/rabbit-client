package com.susion.rabbit.ui.global.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.susion.lifeclean.common.recyclerview.AdapterItemView
import com.susion.rabbit.base.ui.throttleFirstClick
import com.susion.rabbit.base.ui.utils.RabbitUiUtils
import com.susion.rabbit.ui.RabbitUi
import com.susion.rabbit.ui.global.entities.RabbitGlobalModePreInfo
import com.susion.rabbit.ui.global.RabbitGlobalMonitorModeDetailPage
import com.susion.rabbit.ui.monitor.R
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.rabbit_view_global_monitor_pre.view.*

/**
 * susionwang at 2020-01-16
 */
class RabbitGlobalMonitorPreView(context: Context) : LinearLayout(context),
    AdapterItemView<RabbitGlobalModePreInfo> {

    init {
        LayoutInflater.from(context).inflate(R.layout.rabbit_view_global_monitor_pre, this)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        orientation = VERTICAL
    }

    override fun bindData(info: RabbitGlobalModePreInfo, position: Int) {
        info.apply {
            mGlobalMonitorPreViewTvFps.text = avgFps.toString()
            mGlobalMonitorPreViewTvMem.text = RabbitUiUtils.formatFileSize(avgJVMMemory)
            mGlobalMonitorPreViewTvApplicationCreate.text = "${applicationCreateTime} ms"
            mGlobalMonitorPreViewTvColdStart.text = "$appColdStartTime ms"
            mGlobalMonitorPreViewTvPageInflate.text = "$pageAvgInflateTime ms"
            mGlobalMonitorPreViewTvBlockCount.text = blockCount.toString()
            mGlobalMonitorPreViewTvSlowMethodCount.text = slowMethodCount.toString()
            mGlobalMonitorPreViewTvStartTime.text = recordStartTime
            mGlobalMonitorPreViewTvDuration.text = duration
            mGlobalMonitorPreViewIvStatus.visibility = if (isRunning) View.VISIBLE else View.GONE
            mGlobalMonitorPreViewTvScore.text = smoothEvaluateInfo.totalSmooth.toInt().toString()
        }

        throttleFirstClick(Consumer {
            RabbitUi.openPage(RabbitGlobalMonitorModeDetailPage::class.java, info.globalMonitorInfo)
        })

    }

}