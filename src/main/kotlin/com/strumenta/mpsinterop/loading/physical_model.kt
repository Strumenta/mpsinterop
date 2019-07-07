package com.strumenta.mpsinterop.loading

import com.strumenta.mpsinterop.datamodel.Concept
import com.strumenta.mpsinterop.datamodel.Model
import com.strumenta.mpsinterop.datamodel.Property
import com.strumenta.mpsinterop.datamodel.Relation

/**
 * Each concept is identified by an ID globally and by an index within a single mps file,
 * same is true for relations and properties.
 */
class PhysicalModel : LanguageResolver {
    private val conceptsByIndex = HashMap<String, Concept>()
    private val conceptsByName = HashMap<String, Concept>()
    private val relationsByIndex = HashMap<String, Relation>()
    private val propertiesByIndex = HashMap<String, Property>()
    val model = Model()

    fun registerConcept(concept: Concept, index: String) {
        conceptsByIndex[index] = concept
        conceptsByName[concept.name] = concept
    }

    fun registerProperty(property: Property, index: String) {
        propertiesByIndex[index] = property
    }

    fun registerRelation(relation: Relation, index: String) {
        relationsByIndex[index] = relation
    }

    fun conceptByIndex(index: String) : Concept = conceptsByIndex[index]!!

    fun relationByIndex(index: String) : Relation = relationsByIndex[index]!!

    fun propertyByIndex(index: String) : Property = propertiesByIndex[index]!!

    override fun conceptByName(name: String): Concept = conceptsByName[name]!!
}
