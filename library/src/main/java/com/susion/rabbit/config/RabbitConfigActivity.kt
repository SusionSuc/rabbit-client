package com.susion.rabbit.config

import android.os.Bundle
import com.susion.rabbit.R
import com.susion.rabbit.base.RabbitBaseActivity
import com.susion.rabbit.base.view.RabbitSwitchButton
import com.susion.rabbit.trace.RabbitTracer
import kotlinx.android.synthetic.main.activity_dev_tools_config.*

class RabbitConfigActivity : RabbitBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dev_tools_config)
        setBackListener(mDevToolsConfigActionBar)
        mDevToolsConfigActionBar.setTitle("功能配置")
        mRabbitConfigSBOpenFpsMonitor.refreshUi("FPS监控", RabbitTracer.isFPSTracerEnable())

        mRabbitConfigSBOpenFpsMonitor.checkedStatusChangeListener = object : RabbitSwitchButton.CheckedStatusChangeListener{
            override fun checkedStatusChange(isChecked: Boolean) {
                if (isChecked){
                    RabbitTracer.enableFPSTracer()
                }else{
                    RabbitTracer.disableFPSTracer()
                }
            }

        }
    }

}
