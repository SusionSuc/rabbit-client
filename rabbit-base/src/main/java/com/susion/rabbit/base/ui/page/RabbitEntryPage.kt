package com.susion.rabbit.base.ui.page

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.susion.rabbit.base.R
import com.susion.rabbit.base.config.RabbitMainFeatureInfo
import com.susion.rabbit.base.ui.adapter.RabbitRvAdapter
import com.susion.rabbit.base.ui.getDrawable
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

    private val rv = RecyclerView(context)
    private val featuresAdapter by lazy {
        object : RabbitRvAdapter<RabbitMainFeatureInfo>(defaultSupportFeatures) {
            override fun createItem(type: Int) = RabbitMainFeatureView(context)
            override fun getItemType(data: RabbitMainFeatureInfo) = 0
        }
    }

    init {
        addView(rv)
        rv.adapter = featuresAdapter
        rv.layoutParams =
            LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT).apply {
                topMargin = ACTION_BAR_HEIGHT
            }
        rv.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        setTitle("Rabbit")
        rv.background = getDrawable(context, R.color.rabbit_white)

        if (rightOpeClickCallback != null) {
            actionBar.setRightOperate(
                R.drawable.rabbit_icon_entry_quick_function,
                rightOpeClickCallback
            )
        }
    }

    override fun getLayoutResId() = INVALID_RES_ID

}