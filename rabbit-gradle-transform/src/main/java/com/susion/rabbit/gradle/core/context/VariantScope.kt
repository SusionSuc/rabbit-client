package com.susion.rabbit.gradle.core.context

import com.android.build.api.artifact.ArtifactType
import com.android.build.gradle.internal.scope.AnchorOutputType
import com.android.build.gradle.internal.scope.InternalArtifactType
import com.android.build.gradle.internal.scope.VariantScope
import java.io.File

//gradle 3.3 +
internal object VariantScopeV33 {

    /**
     * The merged AndroidManifest.xml
     */
    fun getMergedManifests(scope: VariantScope): Collection<File> {
        return getFinalArtifactFiles(
            scope,
            InternalArtifactType.BUNDLE_MANIFEST
        )
    }

    /**
     * The merged resources
     */
    fun getMergedRes(scope: VariantScope): Collection<File> {
        return getFinalArtifactFiles(
            scope,
            InternalArtifactType.MERGED_RES
        )
    }

    /**
     * The merged assets
     */
    fun getMergedAssets(scope: VariantScope): Collection<File> {
        return getFinalArtifactFiles(
            scope,
            InternalArtifactType.MERGED_ASSETS
        )
    }

    /**
     * The processed resources
     */
    fun getProcessedRes(scope: VariantScope): Collection<File> {
        return getFinalArtifactFiles(
            scope,
            InternalArtifactType.PROCESSED_RES
        )
    }

    /**
     * All of classes
     */
    fun getAllClasses(scope: VariantScope): Collection<File> {
        return getFinalArtifactFiles(
            scope,
            AnchorOutputType.ALL_CLASSES
        )
    }

    fun getSymbolList(scope: VariantScope): Collection<File> {
        return getFinalArtifactFiles(
            scope,
            InternalArtifactType.SYMBOL_LIST
        )
    }

    fun getSymbolListWithPackageName(scope: VariantScope): Collection<File> {
        return getFinalArtifactFiles(
            scope,
            InternalArtifactType.SYMBOL_LIST_WITH_PACKAGE_NAME
        )
    }

    fun getApk(scope: VariantScope): Collection<File> {
        return getFinalArtifactFiles(
            scope,
            InternalArtifactType.APK
        )
    }

    fun getJavac(scope: VariantScope): Collection<File> {
        return getFinalArtifactFiles(
            scope,
            InternalArtifactType.JAVAC
        )
    }


    fun getFinalArtifactFiles(scope: VariantScope, type: ArtifactType): Collection<File> {
        return scope.artifacts.getFinalArtifactFiles(type).files
    }

    fun getRawAndroidResources(scope: VariantScope): Collection<File> {
        return scope.variantData.allRawAndroidResources.files
    }

}

private val ALL_CLASSES_GETTER = VariantScopeV33::getAllClasses

private val APK_GETTER = VariantScopeV33::getApk

private val JAVAC_GETTER = VariantScopeV33::getJavac

private val MERGED_ASSETS_GETTER = VariantScopeV33::getMergedAssets

private val MERGED_MANIFESTS_GETTER = VariantScopeV33::getMergedManifests

private val MERGED_RESOURCE_GETTER = VariantScopeV33::getMergedRes

private val PROCESSED_RES_GETTER = VariantScopeV33::getProcessedRes

private val SYMBOL_LIST_GETTER = VariantScopeV33::getSymbolList

private val SYMBOL_LIST_WITH_PACKAGE_NAME_GETTER = VariantScopeV33::getSymbolListWithPackageName


private val RAW_ANDROID_RESOURCES_GETTER = VariantScopeV33::getRawAndroidResources

/**
 * The output directory of APK files
 */
val VariantScope.apk: Collection<File>
    get() = APK_GETTER(this)

val VariantScope.javac: Collection<File>
    get() = JAVAC_GETTER(this)

/**
 * The output directory of merged [AndroidManifest.xml](https://developer.android.com/guide/topics/manifest/manifest-intro)
 */
val VariantScope.mergedManifests: Collection<File>
    get() = MERGED_MANIFESTS_GETTER(this)

/**
 * The output directory of merged resources
 */
val VariantScope.mergedRes: Collection<File>
    get() = MERGED_RESOURCE_GETTER(this)

/**
 * The output directory of merged assets
 */
val VariantScope.mergedAssets: Collection<File>
    get() = MERGED_ASSETS_GETTER(this)

/**
 * The output directory of processed resources: *resources-**variant**.ap\_*
 */
val VariantScope.processedRes: Collection<File>
    get() = PROCESSED_RES_GETTER(this)

/**
 * All of classes
 */
val VariantScope.allClasses: Collection<File>
    get() = ALL_CLASSES_GETTER(this)

val VariantScope.symbolList: Collection<File>
    get() = SYMBOL_LIST_GETTER(this)

val VariantScope.symbolListWithPackageName: Collection<File>
    get() = SYMBOL_LIST_WITH_PACKAGE_NAME_GETTER(
        this
    )
