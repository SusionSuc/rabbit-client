package com.susion.rabbit.ui.base.page

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.susion.rabbit.ui.R
import com.susion.rabbit.ui.base.RabbitBasePage
import com.susion.rabbit.ui.base.RabbitMainFeatureInfo
import com.susion.rabbit.ui.base.adapter.RabbitRvAdapter
import com.susion.rabbit.ui.base.getDrawable
import com.susion.rabbit.ui.base.view.RabbitMainFeatureView

/**
 * susionwang at 2019-10-21
 * 入口View
 */
class RabbitEntryPage(context: Context, val defaultSupportFeatures:ArrayList<RabbitMainFeatureInfo>) : RabbitBasePage(context) {

    private val rv = RecyclerView(context)
    private val featuresAdapter by lazy {
        object : RabbitRvAdapter<RabbitMainFeatureInfo>(defaultSupportFeatures) {
            override fun createItem(type: Int) =
                RabbitMainFeatureView(context)
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

}