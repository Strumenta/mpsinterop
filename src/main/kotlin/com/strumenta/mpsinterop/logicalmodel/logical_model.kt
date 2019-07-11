package com.strumenta.mpsinterop.logicalmodel

import com.strumenta.mpsinterop.physicalmodel.INAMED_CONCEPT
import com.strumenta.mpsinterop.physicalmodel.NAME_PROPERTY
import java.util.*

data class Language(val name: String) {
    val concepts = LinkedList<Concept>()
}

data class Concept(val id: String, val name: String, val superConcept: Concept?, val implementedConcepts: List<Concept>) {
    fun findProperty(conceptName: String, propertyName: String): Property? {
        if (this.name == conceptName) {
            TODO()
        }
        val prop = this.superConcept?.findProperty(conceptName, propertyName)
        if (prop != null) {
            return prop
        }
        return this.implementedConcepts.map { it.findProperty(conceptName, propertyName) }.find { it != null }
    }
//    private val properties = LinkedList<Property>()
//    private val relations = LinkedList<Relation>()
//
//    fun addProperty(property: Property) {
//        properties.add(property)
//    }
//
//    fun propertyByName(name: String): Property {
//        return properties.find { it.name == name }
//                ?: throw IllegalArgumentException("Property $name not found in concept $name")
//    }
//
//    fun addRelation(relation: Relation) {
//        relations.add(relation)
//    }
//
//    fun relationByName(name: String): Relation {
//        return relations.find { it.name == name }!!
//    }
//
}
//
//enum class RelationType {
//    CONTAINMENT,
//    REFERENCE
//}
//
//enum class Multiplicity {
//    OPTIONAL,
//    SINGLE,
//    MANY
//}
//
//data class Relation(val container: Concept, val id: String, val name: String,
//                    val type: RelationType,
//                    val multiplicity: Multiplicity) {
//    init {
//        if (type == RelationType.REFERENCE && multiplicity == Multiplicity.MANY) {
//            throw IllegalArgumentException("A reference can have the many multiplicity")
//        }
//    }
//}
//

interface PropertyType

data class Property(val container: Concept, val id: String, val name: String, val type: PropertyType)

class Node(val parent: Node?, val concept: Concept, val id: String) {
    val root: Boolean
        get() = parent == null
//    private val properties = HashMap<String, MutableList<String>>()
//    private val children = HashMap<String, MutableList<Node>>()
//    private val references = HashMap<String, Node>()
//
//    fun addChild(relation: PhysicalRelation, node: Node) {
////        if (relation.type != RelationType.CONTAINMENT) {
////            throw IllegalArgumentException("The relation ${relation.name} should be of containment instead it is ${relation.type}" +
////                    "")
////        }
//        if (relation.name !in children) {
//            children[relation.name] = LinkedList()
//        }
//        children[relation.name]!!.add(node)
//    }
//
//    fun addReference(relation: PhysicalRelation, node: Node) {
////        if (relation.type != RelationType.REFERENCE) {
////            throw IllegalArgumentException("The relation ${relation.name} should be a reference instead it is ${relation.type}")
////        }
//        references[relation.name] = node
//    }
//
//    fun addProperty(property: PhysicalProperty, propertyValue: String) {
//        if (property.name !in properties) {
//            properties[property.name] = LinkedList()
//        }
//        properties[property.name]!!.add(propertyValue)
//    }
//
//    fun singlePropertyValue(property: PhysicalProperty) : String {
//        return properties[property.name]!![0]
//    }
//
//    fun singlePropertyValue(propertyName: String) : String {
//        return properties[propertyName]!![0]
//    }
//
//    fun children(relation: Relation) = children[relation.name] ?: emptyList<Node>()
//
//    fun reference(relation: Relation) = references[relation.name]
//
//    fun ancestor(condition: (Node) -> Boolean ): Node? {
//        if (parent == null) {
//            return null
//        }
//        if (condition(parent)) {
//            return parent
//        }
//        return parent.ancestor(condition)
//    }
//

    fun singlePropertyValue(conceptName: String, propertyName: String) : String? {
        val property = this.concept.findProperty(conceptName, propertyName)

        if (property == null) {
            throw java.lang.IllegalArgumentException("This node has not the property $conceptName/$propertyName")
        }

        // We should find the property definition first
        TODO()
    }
}
//
class Model(val name: String) {
    private val roots = LinkedList<Node>()

    val numberOfRoots: Int
        get() = this.roots.size

    fun addRoot(root: Node) {
        if (!root.root) {
            throw IllegalArgumentException("The given node is not a root")
        }
        roots.add(root)
    }

    fun onRoots(op: (Node)->Unit) {
        roots.forEach { op(it) }
    }

    fun onRoots(concept: Concept, op: (Node)->Unit) {
        roots.filter { it.concept == concept }.forEach { op(it) }
    }

    fun getRootByName(name: String): Node {
        return roots.find { it.name() == name }!!
    }
}

fun Node.name() : String? {
    return singlePropertyValue(INAMED_CONCEPT, NAME_PROPERTY)
}