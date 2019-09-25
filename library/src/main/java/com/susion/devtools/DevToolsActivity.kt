package com.susion.devtools

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.susion.devtools.base.DevToolsBaseActivity
import com.susion.devtools.net.ui.HttpLogListActivity
import com.susion.devtools.utils.simpleStartActivity
import kotlinx.android.synthetic.main.activity_dev_tools.*

/**
 * DevTools Entry Page
 * */
class DevToolsActivity : DevToolsBaseActivity() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, DevToolsActivity::class.java)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dev_tools)
        setBackListener(mDevToolsActionBar)
        mDevToolsActionBar.setTitle("DevTools")
        mDevToolsTvHttpLog.setOnClickListener {
            simpleStartActivity(HttpLogListActivity::class.java)
        }
    }

}
