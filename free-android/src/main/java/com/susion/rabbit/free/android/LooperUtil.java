package com.susion.rabbit.free.android;

import android.os.Looper;
import android.os.Message;

/**
 * wangpengcheng.wpc create at 2023/7/3
 */
public class LooperUtil {

    public static void setObserver(final LooperMessageObserver observer) {
        Looper.setObserver(new Looper.Observer() {
            @Override
            public Object messageDispatchStarting() {
                return observer.messageDispatchStarting();
            }

            @Override
            public void messageDispatched(Object token, Message msg) {
                observer.messageDispatched(token, msg);

            }

            @Override
            public void dispatchingThrewException(Object token, Message msg, Exception exception) {
                observer.dispatchingThrewException(token, msg, exception);
            }
        });
    }

}
