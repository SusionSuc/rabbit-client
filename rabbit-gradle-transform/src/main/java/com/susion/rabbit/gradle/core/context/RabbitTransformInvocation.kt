package com.susion.rabbit.gradle.core.context

import com.android.SdkConstants
import com.android.build.api.transform.*
import com.android.build.api.transform.Status.*
import com.android.build.gradle.internal.publishing.AndroidArtifacts.ArtifactScope.ALL
import com.android.build.gradle.internal.publishing.AndroidArtifacts.ArtifactType.AAR
import com.android.build.gradle.internal.publishing.AndroidArtifacts.ArtifactType.JAR
import com.android.build.gradle.internal.publishing.AndroidArtifacts.ConsumedConfigType.RUNTIME_CLASSPATH
import com.susion.rabbit.gradle.GlobalConfig
import com.susion.rabbit.gradle.core.RabbitByteCodeTransformer
import com.susion.rabbit.gradle.core.TransformListener
import com.susion.rabbit.gradle.core.asm.Klass
import com.susion.rabbit.gradle.core.asm.KlassPool
import com.susion.rabbit.gradle.core.rxentension.*
import java.io.File
import java.net.URLClassLoader
import java.util.concurrent.ForkJoinPool

/**
 * Represents a dInvocation of TransformInvocation
 *
 * Rabbit Transform；  提供了众多transform需要用到的上下文
 */
