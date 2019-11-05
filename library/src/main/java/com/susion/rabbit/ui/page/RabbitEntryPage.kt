package com.susion.rabbit.ui.page

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.susion.rabbit.R
import com.susion.rabbit.Rabbit
import com.susion.rabbit.base.RabbitMainFeatureInfo
import com.susion.rabbit.base.adapter.RabbitRvAdapter
import com.susion.rabbit.base.view.RabbitMainFeatureView
import com.susion.rabbit.config.RabbitConfigPage
import com.susion.rabbit.exception.ui.RabbitExceptionListPage
import com.susion.rabbit.net.ui.RabbitHttpLogListPage
import com.susion.rabbit.trace.ui.RabbitUiBlockListPage
import com.susion.rabbit.utils.getDrawable

/**
 * susionwang at 2019-10-21
 * 入口View
 */
class RabbitEntryPage(context: Context) : RabbitBasePage(context) {

    private val rv = RecyclerView(context)
    private val featuresAdapter by lazy {
        object : RabbitRvAdapter<RabbitMainFeatureInfo>(getAllFeatures()) {
            override fun createItem(type: Int) = RabbitMainFeatureView(context)
            override fun getItemType(data: RabbitMainFeatureInfo) = 0
        }
    }

    init {
        addView(rv)
        rv.adapter = featuresAdapter
        rv.layoutParams =
            LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        setTitle("Rabbit")
        rv.background = getDrawable(context, R.color.rabbit_white)
    }

    override fun getLayoutResId() = -1

    private fun getAllFeatures(): ArrayList<RabbitMainFeatureInfo> {
        return ArrayList<RabbitMainFeatureInfo>().apply {
            add(
                RabbitMainFeatureInfo(
                    "Rabbit功能配置",
                    R.drawable.rabbit_icon_feature_setting,
                    RabbitConfigPage::class.java
                )
            )
            addAll(Rabbit.geConfig().entryFeatures)
            add(
                RabbitMainFeatureInfo(
                    "网络日志",
                    R.drawable.rabbit_icon_http,
                    RabbitHttpLogListPage::class.java
                )
            )
            add(
                RabbitMainFeatureInfo(
                    "异常日志", R.drawable.rabbit_icon_exception_face,
                    RabbitExceptionListPage::class.java
                )
            )
            add(
                RabbitMainFeatureInfo(
                    "卡顿日志",
                    R.drawable.rabbit_icon_caton,
                    RabbitUiBlockListPage::class.java
                )
            )
        }
    }

}