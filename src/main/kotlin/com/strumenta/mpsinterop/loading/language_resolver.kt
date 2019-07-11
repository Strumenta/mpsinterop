package com.strumenta.mpsinterop.loading

import com.strumenta.mpsinterop.loading.physicalmodel.PhysicalConcept

interface LanguageResolver {
    fun physicalConceptByName(name: String): PhysicalConcept
}