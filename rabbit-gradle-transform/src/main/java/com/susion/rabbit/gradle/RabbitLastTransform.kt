package com.susion.rabbit.gradle

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.builder.model.AndroidProject
import com.susion.rabbit.gradle.core.AsmClassVisitorTransformer
import com.susion.rabbit.gradle.core.context.RabbitTransformInvocation
import com.susion.rabbit.gradle.core.rxentension.file
import com.susion.rabbit.gradle.transform.*
import com.susion.rabbit.gradle.utils.RabbitTransformUtils
import java.util.concurrent.TimeUnit

/**
 * susionwang at 2019-11-12
 *
 * 后置的transform， 需要用到[RabbitFirstTransform] 收集的一些信息
 */
class RabbitLastTransform : Transform() {

    override fun getName() = "rabbit-last-transform"

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> =
        TransformManager.CONTENT_CLASS

    override fun isIncremental() = true

    override fun getScopes(): MutableSet<QualifiedContent.ScopeType> =
        TransformManager.SCOPE_FULL_PROJECT

    override fun transform(transformInvocation: TransformInvocation?) {

        if (transformInvocation == null) return

        RabbitTransformUtils.print("🍊 rabbit RabbitLastTransform run")

        val transformInstances = listOf(
            BlockCodeLoadTransform()
        )

        RabbitTransformInvocation(
            transformInvocation,
            listOf(AsmClassVisitorTransformer(transformInstances))
        ).apply {
            if (isIncremental) {
                onPreTransform(this)
                doIncrementalTransform()
            } else {
                //删除老的构建内容
                buildDir.file(AndroidProject.FD_INTERMEDIATES, "transforms", "dexBuilder")
                    .let { dexBuilder ->
                        if (dexBuilder.exists()) {
                            dexBuilder.deleteRecursively()
                        }
                    }
                outputProvider.deleteAll()
                onPreTransform(this)
                doFullTransform()
            }

            onPostTransform(this)

        }.executor.apply {
            shutdown()
            awaitTermination(1, TimeUnit.MINUTES)
        }
    }


}