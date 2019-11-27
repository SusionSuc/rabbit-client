package com.susion.devtools

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import com.susion.devtools.net.DevToolsTestApiModel
import com.susion.rabbit.Rabbit
import com.susion.rabbit.base.RabbitBaseActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : RabbitBaseActivity() {

    private val TAG = javaClass.simpleName
    private val PERMISSIONS_STORAGE =
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mDevToolsTestMainActionBar.setTitle("Rabbit")
        mDevToolsTestMainActionBar.hideBackBtn()
        setBackListener(mDevToolsTestMainActionBar)

        requestPermission()

        mDevToolsTestTvRequestNet.setOnClickListener {
            sampleRequestNet()
        }

        mTracerTv.setOnClickListener {
            startActivity(Intent(this, TracerTestActivity::class.java))
        }

        mCrashTv.setOnClickListener {
            val a = 1 / 0
        }

        mLeakCanaryTv.setOnClickListener {
            
        }

    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 1);
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Rabbit.openDevTools(true, this)
    }

    private fun sampleRequestNet() {
        val dis = DevToolsTestApiModel().getAllGameList().subscribe({
            val a = 1
        }, {
            Log.d(TAG, "error : ${it.message}")
        })
    }

}
