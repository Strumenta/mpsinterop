package com.strumenta.mpsinterop.physicalmodel

import com.strumenta.mpsinterop.logicalmodel.LanguageUUID
import com.strumenta.mpsinterop.logicalmodel.NodeId
import java.lang.RuntimeException
import java.util.*
import kotlin.collections.HashMap

/**
 * A model, as defined in a file.
 *
 * Each concept is identified by an ID globally and by an index within a single mps file,
 * same is true for relations and declaredProperties.
 */
class PhysicalModel(val name: String, val uuid: UUID) {

    val roots = LinkedList<PhysicalNode>()

    var module: PhysicalModule? = null
        set(value) {
            if (field != null) {
                field!!.models.remove(this)
            }
            if (value != null) {
                value!!.models.add(this)
            }
            field = value
        }

    val numberOfRoots: Int
        get() = this.roots.size

    fun addRoot(root: PhysicalNode) {
        if (!root.root) {
            throw java.lang.IllegalArgumentException("The given node is not a root")
        }
        root.modelOfWhichIsRoot = this
        roots.add(root)
    }

    fun onRoots(op: (PhysicalNode) -> Unit) {
        roots.forEach { op(it) }
    }

    fun onRoots(concept: PhysicalConcept, op: (PhysicalNode) -> Unit) {
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
    private val languageUUIDsFromIndex = HashMap<String, LanguageUUID>()

    fun registerConcept(concept: PhysicalConcept) {
        conceptsByIndex[concept.index] = concept
        conceptsByQName[concept.qualifiedName] = concept
    }

    fun registerProperty(property: PhysicalProperty) {
        propertiesByIndex[property.index] = property
    }

    fun registerRelation(relation: PhysicalRelation) {
        relationsByIndex[relation.index] = relation
    }

    fun conceptByIndex(index: String): PhysicalConcept = conceptsByIndex[index]!!
    fun languageUUIDByIndex(index: String): LanguageUUID = languageUUIDsFromIndex[index]
            ?: throw java.lang.IllegalArgumentException("Unknown language index $index. Known indexes: ${languageUUIDsFromIndex.keys.joinToString(", ")}")

    fun relationByIndex(index: String): PhysicalRelation = relationsByIndex[index]
            ?: throw java.lang.IllegalArgumentException("Relation with index $index not found")

    fun propertyByIndex(index: String): PhysicalProperty = propertiesByIndex[index]!!

    fun findProperty(conceptName: String, propertyName: String): PhysicalProperty? {
        return conceptsByQName[conceptName]?.propertyByName(propertyName)
    }

    fun getProperty(conceptName: String, propertyName: String): PhysicalProperty {
        return findProperty(conceptName, propertyName)
                ?: throw java.lang.IllegalArgumentException("Property $conceptName.$propertyName not found")
    }

    fun conceptByName(conceptName: String): PhysicalConcept? {
        return conceptsByQName[conceptName]
    }

    fun allRoots(concept: PhysicalConcept): List<PhysicalNode> {
        return roots.filter { it.concept == concept }
    }

    fun languageUuidFromName(languageName: String): LanguageUUID {
        return languageUUIDsFromName[languageName]
                ?: throw RuntimeException("Unable to find UUID for language $languageName")
    }

    fun putLanguageInRegistry(languageUUID: LanguageUUID, languageName: String) {
        languageUUIDsFromName[languageName] = languageUUID
    }

    fun putLanguageIndexInRegistry(languageUUID: LanguageUUID, languageIndex: String) {
        languageUUIDsFromIndex[languageIndex] = languageUUID
    }

    fun findNodeByID(nodeID: Long): PhysicalNode? = findNodeByID(NodeId.regular(nodeID))

    fun findNodeByID(nodeID: NodeId): PhysicalNode? {
        for (root in roots) {
            val res = root.findNodeByID(nodeID)
            if (res != null) {
                return res
            }
        }
        return null
    }

    fun findConceptByID(conceptID: Long): PhysicalConcept? {
        return conceptsByIndex.values.find { it.id == conceptID }
    }
}
