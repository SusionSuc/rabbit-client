//
// Created by susion wang on 2020-03-27.
//

#include <jni.h>
#include <sys/types.h>
#include <pthread.h>
#include "spi.h"

//在新的线程中回调java方法
static pthread_t callback_thread;
static JNIEnv *s_env = nullptr; // 用来调用Java方法

void notify_crash_to_java() {
    pthread_join(callback_thread, nullptr);
}

static void *call_java_method(void *arg) {

    if (s_env == nullptr) {
        LOG_D("call notify_crash_to_java no init JNIEnv");
        return nullptr;
    }

    LOG_D("prepare find callback java class : %s ", JAVA_CLASS_NAME);
    jclass java_class = s_env->FindClass(JAVA_CLASS_NAME);
    if (java_class == nullptr) {
        LOG_D("未找到Java class :%s !", JAVA_CLASS_NAME);
    }

    LOG_D("prepare find callback java method : %s ", JAVA_CRASH_CALLBACK_METHOD_NAME);
    jmethodID method_id = s_env->GetStaticMethodID(java_class, JAVA_CRASH_CALLBACK_METHOD_NAME,
                                                   JAVA_CRASH_CALLBACK_METHOD_SIGNATURE);
    if (method_id == nullptr) {
        LOG_D("未找到 Java 方法 : %s !", JAVA_CRASH_CALLBACK_METHOD_NAME);
        return nullptr;
    }

    s_env->CallStaticVoidMethod(java_class, method_id);
}

void init_java_callback_thread(JNIEnv *env) {
    s_env = env;
    int res = pthread_create(&callback_thread, NULL, &call_java_method, NULL);
    if (res != 0) {
        LOG_D("java callback thread create failed");
    }
}

void test_crash(int type) {

    int *a = nullptr;

    *a = type; // crash!
    (*a)++;
    type = *a;

    return;
}