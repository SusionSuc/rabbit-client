package com.susion.rabbit.net.ui

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.ViewGroup
import com.susion.rabbit.R
import com.susion.rabbit.base.adapter.RabbitRvAdapter
import com.susion.rabbit.db.RabbitDbStorageManager
import com.susion.rabbit.net.entities.RabbitHttpLogInfo
import com.susion.rabbit.net.ui.view.HttpLogPreviewItemView
import com.susion.rabbit.ui.page.RabbitBasePage
import kotlinx.android.synthetic.main.rabbit_page_http_log_list.view.*

/**
 * susionwang at 2019-10-21
 */

class RabbitHttpLogListPage(context: Context) : RabbitBasePage(context) {

    private val logsAdapter by lazy {
        object : RabbitRvAdapter<RabbitHttpLogInfo>(ArrayList()) {
            override fun createItem(type: Int) = HttpLogPreviewItemView(context)
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