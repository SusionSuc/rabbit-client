package com.susion.rabbit.demo

import android.os.Bundle
import com.susion.rabbit.base.RabbitLog

/**
 * wangpengcheng.wpc create at 2023/7/3
 * */
class RabbitLaunchCatonActivity : RabbitBaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onResume() {
        super.onResume()
        RabbitLog.d("caton activity on resume")
    }

}