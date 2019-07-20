package com.strumenta.mpsinterop.logicalmodel

import java.util.*

typealias LanguageUUID = UUID

data class LanguageId(val id: LanguageUUID, val name: String)

data class Language(val id: LanguageUUID, val name: String) {

    fun add(concept: SConcept) {
        concepts.add(concept)
        //concept.language = this
    }

    val concepts = LinkedList<SConcept>()
}