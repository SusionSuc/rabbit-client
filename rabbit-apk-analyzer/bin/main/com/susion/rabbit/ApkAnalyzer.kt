package com.susion.rabbit

import com.google.gson.Gson
import com.susion.rabbit.base.AnalyzerTask
import com.susion.rabbit.entities.RabbitReportInfo
import com.susion.rabbit.helper.FileUtil
import com.susion.rabbit.task.*
import okhttp3.*
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.system.exitProcess

/**
 * susionwang at 2019-11-29
 */
object ApkAnalyzer {

    private val analyzerTasks = ArrayList<AnalyzerTask>().apply {
        add(AppInfoAnalyzerTask())
        add(BigImageResAnalyzerTask())
        add(ApkComposeAnalyzerTask())
        add(DuplicatedFileAnalyzerTask())
        add(MethodCountAnalyzerTask())
    }

    var globalConfig: Config = Config()

    @JvmStatic
    fun main(args: Array<String>) {

        if (args.isEmpty()) {
            errorExit("请输入配置文件Path")
        }

        globalConfig = loadConfig(args[0]) ?: Config("")

        if (!globalConfig.isValid()) {
            errorExit("配置文件错误!")
        }

        val unzipResult = UnzipApkTask().unzipApk(globalConfig.apkPath)

        val jsonStr = StringBuilder()
        jsonStr.append("{")
        analyzerTasks.forEachIndexed { index, analyzerTask ->
            jsonStr.append("\"${analyzerTask.getResultName()}\":")
            jsonStr.append(analyzerTask.analyze(unzipResult))
            if (index != analyzerTasks.size - 1) {
                jsonStr.append(",")
            }
        }
        jsonStr.append("}")

        writeResult(jsonStr.toString(), File(unzipResult.unZipDir).parentFile)

        uploadResult(globalConfig.uploadPath, jsonStr.toString())
    }


    private fun loadConfig(filePath: String): Config? {
        try {
            val configStr = FileUtil.getStringFromFile(File(filePath))
            return Gson().fromJson(configStr, Config::class.java)
        } catch (e: Exception) {
        }
        return null
    }

    @JvmStatic
    fun errorExit(errMsg: String) {
        print(errMsg)
        exitProcess(0)
    }

    private fun writeResult(resultStr: String, targetDir: File) {
        if (!targetDir.exists()) return
        val resultFile = File(targetDir, "apk-analyzer-result.json")

        if (resultFile.exists()) {
            resultFile.delete()
        }

        resultFile.writeText(resultStr)

        print("分析结果生成成功! --> apk-analyzer-result.json")
    }

    private fun uploadResult(uploadPath: String, resultStr: String) {

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .protocols(Collections.unmodifiableList(listOf(Protocol.HTTP_1_1)))
            .build()

        val reportInfo = RabbitReportInfo(info_str = resultStr)

        //构建请求
        val trackRequest = Request.Builder()
            .url(uploadPath)
            .post(getRequestBody(Gson().toJson(reportInfo)))
            .build()

        //请求服务器
        try {
            val resp = okHttpClient.newCall(trackRequest).execute()
            val code = resp.code()
            resp.body()?.close()
            if (code in 200..299) {
                print("日志上传成功!")
            } else {
                print("日志上传失败!")
            }
        } catch (e: Exception) {
            print("日志上传 exception : ${e.message}")
        }
    }

    private fun getRequestBody(content: String): RequestBody {
        return RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            Gson().toJson(InnerRequestBody(getBase64Encode(content.toByteArray())))
        )
    }

    private fun getBase64Encode(byteArray: ByteArray): String {
        return Base64.getEncoder().encodeToString(byteArray).trim()
    }

    private class InnerRequestBody(val content: String)

}