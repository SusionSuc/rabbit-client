package com.susion.rabbit.monitor.core;

import android.os.Handler;
import android.os.Message;
import android.os.MessageQueue;
import android.view.Choreographer;
import android.view.ViewParent;
import androidx.annotation.NonNull;
import com.susion.rabbit.base.RabbitLog;
import com.susion.rabbit.monitor.reflect.DoubleReflectHelper;
import com.susion.rabbit.monitor.reflect.Reflect;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * wangpengcheng.wpc create at 2023/7/4
 */
public class MainThreadEnv {

    private Choreographer mainChoreographer = Choreographer.getInstance();
    private DoubleReflectHelper mDoubleReflectHelper = new DoubleReflectHelper();
    
    /**
     * class info
     * */
    private Class<?> activityThreadClass;
    private Class<?> choreographerClass;
    private Class<?> viewRootImplClass;
    private Class<?> handlerClass;
    private Class<?> messageClass;
    private Class<?> messageQueueClass;
    private Method metaClassGetDeclaredMethodMethod;
    private Method metaClassGetDeclaredFieldMethod;
    private Method metaFieldGetMethod;
    private Method metaSetAccessibleMethod;

    public Handler activityThreadHandler;
    public Handler frameHandler;
    public MessageQueue mainMessageQueue;

    // activity lifecycle
    private Class<?> clientTransactionClass;
    private Class<?> lifecycleStateRequestClass;
    private Method targetStateMethod;
    private Field lifecycleStateRequestFiled;
    
    public void init() {
        try {
            activityThreadClass = Class.forName("android.app.ActivityThread");
            choreographerClass = Class.forName("android.view.Choreographer");
            viewRootImplClass = Class.forName("android.view.ViewRootImpl");
            handlerClass = Class.forName("android.os.Handler");
            messageQueueClass = Class.forName("android.os.MessageQueue");
            messageClass = Class.forName("android.os.Message");
            clientTransactionClass = Class.forName("android.app.servertransaction.ClientTransaction");
            lifecycleStateRequestClass = Class.forName("android.app.servertransaction.ActivityLifecycleItem");
            lifecycleStateRequestFiled = clientTransactionClass.getDeclaredField("mLifecycleStateRequest");
            targetStateMethod = lifecycleStateRequestClass.getMethod("getTargetState");
            lifecycleStateRequestFiled.setAccessible(true);
        } catch (Exception ignore) {
            ignore.printStackTrace();
            return;
        }
        activityThreadHandler = hookActivityThreadHandler();
        if (activityThreadHandler == null) {
            activityThreadHandler = hookActivityThreadHandlerUseDoubleReflect();
            if (activityThreadHandler == null) {
                RabbitLog.d("MainThreadEnv hook activityThreadHandler failed");
            }
        }
        frameHandler = hookChoreographerHandlerUseDoubleReflect(mainChoreographer);
        if (frameHandler == null) {
            frameHandler = hookChoreographerHandler(mainChoreographer);
            if (frameHandler == null) {
                RabbitLog.d("MainThreadEnv hook frameHandler failed");
            }
        }
        mainMessageQueue = hookMainMessageQueueUseDoubleReflect(activityThreadHandler);
        if (mainMessageQueue == null) {
            mainMessageQueue = hookMainMessageQueue(activityThreadHandler);
            if (mainMessageQueue == null) {
                RabbitLog.d("MainThreadEnv hook mainMessageQueue failed");
            }
        }
    }

