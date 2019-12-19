package com.susion.rabbit.ui

import android.content.Context
import android.view.ViewGroup
import com.susion.rabbit.base.RabbitSettings
import com.susion.rabbit.base.common.RabbitUiUtils
import com.susion.rabbit.ui.base.view.RabbitSwitchButton
import com.susion.rabbit.ui.base.RabbitBasePage
import kotlinx.android.synthetic.main.rabbit_page_config.view.*

/**
 * susionwang at 2019-10-21
 *
 */
class RabbitConfigPage(context: Context) : RabbitBasePage(context) {

    override fun getLayoutResId() = R.layout.rabbit_page_config

    init {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        setTitle("功能配置")

        //监控相关的配置
        RabbitUi.config.monitorList.forEach { monitor ->
            val monitorInfo = monitor.getMonitorInfo()
            val switchBtn = RabbitSwitchButton(context).apply {
                LayoutParams(LayoutParams.MATCH_PARENT, RabbitUiUtils.dp2px(60f))
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