internal class RabbitTransformInvocation(
    private val dInvocation: TransformInvocation,
    private val transformers: List<RabbitByteCodeTransformer>
) :
    TransformInvocation, TransformContext,
    TransformListener,
    ArtifactManager {

    override val name: String = dInvocation.context.variantName

    override val projectDir: File = dInvocation.project.projectDir

    override val buildDir: File = dInvocation.project.buildDir

    override val temporaryDir: File = dInvocation.context.temporaryDir

    override val reportsDir: File = File(buildDir, "reports").apply { mkdirs() }

    override val executor = ForkJoinPool(
        Runtime.getRuntime().availableProcessors(),
        ForkJoinPool.defaultForkJoinWorkerThreadFactory,
        null,
        true
    )

    override val bootClasspath = dInvocation.bootClasspath

    override val compileClasspath = dInvocation.compileClasspath

    override val runtimeClasspath = dInvocation.runtimeClasspath

    override val artifacts = this

    override val klassPool =
        KlassPoolImpl(
            runtimeClasspath
        )

    override val applicationId = dInvocation.applicationId

    override val originalApplicationId = dInvocation.originalApplicationId

    override val isDebuggable = dInvocation.variant.buildType.isDebuggable

    override fun hasProperty(name: String): Boolean {
        return project.hasProperty(name)
    }

    override fun getProperty(name: String): String? {
        return project.properties[name]?.toString()
    }

    override fun getInputs(): MutableCollection<TransformInput> = dInvocation.inputs

    override fun getSecondaryInputs(): MutableCollection<SecondaryInput> =
        dInvocation.secondaryInputs

    override fun getReferencedInputs(): MutableCollection<TransformInput> =
        dInvocation.referencedInputs

    override fun isIncremental() = dInvocation.isIncremental

    override fun getOutputProvider(): TransformOutputProvider = dInvocation.outputProvider

    override fun getContext(): Context = dInvocation.context

    override fun onPreTransform(context: TransformContext) = transformers.forEach {
        it.onPreTransform(this)
    }

    override fun onPostTransform(context: TransformContext) = transformers.forEach {
        it.onPostTransform(this)
    }

    override fun get(type: String): Collection<File> = when (type) {
        ArtifactManager.AAR -> variant.scope.getArtifactCollection(
            RUNTIME_CLASSPATH,
            ALL,
            AAR
        ).artifactFiles.files
        ArtifactManager.ALL_CLASSES -> variant.scope.allClasses
        ArtifactManager.APK -> variant.scope.apk
        ArtifactManager.JAR -> variant.scope.getArtifactCollection(
            RUNTIME_CLASSPATH,
            ALL,
            JAR
        ).artifactFiles.files
        ArtifactManager.JAVAC -> variant.scope.javac
        ArtifactManager.MERGED_ASSETS -> variant.scope.mergedAssets
        ArtifactManager.MERGED_RES -> variant.scope.mergedRes
        ArtifactManager.MERGED_MANIFESTS -> variant.scope.mergedManifests.search { SdkConstants.FN_ANDROID_MANIFEST_XML == it.name }
        ArtifactManager.PROCESSED_RES -> variant.scope.processedRes.search {
            it.name.startsWith(
                SdkConstants.FN_RES_BASE
            ) && it.name.endsWith(SdkConstants.EXT_RES)
        }
        ArtifactManager.SYMBOL_LIST -> variant.scope.symbolList
        ArtifactManager.SYMBOL_LIST_WITH_PACKAGE_NAME -> variant.scope.symbolListWithPackageName
        else -> TODO("Unexpected type: $type")
    }

    //接入  ->   bytecode.transform(this)
    internal fun doFullTransform() {
        this.inputs.parallelStream().forEach { input ->
            input.directoryInputs.parallelStream().forEach {
                it.file.transformFileToByArray(
                    outputProvider.getContentLocation(
                        it.name,
                        it.contentTypes,
                        it.scopes,
                        Format.DIRECTORY
                    )
                ) { bytecode ->
                    bytecode.transform(this)
                }
            }
            input.jarInputs.parallelStream().forEach {
                project.logger.info("Transforming ${it.file}")
                it.file.transformFileToByArray(
                    outputProvider.getContentLocation(
                        it.name,
                        it.contentTypes,
                        it.scopes,
                        Format.JAR
                    )
                ) { bytecode ->
                    bytecode.transform(this)
                }
            }
        }
    }

    @Suppress("NON_EXHAUSTIVE_WHEN")
    internal fun doIncrementalTransform() {
        this.inputs.parallelStream().forEach { input ->
            input.jarInputs.parallelStream()
                .filter { it.status != NOTCHANGED }
                .forEach { jarInput ->
                    when (jarInput.status) {
                        REMOVED -> jarInput.file.delete()
                        CHANGED, ADDED -> {
                            val root = outputProvider.getContentLocation(
                                jarInput.name,
                                jarInput.contentTypes,
                                jarInput.scopes,
                                Format.JAR
                            )
                            project.logger.info("Transforming ${jarInput.file}")
                            jarInput.file.transformFileToByArray(root) { bytecode ->
                                bytecode.transform(this)
                            }
                        }
                    }
                }

            input.directoryInputs.parallelStream().forEach { dirInput ->
                val base = dirInput.file.toURI()
                dirInput.changedFiles.ifNotEmpty {
                    it.forEach { file, status ->
                        when (status) {
                            REMOVED -> file.delete()
                            ADDED, CHANGED -> {
                                val root = outputProvider.getContentLocation(
                                    dirInput.name,
                                    dirInput.contentTypes,
                                    dirInput.scopes,
                                    Format.DIRECTORY
                                )
                                project.logger.info("Transforming $file")
                                file.transformFileToByArray(
                                    File(
                                        root,
                                        base.relativize(file.toURI()).path
                                    )
                                ) { bytecode ->
                                    bytecode.transform(this)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // 应用在每一个class文件上
    private fun ByteArray.transform(invocation: RabbitTransformInvocation): ByteArray {
        if (!GlobalConfig.pluginConfig.enable) return this
        return transformers.fold(this) { bytes, transformer ->
            transformer.transform(invocation, bytes, "")
        }
    }

    internal class KlassPoolImpl(private val classpath: Collection<File>) :
        KlassPool {

        private val classLoader =
            URLClassLoader(classpath.map { it.toURI().toURL() }.toTypedArray())

        private val klasses = mutableMapOf<String, Klass>()

        override fun get(type: String): Klass {
            val name = normalize(type)
            return klasses.getOrDefault(name, findClass(name))
        }

        internal fun findClass(name: String): Klass {
            return try {
                LoadedKlass(
                    this,
                    Class.forName(name, false, classLoader)
                ).also {
                    klasses[name] = it
                }
            } catch (e: Throwable) {
                DefaultKlass(
                    name
                )
            }
        }

        override fun toString(): String {
            return "classpath: $classpath"
        }

    }

    internal class DefaultKlass(name: String) :
        Klass {

        override val qualifiedName: String = name

        override fun isAssignableFrom(type: String) = false

        override fun isAssignableFrom(klass: Klass) = klass.qualifiedName == this.qualifiedName

    }

    internal class LoadedKlass(val pool: KlassPoolImpl, val clazz: Class<out Any>) :
        Klass {

        override val qualifiedName: String = clazz.name

        override fun isAssignableFrom(type: String) =
            isAssignableFrom(
                pool.findClass(
                    normalize(
                        type
                    )
                )
            )

        override fun isAssignableFrom(klass: Klass) =
            klass is LoadedKlass && clazz.isAssignableFrom(klass.clazz)
    }

}

private fun normalize(type: String) = if (type.contains('/')) {
    type.replace('/', '.')
} else {
    type
}
