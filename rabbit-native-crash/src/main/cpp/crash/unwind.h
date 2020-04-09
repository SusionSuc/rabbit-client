//
// Created by susion wang on 2020-04-08.
//

#ifndef RABBIT_CLIENT_UNWIND_H
#define RABBIT_CLIENT_UNWIND_H

#include <jni.h>
#include "signal.h"
#include <ucontext.h>

#ifdef __cplusplus
extern "C" {
#endif

#if defined(__arm__)
#define UNW_TARGET "arm"
#define UNW_REG_IP 14
#define UNW_CURSOR_LEN 4096
#elif defined(__aarch64__)
#define UNW_TARGET "aarch64"
#define UNW_REG_IP 30
#define UNW_CURSOR_LEN 4096
#elif defined(__i386__)
#define UNW_TARGET "x86"
#define UNW_REG_IP 8
#define UNW_CURSOR_LEN 127
#elif defined(__x86_64__)
#define UNW_TARGET "x86_64"
#define UNW_REG_IP 16
#define UNW_CURSOR_LEN 127
#endif

typedef struct {
    uintptr_t opaque[UNW_CURSOR_LEN];
} unw_cursor_t;

#if defined(__arm__)
typedef struct
{
    uintptr_t r[16];
} unw_context_t;
#else
typedef ucontext_t unw_context_t;
#endif

void init_unwind();
size_t get_crash_stack(int api_level, siginfo_t *si, ucontext_t *uc, char *buf, size_t buf_len);

typedef int (*t_unw_init_local)(unw_cursor_t *, unw_context_t *);
typedef int (*t_unw_get_reg)(unw_cursor_t *, int, uintptr_t *);
typedef int (*t_unw_step)(unw_cursor_t *);

static void *libunwind = NULL;
static t_unw_init_local unw_init_local = NULL;
static t_unw_get_reg unw_get_reg = NULL;
static t_unw_step unw_step = NULL;

#ifdef __cplusplus
}
#endif

#endif //RABBIT_CLIENT_UNWIND_H