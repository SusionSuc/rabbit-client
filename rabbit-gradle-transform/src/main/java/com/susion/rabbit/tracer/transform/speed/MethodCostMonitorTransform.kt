package com.susion.rabbit.tracer.transform.speed

import com.google.auto.service.AutoService
import com.susion.rabbit.tracer.MethodTracer
import com.susion.rabbit.tracer.transform.GlobalConfig
import com.susion.rabbit.tracer.transform.core.RabbitClassTransformer
import com.susion.rabbit.tracer.transform.core.context.TransformContext
import com.susion.rabbit.tracer.transform.core.rxentension.className
import com.susion.rabbit.tracer.transform.core.rxentension.find
import com.susion.rabbit.tracer.transform.utils.RabbitTransformUtils
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.VarInsnNode

/**
 * susionwang at 2020-01-02
 */
@AutoService(RabbitClassTransformer::class)
class MethodCostMonitorTransform : RabbitClassTransformer {

    private val notTraceMethods = listOf("<init>", "<clinit>")

    override fun transform(context: TransformContext, klass: ClassNode): ClassNode {

        if (!RabbitTransformUtils.classInPkgList(klass.className, GlobalConfig.methodMonitorPkgs)) {
            return klass
        }

        insertMethodTraceCode(klass)

        return klass
    }

    private fun insertMethodTraceCode(klass: ClassNode) {

        for (method in klass.methods) {
            //超两行的函数才统计
            if (notTraceMethods.contains(method.name) && method.instructions.size() > 2) {
                continue
            }

            val methodName = "${klass.name.replace("/", ".")}&${method.name}()"
            val isStaticMethod = (method.access and Opcodes.ACC_STATIC )== Opcodes.ACC_STATIC
            method.instructions?.find(Opcodes.RETURN)?.apply {
                if (!isStaticMethod){
                    method.instructions?.insertBefore(this, VarInsnNode(Opcodes.ALOAD, 0))
                }
                method.instructions?.insertBefore(this, LdcInsnNode(methodName)) //参数
                method.instructions?.insertBefore(this, getMethodRecordEndMethod())
            }

            method.instructions?.find(Opcodes.ALOAD)?.apply {
                method.instructions?.insertBefore(this, getMethodRecordStartMethod())
            }

            RabbitTransformUtils.print("MethodCostMonitorTransform -> trace method $methodName")
        }
    }


    private fun getMethodRecordStartMethod(): MethodInsnNode {
        return MethodInsnNode(
            Opcodes.INVOKESTATIC,
            MethodTracer.CLASS_PATH,
            MethodTracer.METHOD_RECORD_METHOD_START,
            "()V",
            false
        )
    }

    private fun getMethodRecordEndMethod(): MethodInsnNode {
        return MethodInsnNode(
            Opcodes.INVOKESTATIC,
            MethodTracer.CLASS_PATH,
            MethodTracer.METHOD_RECORD_METHOD_END,
            MethodTracer.METHOD_RECORD_METHOD_END_PARAMS,
            false
        )
    }

}