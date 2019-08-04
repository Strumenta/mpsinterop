package com.strumenta.mpsinterop.logicalmodel

import java.util.*
import kotlin.collections.HashMap

open class Node(val concept: Concept, val nodeId: NodeId?) {

    private val childrenMap = HashMap<ContainmentLink, MutableList<Node>>()
    private val referencesMap = HashMap<ReferenceLink, Node>()
    private val properties = HashMap<Property, String?>()

    // //////////////////////////////////////////
    // Root and model
    // //////////////////////////////////////////

    val isRoot: Boolean
        get() = parent == null

    internal var modelOfWhichIsRoot: Model? = null
    val model: Model?
        get() = if (isRoot) modelOfWhichIsRoot else parent!!.model

    // //////////////////////////////////////////
    // Parent
    // //////////////////////////////////////////

    private var _parent: Node? = null
    var parent: Node?
        get() = _parent
        set(value) {
            if (value != null) {
                // We cannot set the parent directly because we need to know the containment link
                require(value.hasChild(this))
            }
            if (_parent != null) {
                _parent!!.removeChild(this)
            }
            _parent = value
        }

    // //////////////////////////////////////////
    // Children
    // //////////////////////////////////////////

    val children: List<Node>
        get() = childrenMap.entries.sortedBy { it.key.name }.foldRight(LinkedList()) {
            l, acc ->
            acc.addAll(l.value)
            acc
        }

    val numberOfChildren: Int
        get() = childrenMap.values.fold(0) { acc, mutableList -> acc + mutableList.size }

    fun addChild(link: ContainmentLink, node: Node) {
        require(concept.hasLink(link)) { "Link unknown: $link" }
        childrenMap.computeIfAbsent(link) {
            LinkedList()
        }.add(node)
        node.parent = this
    }

    fun getChildren(link: ContainmentLink) : List<Node> {
        require(concept.hasLink(link)) { "Link unknown: $link" }
        return childrenMap[link] ?: emptyList()
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

    // //////////////////////////////////////////
    // References
    // //////////////////////////////////////////

    fun setReference(link: ReferenceLink, node: Node?) {
        require(concept.hasLink(link)) { "Link unknown: $link" }
        if (node == null) {
            referencesMap.remove(link)
        } else {
            referencesMap[link] = node
        }
    }

    fun getReference(link: ReferenceLink) : Node? {
        require(concept.hasLink(link)) { "Link unknown: $link" }
        return referencesMap[link]
    }

    // //////////////////////////////////////////
    // Properties
    // //////////////////////////////////////////

    val numberOfProperties: Int
        get() = properties.size

    fun setProperty(property: Property, value: String?) {
        require(concept.hasProperty(property)) { "Link unknown: $property" }
        properties[property] = value
    }

    fun propertyValue(name: String): String {
        return properties[property(name)] ?: "No value for property $name"
    }

    fun property(name: String): Property {
        return properties.keys.find { it.name == name } ?: concept.findProperty(name)!!
    }

    fun booleanPropertyValue(name: String): Boolean {
        val p = properties.keys.find { it.name == name }
        return if (p == null) {
            false
        } else {
            properties[p]!!.toBoolean()
        }
    }

    // //////////////////////////////////////////
    // Naming
    // //////////////////////////////////////////

    val name: String?
        get() = this.properties.entries.firstOrNull { it.key.name == "name" }?.value

    // //////////////////////////////////////////
    // Misc
    // //////////////////////////////////////////

    override fun toString(): String {
        return "Node $name [${concept.name}] ($nodeId)"
    }
}
