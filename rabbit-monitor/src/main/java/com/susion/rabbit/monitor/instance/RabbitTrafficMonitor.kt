package com.susion.rabbit.monitor.instance

import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import com.susion.rabbit.common.RabbitUiUtils
import com.susion.rabbit.monitor.core.RabbitMonitorProtocol


/**
 * susionwang at 2019-12-04
 * 流量监控
 */
internal class RabbitTrafficMonitor : RabbitMonitorProtocol {

    override fun open(context: Context) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            com.susion.rabbit.RabbitLog.e("不支持流量监控！")
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

        com.susion.rabbit.RabbitLog.d("rx : ${RabbitUiUtils.formatFileSize(trafficBucket.rxBytes)}")
        com.susion.rabbit.RabbitLog.d("tx : ${RabbitUiUtils.formatFileSize(trafficBucket.txBytes)}")


    }

    override fun close() {

    }

    override fun getMonitorInfo() = RabbitMonitorProtocol.TRAFFIC

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