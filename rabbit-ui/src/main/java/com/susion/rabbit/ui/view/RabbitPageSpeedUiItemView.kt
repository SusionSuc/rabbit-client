package com.susion.rabbit.ui.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import com.susion.lifeclean.common.recyclerview.AdapterItemView
import com.susion.rabbit.base.entities.RabbitPageSpeedUiInfo
import com.susion.rabbit.base.ui.dp2px
import com.susion.rabbit.base.ui.getDrawable
import com.susion.rabbit.base.ui.throttleFirstClick
import com.susion.rabbit.ui.RabbitUi
import com.susion.rabbit.ui.monitor.R
import com.susion.rabbit.ui.page.RabbitSpeedPageDetailPage
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.rabbit_view_page_speed_item.view.*

/**
 * susionwang at 2019-09-25
 */
class RabbitPageSpeedUiItemView(context: Context) : LinearLayout(context),
    AdapterItemView<RabbitPageSpeedUiInfo> {

    init {
        LayoutInflater.from(context).inflate(R.layout.rabbit_view_page_speed_item, this)
        layoutParams =
            LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                val mr15 = dp2px(15f)
                setMargins(mr15, mr15, mr15, 0)
            }
        orientation = VERTICAL
        background = getDrawable(context, R.color.rabbit_bg_card)
    }

    override fun bindData(uiInfo: RabbitPageSpeedUiInfo, position: Int) {

        mRabbitUiBlockItemViewTvLine1.text = uiInfo.pageName.split(".").lastOrNull() ?: ""
        mRabbitUiBlockItemViewTvLine2.text = "记录数 : ${uiInfo.speedInfoList.size}"

        throttleFirstClick(Consumer {
            RabbitUi.openPage(RabbitSpeedPageDetailPage::class.java, uiInfo)
        })

    }

}