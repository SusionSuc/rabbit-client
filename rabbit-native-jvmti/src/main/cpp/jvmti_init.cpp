//
// Created by susion wang on 2020-04-22.
//
#include <cstring>
#include "jvmti.h"
#include "common.h"
#include "jvmti_thread.h"
#include "jvmti_handler.h"

//jin方法映射表
static const char *className = "com/susion/rabbit/jvmti/RabbitJvmTi";
static JNINativeMethod native_methods[] = {
        {"registerCommunityHandler", "(Lcom/susion/rabbit/jvmti/NativeCommunityHandler;)V", (void *) register_community_handler}
};

jvmtiEnv *create_jvmti_env(JavaVM *vm) {
    jvmtiEnv *jvmti_env;
    jint result = vm->GetEnv((void **) &jvmti_env, JVMTI_VERSION_1_2);
    if (result != JNI_OK) {
        return nullptr;
    }
    return jvmti_env;
}

void enable_event_notification(jvmtiEnv *jvmti, jvmtiEventMode mode, jvmtiEvent event_type) {
    jvmtiError err = jvmti->SetEventNotificationMode(mode, event_type, nullptr);
}

extern "C" JNIEXPORT jint JNICALL Agent_OnAttach(JavaVM *vm, char *options, void *reserved) {

    LOG_D("Agent_OnAttach call");

    jvmtiEnv *jvmti_env = create_jvmti_env(vm);

    if (jvmti_env == nullptr) {
        return JNI_ERR;
    }

    jvmtiEventCallbacks callbacks;
    memset(&callbacks, 0, sizeof(callbacks));

    callbacks.ThreadStart = &on_thread_start;
    callbacks.ThreadEnd = &on_thread_stop;

    int error = jvmti_env->SetEventCallbacks(&callbacks, sizeof(callbacks));

    enable_event_notification(jvmti_env, JVMTI_ENABLE, JVMTI_EVENT_THREAD_START);

    enable_event_notification(jvmti_env, JVMTI_ENABLE, JVMTI_EVENT_THREAD_END);

    return JNI_OK;

}


//动态注册jni方法，so加载完成后，后自动调用这个方法
JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    LOG_D("JNI_OnLoad()");

    JNIEnv *env;

    if (vm == nullptr) return ERROR_CODE_INT;

    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return ERROR_CODE_INT;
    }

    if (env == nullptr) return ERROR_CODE_INT;

    jclass cls = env->FindClass(className);

    if (cls == nullptr) return ERROR_CODE_INT;

    jint native_methods_count = sizeof(native_methods) / sizeof(native_methods[0]);

    env->RegisterNatives(cls, native_methods, native_methods_count);

    LOG_D("register native method success!");

    return JNI_VERSION_1_6;

}