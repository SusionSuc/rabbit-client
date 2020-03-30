//
// Created by susion wang on 2020-03-27.
//

#include <jni.h>
#include <sys/types.h>
#include <pthread.h>
#include "spi.h"
#include <sys/eventfd.h>
#include <unistd.h>

//在新的线程中回调java方法
static pthread_t callback_thread;
static JavaVM *JVM = nullptr;
static int callback_thread_execute_flag = -1;
static jclass callback_jclass = nullptr;
static jmethodID callback_method_id = nullptr;


void notify_crash_to_java() {
    LOG_D("notify_crash_to_java()");
    if (callback_thread_execute_flag < 0) return;
    //唤醒java回调线程
    uint64_t data = 1;
    if (sizeof(data) !=
        XCC_UTIL_TEMP_FAILURE_RETRY(write(callback_thread_execute_flag, &data, sizeof(data))))
        return;

    pthread_join(callback_thread, nullptr);
}

static void *call_java_method(void *arg) {

    LOG_D("call_java_method()");

    JNIEnv *s_env = nullptr;
    uint64_t data = 0;

    JavaVMAttachArgs attach_args = {
            .version = JNI_VERSION_1_6,
            .name    = "crash_callback",
            .group   = NULL
    };

    // 把 java env attach 到当前线程上
    if (JNI_OK != JVM->AttachCurrentThread(&s_env, &attach_args)) return nullptr;

    if (s_env == nullptr) {
        LOG_D("call notify_crash_to_java no init JNIEnv");
        goto exit;
    }

    //当前还没有发生native crash
    if (sizeof(data) !=
        XCC_UTIL_TEMP_FAILURE_RETRY(read(callback_thread_execute_flag, &data, sizeof(data)))) {
        goto exit;
    }

    s_env->CallStaticVoidMethod(callback_jclass, callback_method_id);

    if (s_env->ExceptionCheck()) {
        s_env->ExceptionClear();
    }

    exit:
        JVM->DetachCurrentThread();
        return nullptr;
}

void init_java_callback_thread(JavaVM *jvm) {

    LOG_D("init_java_callback_thread()");

    JVM = jvm;
    JNIEnv *env;

    if (jvm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return;
    }

    if (0 > (callback_thread_execute_flag = eventfd(0, EFD_CLOEXEC))) {
        LOG_D("callback_thread_execute_flag create failed");
    }

    int res = pthread_create(&callback_thread, nullptr, call_java_method, nullptr);
    if (res != 0) {
        LOG_D("java callback thread create failed");
    }

    //缓存需要回调的java方法的信息
    jclass cls = env->FindClass(JAVA_CLASS_NAME);
    callback_jclass = static_cast<jclass>(env->NewGlobalRef(cls));
    callback_method_id = env->GetStaticMethodID(callback_jclass, JAVA_CRASH_CALLBACK_METHOD_NAME,
                                                JAVA_CRASH_CALLBACK_METHOD_SIGNATURE);
}

void test_crash(int type) {

    int *a = nullptr;

    *a = type; // crash!
    (*a)++;
    type = *a;

    return;

}