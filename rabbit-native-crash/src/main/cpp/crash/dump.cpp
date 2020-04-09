//
// Created by susion wang on 2020-03-27.
//

#include <jni.h>
#include <sys/types.h>
#include <pthread.h>
#include "dump.h"
#include <sys/eventfd.h>
#include <unistd.h>
#include "common.h"

//在新的线程中回调java方法
static pthread_t dump_thread;
static JavaVM *JVM = nullptr;
static jclass callback_jclass = nullptr;
static jmethodID callback_method_id = nullptr;

//dump线程唤醒
static int crash_listener_fd = -1;
static eventfd_t CRASH_FLAG = 1;

//crash 上下文
static crash_context context;

//在子线程中做native崩溃堆栈解析
void start_dump_crash(int sig, siginfo_t *si, void *uc) {

    LOG_D("start dump crash stack");

    if (crash_listener_fd < 0) return;

    //修改crash 发生标识， 唤起 dump 线程
    if (eventfd_write(crash_listener_fd, CRASH_FLAG) == ERROR_CODE_INT) {
        LOG_D("eventfd_write failed");
        return;
    }

    context.si = si;
    context.sig = sig;
    context.uc = uc;
    pthread_join(dump_thread, nullptr);
}

//还原crash stack
int dump_crash() {
    LOG_D("crash signal value : %d", context.sig);

}

//运行在新的子线程中
static void *dump_crash_in_new_thread(void *arg) {

    JNIEnv *s_env = nullptr;

    JavaVMAttachArgs attach_args = {
            .version = JNI_VERSION_1_6,
            .name    = "crash_callback",
            .group   = NULL
    };

    // 把 java env attach 到当前线程上
    if (JNI_OK != JVM->AttachCurrentThread(&s_env, &attach_args)) {
        LOG_D("dump thread attach jvm env failed!");
        return nullptr;
    }

    //如果没有发生crash, 这个线程不工作
    eventfd_t count;
    eventfd_read(crash_listener_fd, &count);
    if (count != CRASH_FLAG) {
        goto exit;
    }

    LOG_D("dump crash in new thread");

    if (s_env == nullptr) {
        LOG_D("call notify_crash_to_java no init JNIEnv");
        goto exit;
    }

    dump_crash();

    //回调 Java 方法
    s_env->CallStaticVoidMethod(callback_jclass, callback_method_id);

    if (s_env->ExceptionCheck()) {
        s_env->ExceptionClear();
    }

    exit:
    JVM->DetachCurrentThread();
    return nullptr;
}

void init_dump_thread(JavaVM *jvm) {

    LOG_D("init_dump_thread()");

    JVM = jvm;
    JNIEnv *env;

    if (jvm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return;
    }

    //关于 eventfd() : https://juejin.im/post/5ad60fef5188255cb32ea496
    if ((crash_listener_fd = eventfd(0, EFD_CLOEXEC)) < 0) {
        LOG_D("crash happened!");
    }

    int res = pthread_create(&dump_thread, nullptr, dump_crash_in_new_thread, nullptr);
    if (res != 0) {
        LOG_D("dump thread create failed");
    }

    //缓存需要回调的java方法的信息
    jclass cls = env->FindClass(JAVA_CLASS_NAME);
    callback_jclass = static_cast<jclass>(env->NewGlobalRef(cls));
    callback_method_id = env->GetStaticMethodID(callback_jclass,
                                                JAVA_CRASH_CALLBACK_METHOD_NAME,
                                                JAVA_CRASH_CALLBACK_METHOD_SIGNATURE);
}
