package com.susion.rabbit.ui.page

import android.content.Context
import android.view.ViewGroup
import com.susion.rabbit.ui.base.RabbitBasePage
import com.susion.rabbit.ui.base.adapter.RabbitRvAdapter
import com.susion.rabbit.base.entities.RabbitHttpLogInfo
import com.susion.rabbit.ui.view.HttpLogPreviewItemView
import com.susion.rabbit.storage.RabbitDbStorageManager
import com.susion.rabbit.ui.R
import kotlinx.android.synthetic.main.rabbit_page_http_log_list.view.*

/**
 * susionwang at 2019-10-21
 */

class RabbitHttpLogListPage(context: Context) : RabbitBasePage(context) {

    private val logsAdapter by lazy {
        object : RabbitRvAdapter<RabbitHttpLogInfo>(ArrayList()) {
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