package com.susion.rabbit.tracer.monitor;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.susion.rabbit.tracer.RabbitTracerEventNotifier;

/**
 * susionwang at 2019-11-15
 *
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
        FrameLayout contentView = activity.findViewById(android.R.id.content);
        ViewGroup contentViewParent = (ViewGroup) contentView.getParent();

        if (contentView != null && contentViewParent != null) {
            ActivitySpeedMonitor newParent = new ActivitySpeedMonitor(contentView.getContext());
            if (contentView.getLayoutParams() != null) {
                newParent.setLayoutParams(contentView.getLayoutParams());
            }
            contentViewParent.removeView(contentView);
            newParent.addView(contentView);
            contentViewParent.addView(newParent);
        }
    }

    public static void activityCreateStart(Activity activity){
        RabbitTracerEventNotifier.eventNotifier.activityCreateStart(activity, System.currentTimeMillis());
    }

    public static void activityResumeEnd(Activity activity){
        RabbitTracerEventNotifier.eventNotifier.activityResumeEnd(activity, System.currentTimeMillis());
    }

}
