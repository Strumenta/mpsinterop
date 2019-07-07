package com.strumenta.mpsinterop.loading

interface LanguageResolver {
    fun physicalConceptByName(name: String): PhysicalConcept
}