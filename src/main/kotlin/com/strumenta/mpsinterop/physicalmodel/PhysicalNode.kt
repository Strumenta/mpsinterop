package com.strumenta.mpsinterop.physicalmodel

import com.strumenta.mpsinterop.logicalmodel.NodeId
import java.lang.IllegalStateException
import java.lang.UnsupportedOperationException
import java.util.*
import kotlin.collections.HashMap

class PhysicalNode(val parent: PhysicalNode?, val concept: PhysicalConcept, val id: NodeId) {
    private val properties = HashMap<PhysicalProperty, String>()
    private val children = HashMap<PhysicalRelation, MutableList<PhysicalNode>>()
    private val references = HashMap<PhysicalRelation, PhysicalReferenceValue>()

    // //////////////////////////////////////////
    // Root and model
    // //////////////////////////////////////////

    val isRoot: Boolean
        get() = parent == null
    internal var modelOfWhichIsRoot: PhysicalModel? = null
    val model: PhysicalModel?
        get() = if (isRoot) modelOfWhichIsRoot else parent!!.model

    // //////////////////////////////////////////
    // Naming
    // //////////////////////////////////////////

    fun qualifiedName(): String {
        if (isRoot) {
            val model = model ?: throw IllegalStateException("The node must be attached into a model to get a qualified name")
            val name = name() ?: throw IllegalStateException("The node has not simple name")
            return model.name + "." + name
        } else {
            throw UnsupportedOperationException("Only root nodes can have a qualified name")
        }
    }

    // //////////////////////////////////////////
    // Children
    // //////////////////////////////////////////

    fun addChild(relation: PhysicalRelation, node: PhysicalNode) {
        if (relation !in children) {
            children[relation] = LinkedList()
        }
        children[relation]!!.add(node)
    }

    fun children(relation: PhysicalRelation) : List<PhysicalNode> {
        if (relation.kind != RelationKind.CONTAINMENT) {
            throw java.lang.IllegalArgumentException("Containment relation expected")
        }
        return children[relation] ?: emptyList()
    }

    fun children(relationName: String): List<PhysicalNode> {
        val relation = children.keys.find { it.name == relationName }
        return if (relation == null) {
            emptyList()
        } else {
            children[relation]!!
        }
    }

    fun numberOfChildren(relationName: String): Int {
        val rs = children.keys.filter { it.name == relationName }
        return when (rs.size) {
            0 -> 0
            1 -> children[rs[0]]!!.size
            else -> throw IllegalArgumentException("Ambiguous reference name $relationName")
        }
    }

    fun findNodeByID(nodeID: NodeId): PhysicalNode? {
        if (this.id == nodeID) {
            return this
        }
        for (cl in children.values) {
            for (c in cl) {
                val res = c.findNodeByID(nodeID)
                if (res != null) {
                    return res
                }
            }
        }
        return null
    }

    // //////////////////////////////////////////
    // References
    // //////////////////////////////////////////

    fun addReference(relation: PhysicalRelation, node: PhysicalReferenceValue) {
        references[relation] = node
    }

    fun reference(relation: PhysicalRelation): PhysicalReferenceValue? {
        if (relation.kind != RelationKind.REFERENCE) {
            throw java.lang.IllegalArgumentException("Reference relation expected")
        }
        return references[relation]
    }

    fun reference(relationName: String): PhysicalReferenceValue? {
        val rs = references.keys.filter { it.name == relationName }
        return when (rs.size) {
            0 -> null
            1 -> reference(rs.first())
            else -> throw IllegalArgumentException("Ambiguous reference name $relationName")
        }
    }

    // //////////////////////////////////////////
    // Properties
    // //////////////////////////////////////////

    fun addProperty(property: PhysicalProperty, propertyValue: String) {
        properties[property] = propertyValue
    }

    fun propertyValue(property: PhysicalProperty): String? {
        return properties[property]
    }

    fun propertyValue(propertyName: String, defaultValue: String?): String? {
        val properties = properties.keys.filter { it.name == propertyName }
        return when (properties.size) {
            0 -> defaultValue
            1 -> propertyValue(properties.first())
            else -> throw IllegalArgumentException("Ambiguous property name $propertyName")
        }
    }

    fun propertyValue(propertyName: String): String? {
        val properties = properties.keys.filter { it.name == propertyName }
        return when (properties.size) {
            0 -> null
            // 0 -> throw IllegalArgumentException("Unknown property name $propertyName. Known declaredProperties: $properties")
            1 -> propertyValue(properties.first())
            else -> throw IllegalArgumentException("Ambiguous property name $propertyName")
        }
    }

    operator fun set(property: PhysicalProperty, value: String) {
        properties[property] = value
    }

    fun booleanPropertyValue(propertyName: String): Boolean {
        val prop = properties.keys.find { it.name == propertyName }
        return if (prop == null) {
            false
        } else {
            properties[prop]!!.toBoolean()
        }
    }

    fun longPropertyValue(propertyName: String): Long {
        val prop = properties.keys.find { it.name == propertyName }
        return if (prop == null) {
            0L
        } else {
            properties[prop]!!.toLong()
        }
    }

    fun stringPropertyValue(propertyName: String): String {
        val prop = properties.keys.find { it.name == propertyName }
        return if (prop == null) {
            ""
        } else {
            properties[prop]!!
        }
    }
}