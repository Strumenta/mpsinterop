package com.strumenta.deprecated_mpsinterop.physicalmodel

import com.strumenta.deprecated_mpsinterop.logicalmodel.LanguageId
import com.strumenta.deprecated_mpsinterop.utils.isSimpleName
import java.util.*

/**
 * Simplified concept, as it appears in a model
 */
data class PhysicalConcept(
    val languageId: LanguageId,
    val id: Long,
    val name: String,
    val index: String
) {
    init {
        require(name.isSimpleName) { "A concept name should not be qualified ($name)" }
    }
    private val properties = LinkedList<PhysicalProperty>()
    private val relations = LinkedList<PhysicalRelation>()

    fun addProperty(property: PhysicalProperty) {
        properties.add(property)
    }

    /**
     * This method does not consider inherited properties
     */
    fun getPropertyByName(name: String): PhysicalProperty {
        return findPropertyByName(name)
                ?: throw IllegalArgumentException("Property $name not found in concept $name")
    }

    fun findPropertyByName(name: String): PhysicalProperty? {
        return properties.find { it.name == name }
    }

    fun addRelation(relation: PhysicalRelation) {
        relations.add(relation)
    }

    fun relationByName(name: String): PhysicalRelation {
        return relations.find { it.name == name }
                ?: throw IllegalArgumentException("Relation $name not found in concept $name")
    }

    fun hasProperty(property: PhysicalProperty): Boolean {
        return properties.contains(property)
    }

    val qualifiedName: String
        get() {
            val languageName = languageId.name
            return "$languageName.structure.$name"
        }
}
