package com.susion.rabbit.gradle.core.rxentension

import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.scope.VariantScope
import com.android.build.gradle.internal.variant.BaseVariantData
import com.android.build.gradle.tasks.ProcessAndroidResources
import com.susion.rabbit.gradle.core.context.ResolvedArtifactResults
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * The variant dependencies
 */
val BaseVariant.dependencies: ResolvedArtifactResults
    get() = ResolvedArtifactResults(this)

/**
 * The variant scope
 *
 * @author johnsonlee
 */
val BaseVariant.scope: VariantScope
    get() = variantData.scope

val BaseVariant.project: Project
    get() = scope.globalScope.project

/**
 * The variant data
 */
val BaseVariant.variantData: BaseVariantData
    get() = javaClass.getDeclaredMethod("getVariantData").invoke(this) as BaseVariantData

@Suppress("DEPRECATION")
val BaseVariant.javaCompilerTask: Task
    get() = this.javaCompileProvider.get()

@Suppress("DEPRECATION")
val BaseVariant.preBuildTask: Task
    get() = this.preBuildProvider.get()

@Suppress("DEPRECATION")
val BaseVariant.assembleTask: Task
    get() = this.assembleProvider.get()

@Suppress("DEPRECATION")
val BaseVariant.mergeAssetsTask: Task
    get() = this.mergeAssetsProvider.get()

@Suppress("DEPRECATION")
val BaseVariant.mergeResourcesTask: Task
    get() = this.mergeResourcesProvider.get()

val BaseVariant.processResTask: ProcessAndroidResources
    get() = project.tasks.withType(ProcessAndroidResources::class.java).findByName("process${name.capitalize()}Resources")!!
