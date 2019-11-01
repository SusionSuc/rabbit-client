package com.susion.rabbit.config

import android.content.Context
import android.view.ViewGroup
import com.susion.rabbit.R
import com.susion.rabbit.Rabbit
import com.susion.rabbit.base.view.RabbitSwitchButton
import com.susion.rabbit.trace.RabbitTracer
import com.susion.rabbit.ui.RabbitUiManager
import com.susion.rabbit.ui.page.RabbitBasePage
import com.susion.rabbit.utils.getDrawable
import kotlinx.android.synthetic.main.activity_dev_tools_config.view.*

/**
 * susionwang at 2019-10-21
 */
class RabbitConfigPage(context: Context) : RabbitBasePage(context) {

    override fun getLayoutResId() = R.layout.activity_dev_tools_config

    init {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        setTitle("功能配置")
        mRabbitConfigSBOpenFpsMonitor.checkedStatusChangeListener =
            object : RabbitSwitchButton.CheckedStatusChangeListener {
                override fun checkedStatusChange(isChecked: Boolean) {
                    if (isChecked) {
                        RabbitTracer.enableFPSTracer()
                        RabbitSettings.setFPSCheckOpenFlag(context, true)
                    } else {
                        RabbitTracer.disableFPSTracer()
                        RabbitSettings.setFPSCheckOpenFlag(context, false)
                        Rabbit.uiManager.updateUiFromAsynThread(RabbitUiManager.MSA_UPDATE_FPS, 0f)
                    }
                }
            }

        mRabbitConfigSBOpenFpsMonitor.refreshUi("打开FPS监控", RabbitTracer.isFPSTracerEnable())
    }

}