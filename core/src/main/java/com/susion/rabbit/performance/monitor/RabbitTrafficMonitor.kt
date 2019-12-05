package com.susion.rabbit.performance.monitor

import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.PowerManager
import com.susion.rabbit.RabbitLog
import com.susion.rabbit.performance.core.RabbitMonitor
import com.susion.rabbit.utils.RabbitUiUtils


/**
 * susionwang at 2019-12-04
 * 流量监控
 */
class RabbitTrafficMonitor : RabbitMonitor {

    override fun open(context: Context) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            RabbitLog.e("不支持流量监控！")
            return
        }

        val networkStatsManager =
            context.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager
        val trafficStatus = networkStatsManager.querySummary(
            ConnectivityManager.TYPE_WIFI,
            getAppUid(context),
            System.currentTimeMillis() - 100000000,
            System.currentTimeMillis()
        )

        val trafficBucket = NetworkStats.Bucket()
        trafficStatus.getNextBucket(trafficBucket)

        RabbitLog.d("rx : ${RabbitUiUtils.formatFileSize(trafficBucket.rxBytes)}")
        RabbitLog.d("tx : ${RabbitUiUtils.formatFileSize(trafficBucket.txBytes)}")


    }

    override fun close() {

    }

    override fun getMonitorInfo() = RabbitMonitor.TRAFFIC

    override fun isOpen() = false

    private fun getAppUid(context: Context): String {
        var uid = ""
        val packageManager = context.packageManager
        try {
            val packageInfo =
                packageManager.getPackageInfo(context.packageName, PackageManager.GET_META_DATA)
            uid = packageInfo.applicationInfo.uid.toString()

        } catch (e: PackageManager.NameNotFoundException) {
        }

        return uid
    }
}