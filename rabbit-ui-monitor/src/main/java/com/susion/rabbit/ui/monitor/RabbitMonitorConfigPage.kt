package com.susion.rabbit.ui.monitor

import android.content.Context
import android.view.ViewGroup
import com.susion.rabbit.base.RabbitSettings
import com.susion.rabbit.base.config.RabbitConfig
import com.susion.rabbit.ui.base.RabbitBasePage
import com.susion.rabbit.ui.base.RabbitUi
import com.susion.rabbit.ui.base.utils.RabbitUiUtils
import com.susion.rabbit.ui.base.view.RabbitSwitchButton
import kotlinx.android.synthetic.main.rabbit_page_config.view.*

/**
 * susionwang at 2019-10-21
 * rabbit 配置
 */
class RabbitMonitorConfigPage(context: Context) : RabbitBasePage(context) {

    override fun getLayoutResId() = R.layout.rabbit_page_config

    init {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        setTitle("功能配置")

        //监控相关的配置
        RabbitMonitorUi.config.monitorList.filter { it.getMonitorInfo().showInExternal }
            .forEach { monitor ->
                val monitorInfo = monitor.getMonitorInfo()
                val switchBtn = RabbitSwitchButton(context).apply {
                    LayoutParams(LayoutParams.MATCH_PARENT, RabbitUiUtils.dp2px(60f))
                }
                mConfigPageRootViewLl.addView(switchBtn)
                switchBtn.checkedStatusChangeListener =
                    object : RabbitSwitchButton.CheckedStatusChangeListener {
                        override fun checkedStatusChange(isChecked: Boolean) {
                            RabbitMonitorUi.eventListener?.toggleMonitorStatus(monitor, isChecked)
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

        //自定义添加的一些配置
        RabbitUi.config.customConfigList.forEach {
            val switchBtn = RabbitSwitchButton(context).apply {
                LayoutParams(LayoutParams.MATCH_PARENT, RabbitUiUtils.dp2px(60f))
            }
            mConfigPageRootViewLl.addView(switchBtn)
            switchBtn.checkedStatusChangeListener =
                object : RabbitSwitchButton.CheckedStatusChangeListener {
                    override fun checkedStatusChange(isChecked: Boolean) {
                        it.statusChangeCallBack.onChange(isChecked)
                    }
                }
            switchBtn.refreshUi(
                it.configName,
                it.initStatus
            )
        }
    }

}