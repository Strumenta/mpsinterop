package com.strumenta.mpsinterop.logicalmodel

import java.util.*

typealias LanguageId = UUID

data class Language(val id: LanguageId, val name: String) {

    fun add(concept: SConcept) {
        concepts.add(concept)
        //concept.language = this
    }

    val concepts = LinkedList<SConcept>()
}