package com.susion.rabbit.transform.speed

import com.google.auto.service.AutoService
import com.susion.rabbit.transform.core.RabbitByteCodeTransformer
import com.susion.rabbit.transform.core.RabbitClassTransformer
import com.susion.rabbit.transform.core.context.TransformContext
import com.susion.rabbit.transform.utils.RabbitTransformPrinter
import org.objectweb.asm.tree.ClassNode

/**
 * susionwang at 2019-11-12
 *
 * 应用启动测速
 */
@AutoService(RabbitClassTransformer::class)
class AppStartSpeedMeasureTransform : RabbitClassTransformer {

    override fun transform(context: TransformContext, klass: ClassNode): ClassNode {
        RabbitTransformPrinter.p("AppStartSpeedMeasureTransform", "transform class name : ${klass.name}")
        return super.transform(context, klass)

    }

}