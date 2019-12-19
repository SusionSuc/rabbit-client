package com.susion.rabbit.ui.base.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import androidx.annotation.ColorRes
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.susion.rabbit.ui.utils.getColor

/**
 * Created by susion on 2018/8/14.
 */
class RabbitSimpleLineDecoration :RecyclerView.ItemDecoration {

    lateinit var mDividerPaint: Paint
    private var mDividerHeight: Int = 0
    private var mMargin: Rect
    private var mStartPos =
        UNDEFINE_POS
    private var endLastCount = 1

    constructor(context: Context, @ColorRes colorId: Int, dividerHeight: Int) {
        init(context, colorId, dividerHeight)
        mMargin = Rect(0, 0, 0, 0)
        mStartPos = 0
    }

    constructor(
        context: Context, @ColorRes colorId: Int,
        dividerHeight: Int,
        margin: Rect = Rect(0, 0, 0, 0)
    ) {
        init(context, colorId, dividerHeight)
        mMargin = margin
        mStartPos = 0
    }

    constructor(
        context: Context, @ColorRes colorId: Int,
        dividerHeight: Int,
        margin: Rect = Rect(0, 0, 0, 0),
        endLastCount: Int
    ) {
        init(context, colorId, dividerHeight)
        mMargin = margin
        mStartPos = 0
        this.endLastCount = endLastCount
    }

    constructor(
        context: Context,
        startPos: Int = 0, @ColorRes colorId: Int,
        dividerHeight: Int,
        margin: Rect = Rect(0, 0, 0, 0)
    ) {
        init(context, colorId, dividerHeight)
        mMargin = margin
        mStartPos = startPos
    }

    private fun init(context: Context, colorResId: Int, dividerHeight: Int) {
        mDividerPaint = Paint()
        mDividerPaint.color = getColor(context, colorResId)
        mDividerHeight = dividerHeight
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: androidx.recyclerview.widget.RecyclerView,
        state: androidx.recyclerview.widget.RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.bottom = mDividerHeight
    }

    override fun onDraw(c: Canvas, parent: androidx.recyclerview.widget.RecyclerView, state: androidx.recyclerview.widget.RecyclerView.State) {
        val childCount = parent.childCount

        val left: Int = if (mMargin.left > 0) {
            parent.paddingLeft + mMargin.left
        } else {
            parent.paddingLeft
        }

        val right: Int = if (mMargin.right > 0) {
            parent.width - parent.paddingRight - mMargin.right
        } else {
            parent.width - parent.paddingRight
        }

        var startPos = 0
        if (mStartPos != UNDEFINE_POS && mStartPos < childCount - 1 && mStartPos >= 0) {
            startPos = mStartPos
        }

        for (i in startPos until childCount ) {
            val view = parent.getChildAt(i)
            val top = view.bottom.toFloat()
            val bottom = (view.bottom + mDividerHeight).toFloat()
            c.drawRect(left.toFloat(), top, right.toFloat(), bottom, mDividerPaint)
        }

    }

    companion object {
        val UNDEFINE_POS = -1
    }
}
