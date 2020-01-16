package com.susion.rabbit.base.ui.page

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.susion.rabbit.base.R
import com.susion.rabbit.base.ui.RabbitPageProtocol
import com.susion.rabbit.base.ui.dp2px
import com.susion.rabbit.base.ui.getColor
import com.susion.rabbit.base.ui.getDrawable
import com.susion.rabbit.base.ui.view.RabbitActionBar

/**
 * susionwang at 2019-10-21
 */
abstract class RabbitBasePage(context: Context) : FrameLayout(context), RabbitPageProtocol {

    val ACTION_BAR_HEIGHT = dp2px(42f)
    val actionBar = RabbitActionBar(context).apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, ACTION_BAR_HEIGHT)
        actionListener = object : RabbitActionBar.ActionListener {
            override fun onBackClick() {
                eventListener?.onBack()
            }
        }
    }

    val emptyIv = ImageView(context).apply {
        layoutParams = LinearLayout.LayoutParams(dp2px(50f), dp2px(50f)).apply {
            gravity = Gravity.CENTER_HORIZONTAL
        }
        setImageDrawable(getDrawable(context, R.drawable.rabbit_icon_empty_data))
    }

    val emptyTv = TextView(context).apply {
        layoutParams =
            LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                topMargin = dp2px(10f)
            }
        setTextColor(getColor(context, R.color.rabbit_black))
        text = "是不是没有打开监控开关呀"
    }

    private val emptyLl = LinearLayout(context).apply {
        layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.CENTER
        }
        orientation = LinearLayout.VERTICAL
        addView(emptyIv)
        addView(emptyTv)
    }

    override var eventListener: RabbitPageProtocol.PageEventListener? = null
    val INVALID_RES_ID = -1
    private val tvToast = TextView(context).apply {
        layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
            topMargin = ACTION_BAR_HEIGHT + dp2px(20f)
            gravity = Gravity.CENTER_HORIZONTAL
            leftMargin = dp2px(20f)
            rightMargin = dp2px(20f)
        }
        background = getDrawable(
            context,
            R.drawable.rabbit_bg_toast
        )
        setTextColor(
            getColor(
                context,
                R.color.rabbit_black
            )
        )
        val pd10 = dp2px(5f)
        setPadding(pd10, pd10, pd10, pd10)
    }

    init {
        addView(actionBar)
        if (getLayoutResId() != INVALID_RES_ID) {
            val inflatedView = LayoutInflater.from(context).inflate(getLayoutResId(), null)
            inflatedView.layoutParams =
                LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT).apply {
                    topMargin = ACTION_BAR_HEIGHT
                }
            addView(inflatedView)
        }
    }

    abstract fun getLayoutResId(): Int

    fun setTitle(title: String) {
        actionBar.setTitle(title)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return true
    }

    fun showToast(msg: String, duration: Long = 1000) {
        tvToast.text = msg
        addView(tvToast)
        postDelayed({
            removeView(tvToast)
        }, duration)
    }

    fun showEmptyView(msg: String = "是不是没有打开监控开关呀") {
        if (emptyLl.parent == null){
            addView(emptyLl)
            emptyTv.text = msg
        }
    }

    fun hideEmptyView() {
        if (emptyLl.parent != null){
            removeView(emptyLl)
        }
    }

}