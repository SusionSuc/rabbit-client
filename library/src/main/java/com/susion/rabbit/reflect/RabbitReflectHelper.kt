package com.susion.rabbit.reflect

/**
 * susionwang at 2019-10-17
 */

object RabbitReflectHelper {

    fun <T> getObjectField(instance: Any, name: String): T? {
        try {
            val field = instance.javaClass.getDeclaredField(name)
            field.isAccessible = true
            return field.get(instance) as T
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

}