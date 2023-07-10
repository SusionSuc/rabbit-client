package com.susion.rabbit.ui.page

import android.content.Context
import android.view.ViewGroup
import com.susion.rabbit.base.RabbitSettings
import com.susion.rabbit.base.ui.dp2px
import com.susion.rabbit.base.ui.page.RabbitBasePage
import com.susion.rabbit.base.ui.view.RabbitSwitchButton
import com.susion.rabbit.ui.RabbitUi
import com.susion.rabbit.ui.monitor.R
import kotlinx.android.synthetic.main.rabbit_page_optimizer.view.mOptimizerConfigPageRootViewLl

/**
 * create by susionwang at 2020-01-10
 */
class RabbitOptimizerPage(context: Context) : RabbitBasePage(context) {

    override fun getLayoutResId() = R.layout.rabbit_page_optimizer

    init {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        setTitle("优化配置")

        //监控相关的配置
        RabbitUi.mConfig.optimizerList.filter { it.getOptimizerInfo().showInExternal }
            .forEach { optimizer ->
                val optimizerInfo = optimizer.getOptimizerInfo()
                val switchBtn = RabbitSwitchButton(context).apply {
                    LayoutParams(LayoutParams.MATCH_PARENT, dp2px(60f))
                }
                mOptimizerConfigPageRootViewLl.addView(switchBtn)
                switchBtn.checkedStatusChangeListener = object : RabbitSwitchButton.CheckedStatusChangeListener {
                    override fun checkedStatusChange(isChecked: Boolean) {
                        RabbitUi.eventListener?.toggleOptimizerStatus(optimizer, isChecked)
                    }
                }
                switchBtn.refreshUi(
                    optimizerInfo.znName,
                    RabbitSettings.autoOpen(context, optimizerInfo.name)
                )
            }
    }

}