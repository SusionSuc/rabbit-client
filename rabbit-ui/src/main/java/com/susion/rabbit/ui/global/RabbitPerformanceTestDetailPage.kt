package com.susion.rabbit.ui.global

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import com.susion.lifeclean.common.recyclerview.SimpleRvAdapter
import com.susion.rabbit.base.common.RabbitAsync
import com.susion.rabbit.base.entities.RabbitAppPerformanceInfo
import com.susion.rabbit.base.ui.page.RabbitBasePage
import com.susion.rabbit.ui.global.entities.RabbitPagePerformanceInfo
import com.susion.rabbit.ui.global.view.RabbitPagePerformanceView
import com.susion.rabbit.ui.monitor.R
import kotlinx.android.synthetic.main.rabbit_page_performance_test_detail.view.*

/**
 * susionwang at 2019-10-29
 */
class RabbitPerformanceTestDetailPage(context: Context) : RabbitBasePage(context) {

    private lateinit var globalMonitorInfo: RabbitAppPerformanceInfo
    private val adapter by lazy {
        SimpleRvAdapter<RabbitPagePerformanceInfo>(context).apply {
            registerMapping(
                RabbitPagePerformanceInfo::class.java,
                RabbitPagePerformanceView::class.java
            )
        }
    }

    override fun getLayoutResId() = R.layout.rabbit_page_performance_test_detail

    override fun setEntryParams(info: Any) {

        if (info !is RabbitAppPerformanceInfo) return

        globalMonitorInfo = info

        mGlobalDetailSRL.setOnRefreshListener {
            loadData()
        }

        mGlobalDetailSRL.isRefreshing = true

        loadData()
    }

    init {
        setTitle("性能测试详情")
        mGlobalDetailRv.adapter = adapter
        mGlobalDetailRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    private fun loadData() {
        RabbitAsync.asyncRunWithResult({
            RabbitPerformanceTestDataAnalyzer.getPageMonitorInfos(globalMonitorInfo)
        }, {
            adapter.data.clear()
            adapter.data.addAll(it)
            adapter.notifyDataSetChanged()
            mGlobalDetailSRL.isRefreshing = false
        })

    }
}