package com.strumenta.mpsinterop.logicalmodel

import com.strumenta.mpsinterop.utils.JavaFriendlyBase64
import java.util.*
import kotlin.collections.HashMap

abstract class SNodeId {
    abstract fun toBase64(): String
    abstract fun toLong(): Long

    companion object {
        fun regular(idValue: Long): SNodeId {
            return RegularSNodeId(idValue)
        }

        fun fromString(s: String): SNodeId {
            return StringSNodeId(s)
        }
    }
}

internal data class StringSNodeId(val value: String) : SNodeId() {
    override fun toBase64(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun toLong(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

internal data class RegularSNodeId(val value: Long) : SNodeId() {
    override fun toBase64() = JavaFriendlyBase64.toString(value)
    override fun toLong() = value
}

class InterfaceSNode(concept: Concept, nodeId: SNodeId?) : SNode(concept, nodeId) {
    fun skipRole(link: ContainmentLink) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}


open class SNode(val concept: Concept, val nodeId: SNodeId?) {

    private var _parent: SNode? = null
    var parent: SNode?
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

    private fun removeChild(child: SNode) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun hasChild(child: SNode): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    val root: Boolean
        get() = parent == null
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

    private val childrenMap = HashMap<ContainmentLink, MutableList<SNode>>()
    private val properties = HashMap<Property, String?>()

    fun addChild(link: ContainmentLink, node: SNode) {
        node.parent = this
        childrenMap.computeIfAbsent(link) {
            LinkedList<SNode>()
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

