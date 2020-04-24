//
// Created by susion wang on 2020-04-23.
//

#include "jvmti_thread.h"
#include "common.h"
#include "jvmti_handler.h"

//getName()
static char *GET_NAME_METHOD_NAME = const_cast<char *>("getName");
static char *GET_NAME_METHOD_SIGNATURE = const_cast<char *>("()Ljava/lang/String;");
//getPriority()
static char *GET_PRIORITY_METHOD_NAME = const_cast<char *>("getPriority");
static char *GET_PRIORITY_METHOD_SIGNATURE = const_cast<char *>("()I");
//getId()
static char *GET_ID_METHOD_NAME = const_cast<char *>("getId");
static char *GET_ID_METHOD_SIGNATURE = const_cast<char *>("()J");

const char* get_thread_name( JNIEnv *jni_env,jthread thread){
    jclass thread_class = jni_env->GetObjectClass(thread);
    jmethodID  get_name_method_id = jni_env->GetMethodID(thread_class,GET_NAME_METHOD_NAME, GET_NAME_METHOD_SIGNATURE);
    jstring name_str = static_cast<jstring>(jni_env->CallObjectMethod(thread, get_name_method_id));
    return jni_env->GetStringUTFChars(name_str, NULL);
}

int get_thread_priority( JNIEnv *jni_env,jthread thread){
    jclass thread_class = jni_env->GetObjectClass(thread);
    jmethodID  method_id = jni_env->GetMethodID(thread_class,GET_PRIORITY_METHOD_NAME, GET_PRIORITY_METHOD_SIGNATURE);
    return jni_env->CallIntMethod(thread, method_id);
}

jlong get_thread_id(JNIEnv *jni_env,jthread thread){
    jclass thread_class = jni_env->GetObjectClass(thread);
    jmethodID  method_id = jni_env->GetMethodID(thread_class,GET_ID_METHOD_NAME, GET_ID_METHOD_SIGNATURE);
    return jni_env->CallLongMethod(thread, method_id);
}

void on_thread_start(jvmtiEnv *jvmti_env, JNIEnv *jni_env, jthread thread) {
    LOG_D("thread start -> id : %d; name : %s ; priority : %d", get_thread_id(jni_env, thread), get_thread_name(jni_env, thread), get_thread_priority(jni_env, thread));
    notifyMessage(jni_env, "thread start!");
}

void on_thread_stop(jvmtiEnv *jvmti_env, JNIEnv *jni_env, jthread thread) {
    LOG_D("thread end -> id : %d; name : %s ; priority : %d", get_thread_id(jni_env, thread), get_thread_name(jni_env, thread), get_thread_priority(jni_env, thread));
}

