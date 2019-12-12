package com.susion.rabbit.monitor.utils

import java.lang.reflect.Method

/**
 * susionwang at 2019-10-17
 */

object RabbitReflectHelper {

    fun <T> reflectField(instance: Any?, name: String): T? {
        if (instance == null) return null
        try {
            val field = instance.javaClass.getDeclaredField(name)
            field.isAccessible = true
            return field.get(instance) as T
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }


    fun reflectMethod(instance: Any?, name: String, vararg argTypes: Class<*>): Method? {
        if (instance == null) return null
        try {
            val method = instance.javaClass.getDeclaredMethod(name, *argTypes)
            method.isAccessible = true
            return method
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}