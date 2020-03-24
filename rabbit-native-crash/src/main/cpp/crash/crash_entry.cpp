#include <jni.h>
#include <cstdio>
#include <cstdlib>
#include "android/log.h"

//
// Created by susion wang on 2020-03-24.
//

using namespace std;

static const char *className = "com/susion/rabbit/native_crash/RabbitNativeCrashCaptor";

static jstring init_native_crash_captor(JNIEnv* env, jobject obj) {
    return env->NewStringUTF("hello world rabbit native crash");
}

static JNINativeMethod native_methods[] = {
        {"nativeInitCaptor", "(Ljava/lang/String;)Ljava/lang/String;", (void *) init_native_crash_captor}
};

//动态注册jni方法，so加载完成后，后自动调用这个方法
JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {

    JNIEnv *env;

    jint result = -1;

    if (vm == nullptr) return result;

    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return result;
    }

    if (env == nullptr) return result;

    jclass cls = env->FindClass(className);

    if (cls == nullptr) return result;

    jint native_methods_count = sizeof(native_methods) / sizeof(native_methods[0]);

    env->RegisterNatives(cls, native_methods, native_methods_count);

//    __android_log_print("")

    return JNI_VERSION_1_6;

}