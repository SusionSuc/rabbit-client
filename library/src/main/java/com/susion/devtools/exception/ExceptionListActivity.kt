package com.susion.devtools.exception

import android.os.Bundle
import com.susion.devtools.R
import com.susion.devtools.base.DevToolsBaseActivity
import com.susion.devtools.base.adapter.CommonRvAdapter
import com.susion.devtools.net.entities.HttpLogInfo
import com.susion.devtools.net.ui.view.HttpLogPreviewItemView
import kotlinx.android.synthetic.main.activity_exception_list.*

class ExceptionListActivity : DevToolsBaseActivity() {

    private val logsAdapter by lazy {
        object : CommonRvAdapter<HttpLogInfo>(ArrayList()) {
            override fun createItem(type: Int) = HttpLogPreviewItemView(this@ExceptionListActivity)
            override fun getItemType(data: HttpLogInfo) = 0
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exception_list)
        mExceptionListActionBar.setTitle("App异常日志")
        setBackListener(mExceptionListActionBar)

    }

}
