package com.susion.rabbit.base.ui.page

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.susion.rabbit.base.R
import com.susion.rabbit.base.config.RabbitMainFeatureInfo
import com.susion.rabbit.base.ui.adapter.RabbitRvAdapter
import com.susion.rabbit.base.ui.dp2px
import com.susion.rabbit.base.ui.getDrawable
import com.susion.rabbit.base.ui.utils.RabbitSimpleCardDecoration
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
        object : RabbitRvAdapter<RabbitMainFeatureInfo>(defaultSupportFeatures) {
            override fun createItem(type: Int) = RabbitMainFeatureView(context)
            override fun getItemType(data: RabbitMainFeatureInfo) = 0
        }
    }

    private val rv = RecyclerView(context).apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutManager = GridLayoutManager(context, 2)
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