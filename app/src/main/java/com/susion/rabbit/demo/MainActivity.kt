package com.susion.rabbit.demo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.susion.rabbit.Rabbit
import com.susion.rabbit.base.RabbitLog
import com.susion.rabbit.base.TAG_NATIVE
import com.susion.rabbit.base.common.DeviceUtils
import com.susion.rabbit.base.entities.RabbitFPSInfo
import com.susion.rabbit.base.ui.dp2px
import com.susion.rabbit.base.ui.throttleFirstClick
import com.susion.rabbit.demo.net.DevToolsTestApiModel
import com.susion.rabbit.demo.page.SimpleListActivity
import com.susion.rabbit.native_crash.RabbitNativeCrashCaptor
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

//随意的测试代码
class MainActivity : RabbitBaseActivity() {

    private val objList = ArrayList<Any>()
    private val TAG = "rabbit_test_main"
    private val PERMISSIONS_STORAGE =
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermission()

        mDevToolsTestTvRequestNet.setOnClickListener {
            sampleRequestNet()
        }

        mCrashTv.setOnClickListener {
            val a = 1 / 0
        }

        mGenerateObjTv.setOnClickListener {
            (0..1000).forEach { _ ->
                objList.add(View(this))
            }
        }

        mAnrTv.throttleFirstClick(Consumer {
            Thread.sleep(15000)
        })

        mSlowMethodTv.setOnClickListener {
            startActivity(Intent(this, MethodCostTraceActivity::class.java))
        }

        mBlockTv.setOnClickListener {
            Thread.sleep(2000)
        }

        mSimpleListRv.setOnClickListener {
            startActivity(Intent(this, SimpleListActivity::class.java))
        }

        logDeviceInfo()

        sampleRequestNet()

//        loadNativeLib()

        Thread(Runnable {
            Thread.sleep(2000)
//            mGenerateObjTv.layoutParams = ViewGroup.LayoutParams(200, 200)
            mGenerateObjTv.setText("对象")
        }, "rabbit-test-thread").start()

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
        Rabbit.open(true, this)
    }


    private fun logDeviceInfo() {
        RabbitLog.d(TAG, "phone brand : ${Build.BRAND}")
        RabbitLog.d(TAG, "phone product : ${Build.PRODUCT}")
        RabbitLog.d(TAG, "phone model : ${Build.MODEL}")
        RabbitLog.d(TAG, "phone hardware : ${Build.HARDWARE}")
        RabbitLog.d(TAG, "cpu name : ${DeviceUtils.getCpuName()}")
        RabbitLog.d(TAG, "device name : ${DeviceUtils.getDeviceName()}")
    }

    private val netRequestFinishView by lazy {
        TextView(this).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(40f))
            text = "首页请求完成!"
            gravity = Gravity.CENTER
            background =
                com.susion.rabbit.base.ui.getDrawable(this@MainActivity, R.color.rabbit_bg_card)
        }
    }

    private fun sampleRequestNet() {
        val dis = DevToolsTestApiModel().getAllGameList().subscribe({
            mRabbitMainAcRootLl.removeView(netRequestFinishView)
            mRabbitMainAcRootLl.addView(netRequestFinishView, 1)
        }, {
            Rabbit.saveCrashLog(it)
            Log.d(TAG, "error : ${it.message}")
        })
    }

    fun testRtn0(): Int {
        val a = 1
        val b = 2
        logDeviceInfo()
        return 3
    }

    fun testRtn1(): Int {
        val a = 1
        val b = 2
        logDeviceInfo()
        return a + b
    }

    fun testRtn2(): RabbitFPSInfo {
        val a = 1
        val b = 2
        logDeviceInfo()
        return RabbitFPSInfo()
    }

    private fun loadNativeLib() {
        try {
            RabbitNativeCrashCaptor().init()
        } catch (e: Exception) {
            RabbitLog.d(TAG_NATIVE, "native crash!")
        }

    }

}
