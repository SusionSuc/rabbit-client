package com.susion.rabbit.exception.ui

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import com.susion.rabbit.R
import com.susion.rabbit.base.adapter.RabbitRvAdapter
import com.susion.rabbit.db.RabbitDbStorageManager
import com.susion.rabbit.exception.entities.RabbitExceptionInfo
import com.susion.rabbit.exception.ui.view.ExceptionLogPreviewItemView
import com.susion.rabbit.ui.page.RabbitBasePage
import kotlinx.android.synthetic.main.activity_exception_list.view.*

/**
 * susionwang at 2019-10-21
 */

class RabbitExceptionListPage(context: Context): RabbitBasePage(context) {

    override fun getLayoutResId()  = R.layout.activity_exception_list

    private val logsAdapter by lazy {
        object : RabbitRvAdapter<RabbitExceptionInfo>(ArrayList()) {
            override fun createItem(type: Int) = ExceptionLogPreviewItemView(context)
            override fun getItemType(data: RabbitExceptionInfo) = 0
        }
    }

    init {
        setTitle("App异常日志")
        mExceptionLogRv.adapter = logsAdapter
        mExceptionLogRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        loadAllData()
        mExceptionLogListSPL.setOnRefreshListener {
            loadAllData()
        }
    }

    private fun loadAllData() {
        RabbitDbStorageManager.getAll(RabbitExceptionInfo::class.java) {
            mExceptionLogListSPL.isRefreshing = false
            if (it.isNotEmpty()) {
                logsAdapter.data.clear()
                logsAdapter.data.addAll(it)
                logsAdapter.notifyDataSetChanged()
            }
        }
    }

}