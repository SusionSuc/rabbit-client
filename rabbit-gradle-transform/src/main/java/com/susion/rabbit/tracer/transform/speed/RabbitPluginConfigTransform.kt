package com.susion.rabbit.tracer.transform.speed

import com.google.auto.service.AutoService
import com.susion.rabbit.tracer.RabbitPluginConfig
import com.susion.rabbit.tracer.transform.GlobalConfig
import com.susion.rabbit.tracer.transform.core.RabbitClassTransformer
import com.susion.rabbit.tracer.transform.core.context.TransformContext
import com.susion.rabbit.tracer.transform.core.rxentension.find
import com.susion.rabbit.tracer.transform.utils.RabbitTransformUtils
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.MethodInsnNode

/**
 * susionwang at 2020-01-02
 */
@AutoService(RabbitClassTransformer::class)
class RabbitPluginConfigTransform : RabbitClassTransformer {

    override fun transform(context: TransformContext, klass: ClassNode): ClassNode {

        if (klass.name != RabbitPluginConfig.CLASS_PATH) {
            return klass
        }

        loadMethodPkgConfig(klass)

        return klass
    }

    private fun loadMethodPkgConfig(klass: ClassNode) {
        val method = klass.methods.find { it.name == RabbitPluginConfig.METHOD_INJECT_METHOD_PKGS }
        RabbitTransformUtils.print("config name :${klass.name}   injectMethodPkgsMethod : ${method?.name}")
        RabbitTransformUtils.print(" GlobalConfig.methodMonitorPkgs size : ${GlobalConfig.methodMonitorPkgs.size}")
        method?.instructions?.find(Opcodes.RETURN)?.apply {
            GlobalConfig.methodMonitorPkgs.forEach {name->
                RabbitTransformUtils.print("🍎 RabbitPluginConfigTransform  add method pkg : $name")
//                method.instructions?.insertBefore(this, VarInsnNode(Opcodes.ALOAD, 0))   调静态方法不需要这玩意， 这个是this
                method.instructions?.insertBefore(this, LdcInsnNode(name))
                method.instructions?.insertBefore(this, getAddMethodPkgMethod())
            }
        }
    }

    private fun getAddMethodPkgMethod():MethodInsnNode{
        return MethodInsnNode(
            Opcodes.INVOKESTATIC,
            RabbitPluginConfig.CLASS_PATH,
            RabbitPluginConfig.METHOD_ADD_PKG_TO_METHOD_PKGS,
            RabbitPluginConfig.METHOD_ADD_PKG_TO_METHOD_PKGS_PARAMS,
            false
        )
    }

}