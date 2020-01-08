package com.susion.rabbit.gradle.transform

import com.susion.rabbit.gradle.GlobalConfig
import com.susion.rabbit.gradle.core.RabbitClassTransformer
import com.susion.rabbit.gradle.core.context.TransformContext
import com.susion.rabbit.gradle.core.rxentension.asIterable
import com.susion.rabbit.gradle.core.rxentension.className
import com.susion.rabbit.gradle.utils.IO_APIS
import com.susion.rabbit.gradle.utils.RabbitTransformUtils
import com.susion.rabbit.tracer.RabbitScanIoOpHelper
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodInsnNode

/**
 * susionwang at 2020-01-02
 * 扫描 IO 函数
 */
class IoMethodScanTransform : RabbitClassTransformer {

    override fun transform(
        context: TransformContext,
        klass: ClassNode,
        classFilePath: String
    ): ClassNode {

        if (!RabbitTransformUtils.classInPkgList(
                klass.className,
                GlobalConfig.pluginConfig.ioScanPkgs
            )
        ) {
            return klass
        }

        scanIoMethod(klass)

        return klass
    }

    private fun scanIoMethod(klass: ClassNode) {

        klass.methods.forEach { method ->

            val invokeStr = "${klass.name}.${method.name}"

            method.instructions.iterator().asIterable().filterIsInstance(MethodInsnNode::class.java)
                .forEach { invoke ->

                    val calledStr = "${invoke.owner}.${invoke.name}"

                    if (IO_APIS.any { it.contains(calledStr) }) {

                        val ioCallStr = "${invokeStr.replace("/", ".")}()&${calledStr.replace("/", ".")}()"

                        GlobalConfig.ioMethodCall.add(ioCallStr)

                        RabbitTransformUtils.print("💻 🍎 IoMethodScanTransform : scan io method -> $ioCallStr")
                    }
                }

        }
    }

}