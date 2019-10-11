package com.susion.devtools.exception.ui

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.susion.devtools.R
import com.susion.devtools.base.DevToolsBaseActivity
import com.susion.devtools.base.adapter.CommonRvAdapter
import com.susion.devtools.exception.ExceptionLogStorageManager
import com.susion.devtools.exception.entities.ExceptionInfo
import com.susion.devtools.exception.view.ExceptionLogPreviewItemView
import kotlinx.android.synthetic.main.activity_exception_list.*

class ExceptionListActivity : DevToolsBaseActivity() {

    private val logsAdapter by lazy {
        object : CommonRvAdapter<ExceptionInfo>(ArrayList()) {
            override fun createItem(type: Int) = ExceptionLogPreviewItemView(this@ExceptionListActivity)
            override fun getItemType(data: ExceptionInfo) = 0
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exception_list)
        mExceptionListActionBar.setTitle("App异常日志")
        setBackListener(mExceptionListActionBar)
        mExceptionLogRv.adapter = logsAdapter
        mExceptionLogRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        ExceptionLogStorageManager.getAllExceptionFiles {
            if (it.isNotEmpty()) {
                logsAdapter.data.addAll(it)
                logsAdapter.notifyDataSetChanged()
            }
        }
    }

}
