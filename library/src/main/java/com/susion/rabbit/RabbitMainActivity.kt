package com.susion.rabbit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.susion.rabbit.base.RabbitBaseActivity
import com.susion.rabbit.base.RabbitMainFeatureInfo
import com.susion.rabbit.base.adapter.RabbitRvAdapter
import com.susion.rabbit.base.view.RabbitMainFeatureView
import com.susion.rabbit.config.RabbitConfigActivity
import com.susion.rabbit.exception.ui.RabbitExceptionListActivity
import com.susion.rabbit.net.ui.RabbitHttpLogListActivity
import kotlinx.android.synthetic.main.activity_dev_tools.*

/**
 * Rabbit Entry Page
 * */
class RabbitMainActivity : RabbitBaseActivity() {

    private val TAG = javaClass.simpleName
    companion object {
        fun start(context: Context) {
            val intent = Intent(context, RabbitMainActivity::class.java)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    private val featuresAdapter by lazy {
        object : RabbitRvAdapter<RabbitMainFeatureInfo>(getAllFeatures()) {
            override fun createItem(type: Int) = RabbitMainFeatureView(this@RabbitMainActivity)
            override fun getItemType(data: RabbitMainFeatureInfo) = 0
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dev_tools)
        setBackListener(mDevToolsActionBar)
        mDevToolsActionBar.setTitle("Rabbit")
        mDevToolsEntryRv.adapter = featuresAdapter
        mDevToolsEntryRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    private fun getAllFeatures(): ArrayList<RabbitMainFeatureInfo> {
        return ArrayList<RabbitMainFeatureInfo>().apply {
            add(RabbitMainFeatureInfo("Rabbit功能配置", R.drawable.devtools_icon_feature_setting, RabbitConfigActivity::class.java))
            addAll(Rabbit.getCustomConfig().entryFeatures)
            add(RabbitMainFeatureInfo("网络日志", R.drawable.devtools_icon_http, RabbitHttpLogListActivity::class.java))
            add(RabbitMainFeatureInfo("异常日志", R.drawable.devtools_icon_exception_face,RabbitExceptionListActivity::class.java))
            add(RabbitMainFeatureInfo("卡顿检测(FPS)", R.drawable.devtools_icon_caton,null))
        }
    }

}
