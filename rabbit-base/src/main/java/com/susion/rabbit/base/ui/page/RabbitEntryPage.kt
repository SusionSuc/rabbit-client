package com.susion.rabbit.base.ui.page

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.susion.lifeclean.common.recyclerview.SimpleRvAdapter
import com.susion.rabbit.base.R
import com.susion.rabbit.base.config.RabbitEntryTitleInfo
import com.susion.rabbit.base.config.RabbitMainFeatureInfo
import com.susion.rabbit.base.ui.dp2px
import com.susion.rabbit.base.ui.getDrawable
import com.susion.rabbit.base.ui.utils.RabbitSimpleCardDecoration
import com.susion.rabbit.base.ui.view.RabbitEntryTitleView
import com.susion.rabbit.base.ui.view.RabbitMainFeatureView

/**
 * susionwang at 2019-10-21
 * 入口View
 */
class RabbitEntryPage(
    context: Context,
    val defaultSupportFeatures: ArrayList<RabbitMainFeatureInfo>,
    rightOpeClickCallback: (() -> Unit)? = null
) : RabbitBasePage(context) {

    private val featuresAdapter by lazy {
        val categoryFeaturesMap = HashMap<String, MutableList<RabbitMainFeatureInfo>>()
        defaultSupportFeatures.forEach { featureInfo->
            val list = categoryFeaturesMap.get(featureInfo.type) ?: let{
                val newList = ArrayList<RabbitMainFeatureInfo>()
                categoryFeaturesMap.put(featureInfo.type, newList)
                newList
            }
            list.add(featureInfo)
        }
        val newFeatures = ArrayList<Any>()
        categoryFeaturesMap.forEach { entry ->
            newFeatures.add(RabbitEntryTitleInfo(entry.key))
            newFeatures.addAll(entry.value)
        }
        SimpleRvAdapter(context, newFeatures).apply {
            registerMapping(RabbitMainFeatureInfo::class.java, RabbitMainFeatureView::class.java)
            registerMapping(RabbitEntryTitleInfo::class.java, RabbitEntryTitleView::class.java)
        }
    }

    private val rv = RecyclerView(context).apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutManager = GridLayoutManager(context, 2).apply {
            spanSizeLookup = object  : SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    val data = featuresAdapter.data[position]
                    if (data is RabbitEntryTitleInfo) {
                        return 2
                    }
                    return 1
                }
            }
        }
        addItemDecoration(RabbitSimpleCardDecoration(dp2px(5f), dp2px(5f)))
        adapter = featuresAdapter
        setPadding(0, dp2px(5f), 0, 0)
    }

    private val refreshView = SwipeRefreshLayout(context).apply {
        layoutParams =
            LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT).apply {
                topMargin = ACTION_BAR_HEIGHT
            }
    }


    init {
        refreshView.addView(rv)

        addView(refreshView)
        setTitle("Rabbit")

        background = getDrawable(context, R.color.rabbit_white)

        if (rightOpeClickCallback != null) {
            actionBar.setRightOperate(
                R.drawable.rabbit_icon_entry_quick_function,
                rightOpeClickCallback
            )
        }

        refreshView.setOnRefreshListener {
            postDelayed({
                showToast("功能不完善？提pr一块改善rabbit吧")
                refreshView.isRefreshing = false
            }, 1500)
        }

    }

    override fun getLayoutResId() = INVALID_RES_ID

}