package com.strumenta.mpsinterop.datamodel

import com.strumenta.mpsinterop.loading.LanguageResolver
import com.strumenta.mpsinterop.name
import java.lang.IllegalArgumentException
import java.util.*

data class Concept(val id: String, val name: String) {
    private val properties = LinkedList<Property>()
    private val relations = LinkedList<Relation>()

    fun addProperty(property: Property) {
        properties.add(property)
    }

    fun propertyByName(name: String): Property {
        return properties.find { it.name == name } ?: throw IllegalArgumentException("Property $name not found in concept $name")
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

/**
 * Type and multiplicity could be null if we do not have access to the language definition
 * and we are just loading the relations as used in a model
 */
data class Relation(val container: Concept, val id: String, val name: String,
                    val type: RelationType? = null,
                    val multiplicity: Multiplicity? = null) {
    init {
        if (type == RelationType.REFERENCE && multiplicity == Multiplicity.MANY) {
            throw java.lang.IllegalArgumentException("A reference can have the many multiplicity")
        }
    }

    fun couldBeReference() = type == null || type == RelationType.REFERENCE
    fun couldBeContainment() = type == null || type == RelationType.CONTAINMENT
}

data class Property(val container: Concept, val id: String, val name: String)

class Node(val parent: Node?, val concept: Concept, val id: String) {
    val root: Boolean
        get() = parent == null
    private val properties = HashMap<Property, MutableList<String>>()
    private val children = HashMap<Relation, MutableList<Node>>()
    private val references = HashMap<Relation, Node>()

    fun addChild(relation: Relation, node: Node) {
        if (!relation.couldBeContainment()) {
            throw IllegalArgumentException("The relation ${relation.name} should be of containment instead it is ${relation.type}" +
                    "")
        }
        if (relation !in children) {
            children[relation] = LinkedList()
        }
        children[relation]!!.add(node)
    }

    fun addReference(relation: Relation, node: Node) {
        if (!relation.couldBeReference()) {
            throw IllegalArgumentException("The relation ${relation.name} should be a reference instead it is ${relation.type}")
        }
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

    val numberOfRoots: Int
        get() = this.roots.size

    fun addRoot(root: Node) {
        if (!root.root) {
            throw IllegalArgumentException("The given node is not a root")
        }
        roots.add(root)
    }

    fun onRoots(concept: Concept, op: (Node)->Unit) {
        roots.filter { it.concept == concept }.forEach { op(it) }
    }

    fun getRootByName(name: String, languageResolver: LanguageResolver): Node {
        return roots.find { it.name(languageResolver) == name }!!
    }
}
