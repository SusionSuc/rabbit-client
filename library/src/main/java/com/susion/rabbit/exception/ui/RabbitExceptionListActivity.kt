package com.susion.rabbit.exception.ui

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.susion.rabbit.R
import com.susion.rabbit.base.RabbitBaseActivity
import com.susion.rabbit.base.adapter.RabbitRvAdapter
import com.susion.rabbit.exception.RabbitExceptionLogStorageManager
import com.susion.rabbit.exception.entities.RabbitExceptionInfo
import com.susion.rabbit.exception.view.ExceptionLogPreviewItemView
import kotlinx.android.synthetic.main.activity_exception_list.*

class RabbitExceptionListActivity : RabbitBaseActivity() {

    private val logsAdapter by lazy {
        object : RabbitRvAdapter<RabbitExceptionInfo>(ArrayList()) {
            override fun createItem(type: Int) = ExceptionLogPreviewItemView(this@RabbitExceptionListActivity)
            override fun getItemType(data: RabbitExceptionInfo) = 0
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exception_list)
        mExceptionListActionBar.setTitle("App异常日志")
        setBackListener(mExceptionListActionBar)
        mExceptionLogRv.adapter = logsAdapter
        mExceptionLogRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        RabbitExceptionLogStorageManager.getAllExceptionFiles {
            if (it.isNotEmpty()) {
                logsAdapter.data.addAll(it)
                logsAdapter.notifyDataSetChanged()
            }
        }
    }

}
