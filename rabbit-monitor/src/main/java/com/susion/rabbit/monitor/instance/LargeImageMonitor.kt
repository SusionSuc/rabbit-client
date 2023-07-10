package com.susion.rabbit.monitor.instance

import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.susion.rabbit.monitor.RabbitMonitor
import com.susion.rabbit.monitor.utils.RabbitMonitorUtils.findActivityNameByView
import com.susion.rabbit.monitor.utils.RabbitMonitorUtils.findViewIDNameByView

/**
 * 主要思路
 * 在Activity onStop时遍历所有子View,查找ImageView
 * 查找内存占用超过阈值的ImageView的id并记录
 * 这种方式比起插桩方式更为简单，缺点在于实时性不够，需要在页面关闭时才会去遍历
 */
internal class LargeImageMonitor : Application.ActivityLifecycleCallbacks {
    fun init(application: Application) {
        application.registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {}

    override fun onActivityResumed(activity: Activity) {}

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {
        val fragments: List<Fragment>? = getAllFragmentsFromActivity(activity)
        if (fragments.isNullOrEmpty()) {
            val childViews: List<View> = getAllChildViews(activity.window.decorView)
            checkBitmapIsTooBig(activity, childViews)
        } else {
            for (fragment in fragments) {
                val childViews: List<View> = getAllChildViews(fragment.view)
                checkBitmapIsTooBig(activity, childViews, fragment::class.java.name)
            }
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {}

    override fun onActivityDestroyed(activity: Activity?) {}

    private fun getAllFragmentsFromActivity(activity: Activity): List<Fragment>? {
        if (activity is FragmentActivity) {
            return activity.supportFragmentManager.fragments
        }
        return null
    }

    private fun getAllChildViews(view: View?): List<View> {
        val allChildren = ArrayList<View>()
        if (view is ViewGroup) {
            for (i in 0..view.childCount) {
                val viewChild: View = view.getChildAt(i) ?: return allChildren
                allChildren.add(viewChild)
                allChildren.addAll(getAllChildViews(viewChild))
            }
        }
        return allChildren
    }

    private fun checkBitmapIsTooBig(
        context: Context,
        views: List<View>,
        containerName: String? = null
    ) {
        for (view in views) {
            checkBitmapIsTooBig(context, view, containerName)
        }
    }

    private fun checkBitmapIsTooBig(
        context: Context,
        view: View,
        containerName: String? = null
    ) {
        val viewIdName = findViewIDNameByView(context, view)
        val activityName = if (containerName.isNullOrEmpty()) findActivityNameByView(view) else containerName
        if (view is ImageView) {
            if (view.drawable !is BitmapDrawable) {
                return
            }
            val bmDrawable: BitmapDrawable? = view.drawable as BitmapDrawable?
            val bm: Bitmap? = bmDrawable?.bitmap

            bm?.let {
                val memorySize = it.byteCount / 1024
                if (memorySize > RabbitMonitor.mConfig.largeImageMemoryThreshold) {
                    //TODO 保存activityName,viewId,图片信息到数据库，供后续查询
                }
            }
        }
    }
}