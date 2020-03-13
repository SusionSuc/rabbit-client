package com.susion.rabbit.gradle.transform.visitor

import com.susion.rabbit.gradle.GlobalConfig
import com.susion.rabbit.gradle.utils.RabbitTransformUtils
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * susionwang at 2020-01-14
 */
class MethodCostClassVisitor(api: Int, cv: ClassVisitor) : ClassVisitor(api, cv) {

    private val notTraceMethods = listOf("<init>", "<clinit>")
    private var className: String = ""
    private var isInConfigPkgList = false

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)

        this.className = name?.replace("/", ".") ?: ""

        isInConfigPkgList = RabbitTransformUtils.classInPkgList(
            className,
            GlobalConfig.pluginConfig.methodMonitorPkgs
        )

    }

    override fun visitMethod(
        access: Int,
        name: String?,
        desc: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {

        val isUnImplMethod = access and Opcodes.ACC_ABSTRACT > 0    //未实现的方法

        return if (notTraceMethods.contains(name) || isUnImplMethod || !isInConfigPkgList) {
            super.visitMethod(access, name, desc, signature, exceptions)
        } else {
            val methodName = "$className&$name()"
            RabbitTransformUtils.print("MethodCostClassVisitor -> className : $className  methodName-> $methodName")
            val mv = cv.visitMethod(access, name, desc, signature, exceptions)
            MethodCostMethodVisitor(
                api,
                mv,
                access,
                name,
                desc,
                methodName
            )
        }
    }

}