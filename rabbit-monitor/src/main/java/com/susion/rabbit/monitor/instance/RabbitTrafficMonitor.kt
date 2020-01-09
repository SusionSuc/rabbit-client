package com.susion.rabbit.monitor.instance

import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import com.susion.rabbit.base.RabbitLog
import com.susion.rabbit.base.RabbitMonitorProtocol
import com.susion.rabbit.base.ui.utils.RabbitUiUtils

/**
 * susionwang at 2019-12-04
 * 流量监控
 */
internal class RabbitTrafficMonitor(override var isOpen: Boolean = false) :
    RabbitMonitorProtocol {

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

        isOpen = true
    }

    override fun close() {
        isOpen = false
    }

    override fun getMonitorInfo() = RabbitMonitorProtocol.TRAFFIC

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