package com.susion.rabbit.ui.base.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.susion.rabbit.ui.base.*
import com.susion.rabbit.ui.base.adapter.RabbitAdapterItemView
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.rabbit_view_main_feature_view.view.*

/**
 * susionwang at 2019-10-12
 */

class RabbitMainFeatureView : RelativeLayout,
    RabbitAdapterItemView<RabbitMainFeatureInfo> {

    var clickListener:ClickListener? = null
    lateinit var mFeatureInfo: RabbitMainFeatureInfo

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        initView()
    }

    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.rabbit_view_main_feature_view, this)
        layoutParams = MarginLayoutParams(LayoutParams.MATCH_PARENT,
            dp2px(50f)
        ).apply {
            topMargin = dp2px(10f)
        }
        val pd10 = dp2px(10f)
        setPadding(pd10, 0, 0, 0)
        throttleFirstClick(Consumer {
            clickListener?.onClick()
            RabbitUi.openPage(mFeatureInfo.pageClass)
            if (mFeatureInfo.pageClass == null){
                mFeatureInfo.action()
            }
        })
    }

    override fun bindData(fearureInfo: RabbitMainFeatureInfo, position: Int) {
        mFeatureInfo = fearureInfo
        mDevToolsMainFeatureTvName.text = fearureInfo.name
        mDevToolsMainFeatureIvIcon.setImageDrawable(
            getDrawable(
                context,
                fearureInfo.icon
            )
        )
    }

    interface ClickListener{
        fun onClick()
    }

}