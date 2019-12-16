package com.susion.rabbit.task

import com.susion.rabbit.ApkAnalyzer
import com.susion.rabbit.entities.UnZipApkFileInfo
import com.susion.rabbit.helper.FileUtil
import com.susion.rabbit.helper.Util
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

/**
 * susionwang at 2019-11-29
 * 解压APK
 */
class UnzipApkTask {

    fun unzipApk(apkPath: String): UnZipApkFileInfo {
        val unZipResult = UnZipApkFileInfo()
        val apkFile = File(apkPath)
        val unZipDir = getUnzipPath(apkFile)

        unZipResult.unZipDir = unZipDir.absolutePath
        unZipResult.apkSize = apkFile.length()

        if (unZipDir.exists()) {
           FileUtil.deleteDirectory(unZipDir)
        }

        if (!unZipDir.mkdir()) {
            throw RuntimeException("---Create directory '" + unZipDir.absolutePath + "' failed!")
        }

        //收集所有解压的文件的信息
        val zipApk = ZipFile(apkFile)
        val entries = zipApk.entries()
        while (entries.hasMoreElements()) {

            val entry = entries.nextElement() as ZipEntry

            val outName = writeEntry(zipApk, entry, unZipDir.absolutePath) ?: ""
            val fileAbsolutePath = "$unZipDir/$outName"

            val fileType = getFileSuffix(outName)
            var fileList = unZipResult.fileMap[fileType]
            if (fileList == null) {
                fileList = ArrayList()
                unZipResult.fileMap[fileType] = fileList
            }

            fileList.add(UnZipApkFileInfo.FileInfo(outName, fileAbsolutePath, entry.compressedSize))

        }

        //读取类混淆文件
        unZipResult.proguardClassMap.putAll(readMappingTxtFile())

        return unZipResult

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

    private fun getFileSuffix(name: String): String {
        val index = name.indexOf('.')
        return if (index >= 0 && index < name.length - 1) {
            name.substring(index + 1)
        } else ""
    }

    @Throws(IOException::class)
    private fun readMappingTxtFile(): Map<String, String> {
        val classMappingFile = File(ApkAnalyzer.globalConfig.classMappingFilePath)
        if (!classMappingFile.exists()) return emptyMap()

        val resMap = HashMap<String, String>()
        val bufferedReader = BufferedReader(FileReader(classMappingFile))
        var beforeClass: String
        var afterClass: String
        bufferedReader.use { bufferedReader ->
            var line: String? = bufferedReader.readLine()
            while (line != null) {
                if (!line.startsWith(" ")) {
                    val pair =
                        line.split("->".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if (pair.size == 2) {
                        beforeClass = pair[0].trim { it <= ' ' }
                        afterClass = pair[1].trim { it <= ' ' }
                        afterClass = afterClass.substring(0, afterClass.length - 1)
                        if (!Util.isNullOrNil(beforeClass) && !Util.isNullOrNil(afterClass)) {
                            resMap[afterClass] = beforeClass
                        }
                    }
                }
                line = bufferedReader.readLine()
            }
        }

        return resMap
    }

}