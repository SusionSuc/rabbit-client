//
// Created by susion wang on 2020-03-25.
//
#include "android/log.h"

#ifndef RABBIT_CLIENT_UTLIS_H
#define RABBIT_CLIENT_UTLIS_H

static char *JAVA_CRASH_CALLBACK_METHOD_NAME = const_cast<char *>("onCaptureNativeCrash");
static char *JAVA_CRASH_CALLBACK_METHOD_SIGNATURE = const_cast<char *>("()V");
static char *JAVA_CLASS_NAME = const_cast<char *>("com/susion/rabbit/native_crash/RabbitNativeCrashCaptor");

#define LOG_D(...)  __android_log_print(ANDROID_LOG_DEBUG,"rabbit-native",__VA_ARGS__)

#define ERROR_CODE_INT -1
#define SUCCESS_CODE_INT 1

#ifdef __cplusplus
extern "C" {
#endif

void test_crash(int type);
void notify_crash_to_java();
void init_java_callback_thread(JNIEnv *env);

#ifdef __cplusplus
}
#endif

#endif //RABBIT_CLIENT_UTLIS_H
