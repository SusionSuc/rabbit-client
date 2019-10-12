package com.susion.devtools

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.susion.devtools.base.DevToolsBaseActivity
import com.susion.devtools.base.DevToolsMainFeatureInfo
import com.susion.devtools.base.adapter.CommonRvAdapter
import com.susion.devtools.base.view.DevToolsMainFeatureView
import com.susion.devtools.exception.ui.ExceptionListActivity
import com.susion.devtools.net.ui.HttpLogListActivity
import com.susion.devtools.utils.simpleStartActivity
import kotlinx.android.synthetic.main.activity_dev_tools.*

/**
 * DevTools Entry Page
 * */
class DevToolsActivity : DevToolsBaseActivity() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, DevToolsActivity::class.java)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    private val featuresAdapter by lazy {
        object : CommonRvAdapter<DevToolsMainFeatureInfo>(getAllFeatures()) {
            override fun createItem(type: Int) = DevToolsMainFeatureView(this@DevToolsActivity)
            override fun getItemType(data: DevToolsMainFeatureInfo) = 0
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dev_tools)
        setBackListener(mDevToolsActionBar)
        mDevToolsActionBar.setTitle("DevTools")
        mDevToolsEntryRv.adapter = featuresAdapter
        mDevToolsEntryRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    private fun getAllFeatures(): ArrayList<DevToolsMainFeatureInfo> {
        return ArrayList<DevToolsMainFeatureInfo>().apply {
            add(DevToolsMainFeatureInfo("业务功能面板", R.drawable.devtools_icon_bussiness, null))
            add(DevToolsMainFeatureInfo("网络日志", R.drawable.devtools_icon_http, HttpLogListActivity::class.java))
            add(DevToolsMainFeatureInfo("异常日志", R.drawable.devtools_icon_exception_face,ExceptionListActivity::class.java))
            add(DevToolsMainFeatureInfo("卡顿检测", R.drawable.devtools_icon_caton,null))
        }
    }
}
