package com.susion.rabbit

import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.PixelFormat
import android.os.*
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import com.susion.rabbit.base.RabbitMainFeatureInfo
import com.susion.rabbit.common.RabbitActivityLifecycleWrapper
import com.susion.rabbit.page.RabbitEntryPage
import com.susion.rabbit.utils.dp2px
import com.susion.rabbit.utils.getDrawable
import com.susion.rabbit.view.RabbitFloatingView
import java.lang.ref.WeakReference

/**
 * susionwang at 2019-10-21
 * Rabbit Ui 控制
 */
object RabbitUi {

    const val PAGE_NULL = 1 //一个页面都没有
    const val PAGE_HIDE = 2
    const val PAGE_SHOWING = 3

    private val uiHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                RabbitUiEvent.MSG_UPDATE_FPS -> {
                    if (msg.obj is Float) {
                        floatingView.updateFps(msg.obj as Float)
                    }
                }
                RabbitUiEvent.MSG_UPDATE_MEMORY_VALUE -> {
                    if (msg.obj is String) {
                        floatingView.updateMemorySize(msg.obj as String)
                    }
                }
            }
        }
    }

    var config: Config = Config()
    var application: Application? = null

    //页面是否在展示
    private var pageShowStatus = PAGE_NULL

    //悬浮的View是否在显示
    private var floatingViewIsShow = false

    //rabbit floating views
    private val floatingView by lazy {
        RabbitFloatingView(application!!)
    }

    //rabbit page
    private val pageList = ArrayList<RabbitPageProtocol>()
    private val pageContainer by lazy {
        FrameLayout(application).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            background = getDrawable(context, R.color.rabbit_transparent_black)
        }
    }

    //当前应用正在展示的Activity
    var appCurrentActivity: WeakReference<Activity?>? = null

    fun init(application: Application, config: Config) {
        this.application = application
        this.config = config
        application.registerActivityLifecycleCallbacks(object :
            RabbitActivityLifecycleWrapper() {
            override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
                appCurrentActivity = WeakReference(activity)
            }
        })
    }

    private fun showRabbitEntryPage() {
        hideFloatingView()
        val entryPage = RabbitEntryPage(application!!)
        getWm().addView(pageContainer, getPageParams())
        pushPageToTopLevel(entryPage)
        showFloatingView()
    }

    private fun getPageParams(): WindowManager.LayoutParams {
        return WindowManager.LayoutParams().apply {
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR
            }
            flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
            format = PixelFormat.RGBA_8888
            gravity = Gravity.START or Gravity.TOP
            height = WindowManager.LayoutParams.MATCH_PARENT
            width = WindowManager.LayoutParams.MATCH_PARENT
            x = 0
            y = 0
            windowAnimations = android.R.style.Animation_Toast
        }
    }

    private fun getWm() = (application!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager)

    private fun addPage(pageClass: Class<out View>): RabbitPageProtocol? {
        var newedView: View? = null

        for (surInt in pageClass.interfaces) {
            if (surInt == RabbitPageProtocol::class.java) {
                newedView = pageClass.getConstructor(Context::class.java).newInstance(application)
                break
            }
        }

        if (newedView == null) {
            for (surInt in pageClass.superclass.interfaces) {
                if (surInt == RabbitPageProtocol::class.java) {
                    newedView = pageClass.getConstructor(Context::class.java)
                        .newInstance(application)
                    break
                }
            }
        }

        if (newedView == null || newedView !is RabbitPageProtocol) return null

        pageList.add(newedView)

        return newedView
    }

    fun openPage(pageClass: Class<out View>?, params: Any? = null) {
        if (pageClass == null) return
        val pageProtocol = addPage(pageClass) ?: return
        if (params != null) {
            pageProtocol.setEntryParams(params)
        }
        pushPageToTopLevel(pageProtocol)
    }

    private fun setBackEventListener(rabbitPage: RabbitPageProtocol) {
        rabbitPage.eventListener = object : RabbitPageProtocol.PageEventListener {
            override fun onBack() {
                popPageFromTopLevel()
            }
        }
    }

    private fun pushPageToTopLevel(newPage: RabbitPageProtocol) {
        pageList.add(newPage)

        if (newPage is View) {

            setBackEventListener(newPage)

            newPage.layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            ).apply {
                topMargin = dp2px(50f)
            }

            pageContainer.addView(newPage)

            pageShowStatus = PAGE_SHOWING
        }
    }

    private fun popPageFromTopLevel() {
        val removedPage = pageList.removeAt(pageList.size - 1)
        pageContainer.removeView(removedPage as View)
        if (pageList.isEmpty()) {
            pageShowStatus = PAGE_NULL
            getWm().removeView(pageContainer)
        }
    }

    fun showFloatingView() {
        if (floatingViewIsShow) return
        floatingViewIsShow = true
        floatingView.show()
    }

    fun hideFloatingView() {
        floatingViewIsShow = false
        floatingView.hide()
    }

    fun handleFloatingViewClickEvent() {
        RabbitLog.d("pageShowStatus : $pageShowStatus")
        when (pageShowStatus) {
            PAGE_NULL -> showRabbitEntryPage()
            PAGE_SHOWING -> hideRabbitPage()
            PAGE_HIDE -> restoreRabbitPage()
        }
    }

    private fun restoreRabbitPage() {
        pageShowStatus = PAGE_SHOWING
        pageContainer.visibility = View.VISIBLE
    }

    private fun hideRabbitPage() {
        pageShowStatus = PAGE_HIDE
        pageContainer.visibility = View.GONE
    }

    fun hideAllPage() {
        if (pageShowStatus != PAGE_SHOWING) return
        hideRabbitPage()
    }

    fun updateUiFromAsyncThread(msgType: Int, params: Any) {
        val msg = uiHandler.obtainMessage()
        msg.what = msgType
        msg.obj = params
        uiHandler.sendMessage(msg)
    }

    fun pageIsShow() = pageShowStatus == PAGE_SHOWING

    class Config(var entryFeatures: List<RabbitMainFeatureInfo> = ArrayList())

}