package com.susion.rabbit.ui.global

import android.content.Context
import com.susion.lifeclean.common.recyclerview.SimpleRvAdapter
import com.susion.rabbit.base.common.RabbitAsync
import com.susion.rabbit.base.entities.RabbitGlobalMonitorInfo
import com.susion.rabbit.base.ui.page.RabbitBasePage
import com.susion.rabbit.ui.global.entities.RabbitPageGlobalMonitorInfo
import com.susion.rabbit.ui.global.view.RabbitPageGlobalMonitorInfoView
import com.susion.rabbit.ui.monitor.R
import kotlinx.android.synthetic.main.rabbit_page_global_monitor_detail.view.*

/**
 * susionwang at 2019-10-29
 */
class RabbitGlobalMonitorModeDetailPage(context: Context) : RabbitBasePage(context) {

    private lateinit var globalMonitorInfo: RabbitGlobalMonitorInfo
    private val adapter by lazy {
        SimpleRvAdapter<RabbitPageGlobalMonitorInfo>(context).apply {
            registerMapping(
                RabbitPageGlobalMonitorInfo::class.java,
                RabbitPageGlobalMonitorInfoView::class.java
            )
        }
    }

    override fun getLayoutResId() = R.layout.rabbit_page_global_monitor_detail

    override fun setEntryParams(info: Any) {
        if (info !is RabbitGlobalMonitorInfo) return

        globalMonitorInfo = info

        mGlobalDetailSRL.setOnRefreshListener {
            loadData()
        }

        mGlobalDetailSRL.isRefreshing = true

        loadData()
    }

    init {

        setTitle("性能测试详情")

    }

    private fun loadData() {
        RabbitAsync.asyncRunWithResult({
            RabbitGlobalMonitorDataHelper.getPageMonitorInfos(globalMonitorInfo)
        }, {
            adapter.data.clear()
            adapter.data.addAll(it)
            adapter.notifyDataSetChanged()
            mGlobalDetailSRL.isRefreshing = false
        })

    }
}