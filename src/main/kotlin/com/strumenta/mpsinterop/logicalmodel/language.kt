package com.strumenta.mpsinterop.logicalmodel

import java.util.*

typealias LanguageUUID = UUID

data class LanguageId(val id: LanguageUUID, val name: String)

data class Language(val id: LanguageUUID, val name: String) {

    fun add(concept: SConcept) {
        concepts.add(concept)
        //concept.language = this
    }

    fun hasConceptWithID(conceptID: Long): Boolean {
        return conceptByID(conceptID) != null
    }

    fun conceptByID(conceptID: Long) = concepts.find { it.id.idValue == conceptID }

    val concepts = LinkedList<SConcept>()
}