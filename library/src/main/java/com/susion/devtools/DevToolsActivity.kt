package com.susion.devtools

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.susion.devtools.utils.DevToolsUiUtils
import kotlinx.android.synthetic.main.activity_dev_tools.*

class DevToolsActivity : AppCompatActivity() {

    companion object{
        fun start(context: Context){
            val intent = Intent(context, DevToolsActivity::class.java)
            if (context !is Activity){
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dev_tools)
        DevToolsUiUtils.setGeneralStatusBarColor(window)
        mDevToolsBtBack.setOnClickListener {
            finish()
        }

        mForumSimpleToolBarTvHttpLog.setOnClickListener {

        }
    }

}
