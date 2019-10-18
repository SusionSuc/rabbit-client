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
        mDevToolsTestTvOpen.setOnClickListener {
            if (!Rabbit.devToolsIsOpen()) {
                Rabbit.openDevTools(true, this)
            } else {
            }
            refreshOpenStatus()
        }

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

        mDevToolsTestTvRequestNet.postDelayed({
            refreshOpenStatus()
        }, 500)

        mGetMainThreadStack.setOnClickListener {
            Log.d(TAG, "start time : ${System.currentTimeMillis()}")
            val stack = Exception("custom").stackTrace
            val a = 1
            Log.d(TAG, "trace : ${traceToString(0, stack)}")
            Log.d(TAG, "end time : ${System.currentTimeMillis()}")
        }

        mGetMainThreadStack2.setOnClickListener {
            Log.d(TAG, "start time : ${System.currentTimeMillis()}")
            val mainThread = Looper.getMainLooper().thread
            val stackArray = mainThread.stackTrace
            Log.d(TAG, "trace :${traceToString(0, stackArray)}")
            Log.d(TAG, "end time : ${System.currentTimeMillis()}")
        }

    }

    private fun refreshOpenStatus() {
        mDevToolsTestTvOpen.text = if (Rabbit.devToolsIsOpen()) "关闭DevTools" else "打开DevTools"
    }

    private fun sampleRequestNet() {
        val dis = DevToolsTestApiModel().getAllGameList().subscribe({
            val a = 1
        }, {
            Log.d(TAG, "error : ${it.message}")
        })
    }


    fun traceToString(skipStackCount: Int, stackArray: Array<StackTraceElement>): String {

        if (stackArray.isEmpty()) {
            return "[]"
        }

        val b = StringBuilder()
        for (i in 0 until stackArray.size - skipStackCount) {
            if (i == stackArray.size - skipStackCount - 1) {
                return b.toString()
            }
            b.append(stackArray[i])
            b.append("\n")
        }
        return b.toString()
    }

}
