package com.susion.rabbit.transform.core

import com.susion.rabbit.transform.core.context.TransformContext
import org.objectweb.asm.tree.ClassNode

/**
 * Represents class transformer
 *
 * @author johnsonlee
 */
interface RabbitClassTransformer : TransformListener {

    /**
     * Transform the specified class node
     *
     * @param context The transform context
     * @param klass The class node to be transformed
     * @return The transformed class node
     */
    fun transform(context: TransformContext, klass: ClassNode) = klass

}
