package com.susion.rabbit.ui.global.view

import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.susion.lifeclean.common.recyclerview.AdapterItemView
import com.susion.rabbit.ui.global.entities.RabbitAppPerformanceOverviewInfo
import com.susion.rabbit.ui.monitor.R

/**
 * create by susionwang at 2020-02-10
 * 页面的性能测速概览
 */
class RabbitPagePerformanceTestInfoView(context: Context) : LinearLayout(context),
    AdapterItemView<RabbitAppPerformanceOverviewInfo> {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_page_global_monitor_info, this)
    }

    override fun bindData(data: RabbitAppPerformanceOverviewInfo, position: Int) {

    }

}