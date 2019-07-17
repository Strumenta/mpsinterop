package com.strumenta.mpsinterop.binary

import java.util.*
import kotlin.collections.HashMap

open class SNode(val concept: SConcept, val nodeId: SNodeId?) {
    val numberOfChildren: Int
        get() = childrenMap.values.fold(0) { acc, mutableList -> acc + mutableList.size  }
    val numberOfProperties: Int
        get() = properties.size
    val children : List<SNode>
        get() = childrenMap.entries.sortedBy { it.key.name }.foldRight(LinkedList()) {
            l, acc ->
                acc.addAll(l.value)
                acc
        }

    private val childrenMap = HashMap<SContainmentLink, MutableList<SNode>>()
    private val properties = HashMap<SProperty, String>()

    fun addChild(link: SContainmentLink, node: SNode) {
        childrenMap.computeIfAbsent(link) {
            LinkedList<SNode>()
        }.add(node)
    }

    fun setProperty(id: SProperty, value: String) {
        properties[id] = value
    }

//    fun property(name: String): SProperty {
//        properties.keys.find { it.name == name }!!
//    }

    val name : String?
        get() = this.properties.entries.first { it.key.propertyName == "name" }?.value
}

