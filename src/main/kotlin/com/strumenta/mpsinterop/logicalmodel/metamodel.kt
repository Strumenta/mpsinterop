package com.strumenta.mpsinterop.logicalmodel

import com.strumenta.mpsinterop.registries.LanguageRegistry

enum class ConceptKind {
    NORMAL,
    INTERFACE,
    IMPLEMENTATION,
    IMPLEMENTATION_WITH_STUB
}

data class SConcept(val id: SConceptId, val name: String) {
    fun qname(languageRegistry: LanguageRegistry) : String {
        val languageName = languageName(languageRegistry)
        return "$languageName.$name"
    }

    fun language(languageRegistry: LanguageRegistry) : Language {
        return languageRegistry.get(id.languageId)!!
    }

    fun languageName(languageRegistry: LanguageRegistry) : String {
        return languageRegistry.getName(id.languageId)!!
    }

    fun findProperty(conceptName: String, propertyName: String): SProperty {
        TODO()
    }
}

data class SConceptId(val languageId: LanguageId, val idValue: Long)
data class SContainmentLink(val link: SContainmentLinkId, val name: String)
data class SContainmentLinkId(val conceptId: SConceptId, val idValue: Long)
data class SProperty(val sPropertyId: SPropertyId, val propertyName: String?)
data class SPropertyId(val conceptId: SConceptId, val idValue: Long)
data class SReferenceLink(val link: SReferenceLinkId, val name: String)
data class SReferenceLinkId(val conceptId: SConceptId, val idValue: Long)
interface SReference
data class StaticReference(val sref: SReferenceLink, val node: SNode, val modelRef: SModelReference,
                           val targetNodeId: SNodeId?, val resolveInfo: String?) : SReference

