package com.susion.rabbit.ui.monitor.page

import android.content.Context
import android.view.ViewGroup
import com.susion.rabbit.base.entities.RabbitHttpLogInfo
import com.susion.rabbit.ui.monitor.view.HttpLogPreviewItemView
import com.susion.rabbit.storage.RabbitDbStorageManager
import com.susion.rabbit.ui.monitor.R
import kotlinx.android.synthetic.main.rabbit_page_http_log_list.view.*

/**
 * susionwang at 2019-10-21
 */

class RabbitHttpLogListPage(context: Context) : com.susion.rabbit.ui.base.RabbitBasePage(context) {

    private val logsAdapter by lazy {
        object : com.susion.rabbit.ui.base.adapter.RabbitRvAdapter<RabbitHttpLogInfo>(ArrayList()) {
            override fun createItem(type: Int) =
                HttpLogPreviewItemView(context)
            override fun getItemType(data: RabbitHttpLogInfo) = 0
        }
    }

    init {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        setTitle("网络日志")
        mHttpLogRv.adapter = logsAdapter
        mHttpLogRv.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
            context,
            androidx.recyclerview.widget.LinearLayoutManager.VERTICAL,
            false
        )
        loadData()

        mHttpLogListSPL.setOnRefreshListener {
            loadData()
        }

    }

    private fun loadData() {
        RabbitDbStorageManager.getAll(RabbitHttpLogInfo::class.java) {
            mHttpLogListSPL.isRefreshing = false
            if (it.isNotEmpty()) {
                logsAdapter.data.clear()
                logsAdapter.data.addAll(it)
                logsAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun getLayoutResId() = R.layout.rabbit_page_http_log_list

}