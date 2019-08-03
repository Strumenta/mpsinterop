package com.strumenta.mpsinterop.physicalmodel

import com.strumenta.mpsinterop.logicalmodel.LanguageUUID
import com.strumenta.mpsinterop.logicalmodel.NodeId
import com.strumenta.mpsinterop.utils.Base64
import java.lang.RuntimeException
import java.lang.UnsupportedOperationException
import java.util.*
import kotlin.collections.HashMap

enum class RelationKind {
    CONTAINMENT,
    REFERENCE
}

data class PhysicalRelation(
    val container: PhysicalConcept,
    val id: Long,
    val name: String,
    val index: String,
    val kind: RelationKind
)

data class PhysicalProperty(val container: PhysicalConcept, val id: Long, val name: String, val index: String)

open class PhysicalModule(val name: String, val uuid: UUID) {
    val models = LinkedList<PhysicalModel>()
}

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
        conceptsByQName[concept.qname] = concept
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
    fun getProperty(conceptName: String, propertyName: String): PhysicalProperty {
        return conceptsByQName[conceptName]?.propertyByName(propertyName)
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
}

sealed class ReferenceTarget

data class InModelReferenceTarget(
    val physicalModel: PhysicalModel,
    val nodeID: String
) : ReferenceTarget()
data class OutsideModelReferenceTarget(
    val physicalModel: PhysicalModel,
    val importIndex: String,
    val nodeIndex: String
) : ReferenceTarget() {
    val modelUIID: UUID = physicalModel.languageUUIDByIndex(importIndex)
    val nodeID: Long
        get() {
            return Base64.parseLong(nodeIndex)
        }
}
class ExplicitReferenceTarget(val model: UUID, val nodeId: NodeId) : ReferenceTarget()
class FailedLoadingReferenceTarget(val e: Throwable) : ReferenceTarget()
object NullReferenceTarget : ReferenceTarget()

data class PhysicalReferenceValue(val target: ReferenceTarget, val resolve: String?)

class PhysicalNode(val parent: PhysicalNode?, val concept: PhysicalConcept, val id: NodeId) {
    val root: Boolean
        get() = parent == null
    internal var modelOfWhichIsRoot: PhysicalModel? = null
    val model: PhysicalModel?
        get() = if (root) modelOfWhichIsRoot else parent!!.model
    private val properties = HashMap<PhysicalProperty, String>()
    private val children = HashMap<PhysicalRelation, MutableList<PhysicalNode>>()
    private val references = HashMap<PhysicalRelation, PhysicalReferenceValue>()

    fun qname(): String {
        if (root) {
            return model!!.name + "." + name()
        } else {
            throw UnsupportedOperationException()
        }
    }

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

    fun propertyValue(property: PhysicalProperty): String {
        return properties[property]!!
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

    fun children(relationName: String): List<PhysicalNode> {
        val relation = children.keys.find { it.name == relationName }
        return if (relation == null) {
            emptyList()
        } else {
            children[relation]!!
        }
    }

    fun children(relation: PhysicalRelation) = children[relation] ?: emptyList<PhysicalNode>()

    fun reference(relation: PhysicalRelation) = references[relation]

    fun reference(relationName: String): PhysicalReferenceValue? {
        val rs = references.keys.filter { it.name == relationName }
        return when (rs.size) {
            0 -> null
            1 -> reference(rs.first())
            else -> throw IllegalArgumentException("Ambiguous reference name $relationName")
        }
    }

    fun ancestor(condition: (PhysicalNode) -> Boolean): PhysicalNode? {
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
}
