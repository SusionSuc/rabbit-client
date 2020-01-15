package com.susion.rabbit.gradle.core

import com.susion.rabbit.gradle.core.context.TransformContext


/**
 * Represents bytecode transformer
 */
interface RabbitClassByteCodeTransformer : TransformListener {

    /**
     * Returns the transformed bytecode
     *
     * @param context
     *         The transforming context
     * @param bytecode
     *         The bytecode to be transformed
     * @return the transformed bytecode
     */
    fun transform(context: TransformContext, bytecode: ByteArray, classFilePath: String): ByteArray

}
