package com.susion.rabbit.ui.global.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import com.susion.lifeclean.common.recyclerview.AdapterItemView
import com.susion.rabbit.base.ui.utils.RabbitUiUtils
import com.susion.rabbit.ui.global.entities.RabbitAppPerformanceOverviewInfo
import com.susion.rabbit.ui.global.entities.RabbitPagePerformanceInfo
import com.susion.rabbit.ui.monitor.R
import kotlinx.android.synthetic.main.view_page_performance_test_info.view.*

/**
 * create by susionwang at 2020-02-10
 * 页面的性能测速概览
 */
class RabbitPagePerformanceView(context: Context) : LinearLayout(context),
    AdapterItemView<RabbitPagePerformanceInfo> {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_page_performance_test_info, this)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        orientation = VERTICAL
    }

    override fun bindData(info: RabbitPagePerformanceInfo, position: Int) {
        info.apply {
            mPagePerformanceTvPageName.text = RabbitUiUtils.dropPackageName(pageName)
            mPagePerformanceTvAvgFps.text = avgFps.toString()
            mPagePerformanceTvAvgMem.text = RabbitUiUtils.formatFileSize(avgMem)
            mPagePerformanceTvBlockCount.text = blockCount.toString()
            mPagePerformanceTvSlowMethodCount.text = slowMethodCount.toString()
            mPagePerformanceTvInflateTime.text = "$avgInlfateTime ms"
            mPagePerformanceTvRenderTime.text = "$avgFullRenderTime ms"
        }
    }
}