    private Handler hookActivityThreadHandler() {
        try {
            Method currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread");
            Object activityThread = currentActivityThreadMethod.invoke(null);
            Field mH = activityThreadClass.getDeclaredField("mH");
            mH.setAccessible(true);
            return (Handler) mH.get(activityThread);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    private Handler hookActivityThreadHandlerUseDoubleReflect() {
        try {
            Method currentActivityThreadMethod = (Method) getMetaClassGetDeclaredMethodMethod().invoke(activityThreadClass, "currentActivityThread", null);
            Object activityThread = currentActivityThreadMethod.invoke(null);
            Field mH = (Field) getMetaClassGetDeclaredFieldMethod().invoke(activityThreadClass, "mH");
            getMetaSetAccessibleMethod().invoke(mH, true);
            return (Handler) getMetaFieldGetMethod().invoke(mH, activityThread);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    private Handler hookChoreographerHandler(Choreographer choreographer) {
        try {
            Field mHandler = choreographerClass.getDeclaredField("mHandler");
            mHandler.setAccessible(true);
            return (Handler) mHandler.get(choreographer);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    private Handler hookChoreographerHandlerUseDoubleReflect(Choreographer choreographer) {
        try {
            Field mHandler = (Field) getMetaClassGetDeclaredFieldMethod().invoke(choreographerClass, "mHandler");
            getMetaSetAccessibleMethod().invoke(mHandler, true);
            return (Handler) getMetaFieldGetMethod().invoke(mHandler, choreographer);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    private Handler hookViewRootImplHandler(ViewParent viewRoot) {
        try {
            Field mHandler = viewRootImplClass.getDeclaredField("mHandler");
            mHandler.setAccessible(true);
            return (Handler) mHandler.get(viewRoot);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    private Handler hookViewRootImplHandlerUseDoubleReflect(ViewParent viewRoot) {
        try {
            Field mHandler = (Field) getMetaClassGetDeclaredFieldMethod().invoke(viewRootImplClass, "mHandler");
            getMetaSetAccessibleMethod().invoke(mHandler, true);
            return (Handler) getMetaFieldGetMethod().invoke(mHandler, viewRoot);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    private MessageQueue hookMainMessageQueue(Handler handler) {
        try {
            Field mQueueField = handlerClass.getDeclaredField("mQueue");
            mQueueField.setAccessible(true);
            return (MessageQueue) mQueueField.get(handler);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    private MessageQueue hookMainMessageQueueUseDoubleReflect(Handler handler) {
        try {
            Field mQueueField = (Field) getMetaClassGetDeclaredFieldMethod().invoke(handlerClass, "mQueue");
            getMetaSetAccessibleMethod().invoke(mQueueField, true);
            return (MessageQueue) getMetaFieldGetMethod().invoke(mQueueField, handler);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    @NonNull
    private Method getMetaClassGetDeclaredMethodMethod() throws NoSuchMethodException {
        if (metaClassGetDeclaredMethodMethod == null) {
            metaClassGetDeclaredMethodMethod = Class.class.getDeclaredMethod("getDeclaredMethod", String.class, Class[].class);
        }
        return metaClassGetDeclaredMethodMethod;
    }

    @NonNull
    private Method getMetaClassGetDeclaredFieldMethod() throws NoSuchMethodException {
        if (metaClassGetDeclaredFieldMethod == null) {
            metaClassGetDeclaredFieldMethod = Class.class.getDeclaredMethod("getDeclaredField", String.class);
        }
        return metaClassGetDeclaredFieldMethod;
    }

    @NonNull
    private Method getMetaFieldGetMethod() throws NoSuchMethodException {
        if (metaFieldGetMethod == null) {
            metaFieldGetMethod = Field.class.getDeclaredMethod("get", Object.class);
        }
        return metaFieldGetMethod;
    }

    @NonNull
    private Method getMetaSetAccessibleMethod() throws NoSuchMethodException {
        if (metaSetAccessibleMethod == null) {
            metaSetAccessibleMethod = AccessibleObject.class.getDeclaredMethod("setAccessible", boolean.class);
        }
        return metaSetAccessibleMethod;
    }

    public Message getMsgQueueFirstMessage() {
        try {
            Field mMessagesField = messageQueueClass.getDeclaredField("mMessages");
            mMessagesField.setAccessible(true);
            return (Message) mMessagesField.get(mainMessageQueue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getLifecycleTargetState(Object obj) {
        try {
            Object lifecycleStateRequest = lifecycleStateRequestFiled.get(obj);
            if (lifecycleStateRequest == null) {
                return -1;
            }
            Integer targetState = (Integer)targetStateMethod.invoke(lifecycleStateRequest);
            if (targetState == null) {
                return -1;
            }
            return targetState;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return  -1;
    }

    public boolean moveMessageToFront(Handler handler, int what) {
        try {
            Field mMessagesField = messageQueueClass.getDeclaredField("mMessages");
            mMessagesField.setAccessible(true);
            Message message = (Message) mMessagesField.get(mainMessageQueue);
            while (message != null) {
                if (message.what == what && message.getTarget() == handler) {
                    Message newMessage = Message.obtain(message);
                    handler.removeMessages(message.what);
                    handler.sendMessageAtFrontOfQueue(newMessage);
                    return true;
                }
                message = getNextMessage(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Message getNextMessage(Message message) {
        try {
            Field nextField = messageClass.getDeclaredField("next");
            nextField.setAccessible(true);
            Object next = nextField.get(message);
            if (next != null) {
                return (Message) next;
            }
        } catch (Exception e) {
           e.printStackTrace();
        }
        return null;
    }


    public String messageQueueListStr() {
        try {
            Field mMessagesField = messageQueueClass.getDeclaredField("mMessages");
            mMessagesField.setAccessible(true);
            Message message = (Message) mMessagesField.get(mainMessageQueue);
            StringBuilder stringBuilder = new StringBuilder("message queue msg list: \n");
            while (message != null) {
                stringBuilder.append(">");
                stringBuilder.append(message).append("\n");
                message = getNextMessage(message);
            }
            return stringBuilder.toString();
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
        return "";
    }

    public boolean useVsync() {
        try {
            return Reflect.on(Choreographer.getInstance()).field("USE_VSYNC").get();
        } catch (Exception e) {
            try {
                Field useVsync = mDoubleReflectHelper.getDoubleReflectUseVsync();
                if (useVsync != null) {
                    return useVsync.getBoolean(Choreographer.getInstance());
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return false;
    }

}
