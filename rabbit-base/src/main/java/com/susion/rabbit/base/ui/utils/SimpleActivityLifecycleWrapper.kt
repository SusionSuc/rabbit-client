package com.susion.rabbit.base.ui.utils

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 * susionwang at 2019-11-26
 */
internal open class SimpleActivityLifecycleWrapper :Application.ActivityLifecycleCallbacks{
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        
    }

    override fun onActivityStarted(activity: Activity) {
        
    }

    override fun onActivityResumed(activity: Activity) {
        
    }

    override fun onActivityPaused(activity: Activity) {
        
    }

    override fun onActivityStopped(activity: Activity) {
        
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        
    }

    override fun onActivityDestroyed(activity: Activity) {
        
    }


}