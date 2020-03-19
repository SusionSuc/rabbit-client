package com.susion.rabbit.ui.page

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.susion.lifeclean.common.recyclerview.SimpleRvAdapter
import com.susion.rabbit.base.RabbitLog
import com.susion.rabbit.base.TAG_MONITOR_UI
import com.susion.rabbit.base.common.RabbitUtils
import com.susion.rabbit.base.entities.RabbitBlockFrameInfo
import com.susion.rabbit.base.entities.RabbitBlockStackTraceInfo
import com.susion.rabbit.base.ui.page.RabbitBasePage
import com.susion.rabbit.ui.monitor.R
import com.susion.rabbit.ui.view.RabbitBlockStackTraceView
import kotlinx.android.synthetic.main.rabbit_page_ui_block.view.*
import java.util.concurrent.TimeUnit

/**
 * susionwang at 2019-10-21
 */

class RabbitBlockDetailPage(context: Context) : RabbitBasePage(context) {

    private val logsAdapter by lazy {
        SimpleRvAdapter<RabbitBlockStackTraceInfo>(context).apply {
            registerMapping(
                RabbitBlockStackTraceInfo::class.java,
                RabbitBlockStackTraceView::class.java
            )
        }
    }

    override fun getLayoutResId() = R.layout.rabbit_page_ui_block

    init {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        setTitle("卡顿详情")
    }

    @SuppressLint("SetTextI18n")
    override fun setEntryParams(blockInfo: Any) {

        if (blockInfo !is RabbitBlockFrameInfo) return

        try {
            val traceList = RabbitUtils.getStackTraceList(blockInfo.blockFrameStrackTraceStrList)
            logsAdapter.data.addAll(traceList.sortedByDescending { it.collectCount })
            mRabbitBlockDetailRv.adapter = logsAdapter
            mRabbitBlockDetailRv.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            mRabbitBlockDetailTvCostTime.text =
                "卡顿时长 : ${translateToMs(blockInfo.costTime)} Ms ; 抓取主线程堆栈 : ${traceList.size} 次"
        } catch (e: Exception) {
            RabbitLog.d(TAG_MONITOR_UI, "block frame gson transform error")
        }
    }

    private fun translateToMs(ns: Long): Long {
        return TimeUnit.MILLISECONDS.convert(ns, TimeUnit.NANOSECONDS)
    }

}