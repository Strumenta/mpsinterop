package com.strumenta.mpsinterop.logicalmodel

import java.util.*

data class SLanguageId(val uuid: UUID)

data class Language(val name: String) {

    fun add(concept: AbstractConcept) {
        concepts.add(concept)
        concept.language = this
    }

    val concepts = LinkedList<AbstractConcept>()
}