package com.strumenta.mpsinterop.physicalmodel

import com.strumenta.mpsinterop.logicalmodel.Model
import java.util.*
import kotlin.collections.HashMap

/**
 * Simplified concept, as it appears in a model
 */
data class PhysicalConcept(val id: String, val name: String, val index: String) {
    private val properties = LinkedList<PhysicalProperty>()
    private val relations = LinkedList<PhysicalRelation>()

    fun addProperty(property: PhysicalProperty) {
        properties.add(property)
    }

    fun propertyByName(name: String): PhysicalProperty {
        return properties.find { it.name == name }
                ?: throw IllegalArgumentException("Property $name not found in concept $name")
    }

    fun addRelation(relation: PhysicalRelation) {
        relations.add(relation)
    }

    fun relationByName(name: String): PhysicalRelation {
        return relations.find { it.name == name }!!
    }

}

enum class RelationKind {
    CONTAINMENT,
    REFERENCE
}

data class PhysicalRelation(val container: PhysicalConcept, val id: String, val name: String, val index: String,
                            val kind: RelationKind)

data class PhysicalProperty(val container: PhysicalConcept, val id: String, val name: String, val index: String)

/**
 * A model, as defined in a file.
 *
 * Each concept is identified by an ID globally and by an index within a single mps file,
 * same is true for relations and properties.
 */
class PhysicalModel(val name: String){

    private val roots = LinkedList<PhysicalNode>()

    val numberOfRoots: Int
        get() = this.roots.size

    fun addRoot(root: PhysicalNode) {
        if (!root.root) {
            throw java.lang.IllegalArgumentException("The given node is not a root")
        }
        root.modelOfWhichIsRoot = this
        roots.add(root)
    }

    fun onRoots(op: (PhysicalNode)->Unit) {
        roots.forEach { op(it) }
    }

    fun onRoots(concept: PhysicalConcept, op: (PhysicalNode)->Unit) {
        roots.filter { it.concept == concept }.forEach { op(it) }
    }

    fun getRootByName(name: String): PhysicalNode {
        return roots.find { it.name() == name }!!
    }

    private val conceptsByIndex = HashMap<String, PhysicalConcept>()
    private val conceptsByName = HashMap<String, PhysicalConcept>()
    private val relationsByIndex = HashMap<String, PhysicalRelation>()
    private val propertiesByIndex = HashMap<String, PhysicalProperty>()

    fun registerConcept(concept: PhysicalConcept) {
        conceptsByIndex[concept.index] = concept
        conceptsByName[concept.name] = concept
    }

    fun registerProperty(property: PhysicalProperty) {
        propertiesByIndex[property.index] = property
    }

    fun registerRelation(relation: PhysicalRelation) {
        relationsByIndex[relation.index] = relation
    }

    fun conceptByIndex(index: String) : PhysicalConcept = conceptsByIndex[index]!!

    fun relationByIndex(index: String) : PhysicalRelation = relationsByIndex[index]
            ?: throw java.lang.IllegalArgumentException("Relation with index $index not found")

    fun propertyByIndex(index: String) : PhysicalProperty = propertiesByIndex[index]!!
    fun getProperty(conceptName: String, propertyName: String): PhysicalProperty {
        return conceptsByName[conceptName]!!.propertyByName(propertyName)
    }

    fun conceptByName(conceptName: String): PhysicalConcept? {
        return conceptsByName[conceptName]
    }

    fun allRoots(concept: PhysicalConcept): List<PhysicalNode> {
        return roots.filter { it.concept == concept }
    }


//    override fun physicalConceptByName(name: String): PhysicalConcept? = conceptsByName[name]
//
//    override fun conceptDeclarationByName(name: String): PhysicalNode? {
//        TODO("not implemented $name") //To change body of created functions use File | Settings | File Templates.
//    }

}

interface ReferenceTarget

data class InModelReferenceTarget(val nodeID: String) : ReferenceTarget
data class OutsideModelReferenceTarget(val importIndex: String, val nodeIndex:String) : ReferenceTarget

data class PhysicalReferenceValue(val target: ReferenceTarget, val resolve: String)

class PhysicalNode(val parent: PhysicalNode?, val concept: PhysicalConcept, val id: String) {
    val root: Boolean
        get() = parent == null
    internal var modelOfWhichIsRoot : PhysicalModel? = null
    val model : PhysicalModel?
        get() = if (root) modelOfWhichIsRoot else parent!!.model
    private val properties = java.util.HashMap<PhysicalProperty, MutableList<String>>()
    private val children = java.util.HashMap<PhysicalRelation, MutableList<PhysicalNode>>()
    private val references = java.util.HashMap<PhysicalRelation, PhysicalReferenceValue>()

    fun addChild(relation: PhysicalRelation, node: PhysicalNode) {
        if (relation !in children) {
            children[relation] = LinkedList()
        }
        children[relation]!!.add(node)
    }

    fun addReference(relation: PhysicalRelation, node: PhysicalReferenceValue) {
        references[relation] = node
    }

    fun addProperty(property: PhysicalProperty, propertyValue: String) {
        if (property !in properties) {
            properties[property] = LinkedList()
        }
        properties[property]!!.add(propertyValue)
    }

    fun singlePropertyValue(property: PhysicalProperty) : String {
        return properties[property]!![0]
    }

    fun singlePropertyValue(propertyName: String) : String {
        val properties = properties.keys.filter { it.name == propertyName }
        return when (properties.size) {
            0 -> throw IllegalArgumentException("Unknown property name $propertyName. Known properties: $properties")
            1 -> singlePropertyValue(properties.first())
            else -> throw IllegalArgumentException("Ambiguous property name $propertyName")
        }
    }

    fun children(relation: PhysicalRelation) = children[relation] ?: emptyList<PhysicalNode>()

    fun reference(relation: PhysicalRelation) = references[relation]

    fun reference(relationName: String) : PhysicalReferenceValue? {
        val rs = references.keys.filter { it.name == relationName }
        return when (references.size) {
            0 -> throw IllegalArgumentException("Unknown reference name $relationName. Known references: ${references.keys.joinToString(", ")}")
            1 -> reference(rs.first())
            else -> throw IllegalArgumentException("Ambiguous reference name $relationName")
        }
    }

    fun ancestor(condition: (PhysicalNode) -> Boolean ): PhysicalNode? {
        if (parent == null) {
            return null
        }
        if (condition(parent)) {
            return parent
        }
        return parent.ancestor(condition)
    }

}