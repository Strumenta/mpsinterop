package com.strumenta.mpsinterop.datamodel

import java.util.*

/**
 * Each concept is identified by an ID globally and by an index within a single mps file.
 */
data class Concept(val id: String, val name: String) {
    val properties = LinkedList<Property>()
    val relations = LinkedList<Relation>()

    fun addProperty(property: Property) {
        properties.add(property)
    }

    fun propertyByName(name: String): Property {
        return properties.find { it.name == name }!!
    }

    fun addRelation(relation: Relation) {
        relations.add(relation)
    }

    fun relationByName(name: String): Relation {
        return relations.find { it.name == name }!!
    }

}

enum class RelationType {
    CONTAINMENT,
    REFERENCE
}

enum class Multiplicity {
    OPTIONAL,
    SINGLE,
    MANY
}

data class Relation(val container: Concept, val id: String, val name: String,
                    val type: RelationType,
                    val multiplicity: Multiplicity) {
    init {
        if (type == RelationType.REFERENCE && multiplicity == Multiplicity.MANY) {
            throw java.lang.IllegalArgumentException("A reference can have the many multiplicity")
        }
    }
}

data class Property(val container: Concept, val id: String, val name: String)

class Node(val parent: Node?, val concept: Concept, val id: String) {
    private val properties = HashMap<Property, MutableList<String>>()
    private val children = HashMap<Relation, MutableList<Node>>()
    private val references = HashMap<Relation, Node>()

    fun addChild(relation: Relation, node: Node) {
        if (relation.type != RelationType.CONTAINMENT) {
            throw java.lang.IllegalArgumentException("The relation should be of containment")
        }
        if (relation !in children) {
            children[relation] = LinkedList()
        }
        children[relation]!!.add(node)
    }

    fun addReference(relation: Relation, node: Node) {
        references[relation] = node
    }

    fun addProperty(property: Property, propertyValue: String) {
        if (property !in properties) {
            properties[property] = LinkedList()
        }
        properties[property]!!.add(propertyValue)
    }

    fun singlePropertyValue(property: Property) : String {
        return properties[property]!![0]
    }

    fun children(relation: Relation) = children[relation] ?: emptyList<Node>()

    fun reference(relation: Relation) = references[relation]

    fun ancestor(condition: (Node) -> Boolean ): Node? {
        if (parent == null) {
            return null
        }
        if (condition(parent)) {
            return parent
        }
        return parent.ancestor(condition)
    }

}

class Model {
    private val roots = LinkedList<Node>()

    fun addRoot(root: Node) {
        roots.add(root)
    }

    fun onRoots(concept: Concept, op: (Node)->Unit) {
        roots.filter { it.concept == concept }.forEach { op(it) }
    }
}