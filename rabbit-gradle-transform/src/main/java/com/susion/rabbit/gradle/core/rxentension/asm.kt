package com.susion.rabbit.gradle.core.rxentension

import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.InsnList

/**
 * susionwang at 2019-11-13
 */

val ClassNode.className: String
    get() = name.replace('/', '.')


/**
 * Find the first instruction node with the specified opcode
 *
 * @param opcode The opcode to search
 */
fun InsnList.find(opcode: Int): AbstractInsnNode? {
    return this.iterator().asIterable().find {
        it.opcode == opcode
    }
}

/**
 * Find all of instruction nodes with the specified opcode
 *
 * @param opcodes The opcode to search
 */
fun InsnList.findAll(vararg opcodes: Int): Collection<AbstractInsnNode> {
    return this.iterator().asIterable().filter {
        it.opcode in opcodes
    }.toList()
}
