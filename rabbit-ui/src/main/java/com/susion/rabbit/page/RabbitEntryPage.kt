package com.susion.rabbit.page

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import com.susion.rabbit.R
import com.susion.rabbit.base.RabbitMainFeatureInfo
import com.susion.rabbit.base.adapter.RabbitRvAdapter
import com.susion.rabbit.base.view.RabbitMainFeatureView
import com.susion.rabbit.RabbitConfigPage
import com.susion.rabbit.RabbitUi
import com.susion.rabbit.base.RabbitBasePage
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
        rv.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
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
            addAll(RabbitUi.config.entryFeatures)
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
                    R.drawable.rabbit_icon_block,
                    RabbitUiBlockListPage::class.java
                )
            )

            add(
                RabbitMainFeatureInfo(
                    "应用测速",
                    R.drawable.rabbit_icon_speed,
                    RabbitAppSpeedMonitorDetailPage::class.java
                )
            )

            add(
                RabbitMainFeatureInfo(
                    "内存泄漏",
                    R.drawable.rabbit_icon_memory_leak,
                    null
                ) {
                    //隐私启动，显示调用会在release构建时有问题
                    val leakIntent = Intent()
                    leakIntent.component = ComponentName(
                        "com.mihoyo.hyperion",
                        "leakcanary.internal.activity.LeakActivity"
                    )
                    RabbitUi.appCurrentActivity?.get()?.startActivity(leakIntent)
                    RabbitUi.hideAllPage()
                }
            )

            add(
                RabbitMainFeatureInfo(
                    "内存分析",
                    R.drawable.rabbit_icon_memory_compose,
                    RabbitMemoryComposePage::class.java
                )
            )
        }
    }

}