package com.susion.rabbit.base.ui.utils.device

import android.annotation.TargetApi
import android.app.AppOpsManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import com.susion.rabbit.base.RabbitLog
import com.susion.rabbit.base.TAG_UI

object OppoUtils {

    /**
     * check oppo permission
     */
    fun checkFloatWindowPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            checkOp(
                context,
                24
            ) //OP_SYSTEM_ALERT_WINDOW = 24;
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
            RabbitLog.e(TAG_UI, "Below API 19 cannot invoke!")
        }
        return false
    }

    /**
     * apply oppo permission
     */
    fun applyOppoPermission(context: Context) {
        try {
            val intent = Intent()
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            val comp = ComponentName(
                "com.coloros.safecenter",
                "com.coloros.safecenter.sysfloatwindow.FloatWindowListActivity"
            )//悬浮窗管理页面
            intent.component = comp
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}
