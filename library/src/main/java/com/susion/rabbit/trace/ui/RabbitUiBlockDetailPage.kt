package com.susion.rabbit.trace.ui

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.susion.rabbit.R
import com.susion.rabbit.RabbitLog
import com.susion.rabbit.base.adapter.RabbitAdapterItemView
import com.susion.rabbit.base.adapter.RabbitRvAdapter
import com.susion.rabbit.base.view.RabbitSimpleKVItemView
import com.susion.rabbit.base.view.RabbitSimpleKvInfo
import com.susion.rabbit.trace.entities.RabbitBlockFrameInfo
import com.susion.rabbit.trace.entities.RabbitBlockStackTraceInfo
import com.susion.rabbit.ui.page.RabbitBasePage
import kotlinx.android.synthetic.main.rabbit_page_ui_block.view.*
import java.util.concurrent.TimeUnit

/**
 * susionwang at 2019-10-21
 */

class RabbitUiBlockDetailPage(context: Context) : RabbitBasePage(context) {

    private val logsAdapter by lazy {
        object : RabbitRvAdapter<Any>(ArrayList()) {
            override fun createItem(type: Int) :RabbitAdapterItemView<*>{
                return when(type){
                    1 -> RabbitSimpleKVItemView(context)
                    else -> RabbitBlockStackTraceView(context)
                }
            }
            override fun getItemType(data: Any):Int{
                return when(data){
                    is RabbitSimpleKvInfo -> 1
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
            mRabbitBlockDetailRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            mRabbitBlockDetailTvCostTime.text = "卡顿时长 : ${translateToMs(blockInfo.costTime)} Ms ; 抓取主线程堆栈 : ${traceList.size} 次"
        } catch (e: Exception) {
            RabbitLog.d("block frame gson transform error")
        }
    }

    private fun getStackTraceList(blockInfo: RabbitBlockFrameInfo) = Gson().fromJson<List<RabbitBlockStackTraceInfo>>(
            blockInfo.blockFrameStrackTraceStrList,
            object : TypeToken<List<RabbitBlockStackTraceInfo>>() {}.type)


    private fun translateToMs(ns: Long): Long {
        return TimeUnit.MILLISECONDS.convert(ns, TimeUnit.NANOSECONDS)
    }

}