package com.susion.rabbit.jvmti;

import android.content.Context;
import android.os.Build;
import android.os.Debug;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * susionwang at 2020-04-24
 */
public class RabbitJvmTi {

    private static final String TAG = "RabbitJvmTi";
    private static final String RABBIT_JVMTI_AGENT = "rabbit-jvmti";


    public static void init(Context context, NativeCommunityHandler communityHandler) {
        attachJvmTiAgent(context);
        registerCommunityHandler(communityHandler);
    }

    private static void attachJvmTiAgent(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ClassLoader classLoader = context.getClassLoader();
                Method findLibrary = ClassLoader.class.getDeclaredMethod("findLibrary", String.class);
                String jvmtiAgentLibPath = (String) findLibrary.invoke(classLoader, RABBIT_JVMTI_AGENT);
                Log.d(TAG, "jvmtiagentlibpath " + jvmtiAgentLibPath);

                File filesDir = context.getFilesDir();
                File jvmtiLibDir = new File(filesDir, "jvmti");
                if (!jvmtiLibDir.exists()) {
                    jvmtiLibDir.mkdirs();

                }
                File agentLibSo = new File(jvmtiLibDir, "agent.so");
                if (agentLibSo.exists()) {
                    agentLibSo.delete();
                }
                Files.copy(Paths.get(new File(jvmtiAgentLibPath).getAbsolutePath()), Paths.get((agentLibSo).getAbsolutePath()));

                Log.d(TAG, agentLibSo.getAbsolutePath() + "," + context.getPackageCodePath());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    Debug.attachJvmtiAgent(agentLibSo.getAbsolutePath(), null, classLoader);
                    Log.d(TAG, "Debug.attachJvmTiAgent ----> ");
                } else {
                    try {
                        Class vmDebugClazz = Class.forName("dalvik.system.VMDebug");
                        Method attachAgentMethod = vmDebugClazz.getMethod("attachAgent", String.class);
                        attachAgentMethod.setAccessible(true);
                        attachAgentMethod.invoke(null, agentLibSo.getAbsolutePath());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                System.loadLibrary(RABBIT_JVMTI_AGENT);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public native static void registerCommunityHandler(NativeCommunityHandler handler);

}
