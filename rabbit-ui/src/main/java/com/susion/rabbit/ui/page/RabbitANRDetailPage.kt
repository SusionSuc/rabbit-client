package com.susion.rabbit.ui.page

import android.content.Context
import android.os.Build
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.susion.lifeclean.common.recyclerview.SimpleRvAdapter
import com.susion.rabbit.base.common.RabbitUtils
import com.susion.rabbit.base.common.rabbitTimeFormat
import com.susion.rabbit.base.entities.RabbitAnrInfo
import com.susion.rabbit.base.entities.RabbitBlockStackTraceInfo
import com.susion.rabbit.base.ui.page.RabbitBasePage
import com.susion.rabbit.ui.monitor.R
import com.susion.rabbit.ui.view.RabbitBlockStackTraceView
import kotlinx.android.synthetic.main.rabbit_page_anr_detail.view.*

/**
 * susionwang at 2020-03-19
 */
class RabbitANRDetailPage(context: Context) : RabbitBasePage(context) {

    private val logsAdapter by lazy {
        SimpleRvAdapter<RabbitBlockStackTraceInfo>(context).apply {
            registerMapping(
                RabbitBlockStackTraceInfo::class.java,
                RabbitBlockStackTraceView::class.java
            )
        }
    }

    init {
        setTitle("ANR详情")
    }

    override fun setEntryParams(info: Any) {

        if (info !is RabbitAnrInfo) return

        if (Build.VERSION.SDK_INT >= 21) {
            showAnrLogForHighVersion(info)
        } else {
            showAnrLogForLowVersion(info)
        }
    }

    private fun showAnrLogForHighVersion(info: RabbitAnrInfo) {
        mAnrDetailLowVersionContainer.visibility = View.GONE
        mAnrDetailHighVersionContainer.visibility = View.VISIBLE

        val traceList = RabbitUtils.getStackTraceList(info.stackStr)
        logsAdapter.data.addAll(traceList.sortedByDescending { it.collectCount })
        mAnrDetailHighVersionStackRv.adapter = logsAdapter
        mAnrDetailHighVersionStackRv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mAnrDetailHighVersionTvTime.text = "${info.pageName}\n${rabbitTimeFormat(info.time)}"
    }

    private fun showAnrLogForLowVersion(info: RabbitAnrInfo) {
        mAnrDetailLowVersionContainer.visibility = View.VISIBLE
        mAnrDetailHighVersionContainer.visibility = View.GONE
        mAnrDetailTvTitle.text = "${info.pageName}\n${rabbitTimeFormat(info.time)}"
        mAnrDetailTvStackStr.text = info.stackStr
    }

    override fun getLayoutResId() = R.layout.rabbit_page_anr_detail

}