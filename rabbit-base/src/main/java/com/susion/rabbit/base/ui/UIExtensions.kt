package com.susion.rabbit.base.ui

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import android.view.View
import android.widget.Toast
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.functions.Consumer
import java.util.concurrent.TimeUnit

/**
 * susionwang at 2019-09-25
 */
private val sMetrics = Resources.getSystem().displayMetrics

fun dp2px(dipValue: Float): Int {
    val scale: Float = sMetrics?.density ?: 1f
    return (dipValue * scale + 0.5f).toInt()
}

fun getDrawable(context: Context?, resId: Int): Drawable? {
    return if (null == context || context.resources == null || resId <= -1) {
        ColorDrawable()
    } else ContextCompat.getDrawable(context, resId)
}


/**
 * get the color by id
 */
fun getColor(context: Context?, resId: Int): Int {
    return if (null == context || context.resources == null || resId <= -1) {
        Color.TRANSPARENT
    } else ContextCompat.getColor(context, resId)
}


fun View.throttleFirstClick(action: Consumer<Any?>) {
    this.clicks().throttleFirst(500, TimeUnit.MILLISECONDS)
        .subscribe(action, Consumer {})
}

fun showToast(context: Context, msg:String){
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}