package com.susion.rabbit.ui.page.global

import android.content.Context
import com.susion.rabbit.base.RabbitMonitorProtocol
import com.susion.rabbit.base.RabbitSettings
import com.susion.rabbit.base.ui.page.RabbitBasePage
import com.susion.rabbit.base.ui.view.RabbitSwitchButton
import com.susion.rabbit.ui.RabbitUi
import com.susion.rabbit.ui.monitor.R
import kotlinx.android.synthetic.main.rabbit_page_global_monitor_mode.view.*

/**
 * susionwang at 2019-10-29
 */
class RabbitGlobalMonitorModePage(context: Context) : RabbitBasePage(context) {

    override fun getLayoutResId() = R.layout.rabbit_page_global_monitor_mode

    init {

        setTitle("全局性能测试")

        mRabbitPageGlobalMonitorModeSwitchBtn.refreshUi(
            "性能测试模式",
            RabbitSettings.autoOpen(context, RabbitMonitorProtocol.GLOBAL_MONITOR.name)
        )
        mRabbitPageGlobalMonitorModeSwitchBtn.checkedStatusChangeListener =
            object : RabbitSwitchButton.CheckedStatusChangeListener {
                override fun checkedStatusChange(isChecked: Boolean) {
                    if (isChecked) {
                        showToast("重启应用后生效!")
                    }
                    RabbitUi.eventListener?.changeGlobalMonitorStatus(isChecked)
                }
            }
    }

}