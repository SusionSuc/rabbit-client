package com.susion.rabbit

import android.content.Context
import android.view.ViewGroup
import com.susion.rabbit.base.view.RabbitSwitchButton
import com.susion.rabbit.base.RabbitBasePage
import com.susion.rabbit.common.RabbitUiUtils
import com.susion.rabbit.monitor.RabbitMonitor
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
        RabbitMonitor.getMonitorList().forEach { monitor ->
            val monitorInfo = monitor.getMonitorInfo()
            val switchBtn = RabbitSwitchButton(context).apply {
                LayoutParams(LayoutParams.MATCH_PARENT, RabbitUiUtils.dp2px(60f))
            }
            mConfigPageRootViewLl.addView(switchBtn)
            switchBtn.checkedStatusChangeListener = object : RabbitSwitchButton.CheckedStatusChangeListener {
                override fun checkedStatusChange(isChecked: Boolean) {
                    if (isChecked) {
                        RabbitMonitor.openMonitor(monitorInfo.name)
                    } else {
                        RabbitMonitor.closeMonitor(monitorInfo.name)
                    }
                    RabbitSettings.setAutoOpenFlag(context, monitorInfo.name, isChecked)
                }
            }
            switchBtn.refreshUi(
                monitorInfo.znName,
                RabbitSettings.autoOpen(context, monitorInfo.name)
            )
        }

    }

}