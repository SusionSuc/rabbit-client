//
// Created by susion wang on 2020-03-25.
//

#include "register.h"
#include <csignal>
#include "dump.h"
#include "malloc.h"
#include <sys/syscall.h>
#include <cstring>
#include <unistd.h>
#include <pthread.h>
#include "stdlib.h"
#include "common.h"

#define DUMP_FUNCTION_STACK_SIZE  (1024*128)

typedef struct {
    int signum;
    struct sigaction oldact;
} CrashSignalInfo;

//支持的 native crash 捕获类型
static CrashSignalInfo support_crash_infos[] =
        {
                {.signum = SIGABRT},
                {.signum = SIGBUS},
                {.signum = SIGFPE},
                {.signum = SIGILL},
                {.signum = SIGSEGV},
                {.signum = SIGTRAP},
                {.signum = SIGSYS},
                {.signum = SIGSTKFLT}
        };

//native code crash 回调
sig_atomic_t has_capture_crash = 0;
static pthread_mutex_t crash_mutex = PTHREAD_MUTEX_INITIALIZER;

//清空当前进程的信号队列
int signal_crash_queue(siginfo_t *si) {
    if (SIGABRT == si->si_signo || SI_FROMUSER(si)) {
        if (0 != syscall(SYS_rt_tgsigqueueinfo, getpid(), gettid(), si->si_signo, si))
            return 1;
    }
    return 0;
}

//native崩溃时会回调这个函数
static void crash_handler(int sig, siginfo_t *si, void *uc) {

    pthread_mutex_lock(&crash_mutex);

    if (has_capture_crash) {
        LOG_D("repeat capture crash!");
        goto exit;
    }

    has_capture_crash = 1;

    start_dump_crash(sig, si, uc);

    if (0 != signal_crash_queue(si)) {
        goto exit;
    }

    pthread_mutex_unlock(&crash_mutex);
    return;

    exit:
    pthread_mutex_unlock(&crash_mutex);
    LOG_D("终止进程!");
    _exit(1);
}

//注册native crash 回调
int register_crash_signal_handler(JNIEnv *env, jobject javaApiObj) {

    LOG_D("register_crash_signal_handler()");

    //1. 分配处理 native crash 的函数堆栈 (紧急情况下使用)
    stack_t dump_stack;
    dump_stack.ss_sp = calloc(1, DUMP_FUNCTION_STACK_SIZE);
    if (dump_stack.ss_sp == nullptr) {
        LOG_D("malloc dump function stack failed!");
        return ERROR_CODE_INT;
    }
    dump_stack.ss_size = DUMP_FUNCTION_STACK_SIZE;
    dump_stack.ss_flags = 0;

    //2. 注册 紧急堆栈
    int res = sigaltstack(&dump_stack, nullptr);
    if (res != 0) {
        LOG_D("register dump function stack failed!");
        return ERROR_CODE_INT;
    }

    //3. 注册crash信号处理函数
    struct sigaction crash_act{};
    memset(&crash_act, 0, sizeof(crash_act));
    sigfillset(&crash_act.sa_mask);
    crash_act.sa_sigaction = crash_handler;
    crash_act.sa_flags = SA_RESTART | SA_SIGINFO | SA_ONSTACK;

    int sig_size = sizeof(support_crash_infos) / sizeof(support_crash_infos[0]);
    LOG_D("resister signal size : %d", sig_size);

    for (int i = 0; i < sig_size; i++) {

        CrashSignalInfo cur_info = support_crash_infos[i];

        res = sigaction(cur_info.signum, &crash_act, &(cur_info.oldact));

        if (res != 0) {
            LOG_D("resister sigaction : %d handler failed", cur_info.signum);
            return ERROR_CODE_INT;
        }
    }

    LOG_D("resister sigaction success!");

    return SUCCESS_CODE_INT;

}

