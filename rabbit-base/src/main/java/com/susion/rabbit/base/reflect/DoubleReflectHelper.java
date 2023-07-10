package com.susion.rabbit.base.reflect;

import android.os.MessageQueue;
import android.view.Choreographer;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * wangpengcheng.wpc create at 2023/7/5
 */
public class DoubleReflectHelper {
    private Method mDoubleReflectMethod;
    private Method mDoubleReflectMethods;
    private Method mDoubleReflectField;
    private Method mDoubleReflectFields;
    private Method mDoubleReflectAccessible;

    private Method mDoubleReflectPostSyncBarrier;
    private Method mDoubleReflectRemoveSyncBarrier;
    private Field mDoubleReflectUseVsync;
    private Field mDoubleReflectHavePendingVsync;

    public Method getDoubleReflectPostSyncBarrier() {
        if (mDoubleReflectPostSyncBarrier == null) {
            mDoubleReflectPostSyncBarrier = getDoubleReflectMethod(MessageQueue.class, "postSyncBarrier", Long.class);
        }
        return mDoubleReflectPostSyncBarrier;
    }

    public Method getDoubleReflectRemoveSyncBarrier() {
        if (mDoubleReflectRemoveSyncBarrier == null) {
            mDoubleReflectRemoveSyncBarrier = getDoubleReflectMethod(MessageQueue.class, "removeSyncBarrier", Integer.class);
        }
        return mDoubleReflectRemoveSyncBarrier;
    }

    public Field getDoubleReflectUseVsync() {
        if (mDoubleReflectUseVsync == null) {
            mDoubleReflectUseVsync = getDoubleReflectField(Choreographer.class, "USE_VSYNC", Boolean.class);
        }
        return mDoubleReflectUseVsync;
    }

    public Field getDoubleReflectHavePendingVsync() {
        if (mDoubleReflectHavePendingVsync == null) {
            try {
                Object receiver = Reflect.on(Choreographer.getInstance()).field("mDisplayEventReceiver").get();
                mDoubleReflectHavePendingVsync = getDoubleReflectField(receiver.getClass(), "mHavePendingVsync", Boolean.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mDoubleReflectHavePendingVsync;
    }

    private Method getDoubleReflectMethod(Class<?> targetClass, String name, Class<?>... args) {
        Class<?> type = targetClass;
        Method reflectMethod = getClassReflectMethod();
        if (reflectMethod == null) {
            return null;
        }
        // 直接获取
        do {
            try {
                Method method = (Method) reflectMethod.invoke(type, name, args);
                return accessible(method);
            } catch (Exception e) {
                e.printStackTrace();
            }
            type = type.getSuperclass();
        } while (type != null);
        // 尝试遍历
        Method reflectMethods = getClassReflectMethods();
        if (reflectMethods == null) {
            return null;
        }
        type = targetClass;
        try {
            Method[] methods = (Method[])reflectMethods.invoke(type);
            for (Method method : methods) {
                if (method.getName().equals(name)) {
                    Class<?>[] paramTypes = method.getParameterTypes();
                    if (paramTypes != null && paramTypes.length == args.length) {
                        return accessible(method);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Field getDoubleReflectField(Class<?> targetClass, String name, Class<?> fieldType) {
        Class<?> type = targetClass;
        Method reflectFieldMethod = getClassReflectField();
        if (reflectFieldMethod == null) {
            return null;
        }
        // 直接获取
        do {
            try {
                Field field = (Field) reflectFieldMethod.invoke(type, name);
                return accessible(field);
            } catch (Exception e) {
                e.printStackTrace();
            }
            type = type.getSuperclass();
        } while (type != null);
        // 尝试遍历
        Method reflectFieldsMethod = getClassReflectFields();
        if (reflectFieldsMethod == null) {
            return null;
        }
        type = targetClass;
        try {
            Field[] fields = (Field[])reflectFieldsMethod.invoke(type);
            for (Field field : fields) {
                if (field.getName().equals(name)) {
                    Class<?> thisType = field.getType();
                    if (thisType.getName().equals(fieldType.getName())) {
                        return accessible(field);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Method getClassReflectMethod() {
        try {
            if (mDoubleReflectMethod == null) {
                mDoubleReflectMethod = Class.class.getDeclaredMethod("getDeclaredMethod", String.class, Class[].class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mDoubleReflectMethod;
    }

    private Method getClassReflectMethods() {
        try {
            if (mDoubleReflectMethods == null) {
                mDoubleReflectMethods = Class.class.getDeclaredMethod("getDeclaredMethods");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mDoubleReflectMethods;
    }

    private Method getClassReflectField() {
        try {
            if (mDoubleReflectField == null) {
                mDoubleReflectField = Class.class.getDeclaredMethod("getDeclaredField", String.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mDoubleReflectField;
    }

    private Method getClassReflectFields() {
        try {
            if (mDoubleReflectFields == null) {
                mDoubleReflectFields = Class.class.getDeclaredMethod("getDeclaredFields");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mDoubleReflectFields;
    }

    private Method getMetaSetAccessibleMethod() {
        try {
            if (mDoubleReflectAccessible == null) {
                mDoubleReflectAccessible = AccessibleObject.class.getDeclaredMethod("setAccessible", boolean.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mDoubleReflectAccessible;
    }

    private <T extends AccessibleObject> T accessible(T accessible) {
        if (accessible == null) {
            return null;
        }

        if (accessible instanceof Member) {
            Member member = (Member) accessible;
            if (Modifier.isPublic(member.getModifiers())
                    && Modifier.isPublic(member.getDeclaringClass().getModifiers())) {
                return accessible;
            }
        }

        // 默认为false,即反射时检查访问权限，
        // 设为true时不检查访问权限,可以访问private字段和方法
        if (!accessible.isAccessible()) {
            accessible.setAccessible(true);
        }

        return accessible;
    }
}
