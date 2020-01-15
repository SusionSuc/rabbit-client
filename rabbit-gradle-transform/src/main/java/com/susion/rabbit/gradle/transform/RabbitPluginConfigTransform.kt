package com.susion.rabbit.gradle.transform

import com.susion.rabbit.tracer.RabbitPluginConfig
import com.susion.rabbit.gradle.GlobalConfig
import com.susion.rabbit.gradle.core.RabbitAsmClassVisitorTransformer
import com.susion.rabbit.gradle.core.context.TransformContext
import com.susion.rabbit.gradle.core.rxentension.find
import com.susion.rabbit.gradle.utils.RabbitTransformUtils
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.MethodInsnNode

/**
 * susionwang at 2020-01-02
 */
class RabbitPluginConfigTransform : RabbitAsmClassVisitorTransformer {

    override fun transform(context: TransformContext, klass: ClassNode,classFilePath:String): ClassNode {

        if (klass.name != RabbitPluginConfig.CLASS_PATH) {
            return klass
        }

        loadMethodPkgConfig(klass)

        return klass
    }

    private fun loadMethodPkgConfig(klass: ClassNode) {
        val method = klass.methods.find { it.name == RabbitPluginConfig.METHOD_INJECT_METHOD_PKGS }
        method?.instructions?.find(Opcodes.RETURN)?.apply {
            GlobalConfig.pluginConfig.methodMonitorPkgs.forEach { name ->
                RabbitTransformUtils.print("RabbitPluginConfigTransform:  add method pkg : $name")
//                method.instructions?.insertBefore(this, VarInsnNode(Opcodes.ALOAD, 0))   调静态方法不需要这玩意， 这个是this
                method.instructions?.insertBefore(this, LdcInsnNode(name))
                method.instructions?.insertBefore(this, getAddMethodPkgMethod())
            }
        }
    }

    private fun getAddMethodPkgMethod(): MethodInsnNode {
        return MethodInsnNode(
            Opcodes.INVOKESTATIC,
            RabbitPluginConfig.CLASS_PATH,
            RabbitPluginConfig.METHOD_ADD_PKG_TO_METHOD_PKGS,
            RabbitPluginConfig.METHOD_ADD_PKG_TO_METHOD_PKGS_PARAMS,
            false
        )
    }

}