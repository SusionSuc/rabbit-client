package com.susion.rabbit.transform.core

import com.susion.rabbit.transform.core.context.TransformContext

/**
 * Represents bytecode transformer
 */
interface RabbitByteCodeTransformer : TransformListener {

    /**
     * Returns the transformed bytecode
     *
     * @param context
     *         The transforming context
     * @param bytecode
     *         The bytecode to be transformed
     * @return the transformed bytecode
     */
    fun transform(context: TransformContext, bytecode: ByteArray): ByteArray

}
