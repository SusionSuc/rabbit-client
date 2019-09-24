package com.susion.devtools

import android.app.Application

/**
 * susionwang at 2019-09-24
 */
class CustomApplication:Application() {

    override fun onCreate() {
        super.onCreate()

        DevTools.init(this)
        DevTools.tryShowDevFloatingView(this)

    }
}