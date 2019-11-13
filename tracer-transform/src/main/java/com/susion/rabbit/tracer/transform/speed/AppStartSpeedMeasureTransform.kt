package com.susion.rabbit.tracer.transform.speed

import com.google.auto.service.AutoService
import com.susion.rabbit.tracer.AppStartTracer
import com.susion.rabbit.tracer.transform.core.RabbitClassTransformer
import com.susion.rabbit.tracer.transform.core.context.ArtifactManager
import com.susion.rabbit.tracer.transform.core.context.TransformContext
import com.susion.rabbit.tracer.transform.core.rxentension.className
import com.susion.rabbit.tracer.transform.core.rxentension.findAll
import com.susion.rabbit.tracer.transform.utils.ComponentHandler
import com.susion.rabbit.tracer.transform.utils.RabbitTransformPrinter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*
import javax.xml.parsers.SAXParserFactory

/**
 * susionwang at 2019-11-12
 *
 * 应用启动测速
 *
 * start time : Application Construct
 * end time : onCreate() end
 */
@AutoService(RabbitClassTransformer::class)
class AppStartSpeedMeasureTransform : RabbitClassTransformer {

    override fun onPostTransform(context: TransformContext) {

    }

    private val applications = mutableSetOf<String>()

    override fun onPreTransform(context: TransformContext) {
        val parser = SAXParserFactory.newInstance().newSAXParser()
        context.artifacts.get(ArtifactManager.MERGED_MANIFESTS).forEach { manifest ->
            val handler = ComponentHandler()
            parser.parse(manifest, handler)
            applications.addAll(handler.applications)
        }

    }

    override fun transform(context: TransformContext, klass: ClassNode): ClassNode {

        if (!this.applications.contains(klass.className)) {
            return klass
        }

        val defaultConstructName = "onCreate()V"

        val construct = klass.methods?.find {
            "${it.name}${it.desc}" == defaultConstructName
        } ?: klass.defaultOnCreate.also {
            klass.methods.add(it)
        }

        construct.instructions?.findAll(Opcodes.RETURN, Opcodes.ATHROW)?.forEach {

            RabbitTransformPrinter.p("insert code to  ${construct.name} --- $it")

            construct.instructions?.insertBefore(
                it,
                MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    AppStartTracer.CLASS_PATH,
                    AppStartTracer.METHOD_RECORD_APP_START_TIME,
                    "()V",
                    false
                )
            )
        }

        return klass

    }

}

//默认构造函数
private val ClassNode.defaultInit: MethodNode
    get() = MethodNode(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null).apply {
        maxStack = 1
        instructions.add(InsnList().apply {
            add(VarInsnNode(Opcodes.ALOAD, 0))
            add(MethodInsnNode(Opcodes.INVOKESPECIAL, superName, name, desc, false))
            add(InsnNode(Opcodes.RETURN))
        })
    }

private val ClassNode.defaultOnCreate: MethodNode
    get() = MethodNode(Opcodes.ACC_PUBLIC, "onCreate", "()V", null, null).apply {
        instructions.add(InsnList().apply {
            add(VarInsnNode(Opcodes.ALOAD, 0))
            add(MethodInsnNode(Opcodes.INVOKESPECIAL, superName, name, desc, false))
            add(InsnNode(Opcodes.RETURN))
        })
        maxStack = 1
    }