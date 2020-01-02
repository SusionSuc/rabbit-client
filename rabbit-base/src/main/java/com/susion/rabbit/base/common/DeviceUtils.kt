package com.susion.rabbit.base.common

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.Log
import androidx.core.content.ContextCompat
import java.io.*
import java.util.*


/**
 * susionwang at 2019-09-23
 */
object DeviceUtils {

    private val TAG = "DeviceUtils"


    /**
     * get system property for "ro.build.version.emui"
     */
    fun getSystemProperty(propName: String): String? {
        val line: String
        var input: BufferedReader? = null
        try {
            val p = Runtime.getRuntime().exec("getprop $propName")
            input = BufferedReader(InputStreamReader(p.inputStream), 1024)
            line = input.readLine()
            input.close()
        } catch (ex: IOException) {
            Log.e(
                DeviceUtils.TAG,
                "Unable to read sysprop $propName",
                ex
            )
            return null
        } finally {
            if (input != null) {
                try {
                    input.close()
                } catch (e: IOException) {
                    Log.e(
                        DeviceUtils.TAG,
                        "Exception while closing InputStream",
                        e
                    )
                }

            }
        }
        return line
    }


    fun getDeviceName(): String {
        val manufacturer = if (Build.MANUFACTURER == null) "" else Build.MANUFACTURER
        val model = if (Build.MODEL == null) "" else Build.MODEL
        return if (model.startsWith(manufacturer)) {
            capitalize(model)
        } else {
            capitalize(manufacturer) + " " + model
        }
    }

    private fun capitalize(s: String): String {
        if (TextUtils.isEmpty(s) || s.trim { it <= ' ' }.length == 0) {
            return ""
        }

        val first = s[0]
        return if (Character.isUpperCase(first)) {
            s
        } else {
            Character.toUpperCase(first) + s.substring(1)
        }
    }

    private var uuid: UUID? = null
    private val PREFS_DEVICE_ID = "device_id"
    private val PREFS_FILE = "pre_device.xml"

    fun getDeviceId(context: Context?): String {
        if (context == null) {
            return ""
        }
        if (uuid == null) {
            synchronized(DeviceUtils::class.java) {
                if (uuid == null) {
                    val prefs = context.getSharedPreferences(
                        PREFS_FILE,
                        0
                    )
                    val id = prefs.getString(
                        PREFS_DEVICE_ID,
                        null
                    )
                    if (id != null) {
                        // Use the ids previously computed and stored in the prefs file
                        uuid = UUID.fromString(id)
                    } else {
                        val androidId =
                            getAndroidId(context)
                        // Use the Android ID unless it's broken, in which case fallback on deviceId,
                        // unless it's not isEmpty, then fallback on a random number which we store
                        // to a prefs file
                        try {
                            if (!TextUtils.isEmpty(androidId) && "9774d56d682e549c" != androidId) {
                                uuid =
                                    UUID.nameUUIDFromBytes(androidId.toByteArray(charset("utf8")))
                            } else {
                                val deviceId =
                                    getIMEIId(context)
                                uuid =
                                    if (deviceId != null && !TextUtils.equals(
                                            deviceId,
                                            "unknow"
                                        )
                                    )
                                        UUID.nameUUIDFromBytes(deviceId!!.toByteArray(charset("utf8")))
                                    else
                                        UUID.randomUUID()
                            }
                        } catch (e: UnsupportedEncodingException) {
                            e.printStackTrace()
                        }

                        prefs.edit().putString(
                            PREFS_DEVICE_ID,
                            uuid.toString()
                        ).apply()
                    }
                }
            }
        }
        return uuid.toString()
    }

    @SuppressLint("MissingPermission")
    fun getIMEIId(context: Context?): String {
        if (context == null) return "unknow"
        var deviceId = ""
        try {
            if (ContextCompat.checkSelfPermission(
                    context,
                    "android.permission.READ_PHONE_STATE"
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val telephony =
                    context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                if (telephony != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        deviceId = telephony.imei
                    } else {
                        deviceId = telephony.deviceId
                    }
                }

            }
        } catch (var3: Exception) {
            var3.printStackTrace()
        }

        return deviceId
    }

    /**
     * 获取设备androidId
     *
     * @param context
     * @return
     */
    fun getAndroidId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }


    fun getAppVersionCode(context: Context): String? {
        val manager = context.packageManager
        var name: String? = null
        try {
            val info = manager.getPackageInfo(context.packageName, 0)
            name = info.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return name
    }

    fun getMemorySize(context: Context): String {
        try {
            //1, 读取Android设备的内存信息文件内容
            val fileReader = FileReader("/proc/meminfo")
            BufferedReader(fileReader).use {
                val size = it.readLine().replace("[^0-9.,]+".toRegex(), "").toLongOrNull() ?: 0
                return formatFileSize(size * 1024)
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "0"
    }

    /**
     * Return whether device is rooted.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    fun isDeviceRooted(): Boolean {
        val su = "su"
        val locations = arrayOf(
            "/system/bin/",
            "/system/xbin/",
            "/sbin/",
            "/system/sd/xbin/",
            "/system/bin/failsafe/",
            "/data/local/xbin/",
            "/data/local/bin/",
            "/data/local/",
            "/system/sbin/",
            "/usr/bin/",
            "/vendor/bin/"
        )
        for (location in locations) {
            if (File(location + su).exists()) {
                return true
            }
        }
        return false
    }

    private fun formatFileSize(size: Long): String {
        if (size <= 0) return ""
        val formater = Formatter()
        return when {
            size < 1024 -> size.toString() + "B"
            size < 1024 * 1024 -> {
                formater.format("%.2f KB", size / 1024f).toString()
            }
            size < 1024 * 1024 * 1024 -> {
                formater.format("%.2f MB", size / 1024f / 1024f).toString()
            }
            else -> {
                formater.format("%.2f GB", size / 1024f / 1024f / 1024f).toString()
            }
        }
    }

}