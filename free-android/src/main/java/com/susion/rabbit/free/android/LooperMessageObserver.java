package com.susion.rabbit.free.android;


import android.os.Message;

/**
 * wangpengcheng.wpc create at 2023/7/3
 */
public interface LooperMessageObserver {

    Object messageDispatchStarting();

    void messageDispatched(Object token, Message msg);

    void dispatchingThrewException(Object token, Message msg, Exception exception);

}
