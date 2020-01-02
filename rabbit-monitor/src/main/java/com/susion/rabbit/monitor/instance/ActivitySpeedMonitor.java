package com.susion.rabbit.monitor.instance;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.susion.rabbit.tracer.RabbitTracerEventNotifier;

import java.util.ArrayList;
import java.util.List;

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
        RabbitTracerEventNotifier.appSpeedNotifier.activityDrawFinish(getContext(), System.currentTimeMillis());
    }

    public static void wrapperViewOnActivityCreateEnd(Activity activity) {
        RabbitTracerEventNotifier.appSpeedNotifier.activityCreateEnd(activity, System.currentTimeMillis());
        ViewGroup contentView = activity.findViewById(android.R.id.content);
        //这里存在问题，强制取第0个View，可能会导致渲染完成时机取的不正确
        if (contentView != null && contentView.getChildCount() >= 1 && contentView instanceof FrameLayout) {
            View realAcContent = contentView.getChildAt(0);
            ActivitySpeedMonitor monitorWrapperView = new ActivitySpeedMonitor(contentView.getContext());
            contentView.addView(monitorWrapperView,0);
            replaceParent(realAcContent, contentView, monitorWrapperView);

            final int childCount = contentView.getChildCount();
            if (childCount > 1) {
                List<View>  oldChilds = new ArrayList<>();
                for (int i = 1; i < childCount; i++) {
                    View child = contentView.getChildAt(i);
                    oldChilds.add(child);
                    contentView.removeView(child);
                }

                for (int i=0; i < oldChilds.size(); i++){
                    monitorWrapperView.addView(oldChilds.get(i));
                }
            }
        }
    }

    public static void activityCreateStart(Activity activity) {
        RabbitTracerEventNotifier.appSpeedNotifier.activityCreateStart(activity, System.currentTimeMillis());
    }

    public static void activityResumeEnd(Activity activity) {
        RabbitTracerEventNotifier.appSpeedNotifier.activityResumeEnd(activity, System.currentTimeMillis());
    }

    public static void replaceParent(View view, ViewGroup oldParent, ViewGroup newParent) {
        oldParent.removeView(view);
        newParent.addView(view);
    }

}
