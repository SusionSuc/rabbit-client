package com.susion.rabbit.base.ui.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import com.susion.rabbit.base.RabbitLog
import com.susion.rabbit.base.ui.utils.device.*

/**
 * susionwang at 2019-09-23
 * open dev tools floating window need request permission
 */
object FloatingViewPermissionHelper {

    /**
     * check permission is ok?
     *
     * @param context
     * @return permission status
     */
    fun checkPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            when {
                UIDeviceUtils.checkIsMiuiRom() -> return miuiPermissionCheck(
                    context
                )
                UIDeviceUtils.checkIsMeizuRom() -> return meizuPermissionCheck(
                    context
                )
                UIDeviceUtils.checkIsHuaweiRom() -> return huaweiPermissionCheck(
                    context
                )
                UIDeviceUtils.checkIs360Rom() -> return qikuPermissionCheck(
                    context
                )
                UIDeviceUtils.checkIsOppoRom() -> return oppoROMPermissionCheck(
                    context
                )
            }
        }
        return commonROMPermissionCheck(
            context
        )
    }


    /**
     * apply permission
     */
    fun tryStartFloatingWindowPermission(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            when {
                UIDeviceUtils.checkIsMiuiRom() -> miuiROMPermissionApply(
                    context
                )
                UIDeviceUtils.checkIsMeizuRom() -> meizuROMPermissionApply(
                    context
                )
                UIDeviceUtils.checkIsHuaweiRom() -> huaweiROMPermissionApply(
                    context
                )
                UIDeviceUtils.checkIs360Rom() -> ROM360PermissionApply(
                    context
                )
                UIDeviceUtils.checkIsOppoRom() -> oppoROMPermissionApply(
                    context
                )
            }
        } else {
            commonROMPermissionApply(
                context
            )
        }
    }

    /**
     * 通用 rom 权限申请
     */
    private fun commonROMPermissionApply(context: Context) {
        if (UIDeviceUtils.checkIsMeizuRom()) {
            meizuROMPermissionApply(
                context
            )
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    commonROMPermissionApplyInternal(
                        context
                    )
                } catch (e: Exception) {
                    RabbitLog.getStackTraceString(e)
                }

            }
        }
    }

    /**
     * apply 360 permission
     */
    private fun ROM360PermissionApply(context: Context) {
        QikuUtils.applyPermission(context)
    }

    /**
     * apply huawei permission
     */
    private fun huaweiROMPermissionApply(context: Context) {
        HuaweiUtils.applyPermission(context)
    }

    /**
     * apply meizu permission
     */
    private fun meizuROMPermissionApply(context: Context) {
        MeizuUtils.applyPermission(context)
    }

    /**
     * apply miui permission
     */
    private fun miuiROMPermissionApply(context: Context) {
        MiuiUtils.applyMiuiPermission(context)
    }

    /**
     * apply oppo permission
     */
    private fun oppoROMPermissionApply(context: Context) {
        OppoUtils.applyOppoPermission(context)
    }

    fun commonROMPermissionApplyInternal(context: Context) {
        val clazz = Settings::class.java
        val field = clazz.getDeclaredField("ACTION_MANAGE_OVERLAY_PERMISSION")
        val intent = Intent(field.get(null).toString())
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.data = Uri.parse("package:" + context.packageName)
        context.startActivity(intent)
    }

    /**
     * show confirm diaRabbitLog
     */
    fun showConfirmDialog(context: Activity, result: OnConfirmResult) {
        val diaRabbitLog = AlertDialog.Builder(context)
            .setCancelable(true)
            .setTitle("")
            .setMessage("开启悬浮窗权限后可以自动打开 Rabbit，是否要打开?")
            .setPositiveButton(
                "现在去开启"
            ) { diaRabbitLog, which ->
                result.confirmResult(true)
                diaRabbitLog.dismiss()
            }.setNegativeButton(
                "暂不开启"
            ) { diaRabbitLog, which ->
                result.confirmResult(false)
                diaRabbitLog.dismiss()
            }.create()
        diaRabbitLog.show()
    }

    interface OnConfirmResult {
        fun confirmResult(confirm: Boolean)
    }


    /**
     * check huawei permission
     */
    private fun huaweiPermissionCheck(context: Context): Boolean {
        return HuaweiUtils.checkFloatWindowPermission(context)
    }

    /**
     * check miui permission
     */
    private fun miuiPermissionCheck(context: Context): Boolean {
        return MiuiUtils.checkFloatWindowPermission(context)
    }

    /**
     * check meizu permission
     */
    private fun meizuPermissionCheck(context: Context): Boolean {
        return MeizuUtils.checkFloatWindowPermission(context)
    }

    /**
     * check 360 permission
     */
    private fun qikuPermissionCheck(context: Context): Boolean {
        return QikuUtils.checkFloatWindowPermission(context)
    }

    /**
     * check oppo permission
     */
    private fun oppoROMPermissionCheck(context: Context): Boolean {
        return OppoUtils.checkFloatWindowPermission(context)
    }

    /**
     * check common permission
     */
    private fun commonROMPermissionCheck(context: Context): Boolean {
        if (UIDeviceUtils.checkIsMeizuRom()) {
            return meizuPermissionCheck(
                context
            )
        } else {
            var result: Boolean? = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    val clazz = Settings::class.java
                    val canDrawOverlays =
                        clazz.getDeclaredMethod("canDrawOverlays", Context::class.java)
                    result = canDrawOverlays.invoke(null, context) as Boolean
                } catch (e: Exception) {
                    RabbitLog.getStackTraceString(e)
                }

            }
            return result ?: true
        }
    }

}