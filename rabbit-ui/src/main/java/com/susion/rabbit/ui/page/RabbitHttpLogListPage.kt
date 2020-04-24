package com.susion.rabbit.ui.page

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.susion.lifeclean.common.recyclerview.SimpleRvAdapter
import com.susion.rabbit.base.entities.RabbitHttpLogInfo
import com.susion.rabbit.ui.view.RabbitHttpLogPreviewView
import com.susion.rabbit.storage.RabbitStorage
import com.susion.rabbit.base.ui.page.RabbitBasePage
import com.susion.rabbit.ui.monitor.R
import kotlinx.android.synthetic.main.rabbit_page_http_log_list.view.*

/**
 * susionwang at 2019-10-21
 */

class RabbitHttpLogListPage(context: Context) : RabbitBasePage(context) {

    private val logsAdapter by lazy {
        SimpleRvAdapter<RabbitHttpLogInfo>(context).apply {
            registerMapping(RabbitHttpLogInfo::class.java, RabbitHttpLogPreviewView::class.java)
        }
    }

    init {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        setTitle("网络日志")
        mHttpLogRv.adapter = logsAdapter
        mHttpLogRv.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )

        mHttpLogListSPL.isRefreshing = true
        loadData()

        mHttpLogListSPL.setOnRefreshListener {
            loadData()
        }

    }

    private fun loadData() {
        RabbitStorage.getAll(RabbitHttpLogInfo::class.java, orderDesc = true) {
            mHttpLogListSPL.isRefreshing = false
            if (it.isNotEmpty()) {
                hideEmptyView()
                logsAdapter.data.clear()
                logsAdapter.data.addAll(it)
                logsAdapter.notifyDataSetChanged()
            } else {
                showEmptyView()
            }
        }
    }

    override fun getLayoutResId() = R.layout.rabbit_page_http_log_list

}