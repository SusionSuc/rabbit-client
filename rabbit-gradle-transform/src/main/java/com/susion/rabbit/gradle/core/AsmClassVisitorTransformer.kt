package com.susion.rabbit.gradle.core

import com.susion.rabbit.gradle.core.context.TransformContext
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.ClassNode
import kotlin.collections.ArrayList

/**
 * Represents bytecode transformer using ASM
 * 使用 ASM 操作字节码
 */
class AsmClassVisitorTransformer(private val transformers:List<RabbitAsmClassVisitorTransformer> = ArrayList()) : RabbitClassByteCodeTransformer {

    override fun transform(context: TransformContext, bytecode: ByteArray, classFilePath:String): ByteArray {
        return ClassWriter(ClassWriter.COMPUTE_MAXS).also { writer ->
            transformers.fold(ClassNode().also { visitClassNode ->
                ClassReader(bytecode).accept(visitClassNode, 0)
            }) { classNode, transformer ->
                transformer.transform(context, classNode,classFilePath)
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
