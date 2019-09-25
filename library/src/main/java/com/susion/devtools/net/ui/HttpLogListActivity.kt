package com.susion.devtools.net.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.susion.devtools.DevToolsActivity
import com.susion.devtools.R
import com.susion.devtools.base.DevToolsBaseActivity
import com.susion.devtools.base.adapter.CommonRvAdapter
import com.susion.devtools.net.HttpLogStorageManager
import com.susion.devtools.net.entities.HttpLogInfo
import com.susion.devtools.net.ui.view.HttpLogPreviewItemView
import kotlinx.android.synthetic.main.activity_http_log_list.*

class HttpLogListActivity : DevToolsBaseActivity() {

    private val logsAdapter by lazy {
        object : CommonRvAdapter<HttpLogInfo>(ArrayList()) {
            override fun createItem(type: Int) = HttpLogPreviewItemView(this@HttpLogListActivity)
            override fun getItemType(data: HttpLogInfo) = 0
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_http_log_list)
        setBackListener(mHttpLogListActionBar)
        mHttpLogListActionBar.setTitle("网络请求日志")
        mHttpLogRv.adapter = logsAdapter
        mHttpLogRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        HttpLogStorageManager.getAllLogFiles {
            if (it.isNotEmpty()) {
                logsAdapter.data.addAll(it)
                logsAdapter.notifyDataSetChanged()
            }
        }
    }
}
