package com.susion.rabbit.ui.monitor.page

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.susion.rabbit.base.RabbitLog
import com.susion.rabbit.base.TAG_MONITOR_UI
import com.susion.rabbit.base.entities.RabbitBlockFrameInfo
import com.susion.rabbit.base.entities.RabbitBlockStackTraceInfo
import com.susion.rabbit.ui.base.RabbitBasePage
import com.susion.rabbit.ui.base.adapter.RabbitAdapterItemView
import com.susion.rabbit.ui.base.adapter.RabbitRvAdapter
import com.susion.rabbit.ui.base.view.RabbitSimpleKVItemView
import com.susion.rabbit.ui.monitor.R
import com.susion.rabbit.ui.monitor.view.RabbitBlockStackTraceView
import kotlinx.android.synthetic.main.rabbit_page_ui_block.view.*
import java.util.concurrent.TimeUnit

/**
 * susionwang at 2019-10-21
 */

class RabbitUiBlockDetailPage(context: Context) : RabbitBasePage(context) {

    private val logsAdapter by lazy {
        object : RabbitRvAdapter<Any>(ArrayList()) {
            override fun createItem(type: Int): RabbitAdapterItemView<*> {
                return when (type) {
                    1 -> RabbitSimpleKVItemView(context)
                    else -> RabbitBlockStackTraceView(context)
                }
            }

            override fun getItemType(data: Any): Int {
                return when (data) {
                    is com.susion.rabbit.ui.base.view.RabbitSimpleKvInfo -> 1
                    else -> 2
                }
            }
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
            val traceList = getStackTraceList(blockInfo)
            logsAdapter.data.addAll(traceList.sortedByDescending { it.collectCount })
            mRabbitBlockDetailRv.adapter = logsAdapter
            mRabbitBlockDetailRv.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
                context,
                androidx.recyclerview.widget.LinearLayoutManager.VERTICAL,
                false
            )
            mRabbitBlockDetailTvCostTime.text =
                "卡顿时长 : ${translateToMs(blockInfo.costTime)} Ms ; 抓取主线程堆栈 : ${traceList.size} 次"
        } catch (e: Exception) {
            RabbitLog.d(TAG_MONITOR_UI, "block frame gson transform error")
        }
    }

    private fun getStackTraceList(blockInfo: RabbitBlockFrameInfo) =
        Gson().fromJson<List<RabbitBlockStackTraceInfo>>(
            blockInfo.blockFrameStrackTraceStrList,
            object : TypeToken<List<RabbitBlockStackTraceInfo>>() {}.type
        )


    private fun translateToMs(ns: Long): Long {
        return TimeUnit.MILLISECONDS.convert(ns, TimeUnit.NANOSECONDS)
    }

}