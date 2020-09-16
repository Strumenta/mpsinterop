package com.strumenta.deprecated_mpsinterop.logicalmodel

import com.strumenta.deprecated_mpsinterop.physicalmodel.PhysicalLanguageModule
import java.util.*

typealias LanguageUUID = UUID

data class LanguageId(val id: LanguageUUID, val name: String)

data class Language(val id: LanguageUUID, val name: String, val physicalLanguageModule: PhysicalLanguageModule? = null) {

    private val _concepts = LinkedList<AbstractConcept>()

    val concepts: List<AbstractConcept>
        get() = _concepts

    fun add(concept: AbstractConcept) {
        if (concept in _concepts) {
            return
        }
        _concepts.add(concept)
        if (concept.language != this) {
            concept.language = this
        }
    }

    fun remove(concept: AbstractConcept) {
        if (concept !in _concepts) {
            return
        }
        _concepts.remove(concept)
        concept.language = null
    }

    fun hasConceptWithID(conceptID: Long): Boolean {
        return conceptByID(conceptID) != null
    }

    fun conceptByID(conceptID: Long) = _concepts.find { it.id == conceptID }

    fun createConcept(conceptId: Long, conceptName: String): Concept {
        val concept = Concept(conceptId, conceptName)
        this.add(concept)
        return concept
    }
}
