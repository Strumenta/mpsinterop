package com.strumenta.mpsinterop

import com.strumenta.mpsinterop.loading.LanguageResolver
import com.strumenta.mpsinterop.loading.PhysicalNode

val INAMED_CONCEPT = "jetbrains.mps.lang.core.structure.INamedConcept"
val NAME_PROPERTY = "name"

fun PhysicalNode.name(languageResolver: LanguageResolver) : String? {
    val iNamedConcept = languageResolver.physicalConceptByName(INAMED_CONCEPT)
    val nameProperty = iNamedConcept.propertyByName(NAME_PROPERTY)
    return singlePropertyValue(nameProperty)
}
