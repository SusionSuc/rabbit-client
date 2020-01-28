package com.susion.rabbit.task

import com.google.gson.Gson
import com.susion.rabbit.ApkAnalyzer
import com.susion.rabbit.base.AnalyzerTask
import com.susion.rabbit.dexdeps.DexData
import com.susion.rabbit.dexdeps.Output
import com.susion.rabbit.entities.UnZipApkFileInfo
import com.susion.rabbit.helper.Util
import java.io.IOException
import java.io.RandomAccessFile

/**
 * susionwang at 2019-12-02
 *
 * 统计APK的方法数。
 *
 * 可以自定义统计某个包下的方法个数
 *
 */
class MethodCountAnalyzerTask : AnalyzerTask {

    private val OTHER = "other-pkg"

    override fun getResultName() = "MethodCount"

    override fun analyze(unZipContext: UnZipApkFileInfo): String {
        //统计分组
        val methodGroup = HashMap<String, Int>()
        ApkAnalyzer.globalConfig.methodGroup.forEach {
            methodGroup[it.packageName] = 0
        }
        methodGroup[OTHER] = 0

        //统计所有方法数量
        unZipContext.fileMap.filter { it.key.contains("dex") }.flatMap { it.value }.forEach {
            countDex(RandomAccessFile(it.path, "rw"), methodGroup, unZipContext.proguardClassMap)
        }

        var totalCount = 0
        methodGroup.forEach { (_, count) ->
            totalCount += count
        }

        methodGroup["total-count"] = totalCount

        return Gson().toJson(methodGroup)
    }

    @Throws(IOException::class)
    private fun countDex(
        dexFile: RandomAccessFile,
        methodGroup: HashMap<String, Int>,
        proguardClassMap: Map<String, String>
    ) {
        val dexData = DexData(dexFile)
        dexData.load()
        val methodRefs = dexData.methodRefs
        for (methodRef in methodRefs) {

            var className = getNormalClassName(methodRef.declClassName)

            if (proguardClassMap.containsKey(className)) {
                className = proguardClassMap[className] ?:""
            }

            if (!Util.isNullOrNil(className)) {
                if (className.indexOf('.') == -1) {
                    continue
                }

                var hasSetting = false
                for (entry in methodGroup.entries) {
                    if (className.startsWith(entry.key)) {
                        methodGroup[entry.key] = entry.value + 1
                        hasSetting = true
                        continue
                    }
                }

                if (!hasSetting) {
                    methodGroup[OTHER] = methodGroup[OTHER]!! + 1
                }

            }
        }
    }

    /*
     *  parse the descriptor of class to normal dot-split class name (XXX.XXX.XXX)
     */
    private fun getNormalClassName(name: String): String {
        if (!Util.isNullOrNil(name)) {
            var className = Output.descriptorToDot(name)
            if (className.endsWith("[]")) {                                                //enum or array
                className = className.substring(0, className.indexOf("[]"))
            }
            return className
        }
        return ""
    }
}