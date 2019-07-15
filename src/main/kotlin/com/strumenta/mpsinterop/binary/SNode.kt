package com.strumenta.mpsinterop.binary

import java.util.*
import kotlin.collections.HashMap

open class SNode(val concept: SConcept, val nodeId: SNodeId?) {
    val numberOfProperties: Int
        get() = properties.size
    private val children = HashMap<SContainmentLink, MutableList<SNode>>()
    private val properties = HashMap<SProperty, String>()

    fun addChild(link: SContainmentLink, node: SNode) {
        children.computeIfAbsent(link) {
            LinkedList<SNode>()
        }.add(node)
    }

    fun setProperty(id: SProperty, value: String) {
        properties[id] = value
    }

    val name : String?
        get() = this.properties.entries.first { it.key.propertyName == "name" }?.value
}

