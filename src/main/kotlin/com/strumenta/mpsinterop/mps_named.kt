package com.strumenta.mpsinterop

import com.strumenta.mpsinterop.datamodel.Node
import com.strumenta.mpsinterop.loading.LanguageResolver

val INAMED_CONCEPT = "jetbrains.mps.lang.core.structure.INamedConcept"
val NAME_PROPERTY = "name"

fun Node.name(languageResolver: LanguageResolver) : String? {
    val iNamedConcept = languageResolver.conceptByName(INAMED_CONCEPT)
    val nameProperty = iNamedConcept.propertyByName(NAME_PROPERTY)
    return singlePropertyValue(nameProperty)
}
