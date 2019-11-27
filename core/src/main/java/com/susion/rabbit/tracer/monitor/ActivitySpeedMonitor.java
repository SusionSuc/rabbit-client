package com.susion.rabbit.tracer.monitor;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActionBarOverlayLayout;

import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.susion.rabbit.tracer.RabbitTracerEventNotifier;

/**
 * susionwang at 2019-11-15
 * <p>
 * Activity 页面测试组件
 * 1. onCreate 执行耗时
 * 2. onCreate -> onResume 耗时
 * 3. 页面渲染耗时
 */
public class ActivitySpeedMonitor extends FrameLayout {

    public ActivitySpeedMonitor(@NonNull Context context) {
        super(context);
    }

    public ActivitySpeedMonitor(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ActivitySpeedMonitor(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ActivitySpeedMonitor(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        RabbitTracerEventNotifier.eventNotifier.activityDrawFinish(getContext(), System.currentTimeMillis());
    }

    public static void wrapperViewOnActivityCreateEnd(Activity activity) {
        RabbitTracerEventNotifier.eventNotifier.activityCreateEnd(activity, System.currentTimeMillis());
        ViewGroup contentView = activity.findViewById(android.R.id.content);
        if (contentView != null && contentView.getChildCount() == 1 && contentView instanceof FrameLayout) {
            View realAcContent = contentView.getChildAt(0);
            ActivitySpeedMonitor monitorWrapperView = new ActivitySpeedMonitor(contentView.getContext());
            contentView.removeView(realAcContent);
            monitorWrapperView.addView(realAcContent);
            contentView.addView(monitorWrapperView);
        }
    }

    public static void activityCreateStart(Activity activity) {
        RabbitTracerEventNotifier.eventNotifier.activityCreateStart(activity, System.currentTimeMillis());
    }

    public static void activityResumeEnd(Activity activity) {
        RabbitTracerEventNotifier.eventNotifier.activityResumeEnd(activity, System.currentTimeMillis());
    }

}
