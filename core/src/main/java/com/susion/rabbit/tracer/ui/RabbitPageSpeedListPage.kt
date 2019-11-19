package com.susion.rabbit.tracer.ui

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.view.ViewGroup
import com.susion.rabbit.R
import com.susion.rabbit.base.adapter.RabbitRvAdapter
import com.susion.rabbit.db.RabbitDbStorageManager
import com.susion.rabbit.tracer.entities.RabbitPageSpeedInfo
import com.susion.rabbit.tracer.entities.RabbitPageSpeedUiInfo
import com.susion.rabbit.ui.page.RabbitBasePage
import kotlinx.android.synthetic.main.rabbit_page_ui_block_list.view.*

/**
 * susionwang at 2019-10-29
 */
class RabbitPageSpeedListPage(context: Context) : RabbitBasePage(context) {

    private val logsAdapter by lazy {
        object : RabbitRvAdapter<RabbitPageSpeedUiInfo>(ArrayList()) {
            override fun createItem(type: Int) = RabbitPageSpeedUiItemView(context)
            override fun getItemType(data: RabbitPageSpeedUiInfo) = 0
        }
    }

    override fun getLayoutResId() = R.layout.rabbit_page_ui_block_list

    init {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        setTitle("页面测试日志")

        mUiBlockPageRv.adapter = logsAdapter
        mUiBlockPageRv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        loadData()

        mUiBlockPageSRL.setOnRefreshListener {
            loadData()
        }
    }

    private fun loadData() {
        RabbitDbStorageManager.getAll(RabbitPageSpeedInfo::class.java) {

            mUiBlockPageSRL.isRefreshing = false

            val pageSpeedMap = LinkedHashMap<String, RabbitPageSpeedUiInfo>()

            it.forEach { speedInfo ->

                var speedUiInfo = pageSpeedMap[speedInfo.pageName]
                if (speedUiInfo == null) {
                    speedUiInfo = RabbitPageSpeedUiInfo(speedInfo.pageName)
                    pageSpeedMap[speedInfo.pageName] = speedUiInfo
                }

                speedUiInfo.speedInfoList.add(speedInfo)
            }

            logsAdapter.data.clear()
            logsAdapter.data.addAll(pageSpeedMap.values)
            logsAdapter.notifyDataSetChanged()
        }
    }

}