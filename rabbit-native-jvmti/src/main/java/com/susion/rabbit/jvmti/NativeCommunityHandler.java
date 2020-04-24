package com.susion.rabbit.jvmti;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * susionwang at 2020-04-24
 */
public class NativeCommunityHandler extends Handler {

    public NativeCommunityHandler(Looper looper) {
        super(looper);
    }

    public void sendStrMsg(String msgStr) {
        Message msg = Message.obtain();
        msg.obj = msgStr;
        sendMessage(msg);
    }

}
