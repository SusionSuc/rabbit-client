package com.susion.rabbit.gradle.core

import com.susion.rabbit.gradle.core.context.TransformContext
import kotlin.collections.ArrayList

/**
 * Represents bytecode transformer using ASM
 * 使用 ASM 操作字节码
 */
class AsmByteArrayTransformer(private val transformers: List<RabbitAsmByteArrayTransformer> = ArrayList()) :
    RabbitClassByteCodeTransformer {

    override fun transform(
        context: TransformContext,
        bytecode: ByteArray,
        classFilePath: String
    ): ByteArray {
        return transformers.fold(bytecode) { classNode, transformer ->
            transformer.transform(context, classNode, classFilePath)
        }
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
