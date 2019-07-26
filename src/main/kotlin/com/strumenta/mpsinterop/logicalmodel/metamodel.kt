package com.strumenta.mpsinterop.logicalmodel

import com.strumenta.mpsinterop.physicalmodel.PhysicalNode
import com.strumenta.mpsinterop.physicalmodel.PhysicalRelation
import com.strumenta.mpsinterop.registries.LanguageRegistry
import java.util.*

enum class ConceptKind {
    NORMAL,
    INTERFACE,
    IMPLEMENTATION,
    IMPLEMENTATION_WITH_STUB
}

data class SConcept(val id: SConceptId, val name: String, val isInterface : Boolean = false) {
    val alias: String? = null
    var rootable: Boolean = false
    var final: Boolean = false
    var abstract: Boolean = false
    var extended: SConcept? = null
    val implemented: MutableList<SConcept> = LinkedList<SConcept>()
    val declaredProperties : MutableList<SProperty> = LinkedList<SProperty>()

    val allProperties : List<SProperty>
        get() {
            val props = LinkedList<SProperty>()
            if (extended != null) {
                props.addAll(extended!!.allProperties)
            }
            implemented.forEach {
                props.addAll(it.allProperties)
            }
            props.addAll(declaredProperties)
            return props
        }

    fun qname(languageRegistry: LanguageRegistry) : String {
        val languageName = languageName(languageRegistry)
        return "$languageName.structure.$name"
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

    fun findProperty(propertyName: String): SProperty {
        val p = declaredProperties.find { it.name == propertyName }
        if (p != null) {
            return p
        } else {
            TODO("Property $propertyName in ${this.name}")
        }
    }

    fun addProperty(property: SProperty) {
        declaredProperties.add(property)
    }
}

data class SConceptId(val languageId: LanguageUUID, val idValue: Long)
data class SContainmentLink(val link: SContainmentLinkId, val name: String)
data class SContainmentLinkId(val conceptId: SConceptId, val idValue: Long)

interface SPropertyType
enum class PrimitiveSPropertyType : SPropertyType {
    BOOLEAN,
    INTEGER,
    STRING
}

data class EnumerationAlternative(val name: String, val value: String)
data class EnumerationSPropertyType(val name: String,
                                    val baseType: PrimitiveSPropertyType,
                                    val alternatives: List<EnumerationAlternative>) : SPropertyType
data class ConstrainedDataTypeDeclaration(val qname: String) : SPropertyType

data class SProperty(val sPropertyId: SPropertyId, val name: String, val type: SPropertyType)
data class SPropertyId(val conceptId: SConceptId, val idValue: Long)
data class SReferenceLink(val link: SReferenceLinkId, val name: String)
data class SReferenceLinkId(val conceptId: SConceptId, val idValue: Long)
interface SReference
data class StaticReference(val sref: PhysicalRelation, val node: PhysicalNode, val modelRef: SModelReference,
                           val targetNodeId: SNodeId?, val resolveInfo: String?) : SReference

