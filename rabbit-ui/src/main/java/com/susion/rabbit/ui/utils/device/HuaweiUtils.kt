package com.susion.rabbit.ui.utils.device

import android.annotation.TargetApi
import android.app.AppOpsManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.susion.rabbit.base.common.DeviceUtils

object HuaweiUtils {

    private val TAG = "HuaweiUtils"

    /**
     * check huawei permission
     */
    fun checkFloatWindowPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            checkOp(context, 24)
        } else true
    }

    /**
     * apply huawei permission granted page
     */
    fun applyPermission(context: Context) {
        try {
            val intent = Intent()
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            var comp = ComponentName(
                "com.huawei.systemmanager",
                "com.huawei.systemmanager.addviewmonitor.AddViewMonitorActivity"
            )
            intent.component = comp
            if (com.susion.rabbit.base.common.DeviceUtils.getEmuiVersion() === 3.1) {
                context.startActivity(intent)
            } else {
                comp = ComponentName(
                    "com.huawei.systemmanager",
                    "com.huawei.notificationmanager.ui.NotificationManagmentActivity"
                )
                intent.component = comp
                context.startActivity(intent)
            }
        } catch (e: SecurityException) {
            val intent = Intent()
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            val comp = ComponentName(
                "com.huawei.systemmanager",
                "com.huawei.permissionmanager.ui.MainActivity"
            )
            intent.component = comp
            context.startActivity(intent)
            Log.e(TAG, Log.getStackTraceString(e))
        } catch (e: ActivityNotFoundException) {
            val intent = Intent()
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            val comp =
                ComponentName("com.Android.settings", "com.android.settings.permission.TabItem")
            intent.component = comp
            context.startActivity(intent)
            e.printStackTrace()
            Log.e(TAG, Log.getStackTraceString(e))
        } catch (e: Exception) {
            Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show()
            Log.e(TAG, Log.getStackTraceString(e))
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
                Log.e(TAG, Log.getStackTraceString(e))
            }

        } else {
            Log.e(TAG, "Below API 19 cannot invoke!")
        }
        return false
    }
}
