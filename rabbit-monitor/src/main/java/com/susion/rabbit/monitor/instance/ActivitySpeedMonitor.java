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

import com.susion.rabbit.base.RabbitLog;
import com.susion.rabbit.tracer.RabbitTracerEventNotifier;

import java.util.ArrayList;
import java.util.List;

/**
 * susionwang at 2019-11-15
 * Activity 页面测试组件
 * 1. onCreate 执行耗时
 * 2. onCreate -> onResume 耗时
 * 3. 页面渲染耗时
 */
public class ActivitySpeedMonitor extends FrameLayout {

    public String activityName = "UnDefined";

    public ActivitySpeedMonitor(@NonNull Context context) {
        super(context);
    }

    public ActivitySpeedMonitor(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ActivitySpeedMonitor(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        RabbitTracerEventNotifier.appSpeedNotifier.activityDrawFinish(activityName, System.currentTimeMillis());

    }

    public static void wrapperViewOnActivityCreateEnd(Activity activity) {
        RabbitTracerEventNotifier.appSpeedNotifier.activityCreateEnd(activity, System.currentTimeMillis());
        ViewGroup contentView = activity.findViewById(android.R.id.content);

        if (contentView != null && contentView.getChildCount() >= 1 && contentView instanceof FrameLayout) {
            View realAcContent = contentView.getChildAt(0);
            ActivitySpeedMonitor monitorWrapperView = new ActivitySpeedMonitor(contentView.getContext());
            monitorWrapperView.activityName = activity.getClass().getSimpleName();
            contentView.addView(monitorWrapperView,0);

            //把activity的根布局包上一层
            contentView.removeView(realAcContent);
            monitorWrapperView.addView(realAcContent);

            final int childCount = contentView.getChildCount();
            if (childCount > 1) {
                List<View>  oldChilds = new ArrayList<>();
                for (int i = 1; i < childCount; i++) {
                    View child = contentView.getChildAt(i);
                    if (child != null){
                        oldChilds.add(child);
                        contentView.removeView(child);
                    }
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

}
