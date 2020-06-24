package com.susion.rabbit.gradle.transform.visitor

import com.susion.rabbit.tracer.MethodTracer
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter


/**
 * susionwang at 2020-01-14
 */

class MethodCostMethodVisitor(
    api: Int,
    methodVisitor: MethodVisitor?,
    access: Int,
    name: String?,
    descriptor: String?,
    val methodNameParams: String
) : AdviceAdapter(api, methodVisitor, access, name, descriptor) {

    override fun onMethodEnter() {
        super.onMethodEnter()
        mv.visitMethodInsn(
            Opcodes.INVOKESTATIC, MethodTracer.CLASS_PATH,
            MethodTracer.METHOD_RECORD_METHOD_START,
            "()V",
            false
        )
    }

    override fun onMethodExit(opcode: Int) {
        mv.visitLdcInsn(methodNameParams)
        mv.visitMethodInsn(
            Opcodes.INVOKESTATIC, MethodTracer.CLASS_PATH,
            MethodTracer.METHOD_RECORD_METHOD_END,
            MethodTracer.METHOD_RECORD_METHOD_END_PARAMS,
            false
        )
    }



}