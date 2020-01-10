package com.susion.rabbit.ui

import android.content.Context
import android.view.ViewGroup
import com.susion.rabbit.base.RabbitSettings
import com.susion.rabbit.base.ui.page.RabbitBasePage
import com.susion.rabbit.base.ui.dp2px
import com.susion.rabbit.base.ui.view.RabbitSwitchButton
import com.susion.rabbit.ui.monitor.R
import kotlinx.android.synthetic.main.rabbit_page_config.view.*

/**
 * susionwang at 2019-10-21
 * rabbit 功能配置
 */
class RabbitMonitorConfigPage(context: Context) : RabbitBasePage(context) {

    override fun getLayoutResId() = R.layout.rabbit_page_config

    init {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        setTitle("监控开关")

        //监控相关的配置
        RabbitUi.mConfig.monitorList.filter { it.getMonitorInfo().showInExternal }
            .forEach { monitor ->
                val monitorInfo = monitor.getMonitorInfo()
                val switchBtn = RabbitSwitchButton(context).apply {
                    LayoutParams(LayoutParams.MATCH_PARENT, dp2px(60f))
                }
                mConfigPageRootViewLl.addView(switchBtn)
                switchBtn.checkedStatusChangeListener =
                    object : RabbitSwitchButton.CheckedStatusChangeListener {
                        override fun checkedStatusChange(isChecked: Boolean) {
                            RabbitUi.eventListener?.toggleMonitorStatus(monitor, isChecked)
                            RabbitSettings.setAutoOpenFlag(
                                context,
                                monitorInfo.name,
                                isChecked
                            )
                        }
                    }
                switchBtn.refreshUi(
                    monitorInfo.znName,
                    RabbitSettings.autoOpen(context, monitorInfo.name)
                )
            }
    }

}