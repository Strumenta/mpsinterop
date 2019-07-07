package com.strumenta.mpsinterop.loading

import com.strumenta.mpsinterop.datamodel.Concept
import com.strumenta.mpsinterop.datamodel.Model
import com.strumenta.mpsinterop.datamodel.Property
import com.strumenta.mpsinterop.datamodel.Relation

class PhysicalModel {
    private val conceptsByIndex = HashMap<String, Concept>()
    private val relationsByIndex = HashMap<String, Relation>()
    private val propertiesByIndex = HashMap<String, Property>()
    val model = Model()

    fun registerConcept(concept: Concept, index: String) {
        conceptsByIndex[index] = concept
    }

    fun registerProperty(property: Property, index: String) {
        propertiesByIndex[index] = property
    }

    fun registerRelation(relation: Relation, index: String) {
        relationsByIndex[index] = relation
    }
}
