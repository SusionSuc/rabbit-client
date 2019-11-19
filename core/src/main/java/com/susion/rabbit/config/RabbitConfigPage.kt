package com.susion.rabbit.config

import android.content.Context
import android.view.ViewGroup
import com.susion.rabbit.R
import com.susion.rabbit.Rabbit
import com.susion.rabbit.base.view.RabbitSwitchButton
import com.susion.rabbit.tracer.RabbitTracer
import com.susion.rabbit.ui.RabbitUiManager
import com.susion.rabbit.ui.page.RabbitBasePage
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
        mRabbitConfigSBOpenFpsMonitor.checkedStatusChangeListener =
            object : RabbitSwitchButton.CheckedStatusChangeListener {
                override fun checkedStatusChange(isChecked: Boolean) {
                    if (isChecked) {
                        RabbitTracer.openFpsMonitor()
                    } else {
                        RabbitTracer.closeFpsMonitor()
                        Rabbit.uiManager.updateUiFromAsynThread(RabbitUiManager.MSA_UPDATE_FPS, 0f)
                    }
                    RabbitSettings.setFPSCheckOpenFlag(context, isChecked)
                    refreshSwitchStatus()
                }
            }

        mRabbitConfigSBOpenBlockMonitor.checkedStatusChangeListener =
            object : RabbitSwitchButton.CheckedStatusChangeListener {
                override fun checkedStatusChange(isChecked: Boolean) {
                    if (isChecked) {
                        RabbitTracer.openBlockMonitor()
                    } else {
                        RabbitTracer.closeBlockMonitor()
                        Rabbit.uiManager.updateUiFromAsynThread(RabbitUiManager.MSA_UPDATE_FPS, 0f)
                    }
                    RabbitSettings.setBlockCheckOpenFlag(context, isChecked)
                    refreshSwitchStatus()
                }
            }

        mRabbitConfigSBPageSpeedMonitor.checkedStatusChangeListener =
            object : RabbitSwitchButton.CheckedStatusChangeListener {
                override fun checkedStatusChange(isChecked: Boolean) {
                    if (isChecked) {
                        RabbitTracer.openPageSpeedMonitor()
                    } else {
                        RabbitTracer.closePageSpeedMonitor()
                    }
                    RabbitSettings.setActivitySpeedMonitorOpenFlag(context, isChecked)
                    refreshSwitchStatus()
                }
            }

        refreshSwitchStatus()
    }

    private fun refreshSwitchStatus() {
        mRabbitConfigSBOpenFpsMonitor.refreshUi("打开FPS监控", RabbitTracer.fpsMonitorIsOpen())
        mRabbitConfigSBOpenBlockMonitor.refreshUi("打开卡顿监控", RabbitTracer.blockMonitorIsOpen())
        mRabbitConfigSBPageSpeedMonitor.refreshUi("打开页面测试监控", RabbitTracer.pageSpeedMonitorIsOpen())
    }

}