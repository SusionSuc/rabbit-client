package com.susion.rabbit.gradle.core

import com.susion.rabbit.gradle.core.context.TransformContext

/**
 * Represents class transformer
 */
interface RabbitAsmByteArrayTransformer : TransformListener {

    /**
     * Transform the specified class node
     *
     * @param context The transform context
     * @param klass The class node to be transformed
     * @return The transformed class node
     */
    fun transform(context: TransformContext, bytes: ByteArray, classFilePath:String ):ByteArray

}
