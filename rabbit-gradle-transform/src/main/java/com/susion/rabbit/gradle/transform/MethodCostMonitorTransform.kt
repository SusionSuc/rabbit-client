package com.susion.rabbit.gradle.transform

import com.susion.rabbit.gradle.GlobalConfig
import com.susion.rabbit.gradle.core.RabbitAsmByteArrayTransformer
import com.susion.rabbit.gradle.core.context.TransformContext
import com.susion.rabbit.gradle.transform.visitor.MethodCostClassVisitor
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassReader.EXPAND_FRAMES
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

/**
 * susionwang at 2020-01-15
 */
class MethodCostMonitorTransform : RabbitAsmByteArrayTransformer {

    override fun transform(
        context: TransformContext,
        bytes: ByteArray,
        classFilePath: String
    ): ByteArray {

        if (!GlobalConfig.pluginConfig.enableMethodCostCheck) {
            return bytes
        }

        val classReader = ClassReader(bytes)
        val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
        val classVisitor = MethodCostClassVisitor(
            Opcodes.ASM6,
            classWriter
        )
        classReader.accept(classVisitor, EXPAND_FRAMES)
        return classWriter.toByteArray()
    }

}