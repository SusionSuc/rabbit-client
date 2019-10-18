package com.susion.rabbit.net.ui

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.susion.rabbit.R
import com.susion.rabbit.base.RabbitBaseActivity
import com.susion.rabbit.base.adapter.RabbitRvAdapter
import com.susion.rabbit.net.RabbitHttpLogStorageManager
import com.susion.rabbit.net.entities.RabbitHttpLogInfo
import com.susion.rabbit.net.ui.view.HttpLogPreviewItemView
import kotlinx.android.synthetic.main.activity_http_log_list.*

class RabbitHttpLogListActivity : RabbitBaseActivity() {

    private val logsAdapter by lazy {
        object : RabbitRvAdapter<RabbitHttpLogInfo>(ArrayList()) {
            override fun createItem(type: Int) = HttpLogPreviewItemView(this@RabbitHttpLogListActivity)
            override fun getItemType(data: RabbitHttpLogInfo) = 0
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_http_log_list)
        setBackListener(mHttpLogListActionBar)
        mHttpLogListActionBar.setTitle("网络请求日志")
        mHttpLogRv.adapter = logsAdapter
        mHttpLogRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        RabbitHttpLogStorageManager.getAllLogFiles {
            if (it.isNotEmpty()) {
                logsAdapter.data.addAll(it)
                logsAdapter.notifyDataSetChanged()
            }
        }
    }
}
