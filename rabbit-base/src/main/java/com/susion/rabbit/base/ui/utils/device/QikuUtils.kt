package com.susion.rabbit.base.ui.utils.device

import android.annotation.TargetApi
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Binder
import android.os.Build
import com.susion.rabbit.base.RabbitLog
import com.susion.rabbit.base.TAG_UI

object QikuUtils {

    /**
     * check 360 permission
     */
    fun checkFloatWindowPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            checkOp(context, 24)
        } else true
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private fun checkOp(context: Context, op: Int): Boolean {
        val version = Build.VERSION.SDK_INT
        if (version >= 19) {
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
            RabbitLog.e("", "Below API 19 cannot invoke!")
        }
        return false
    }

    /**
     * apply 360 permission page
     */
    fun applyPermission(context: Context) {
        val intent = Intent()
        intent.setClassName(
            "com.android.settings",
            "com.android.settings.Settings\$OverlaySettingsActivity"
        )
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        if (isIntentAvailable(intent, context)) {
            context.startActivity(intent)
        } else {
            intent.setClassName(
                "com.qihoo360.mobilesafe",
                "com.qihoo360.mobilesafe.ui.index.AppEnterActivity"
            )
            if (isIntentAvailable(intent, context)) {
                context.startActivity(intent)
            } else {
                RabbitLog.e(
                    TAG_UI,
                    "can't open permission page with particular name, please use " + "\"adb shell dumpsys activity\" command and tell me the name of the float window permission page"
                )
            }
        }
    }

    /**
     * check intent available
     */
    private fun isIntentAvailable(intent: Intent?, context: Context): Boolean {
        return if (intent == null) {
            false
        } else context.packageManager.queryIntentActivities(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY
        ).size > 0
    }

}
