package com.susion.rabbit.demo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.susion.rabbit.ui.base.RabbitBaseActivity
import com.susion.rabbit.ui.base.RabbitBasePage
import kotlinx.android.synthetic.main.activity_tracer_test.*

class TracerTestActivity : RabbitBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracer_test)
        mDemoTvTitle.text = "Tracer"

        mTracerPageTvSimpleList.setOnClickListener {
            startActivity(Intent(this, SimpleListActivity::class.java))
        }

        mTracerPageTvBlock2S.setOnClickListener {
            Thread.sleep(2000)
        }

        mDemoIvBack.setOnClickListener {
            finish()
        }
    }

}
