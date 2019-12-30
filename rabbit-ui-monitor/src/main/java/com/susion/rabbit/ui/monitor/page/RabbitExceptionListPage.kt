package com.susion.rabbit.ui.monitor.page

import android.content.Context
import com.susion.rabbit.base.entities.RabbitExceptionInfo
import com.susion.rabbit.ui.monitor.view.ExceptionLogPreviewItemView
import com.susion.rabbit.storage.RabbitDbStorageManager
import com.susion.rabbit.ui.R
import kotlinx.android.synthetic.main.rabbit_page_exception_list.view.*

/**
 * susionwang at 2019-10-21
 */

class RabbitExceptionListPage(context: Context) : com.susion.rabbit.ui.base.RabbitBasePage(context) {

    override fun getLayoutResId() = R.layout.rabbit_page_exception_list

    private val logsAdapter by lazy {
        object : com.susion.rabbit.ui.base.adapter.RabbitRvAdapter<RabbitExceptionInfo>(ArrayList()) {
            override fun createItem(type: Int) =
                ExceptionLogPreviewItemView(context)
            override fun getItemType(data: com.susion.rabbit.base.entities.RabbitExceptionInfo) = 0
        }
    }

    init {
        setTitle("App异常日志")
        mExceptionLogRv.adapter = logsAdapter
        mExceptionLogRv.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
            context,
            androidx.recyclerview.widget.LinearLayoutManager.VERTICAL,
            false
        )
        loadAllData()
        mExceptionLogListSPL.setOnRefreshListener {
            loadAllData()
        }
    }

    private fun loadAllData() {
        RabbitDbStorageManager.getAll(com.susion.rabbit.base.entities.RabbitExceptionInfo::class.java) {
            mExceptionLogListSPL.isRefreshing = false
            if (it.isNotEmpty()) {
                logsAdapter.data.clear()
                logsAdapter.data.addAll(it)
                logsAdapter.notifyDataSetChanged()
            }
        }
    }

}