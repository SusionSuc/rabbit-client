package com.susion.rabbit.ui.view

import android.content.Context
import android.view.ViewGroup
import com.susion.lifeclean.common.recyclerview.AdapterItemView
import com.susion.rabbit.base.RabbitMonitorProtocol
import com.susion.rabbit.base.RabbitSettings
import com.susion.rabbit.base.ui.dp2px
import com.susion.rabbit.base.ui.view.RabbitSwitchButton
import com.susion.rabbit.ui.RabbitUi

/**
 * susionwang at 2020-03-05
 */
class RabbitConfigSwitchItemView(context: Context) : AdapterItemView<RabbitMonitorProtocol.MonitorInfo>,
    RabbitSwitchButton(context) {

    var toggleListener:RabbitMonitorSwitchListener? = null

    init {
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(60f))
    }

    override fun bindData(data: RabbitMonitorProtocol.MonitorInfo, position: Int) {

        checkedStatusChangeListener = object : CheckedStatusChangeListener {
            override fun checkedStatusChange(isChecked: Boolean) {
                toggleListener?.toggle(isChecked, position)
                RabbitSettings.setAutoOpenFlag(
                    context,
                    data.name,
                    isChecked
                )
            }
        }

        refreshUi(
            data.znName,
            RabbitSettings.autoOpen(context, data.name)
        )

    }

    interface  RabbitMonitorSwitchListener{
        fun toggle(isChecked:Boolean, pos:Int)
    }
}