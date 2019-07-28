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

    val absoluteID : AbsoluteConceptId?
        get() = if (language == null) null else AbsoluteConceptId(language!!.id, id)

    var alias: String? = null
    var rootable: Boolean = false
    var final: Boolean = false
    var abstract: Boolean = false
    var extended: Concept? = null
    val implemented: MutableList<Concept> = LinkedList()
    val declaredProperties : MutableList<Property> = LinkedList()
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

    val allProperties : List<Property>
        get() {
            val props = LinkedList<Property>()
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
            throw IllegalStateException("The concept is not attached to a language")
        } else {
            return language!!.name
        }
    }

    fun findProperty(propertyName: String): Property {
        val p = allProperties.find { it.name == propertyName }
        if (p != null) {
            return p
        } else {
            throw IllegalArgumentException("No property found with name $propertyName")
        }
    }

    fun addProperty(property: Property) {
        if (property in declaredProperties) {
            return
        }
        declaredProperties.add(property)
    }
}

data class AbsoluteConceptId(val languageId: LanguageUUID, val idValue: Long)
data class ContainmentLink(val link: AbsoluteContainmentLinkId, val name: String)
data class AbsoluteContainmentLinkId(val conceptId: AbsoluteConceptId, val idValue: Long)

interface PropertyType
enum class PrimitivePropertyType : PropertyType {
    BOOLEAN,
    INTEGER,
    STRING
}

data class EnumerationAlternative(val name: String, val value: String?)
data class EnumerationPropertyType(val name: String,
                                   val baseType: PrimitivePropertyType,
                                   val alternatives: List<EnumerationAlternative>) : PropertyType
data class ConstrainedDataTypeDeclaration(val qname: String) : PropertyType

data class Property(val sPropertyId: AbsolutePropertyId, val name: String, val type: PropertyType)
data class AbsolutePropertyId(val conceptId: AbsoluteConceptId, val idValue: Long)
data class ReferenceLink(val link: ReferenceLinkId, val name: String)
data class ReferenceLinkId(val conceptId: AbsoluteConceptId, val idValue: Long)
interface Reference
data class StaticReference(val sref: PhysicalRelation, val node: PhysicalNode, val modelRef: SModelReference,
                           val targetNodeId: SNodeId?, val resolveInfo: String?) : Reference

