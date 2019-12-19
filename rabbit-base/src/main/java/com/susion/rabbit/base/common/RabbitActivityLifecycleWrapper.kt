package com.susion.rabbit.base.common

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 * susionwang at 2019-11-26
 */
open class RabbitActivityLifecycleWrapper :Application.ActivityLifecycleCallbacks{
    override fun onActivityResumed(activity: Activity?) {
    }

    override fun onActivityStarted(activity: Activity?) {
    }

    override fun onActivityDestroyed(activity: Activity?) {
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity?) {
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
    }

    override fun onActivityPaused(activity: Activity?) {
    }

}