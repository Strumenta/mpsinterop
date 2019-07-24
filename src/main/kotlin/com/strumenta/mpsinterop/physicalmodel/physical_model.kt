package com.strumenta.mpsinterop.physicalmodel

import com.strumenta.mpsinterop.logicalmodel.LanguageId
import com.strumenta.mpsinterop.logicalmodel.LanguageUUID
import com.strumenta.mpsinterop.logicalmodel.SNodeId
import java.lang.RuntimeException
import java.util.*
import kotlin.collections.HashMap

/**
 * Simplified concept, as it appears in a model
 */
data class PhysicalConcept(val languageId: LanguageId, val id: Long,
                           val name: String, val index: String) {
    init {
        require(name.indexOf('.') == -1) { "A concept name should not be qualified ($name)" }
    }
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

    val qname: String
        get() {
            val languageName = languageId.name
            return "$languageName.structure.$name"
        }

}

enum class RelationKind {
    CONTAINMENT,
    REFERENCE
}

data class PhysicalRelation(val container: PhysicalConcept, val id: Long, val name: String, val index: String,
                            val kind: RelationKind)

data class PhysicalProperty(val container: PhysicalConcept, val id: Long, val name: String, val index: String)

/**
 * A model, as defined in a file.
 *
 * Each concept is identified by an ID globally and by an index within a single mps file,
 * same is true for relations and declaredProperties.
 */
class PhysicalModel(val name: String){

    val roots = LinkedList<PhysicalNode>()

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
    private val conceptsByQName = HashMap<String, PhysicalConcept>()
    private val relationsByIndex = HashMap<String, PhysicalRelation>()
    private val propertiesByIndex = HashMap<String, PhysicalProperty>()
    private val languageUUIDsFromName = HashMap<String, LanguageUUID>()

    fun registerConcept(concept: PhysicalConcept) {
        conceptsByIndex[concept.index] = concept
        conceptsByQName[concept.qname] = concept
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
        return conceptsByQName[conceptName]?.propertyByName(propertyName) ?:
                throw java.lang.IllegalArgumentException("Property $conceptName.$propertyName not found")
    }

    fun conceptByName(conceptName: String): PhysicalConcept? {
        return conceptsByQName[conceptName]
    }

    fun allRoots(concept: PhysicalConcept): List<PhysicalNode> {
        return roots.filter { it.concept == concept }
    }

    fun languageUuidFromName(languageName: String): LanguageUUID {
        return languageUUIDsFromName[languageName] ?: throw RuntimeException("Unable to find UUID for language $languageName")
    }

    fun putLanguageInRegistry(languageUUID: LanguageUUID, languageName: String) {
        languageUUIDsFromName[languageName] = languageUUID
    }


//    override fun physicalConceptByName(name: String): PhysicalConcept? = conceptsByQName[name]
//
//    override fun conceptDeclarationByName(name: String): PhysicalNode? {
//        TODO("not implemented $name") //To change body of created functions use File | Settings | File Templates.
//    }

}

interface ReferenceTarget

data class InModelReferenceTarget(val nodeID: String) : ReferenceTarget
data class OutsideModelReferenceTarget(val importIndex: String, val nodeIndex:String) : ReferenceTarget

data class PhysicalReferenceValue(val target: ReferenceTarget, val resolve: String)

class PhysicalNode(val parent: PhysicalNode?, val concept: PhysicalConcept, val id: SNodeId) {
    val root: Boolean
        get() = parent == null
    internal var modelOfWhichIsRoot : PhysicalModel? = null
    val model : PhysicalModel?
        get() = if (root) modelOfWhichIsRoot else parent!!.model
    private val properties = HashMap<PhysicalProperty, String>()
    private val children = HashMap<PhysicalRelation, MutableList<PhysicalNode>>()
    private val references = HashMap<PhysicalRelation, PhysicalReferenceValue>()

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
        properties[property] = propertyValue
    }

    fun propertyValue(property: PhysicalProperty) : String {
        return properties[property]!!
    }

    fun propertyValue(propertyName: String) : String {
        val properties = properties.keys.filter { it.name == propertyName }
        return when (properties.size) {
            0 -> throw IllegalArgumentException("Unknown property name $propertyName. Known declaredProperties: $properties")
            1 -> propertyValue(properties.first())
            else -> throw IllegalArgumentException("Ambiguous property name $propertyName")
        }
    }

    fun children(relation: PhysicalRelation) = children[relation] ?: emptyList<PhysicalNode>()

    fun reference(relation: PhysicalRelation) = references[relation]

    fun reference(relationName: String) : PhysicalReferenceValue? {
        val rs = references.keys.filter { it.name == relationName }
        return when (rs.size) {
            0 -> null
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

    fun setProperty(property: PhysicalProperty, value: String) {
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

    fun numberOfChildren(relationName: String): Int {
        val rs = children.keys.filter { it.name == relationName }
        return when (rs.size) {
            0 -> 0
            1 -> children[rs[0]]!!.size
            else -> throw IllegalArgumentException("Ambiguous reference name $relationName")
        }
    }

}
