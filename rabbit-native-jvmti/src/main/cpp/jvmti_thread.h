//
// Created by susion wang on 2020-04-23.
//


#include "jni.h"
#include "jvmti.h"

#ifndef RABBIT_CLIENT_JVMTI_THREAD_H
#define RABBIT_CLIENT_JVMTI_THREAD_H

#ifdef __cplusplus
extern "C" {
#endif

void on_thread_start(jvmtiEnv *jvmti_env, JNIEnv *jni_env, jthread thread);
void on_thread_stop(jvmtiEnv *jvmti_env, JNIEnv *jni_env, jthread thread);

#ifdef __cplusplus
}
#endif

#endif //RABBIT_CLIENT_JVMTI_THREAD_H
