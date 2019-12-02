package com.susion.rabbit

import com.susion.rabbit.entities.UnZipApkFileInfo
import com.susion.rabbit.model.utils.FileUtil
import com.susion.rabbit.model.utils.Util
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

/**
 * susionwang at 2019-11-29
 * 解压APK
 */
class UnzipApkTask {

    fun unzipApk(apkPath: String): UnZipApkFileInfo {
        val result = UnZipApkFileInfo()
        val apkFile = File(apkPath)
        val unZipDir = getUnzipPath(apkFile)

        result.dirPath = unZipDir.absolutePath
        result.apkSize = apkFile.length()

        if (unZipDir.exists()) {
            FileUtil.deleteDirectory(unZipDir)
        }

        if (!unZipDir.mkdir()) {
            throw RuntimeException("---Create directory '" + unZipDir.absolutePath + "' failed!")
        }

        val zipApk = ZipFile(apkFile)
        val entries = zipApk.entries()
        while (entries.hasMoreElements()) {
            writeEntry(zipApk, entries.nextElement() as ZipEntry, unZipDir.absolutePath)
        }

        return result
    }

    @Throws(IOException::class)
    private fun writeEntry(zipFile: ZipFile, entry: ZipEntry, outPath: String): String? {
        var readSize: Int
        val readBuffer = ByteArray(4096)
        var bufferedOutput: BufferedOutputStream? = null
        var zipInputStream: InputStream? = null
        val entryName = entry.name
        var outEntryName: String? = null
        val filename: String
        val dir: File
        var file: File? = null
        val index = entryName.lastIndexOf('/')
        if (index >= 0) {
            filename = entryName.substring(index + 1)
            val dirName = entryName.substring(0, index)
            dir = File(outPath, dirName)
            if (!dir.exists() && !dir.mkdirs()) {
                return null
            }
            if (!Util.isNullOrNil(filename)) {
                file = File(dir, filename)
                outEntryName = reverseResguard(dirName, filename)
                if (Util.isNullOrNil(outEntryName)) {
                    outEntryName = entryName
                }
            }
        } else {
            file = File(outPath, entryName)
            outEntryName = entryName
        }
        try {
            if (file != null) {
                if (!file.createNewFile()) {
                    return null
                }
                bufferedOutput = BufferedOutputStream(FileOutputStream(file))
                zipInputStream = zipFile.getInputStream(entry)
                readSize = zipInputStream!!.read(readBuffer)
                while (readSize != -1) {
                    bufferedOutput.write(readBuffer, 0, readSize)
                    readSize = zipInputStream.read(readBuffer)
                }
            } else {
                return null
            }
        } finally {
            zipInputStream?.close()
            bufferedOutput?.close()
        }
        return outEntryName
    }

    private fun reverseResguard(dirName: String, filename: String): String {
        var filename = filename
        var outEntryName = ""
//        if (resDirMap.containsKey(dirName)) {
//            val newDirName = resDirMap.get(dirName)
//            val resource = parseResourceNameFromPath(newDirName, filename)
//            val suffixIndex = filename.indexOf('.')
//            var suffix = ""
//            if (suffixIndex >= 0) {
//                suffix = filename.substring(suffixIndex)
//            }
//            if (resguardMap.containsKey(resource)) {
//                val lastIndex = resguardMap.get(resource).lastIndexOf('.')
//                if (lastIndex >= 0) {
//                    filename = resguardMap.get(resource).substring(lastIndex + 1) + suffix
//                }
//            }
//            outEntryName = newDirName + "/" + filename
//        }
        return outEntryName
    }

    private fun getUnzipPath(apkFile: File): File {
        return File(apkFile.parentFile.absolutePath + File.separator + getApkRawName(apkFile.name) + "_unzip")
    }

    private fun getApkRawName(name: String?): String {
        if (name == null || name.isEmpty()) {
            return ""
        }
        val index = name.indexOf('.')
        return if (index == -1) {
            name
        } else name.substring(0, index)
    }

}