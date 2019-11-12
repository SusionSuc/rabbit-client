package com.susion.rabbit.transform.core

import com.google.auto.service.AutoService
import com.susion.rabbit.transform.core.context.TransformContext
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.ClassNode
import java.util.*

/**
 * Represents bytecode transformer using ASM
 * 使用 ASM 操作字节码
 */
@AutoService(RabbitByteCodeTransformer::class)
class AsmTransformer : RabbitByteCodeTransformer {

    private val transformers = ServiceLoader.load(RabbitClassTransformer::class.java, javaClass.classLoader).toList()

    override fun transform(context: TransformContext, bytecode: ByteArray): ByteArray {
        return ClassWriter(ClassWriter.COMPUTE_MAXS).also { writer ->
            transformers.fold(ClassNode().also { klass ->
                ClassReader(bytecode).accept(klass, 0)
            }) { klass, transformer ->
                transformer.transform(context, klass)
            }.accept(writer)
        }.toByteArray()
    }

    override fun onPreTransform(context: TransformContext) {
        transformers.forEach {
            it.onPreTransform(context)
        }
    }

    override fun onPostTransform(context: TransformContext) {
        transformers.forEach {
            it.onPostTransform(context)
        }
    }

}
