//
// Created by susion wang on 2020-03-25.
//



#ifndef RABBIT_CLIENT_REGISTER_H
#define RABBIT_CLIENT_REGISTER_H

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

int register_crash_signal_handler(JNIEnv *env, jobject javaApiObj);

#ifdef __cplusplus
}
#endif

#endif