package com.susion.rabbit.gradle.transform

import com.susion.rabbit.gradle.GlobalConfig
import com.susion.rabbit.gradle.core.RabbitAsmClassVisitorTransformer
import com.susion.rabbit.gradle.core.context.TransformContext
import com.susion.rabbit.gradle.core.rxentension.find
import com.susion.rabbit.gradle.utils.RabbitTransformUtils
import com.susion.rabbit.tracer.RabbitScanIoOpHelper
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.MethodInsnNode

/**
 * susionwang at 2020-01-02
 * 把扫描的IO调用转变为代码
 */
class BlockCodeLoadTransform : RabbitAsmClassVisitorTransformer {

    override fun transform(
        context: TransformContext,
        klass: ClassNode,
        classFilePath: String
    ): ClassNode {

        if (!GlobalConfig.pluginConfig.enableBlockCodeCheck) {
            return klass
        }

        if (klass.name != RabbitScanIoOpHelper.CLASS_PATH) {
            return klass
        }

        RabbitTransformUtils.print("🌞 BlockCodeLoadTransform start load io method call,  scan ${GlobalConfig.ioMethodCall.size} method !")

        val method = klass.methods.find { it.name == RabbitScanIoOpHelper.METHOD_INJECT_IO_CALL }
        method?.instructions?.find(Opcodes.RETURN)?.apply {
            GlobalConfig.ioMethodCall.forEach { ioCall ->
                method.instructions?.insertBefore(this, LdcInsnNode(ioCall))
                method.instructions?.insertBefore(this, getAddIoMethod())
            }
        }

        return klass
    }

    private fun getAddIoMethod(): MethodInsnNode {
        return MethodInsnNode(
            Opcodes.INVOKESTATIC,
            RabbitScanIoOpHelper.CLASS_PATH,
            RabbitScanIoOpHelper.METHOD_ADD_IO_METHOD,
            RabbitScanIoOpHelper.METHOD_ADD_PKG_IO_PARAMS,
            false
        )
    }

}