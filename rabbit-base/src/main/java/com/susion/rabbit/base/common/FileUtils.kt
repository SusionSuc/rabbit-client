package com.susion.rabbit.base.common

import android.content.Context
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.security.MessageDigest

/**
 * susionwang at 2019-09-24
 */

object FileUtils {

    /**
     * Create a file if it doesn't exist, otherwise delete old file before creating.
     *
     * @param filePath The path of file.
     * @return `true`: success<br></br>`false`: fail
     */
    fun createFileByDeleteOldFile(filePath: String): Boolean {
        return createFileByDeleteOldFile(
            getFileByPath(
                filePath
            )
        )
    }

    /**
     * Create a file if it doesn't exist, otherwise delete old file before creating.
     *
     * @param file The file.
     * @return `true`: success<br></br>`false`: fail
     */
    private fun createFileByDeleteOldFile(file: File?): Boolean {
        if (file == null) return false
        // file exists and unsuccessfully delete then return false
        if (file.exists() && !file.delete()) return false
        if (!createOrExistsDir(file.parentFile)) return false
        try {
            return file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
    }

    /**
     * Return the file by path.
     *
     * @param filePath The path of file.
     * @return the file
     */
    private fun getFileByPath(filePath: String): File? {
        return if (com.susion.rabbit.base.common.FileUtils.isSpace(filePath)) null else File(
            filePath
        )
    }

    private fun isSpace(s: String?): Boolean {
        if (s == null) return true
        var i = 0
        val len = s.length
        while (i < len) {
            if (!Character.isWhitespace(s[i])) {
                return false
            }
            ++i
        }
        return true
    }

    /**
     * Create a directory if it doesn't exist, otherwise do nothing.
     *
     * @param file The file.
     * @return `true`: exists or creates successfully<br></br>`false`: otherwise
     */
    private fun createOrExistsDir(file: File?): Boolean {
        return file != null && if (file.exists()) file.isDirectory else file.mkdirs()
    }

    fun md5(str: String): String {
        val digest = MessageDigest.getInstance("MD5")
        val result = digest.digest(str.toByteArray())
        println("result${result.size}")
        return com.susion.rabbit.base.common.FileUtils.toHex(result)
    }

    private fun toHex(byteArray: ByteArray): String {
        return with(StringBuilder()) {
            byteArray.forEach {
                val hex = it.toInt() and (0xFF)
                val hexStr = Integer.toHexString(hex)
                if (hexStr.length == 1) {
                    append("0").append(hexStr)
                } else {
                    append(hexStr)
                }
            }
            toString()
        }
    }

    fun getAssetString(context: Context, name: String): String {
        var result = ""
        try {
            //获取输入流
            val mAssets = context.assets.open(name)
            //获取文件的字节数
            val length = mAssets.available()
            //创建byte数组
            val buffer = ByteArray(length)
            //将文件中的数据写入到字节数组中
            mAssets.read(buffer)
            mAssets.close()
            result = String(buffer)
            return result
        } catch (e: IOException) {
            e.printStackTrace()
            return result
        }
    }

    fun writeStrToFile(file: File, str: String) {
        createFileByDeleteOldFile(file)
        FileWriter(file).use {
            it.write(str)
        }

    }
}