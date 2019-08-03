package com.strumenta.mpsinterop.physicalmodel

import com.strumenta.mpsinterop.logicalmodel.LanguageId
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
