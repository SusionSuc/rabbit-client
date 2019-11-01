package com.susion.devtools

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.util.Log
import com.susion.devtools.net.DevToolsTestApiModel
import com.susion.rabbit.Rabbit
import com.susion.rabbit.base.RabbitBaseActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.StringBuilder

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

        mDevToolsTestMainActionBar.setTitle("Alpha")
        setBackListener(mDevToolsTestMainActionBar)


        mDevToolsTestTvRequestNet.setOnClickListener {
            sampleRequestNet()
        }

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
