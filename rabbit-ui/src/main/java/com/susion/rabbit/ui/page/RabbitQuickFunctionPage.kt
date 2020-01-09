package com.susion.rabbit.ui.page

import android.content.Context
import android.widget.ArrayAdapter
import com.susion.rabbit.storage.RabbitStorage
import com.susion.rabbit.base.ui.page.RabbitBasePage
import com.susion.rabbit.ui.monitor.R
import com.susion.rabbit.ui.RabbitUi
import kotlinx.android.synthetic.main.rabbit_page_quick_function.view.*

/**
 * susionwang at 2020-01-09
 */
class RabbitQuickFunctionPage(context: Context) : RabbitBasePage(context) {

    override fun getLayoutResId() = R.layout.rabbit_page_quick_function

    init {
        setTitle("快捷功能")
        initQuickClear()
    }

    private fun initQuickClear() {
        val monitors = RabbitUi.mConfig.monitorList.filter { it.getMonitorInfo().showInExternal }.map { it.getMonitorInfo().name }
        val stringAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, monitors)
        mRabbitQuickPageSpinnerMonitors.adapter = stringAdapter
        mRabbitQuickPageClearDataBtn.setOnClickListener {
            RabbitStorage.clearDataByMonitorName(monitors[mRabbitQuickPageSpinnerMonitors.selectedItemPosition])
            showToast("清空成功!")
        }
    }

}