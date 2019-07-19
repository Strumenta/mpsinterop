package com.strumenta.mpsinterop.logicalmodel

import com.strumenta.mpsinterop.utils.JavaFriendlyBase64
import java.util.*
import kotlin.collections.HashMap

abstract class SNodeId {
    abstract fun toBase64(): String

    companion object {
        fun regular(idValue: Long): SNodeId {
            return RegularSNodeId(idValue)
        }
    }
}

internal data class RegularSNodeId(val value: Long) : SNodeId() {
    override fun toBase64() = JavaFriendlyBase64.toString(value)
}

class InterfaceSNode(concept: SConcept, nodeId: SNodeId?) : SNode(concept, nodeId) {
    fun skipRole(link: SContainmentLink) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}


open class SNode(val concept: SConcept, val nodeId: SNodeId?) {

    val root: Boolean
        get() = TODO()
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

    fun propertyValue(name: String): String {
        return properties[property(name)] ?: "No value for property $name"
    }

    fun property(name: String): SProperty {
        return properties.keys.find { it.name == name } ?: concept.findProperty(name)
    }

    val name : String?
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

