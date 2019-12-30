package com.susion.rabbit.ui.base

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.LinearLayout
import com.susion.rabbit.ui.base.view.RabbitActionBar

/**
 * susionwang at 2019-10-21
 */
abstract class RabbitBasePage(context: Context) : LinearLayout(context),
    RabbitPageProtocol {

    private val actionBar = RabbitActionBar(context)
    override var eventListener: RabbitPageProtocol.PageEventListener? = null
    val INVALID_RES_ID = -1

    init {
        addView(actionBar)
        if (getLayoutResId() != INVALID_RES_ID){
            val inflatedView = LayoutInflater.from(context).inflate(getLayoutResId(), null)
            inflatedView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            addView(inflatedView)
        }
        orientation = VERTICAL
        actionBar.apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT,
                dp2px(40f)
            )
            actionListener = object : RabbitActionBar.ActionListener {
                override fun onBackClick() {
                    eventListener?.onBack()
                }
            }
        }
    }

    abstract fun getLayoutResId(): Int

    fun setTitle(title: String) {
        actionBar.setTitle(title)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return true
    }

}