package com.susion.rabbit.ui.view

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.susion.lifeclean.common.recyclerview.AdapterItemView
import com.susion.rabbit.base.common.RabbitUtils
import com.susion.rabbit.base.common.rabbitTimeFormat
import com.susion.rabbit.base.entities.RabbitAnrInfo
import com.susion.rabbit.base.ui.dp2px
import com.susion.rabbit.base.ui.throttleFirstClick
import com.susion.rabbit.ui.RabbitUi
import com.susion.rabbit.ui.monitor.R
import com.susion.rabbit.ui.page.RabbitANRDetailPage
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.rabbit_view_anr_item.view.*

/**
 * susionwang at 2020-03-19
 */
class RabbitAnrItemView(context: Context) : RelativeLayout(context), AdapterItemView<RabbitAnrInfo> {

    init {
        LayoutInflater.from(context).inflate(R.layout.rabbit_view_anr_item, this)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT,
            dp2px(50f)
        ).apply {
            bottomMargin = dp2px(5f)
        }
    }

    override fun bindData(data: RabbitAnrInfo, position: Int) {
        mAnrItemViewTvLine1.text = "${data.pageName}  ${rabbitTimeFormat(data.time)}"

        if (Build.VERSION.SDK_INT >= 21) {
            mAnrItemViewTvLine2.text = RabbitUtils.getBlockStackIdentifierByMaxCount(RabbitUtils.getStackTraceList(data.stackStr))
        }else{
            mAnrItemViewTvLine2.text = data.stackStr
        }

        throttleFirstClick(Consumer {
            RabbitUi.openPage(RabbitANRDetailPage::class.java, data)
        })
    }

}