package com.susion.rabbit.performance.ui

import android.content.Context
import android.view.ViewGroup
import com.susion.rabbit.R
import com.susion.rabbit.base.adapter.RabbitAdapterItemView
import com.susion.rabbit.base.adapter.RabbitRvAdapter
import com.susion.rabbit.db.RabbitDbStorageManager
import com.susion.rabbit.performance.entities.RabbitAppStartSpeedInfo
import com.susion.rabbit.performance.entities.RabbitAppStartSpeedTotalInfo
import com.susion.rabbit.performance.entities.RabbitPageSpeedInfo
import com.susion.rabbit.performance.entities.RabbitPageSpeedUiInfo
import com.susion.rabbit.ui.page.RabbitBasePage
import kotlinx.android.synthetic.main.rabbit_page_ui_block_list.view.*

/**
 * susionwang at 2019-10-29
 */
class RabbitAppSpeedMonitorDetailPage(context: Context) : RabbitBasePage(context) {

    private val logsAdapter by lazy {
        object : RabbitRvAdapter<Any>(ArrayList()) {
            val TYPE_PAGE = 1
            val TYPE_APP = 2
            override fun createItem(type: Int):RabbitAdapterItemView<*> = when(type){
                TYPE_APP -> RabbitAppSpeedInfoView(context)
                else -> RabbitPageSpeedUiItemView(context)
            }
            override fun getItemType(data: Any) = when(data){
                is RabbitAppStartSpeedTotalInfo -> TYPE_APP
                else ->TYPE_PAGE
            }
        }
    }

    override fun getLayoutResId() = R.layout.rabbit_page_ui_block_list

    init {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        setTitle("应用测速日志")

        mUiBlockPageRv.adapter = logsAdapter
        mUiBlockPageRv.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(
                context,
                androidx.recyclerview.widget.LinearLayoutManager.VERTICAL,
                false
            )
        loadData()

        mUiBlockPageSRL.setOnRefreshListener {
            loadData()
        }
    }

    private fun loadData() {

        RabbitDbStorageManager.getAll(RabbitAppStartSpeedInfo::class.java){ speedInfos->

            val info = RabbitAppStartSpeedTotalInfo().apply {
                avgOnCreateTime = "${(speedInfos.map { it.createEndTime - it.createStartTime }.average()).toInt()} ms"
                avgFullStartTime = "${(speedInfos.map { it.fullShowCostTime}.average()).toInt()} ms"
                count = speedInfos.size.toString()
            }

            RabbitDbStorageManager.getAll(RabbitPageSpeedInfo::class.java) {
                mUiBlockPageSRL.isRefreshing = false

                val pageSpeedMap = LinkedHashMap<String, RabbitPageSpeedUiInfo>()

                it.forEach { speedInfo ->
                    var speedUiInfo = pageSpeedMap[speedInfo.pageName]
                    if (speedUiInfo == null) {
                        speedUiInfo =
                            RabbitPageSpeedUiInfo(speedInfo.pageName)
                        pageSpeedMap[speedInfo.pageName] = speedUiInfo
                    }
                    speedUiInfo.speedInfoList.add(speedInfo)
                }

                logsAdapter.data.clear()
                logsAdapter.data.add(info)
                logsAdapter.data.addAll(pageSpeedMap.values)
                logsAdapter.notifyDataSetChanged()
            }
        }

    }


}