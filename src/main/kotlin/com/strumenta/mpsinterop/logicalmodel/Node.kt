package com.strumenta.mpsinterop.logicalmodel

import java.util.*
import kotlin.collections.HashMap

open class Node(val concept: Concept, val nodeId: NodeId?) {

    private var _parent: Node? = null
    var parent: Node?
        get() = _parent
        set(value) {
            if (value != null) {
                require(value.hasChild(this))
            }
            if (_parent != null) {
                _parent!!.removeChild(this)
            }
            _parent = value
        }

    private fun removeChild(child: Node) {
        for (entry in childrenMap) {
            if (entry.value.contains(child)) {
                entry.value.remove(child)
            }
        }
    }

    private fun hasChild(child: Node): Boolean {
        for (entry in childrenMap) {
            if (entry.value.contains(child)) {
                return true
            }
        }
        return false
    }

    val root: Boolean
        get() = parent == null
    val numberOfChildren: Int
        get() = childrenMap.values.fold(0) { acc, mutableList -> acc + mutableList.size }
    val numberOfProperties: Int
        get() = properties.size
    val children: List<Node>
        get() = childrenMap.entries.sortedBy { it.key.name }.foldRight(LinkedList()) {
            l, acc ->
                acc.addAll(l.value)
                acc
        }

    private val childrenMap = HashMap<ContainmentLink, MutableList<Node>>()
    private val properties = HashMap<Property, String?>()

    fun addChild(link: ContainmentLink, node: Node) {
        node.parent = this
        childrenMap.computeIfAbsent(link) {
            LinkedList()
        }.add(node)
    }

    fun setProperty(id: Property, value: String?) {
        properties[id] = value
    }

    fun propertyValue(name: String): String {
        return properties[property(name)] ?: "No value for property $name"
    }

    fun property(name: String): Property {
        return properties.keys.find { it.name == name } ?: concept.findProperty(name)
    }

    val name: String?
        get() = this.properties.entries.firstOrNull { it.key.name == "name" }?.value

    override fun toString(): String {
        return "Node $name [${concept.name}] ($nodeId)"
    }

    fun booleanPropertyValue(name: String): Boolean {
        val p = properties.keys.find { it.name == name }
        return if (p == null) {
            false
        } else {
            properties[p]!!.toBoolean()
        }
    }
}
