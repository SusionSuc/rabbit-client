package com.susion.rabbit.ui.global.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.susion.lifeclean.common.recyclerview.AdapterItemView
import com.susion.rabbit.base.common.RabbitAsync
import com.susion.rabbit.base.entities.RabbitAppPerformanceInfo
import com.susion.rabbit.base.ui.throttleFirstClick
import com.susion.rabbit.base.ui.utils.RabbitUiUtils
import com.susion.rabbit.ui.RabbitUi
import com.susion.rabbit.ui.global.RabbitPerformanceTestDataAnalyzer
import com.susion.rabbit.ui.global.entities.RabbitAppPerformanceOverviewInfo
import com.susion.rabbit.ui.global.RabbitPerformanceTestDetailPage
import com.susion.rabbit.ui.global.entities.RabbitAppPerformancePitInfo
import com.susion.rabbit.ui.monitor.R
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.rabbit_view_app_performance_test_overview.view.*

/**
 * susionwang at 2020-01-16
 */

class RabbitAppPerformancePreView(context: Context) : LinearLayout(context),
    AdapterItemView<RabbitAppPerformancePitInfo> {

    init {
        LayoutInflater.from(context)
            .inflate(R.layout.rabbit_view_app_performance_test_overview, this)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        orientation = VERTICAL
    }

    override fun bindData(info: RabbitAppPerformancePitInfo, position: Int) {
        mGlobalMonitorPreViewLlDataContainer.visibility = View.GONE
        mGlobalMonitorPreViewTvLoad.visibility = View.VISIBLE
        mGlobalMonitorPreViewPbLoading.visibility = View.GONE

        mGlobalMonitorPreViewTvStartTime.text = info.recordStartTime
        mGlobalMonitorPreViewTvDuration.text = RabbitUiUtils.formatTimeDuration(info.duration)
        mGlobalMonitorPreViewIvStatus.visibility = if (info.isRunning) View.VISIBLE else View.GONE

        mGlobalMonitorPreViewTvLoad.throttleFirstClick(Consumer {
            mGlobalMonitorPreViewTvLoad.visibility = View.GONE
            mGlobalMonitorPreViewPbLoading.visibility = View.VISIBLE
            loadData(info.globalMonitorInfo)
        })

        throttleFirstClick(Consumer {
            RabbitUi.openPage(RabbitPerformanceTestDetailPage::class.java, info.globalMonitorInfo)
        })
    }

    private fun loadData(globalMonitorInfo: RabbitAppPerformanceInfo) {
        RabbitAsync.asyncRunWithResult({
            RabbitPerformanceTestDataAnalyzer.getGlobalMonitorPreInfo(globalMonitorInfo)
        }, {
            renderData(it)
            mGlobalMonitorPreViewPbLoading.visibility = View.GONE
        })
    }

    private fun renderData(info: RabbitAppPerformanceOverviewInfo) {
        mGlobalMonitorPreViewLlDataContainer.visibility = View.VISIBLE
        info.apply {
            mGlobalMonitorPreViewTvFps.text = avgFps.toString()
            mGlobalMonitorPreViewTvMem.text = RabbitUiUtils.formatFileSize(avgJVMMemory)
            mGlobalMonitorPreViewTvApplicationCreate.text = "${applicationCreateTime} ms"
            mGlobalMonitorPreViewTvColdStart.text = "$appColdStartTime ms"
            mGlobalMonitorPreViewTvPageInflate.text = "$pageAvgInflateTime ms"
            mGlobalMonitorPreViewTvBlockCount.text = blockCount.toString()
            mGlobalMonitorPreViewTvSlowMethodCount.text = slowMethodCount.toString()
            mGlobalMonitorPreViewTvScore.text = smoothEvaluateInfo.totalSmooth.toInt().toString()
        }
    }

}