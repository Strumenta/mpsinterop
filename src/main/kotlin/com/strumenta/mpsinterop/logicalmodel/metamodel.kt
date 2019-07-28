package com.strumenta.mpsinterop.logicalmodel

import com.strumenta.mpsinterop.physicalmodel.PhysicalNode
import com.strumenta.mpsinterop.physicalmodel.PhysicalRelation
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.util.*

enum class ConceptKind {
    NORMAL,
    INTERFACE,
    IMPLEMENTATION,
    IMPLEMENTATION_WITH_STUB
}

data class Concept(val id: Long, val name: String, val isInterface : Boolean = false) {

    val absoluteID : SConceptId?
        get() = if (language == null) null else SConceptId(language!!.id, id)

    var alias: String? = null
    var rootable: Boolean = false
    var final: Boolean = false
    var abstract: Boolean = false
    var extended: Concept? = null
    val implemented: MutableList<Concept> = LinkedList()
    val declaredProperties : MutableList<SProperty> = LinkedList()
    var language : Language? = null
        set(value) {
            if (field != null) {
                field?.remove(this)
            }
            field = value
            if (field != null) {
                field?.add(this)
            }
        }

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

    fun qname() : String {
        val languageName = languageName()
        return "$languageName.structure.$name"
    }

    private fun languageName() : String {
        if (language == null) {
            throw IllegalStateException("The concept is not attached to a languag")
        } else {
            return language!!.name
        }
    }

    fun findProperty(propertyName: String): SProperty {
        val p = allProperties.find { it.name == propertyName }
        if (p != null) {
            return p
        } else {
            throw IllegalArgumentException("No property found with name $propertyName")
        }
    }

    fun addProperty(property: SProperty) {
        if (property in declaredProperties) {
            return
        }
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

data class EnumerationAlternative(val name: String, val value: String?)
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

