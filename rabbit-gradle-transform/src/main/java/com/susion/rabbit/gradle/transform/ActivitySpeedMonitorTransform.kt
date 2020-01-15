package com.susion.rabbit.gradle.transform

import com.susion.rabbit.gradle.GlobalConfig
import com.susion.rabbit.gradle.core.RabbitAsmClassVisitorTransformer
import com.susion.rabbit.gradle.core.context.ArtifactManager
import com.susion.rabbit.gradle.core.context.TransformContext
import com.susion.rabbit.gradle.core.rxentension.className
import com.susion.rabbit.gradle.core.rxentension.find
import com.susion.rabbit.gradle.utils.ComponentHandler
import com.susion.rabbit.gradle.utils.RabbitTransformUtils
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*
import javax.xml.parsers.SAXParserFactory

/**
 * susionwang at 2019-11-15
 * 在onCreate方法运行完毕时插入监控代码
 */
class ActivitySpeedMonitorTransform : RabbitAsmClassVisitorTransformer {

    private val ACTIVITY_SPEED_MONITOR_CLASS =
        "com/susion/rabbit/monitor/instance/ActivitySpeedMonitor"

    //ActivitySpeedMonitor.wrapperViewOnActivityCreateEnd()
    private val wapperMethodName = "wrapperViewOnActivityCreateEnd"
    private val wapperMethodDesc = "(Landroid/app/Activity;)V"

    //ActivitySpeedMonitor.activityCreateStart()
    private val acCreateStartName = "activityCreateStart"
    private val acCreateStartDesc = "(Landroid/app/Activity;)V"

    //ActivitySpeedMonitor.activityResumeEnd()
    private val acResumeEndName = "activityResumeEnd"
    private val acResumeEndDesc = "(Landroid/app/Activity;)V"

    //activity onCreate 方法
    private val METHOD_ON_CREATE_NAME = "onCreate"
    private val METHOD_ONCREATE_DESC = "(Landroid/os/Bundle;)V"

    //activity onResume 方法
    private val METHOD_ON_RESUME_NAME = "onResume"
    private val METHOD_ON_RESUME_DESC = "()V"

    private val activityList = mutableSetOf<String>()

    override fun onPreTransform(context: TransformContext) {
        val parser = SAXParserFactory.newInstance().newSAXParser()
        context.artifacts.get(ArtifactManager.MERGED_MANIFESTS).forEach { manifest ->
            val handler = ComponentHandler()
            parser.parse(manifest, handler)
            activityList.addAll(handler.activities)
        }
    }

    override fun transform(context: TransformContext, klass: ClassNode,classFilePath:String): ClassNode {

        if (!GlobalConfig.pluginConfig.enableSpeedCheck){
            return klass
        }

        if (!activityList.contains(klass.className) || !RabbitTransformUtils.classInPkgList(klass.className, GlobalConfig.pluginConfig.pageSpeedMonitorPkgs)) {
            return klass
        }

        insertCodeToActivityOnCreate(klass)

        insertCodeToActivityOnResume(klass)

        return klass

    }

    private fun insertCodeToActivityOnCreate(klass: ClassNode) {
        val onCreateMethod = klass.methods?.find {
            "${it.name}${it.desc}" == "$METHOD_ON_CREATE_NAME$METHOD_ONCREATE_DESC"
        } ?: return

        onCreateMethod.instructions?.find(Opcodes.RETURN)?.apply {
            RabbitTransformUtils.print("ActivitySpeedMonitorTransform : insert code to wrap activity content view ---> ${klass.name}")
            onCreateMethod.instructions?.insertBefore(this, VarInsnNode(Opcodes.ALOAD, 0)) //参数
            onCreateMethod.instructions?.insertBefore(this, getWrapSpeedViewMethod())
        }

        onCreateMethod.instructions?.find(Opcodes.ALOAD)?.apply {
            RabbitTransformUtils.print("ActivitySpeedMonitorTransform : insert code to on activity create ---> ${klass.name}")
            onCreateMethod.instructions?.insertBefore(this, VarInsnNode(Opcodes.ALOAD, 0)) //参数
            onCreateMethod.instructions?.insertBefore(this, getAcCreateStateMethod())
        }
    }

    private fun insertCodeToActivityOnResume(klass: ClassNode) {
        var onResumeMethod = klass.methods?.find {
            "${it.name}${it.desc}" == "$METHOD_ON_RESUME_NAME$METHOD_ON_RESUME_DESC"
        }

        if (onResumeMethod == null) {
            onResumeMethod = getDefaultOnResumeMethod(klass)
            klass.methods.add(onResumeMethod)
        }

        onResumeMethod.instructions?.find(Opcodes.RETURN)?.apply {
            RabbitTransformUtils.print("ActivitySpeedMonitorTransform: insert code to  ${onResumeMethod.name} --- ${klass.name}")
            onResumeMethod.instructions?.insertBefore(this, VarInsnNode(Opcodes.ALOAD, 0)) //参数
            onResumeMethod.instructions?.insertBefore(this, getAcResumeStateMethod())
        }
    }

    private fun getAcResumeStateMethod(): MethodInsnNode {
        return MethodInsnNode(
            Opcodes.INVOKESTATIC,
            ACTIVITY_SPEED_MONITOR_CLASS,
            acResumeEndName,
            acResumeEndDesc,
            false
        )
    }

    private fun getWrapSpeedViewMethod() = MethodInsnNode(
        Opcodes.INVOKESTATIC,
        ACTIVITY_SPEED_MONITOR_CLASS,
        wapperMethodName,
        wapperMethodDesc,
        false
    )

    private fun getAcCreateStateMethod() = MethodInsnNode(
        Opcodes.INVOKESTATIC,
        ACTIVITY_SPEED_MONITOR_CLASS,
        acCreateStartName,
        acCreateStartDesc,
        false
    )

    private fun getDefaultOnResumeMethod(klass: ClassNode): MethodNode {
        RabbitTransformUtils.print("ActivitySpeedMonitorTransform: new onResume() Method --> super class name : ${klass.superName}  --->")
        return MethodNode(
            Opcodes.ACC_PROTECTED,
            METHOD_ON_RESUME_NAME,
            METHOD_ON_RESUME_DESC,
            null,
            null
        ).apply {
            instructions.add(InsnList().apply {
                add(VarInsnNode(Opcodes.ALOAD, 0))
                add(
                    MethodInsnNode(
                        Opcodes.INVOKESPECIAL,
                        klass.superName,
                        METHOD_ON_RESUME_NAME,
                        METHOD_ON_RESUME_DESC,
                        false
                    )
                )
                add(InsnNode(Opcodes.RETURN))
            })
            maxStack = 1
        }
    }

}