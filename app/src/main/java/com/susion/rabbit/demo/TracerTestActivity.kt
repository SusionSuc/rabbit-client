package com.susion.rabbit.demo

import android.content.Intent
import android.os.Bundle
import com.susion.rabbit.ui.base.RabbitBaseActivity
import kotlinx.android.synthetic.main.activity_tracer_test.*

class TracerTestActivity : RabbitBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracer_test)
        setActionBar(mTracerPageActionBar)
        mTracerPageActionBar.setTitle("Tracer")

        mTracerPageTvSimpleList.setOnClickListener {
            startActivity(Intent(this, SimpleListActivity::class.java))
        }

        mTracerPageTvBlock2S.setOnClickListener {
            Thread.sleep(2000)
        }

    }

}
