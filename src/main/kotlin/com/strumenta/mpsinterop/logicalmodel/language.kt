package com.strumenta.mpsinterop.logicalmodel

import java.util.*

typealias LanguageUUID = UUID

data class LanguageId(val id: LanguageUUID, val name: String)

data class Language(val id: LanguageUUID, val name: String) {

    private val _concepts = LinkedList<Concept>()

    val concepts : List<Concept>
        get() = _concepts

    fun add(concept: Concept) {
        if (concept in _concepts) {
            return
        }
        _concepts.add(concept)
        if (concept.language != this) {
            concept.language = this
        }
    }

    fun remove(concept: Concept) {
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

}
