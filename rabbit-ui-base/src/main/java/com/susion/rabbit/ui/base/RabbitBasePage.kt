package com.susion.rabbit.ui.base

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.susion.rabbit.ui.base.view.RabbitActionBar

/**
 * susionwang at 2019-10-21
 */
abstract class RabbitBasePage(context: Context) : FrameLayout(context),
    RabbitPageProtocol {

    val ACTION_BAR_HEIGHT = dp2px(42f)
    val actionBar = RabbitActionBar(context).apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, ACTION_BAR_HEIGHT)
        actionListener = object : RabbitActionBar.ActionListener {
            override fun onBackClick() {
                eventListener?.onBack()
            }
        }
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
        background = getDrawable(context, R.drawable.rabbit_bg_toast)
        setTextColor(getColor(context, R.color.rabbit_black))
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

}