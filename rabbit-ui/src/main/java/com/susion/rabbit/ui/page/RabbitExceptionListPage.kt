package com.susion.rabbit.ui.page

import android.content.Context
import com.susion.lifeclean.common.recyclerview.SimpleRvAdapter
import com.susion.rabbit.base.entities.RabbitExceptionInfo
import com.susion.rabbit.base.ui.page.RabbitBasePage
import com.susion.rabbit.storage.RabbitStorage
import com.susion.rabbit.ui.monitor.R
import com.susion.rabbit.ui.view.RabbitExceptionPreviewView
import kotlinx.android.synthetic.main.rabbit_page_exception_list.view.*

/**
 * susionwang at 2019-10-21
 */

class RabbitExceptionListPage(context: Context) : RabbitBasePage(context) {

    override fun getLayoutResId() = R.layout.rabbit_page_exception_list

    private val logsAdapter by lazy {
        SimpleRvAdapter<RabbitExceptionInfo>(context).apply {
            registerMapping(RabbitExceptionInfo::class.java, RabbitExceptionPreviewView::class.java)
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
        RabbitStorage.getAll(RabbitExceptionInfo::class.java, orderDesc = true) {
            mExceptionLogListSPL.isRefreshing = false
            if (it.isNotEmpty()) {
                hideEmptyView()
                logsAdapter.data.clear()
                logsAdapter.data.addAll(it)
                logsAdapter.notifyDataSetChanged()
            }else{
                showEmptyView()
            }
        }
    }

}