package com.susion.rabbit.base.ui.utils.device

import android.annotation.TargetApi
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import com.susion.rabbit.base.RabbitLog
import com.susion.rabbit.base.TAG_UI
import com.susion.rabbit.base.ui.utils.FloatingViewPermissionHelper

object MeizuUtils {


    /**
     * check meizu permission
     */
    fun checkFloatWindowPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            checkOp(context, 24)
        } else true
    }

    /**
     * apply meizu permission granted page
     */
    fun applyPermission(context: Context) {
        try {
            val intent = Intent("com.meizu.safe.security.SHOW_APPSEC")
            intent.putExtra("packageName", context.packageName)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                RabbitLog.e(TAG_UI, "获取悬浮窗权限, 打开AppSecActivity失败, " + RabbitLog.getStackTraceString(e))
                FloatingViewPermissionHelper.commonROMPermissionApplyInternal(
                    context
                )
            } catch (eFinal: Exception) {
                RabbitLog.e(TAG_UI, "获取悬浮窗权限失败, 通用获取方法失败, " + RabbitLog.getStackTraceString(eFinal))
            }

        }

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private fun checkOp(context: Context, op: Int): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val manager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            try {
                val clazz = AppOpsManager::class.java
                val method = clazz.getDeclaredMethod(
                    "checkOp",
                    Int::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType,
                    String::class.java
                )
                return AppOpsManager.MODE_ALLOWED == method.invoke(
                    manager,
                    op,
                    Binder.getCallingUid(),
                    context.packageName
                ) as Int
            } catch (e: Exception) {
                RabbitLog.e(TAG_UI, RabbitLog.getStackTraceString(e))
            }

        } else {
            RabbitLog.e(TAG_UI, "Below API 19 cannot invoke!")
        }
        return false
    }
}
