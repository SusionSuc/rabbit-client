package com.susion.rabbit.gradle.transform

import com.susion.rabbit.gradle.GlobalConfig
import com.susion.rabbit.gradle.core.RabbitAsmClassVisitorTransformer
import com.susion.rabbit.gradle.core.context.TransformContext
import com.susion.rabbit.gradle.core.rxentension.asIterable
import com.susion.rabbit.gradle.core.rxentension.className
import com.susion.rabbit.gradle.utils.DEFAULT_BLOCK_APIS
import com.susion.rabbit.gradle.utils.RabbitTransformUtils
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodInsnNode

/**
 * susionwang at 2020-01-02
 * 扫描 IO 函数
 */
class BlockCodeScanTransform : RabbitAsmClassVisitorTransformer {

    private val blockApis: Set<String> by lazy {
        if (GlobalConfig.pluginConfig.customBlockCodeCheckList.isNotEmpty()) {
            GlobalConfig.pluginConfig.customBlockCodeCheckList
        } else {
            DEFAULT_BLOCK_APIS
        }
    }

    override fun transform(
        context: TransformContext,
        klass: ClassNode,
        classFilePath: String
    ): ClassNode {

        if (!GlobalConfig.pluginConfig.enableBlockCodeCheck) {
            return klass
        }

        if (!RabbitTransformUtils.classInPkgList(
                klass.className,
                GlobalConfig.pluginConfig.blockCodePkgs
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

                    if (blockApis.any { it.contains(calledStr) }) {

                        val ioCallStr =
                            "${invokeStr.replace("/", ".")}()&${calledStr.replace("/", ".")}()"

                        GlobalConfig.ioMethodCall.add(ioCallStr)

                        RabbitTransformUtils.print("💻  BlockCodeScanTransform : scan io method -> $ioCallStr")
                    }
                }

        }
    }

}