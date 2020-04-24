//
// Created by susion wang on 2020-04-24.
//

#include "jvmti_handler.h"
#include "common.h"

//getPriority()
static char *SEND_STR_MSG_METHOD_NAME = const_cast<char *>("sendStrMsg");
static char *SEND_STR_MSG_METHOD_SIGNATURE = const_cast<char *>("(Ljava/lang/String;)V");
static char *COMMUNITY_HANDLER = const_cast<char *>("com/susion/rabbit/jvmti/NativeCommunityHandler");

static jobject handle_obj;
static jmethodID send_msg_method_id;

void notifyMessage(JNIEnv *env, const char *msg) {

    if ( send_msg_method_id == nullptr){
        LOG_D("send_msg_method_id is null!");
        return;
    }

    if (handle_obj == nullptr){
        LOG_D(" handle_obj is null!");
        return;
    }

    if (handle_obj != nullptr && send_msg_method_id != nullptr) {
        LOG_D("notify java handler");
        env->CallVoidMethod(handle_obj, send_msg_method_id, env->NewStringUTF(msg));
    }

}

void register_community_handler(JNIEnv *env, jobject obj) {
    jclass handler_class = env->FindClass(COMMUNITY_HANDLER);
    handle_obj = env->NewGlobalRef(obj);
    send_msg_method_id = env->GetMethodID(handler_class,SEND_STR_MSG_METHOD_NAME, SEND_STR_MSG_METHOD_SIGNATURE);
    if (handle_obj != nullptr && send_msg_method_id != nullptr){
        LOG_D("register_community_handler success!");
    }
}
