package com.susion.devtools

import android.app.Application
import com.susion.rabbit.Rabbit

/**
 * susionwang at 2019-09-24
 */
class CustomApplication:Application() {

    override fun onCreate() {
        super.onCreate()
        Rabbit.attachApplicationContext(this)
    }
}