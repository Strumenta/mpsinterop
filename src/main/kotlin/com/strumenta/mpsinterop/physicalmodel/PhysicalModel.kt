package com.strumenta.mpsinterop.physicalmodel

import com.strumenta.mpsinterop.logicalmodel.LanguageUUID
import com.strumenta.mpsinterop.logicalmodel.NodeId
import java.lang.IllegalArgumentException
import java.lang.RuntimeException
import java.util.*
import kotlin.collections.HashMap

/**
 * A model, as defined in a file.
 *
 * Each concept is identified by an ID globally and by an index within a single mps file,
 * same is true for relations and declaredProperties.
 */
class PhysicalModel(val uuid: UUID, val name: String) {

    private data class LanguageImport(val uuid: UUID, val name: String)
    private data class ModelImport(val uuid: UUID, val name: String, val index: String)

    private val conceptsByIndex = HashMap<String, PhysicalConcept>()
    private val conceptsByQName = HashMap<String, PhysicalConcept>()
    private val relationsByIndex = HashMap<String, PhysicalRelation>()
    private val propertiesByIndex = HashMap<String, PhysicalProperty>()
    // TODO remove
    private val languageUUIDsFromName = HashMap<String, LanguageUUID>()
    private val languages = LinkedList<LanguageImport>()
    private val models = LinkedList<ModelImport>()
    // private val languageUUIDsFromIndex = HashMap<String, LanguageUUID>()

    // /////////////////////////////////////
    // Module
    // /////////////////////////////////////

    var module: PhysicalModule? = null
        set(value) {
            if (field != null) {
                field!!.models.remove(this)
            }
            value?.models?.add(this)
            field = value
        }

    // /////////////////////////////////////
    // Roots
    // /////////////////////////////////////

    private val _roots = LinkedList<PhysicalNode>()

    val roots: List<PhysicalNode>
        get() = _roots

    val numberOfRoots: Int
        get() = this.roots.size

    fun addRoot(root: PhysicalNode) {
        if (!root.root) {
            throw IllegalArgumentException("The given node is not a root")
        }
        root.modelOfWhichIsRoot = this
        _roots.add(root)
    }

    fun onRoots(op: (PhysicalNode) -> Unit) {
        roots.forEach { op(it) }
    }

    fun onRoots(concept: PhysicalConcept, op: (PhysicalNode) -> Unit) {
        roots.filter { it.concept == concept }.forEach { op(it) }
    }

    fun findRootByName(name: String): PhysicalNode? {
        return roots.find { it.name() == name }
    }

    fun getRootByName(name: String) = findRootByName(name) ?: throw IllegalArgumentException("No root found with name $name")

    fun rootsOfConcept(concept: PhysicalConcept): List<PhysicalNode> {
        return roots.filter { it.concept == concept }
    }

    // /////////////////////////////////////
    // Languages
    // /////////////////////////////////////

    fun languageUuidFromName(languageName: String): LanguageUUID {
        return languageUUIDsFromName[languageName]
                ?: throw RuntimeException("Unable to find UUID for language $languageName")
    }

    fun putLanguageInRegistry(languageUUID: LanguageUUID, languageName: String) {
        languageUUIDsFromName[languageName] = languageUUID
    }

    // /////////////////////////////////////
    // Models
    // /////////////////////////////////////

    fun putModelInRegistry(uuid: UUID, name: String, index: String) {
        models.add(ModelImport(uuid, name, index))
    }

    fun modelUUIDByIndex(index: String): UUID = models.find { it.index == index }?.uuid
            ?: throw IllegalArgumentException("Unknown model index $index. Known indexes: ${models.joinToString(", ") { it.index }}")

    // /////////////////////////////////////
    // Concepts
    // /////////////////////////////////////

    fun registerConcept(concept: PhysicalConcept) {
        conceptsByIndex[concept.index] = concept
        conceptsByQName[concept.qualifiedName] = concept
    }

    fun conceptByIndex(index: String): PhysicalConcept = conceptsByIndex[index]!!

    fun conceptByName(conceptName: String): PhysicalConcept? {
        return conceptsByQName[conceptName]
    }

    fun findConceptByID(conceptID: Long): PhysicalConcept? {
        return conceptsByIndex.values.find { it.id == conceptID }
    }

    // /////////////////////////////////////
    // Relations
    // /////////////////////////////////////

    fun registerRelation(relation: PhysicalRelation) {
        relationsByIndex[relation.index] = relation
    }

    fun relationByIndex(index: String): PhysicalRelation = relationsByIndex[index]
            ?: throw IllegalArgumentException("Relation with index $index not found")

    // /////////////////////////////////////
    // Properties
    // /////////////////////////////////////

    fun registerProperty(property: PhysicalProperty) {
        propertiesByIndex[property.index] = property
    }

    fun propertyByIndex(index: String): PhysicalProperty = propertiesByIndex[index]!!

    fun findProperty(conceptName: String, propertyName: String): PhysicalProperty? {
        return conceptsByQName[conceptName]?.propertyByName(propertyName)
    }

    fun getProperty(conceptName: String, propertyName: String): PhysicalProperty {
        return findProperty(conceptName, propertyName)
                ?: throw IllegalArgumentException("Property $conceptName.$propertyName not found")
    }

    // /////////////////////////////////////
    // Nodes
    // /////////////////////////////////////

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
}
