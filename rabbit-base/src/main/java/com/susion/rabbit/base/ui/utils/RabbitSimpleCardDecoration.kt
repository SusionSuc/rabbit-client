package com.susion.rabbit.base.ui.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.susion.rabbit.base.ui.dp2px

/**
 * susionwang at 2020-01-17
 */

class RabbitSimpleCardDecoration(
    val horizontalGap: Int = CARD_GAP,
    val verticalGap: Int = CARD_GAP
) : RecyclerView.ItemDecoration() {

    companion object {
        val CARD_GAP = dp2px(10f)
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val params = view.layoutParams as GridLayoutManager.LayoutParams
        outRect.top = verticalGap
        if (params.spanIndex == 0) {
            outRect.left = horizontalGap
            outRect.right = horizontalGap / 2
        } else {
            outRect.right = horizontalGap
            outRect.left = horizontalGap / 2
        }
    }
}