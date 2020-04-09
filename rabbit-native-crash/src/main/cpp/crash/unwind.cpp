//
// Created by susion wang on 2020-04-08.
//

#include <jni.h>
#include <dlfcn.h>
#include "unwind.h"
#include "common.h"

void init_unwind() {

    libunwind = dlopen("libunwind.so", RTLD_NOW);
    if (nullptr == libunwind) return;

    unw_init_local = (t_unw_init_local) dlsym(libunwind, "_U" UNW_TARGET"_init_local");
    if (nullptr == unw_init_local) goto err;

    unw_get_reg = (t_unw_get_reg) dlsym(libunwind, "_U" UNW_TARGET"_get_reg");
    if (nullptr == unw_get_reg) goto err;

    unw_step = (t_unw_step) dlsym(libunwind, "_U" UNW_TARGET"_step");
    if (nullptr == unw_step) {
        goto err;
    }
    return;

    err:
    dlclose(libunwind);
    libunwind = nullptr;
}

size_t get_crash_stack(int api_level, siginfo_t *si, ucontext_t *uc, char *buf, size_t buf_len) {

}