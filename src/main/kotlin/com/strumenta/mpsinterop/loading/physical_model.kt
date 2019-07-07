package com.strumenta.mpsinterop.loading

import com.strumenta.mpsinterop.name
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

data class PhysicalRelation(val container: PhysicalConcept, val id: String, val name: String, val index: String)

data class PhysicalProperty(val container: PhysicalConcept, val id: String, val name: String, val index: String)

/**
 * A model, as defined in a file.
 *
 * Each concept is identified by an ID globally and by an index within a single mps file,
 * same is true for relations and properties.
 */
class PhysicalModel(val name: String) : LanguageResolver {
    private val roots = LinkedList<PhysicalNode>()

    val numberOfRoots: Int
        get() = this.roots.size

    fun addRoot(root: PhysicalNode) {
        if (!root.root) {
            throw java.lang.IllegalArgumentException("The given node is not a root")
        }
        roots.add(root)
    }

    fun onRoots(op: (PhysicalNode)->Unit) {
        roots.forEach { op(it) }
    }

    fun onRoots(concept: PhysicalConcept, op: (PhysicalNode)->Unit) {
        roots.filter { it.concept == concept }.forEach { op(it) }
    }

    fun getRootByName(name: String, languageResolver: LanguageResolver): PhysicalNode {
        return roots.find { it.name(languageResolver) == name }!!
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

    fun relationByIndex(index: String) : PhysicalRelation = relationsByIndex[index]!!

    fun propertyByIndex(index: String) : PhysicalProperty = propertiesByIndex[index]!!

    override fun physicalConceptByName(name: String): PhysicalConcept = conceptsByName[name]!!
}

class PhysicalNode(val parent: PhysicalNode?, val concept: PhysicalConcept, val id: String) {
    val root: Boolean
        get() = parent == null
    private val properties = java.util.HashMap<PhysicalProperty, MutableList<String>>()
    private val children = java.util.HashMap<PhysicalRelation, MutableList<PhysicalNode>>()
    private val references = java.util.HashMap<PhysicalRelation, PhysicalNode>()

    fun addChild(relation: PhysicalRelation, node: PhysicalNode) {
        if (relation !in children) {
            children[relation] = LinkedList()
        }
        children[relation]!!.add(node)
    }

    fun addReference(relation: PhysicalRelation, node: PhysicalNode) {
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

    fun children(relation: PhysicalRelation) = children[relation] ?: emptyList<PhysicalNode>()

    fun reference(relation: PhysicalRelation) = references[relation]

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
