package com.strumenta.mpsinterop.logicalmodel

import com.strumenta.mpsinterop.physicalmodel.PhysicalNode
import com.strumenta.mpsinterop.physicalmodel.PhysicalRelation
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.util.*

abstract class AbstractConcept(open val id: Long, open val name: String) {
    var rootable: Boolean = false
    private val _declaredProperties : MutableList<Property> = LinkedList()
    val declaredProperties : List<Property>
        get() = _declaredProperties
    abstract val allProperties : List<Property>
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

    val absoluteID : AbsoluteConceptId?
        get() = if (language == null) null else AbsoluteConceptId(language!!.id, id)

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

    fun addProperty(property: Property) {
        if (property in _declaredProperties) {
            return
        }
        _declaredProperties.add(property)
    }

    fun findProperty(propertyName: String): Property {
        val p = allProperties.find { it.name == propertyName }
        if (p != null) {
            return p
        } else {
            throw IllegalArgumentException("No property found with name $propertyName")
        }
    }

}

data class Concept(override val id: Long, override val name: String)
    : AbstractConcept(id, name){

    var alias: String? = null
    var final: Boolean = false
    var abstract: Boolean = false
    var extended: Concept? = null
    val implemented: MutableList<InterfaceConcept> = LinkedList()

    override val allProperties : List<Property>
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

}

data class InterfaceConcept(override val id: Long, override val name: String)
    : AbstractConcept(id, name){

    var alias: String? = null
    var final: Boolean = false
    var abstract: Boolean = false
    var extended: MutableList<InterfaceConcept> = LinkedList<InterfaceConcept>()

    override val allProperties : List<Property>
        get() {
            val props = LinkedList<Property>()
            extended.forEach {
                props.addAll(it.allProperties)
            }
            props.addAll(declaredProperties)
            return props
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
data class ReferenceLink(val link: AbsoluteReferenceLinkId, val name: String)
data class AbsoluteReferenceLinkId(val conceptId: AbsoluteConceptId, val idValue: Long)
interface Reference
data class StaticReference(val sref: PhysicalRelation, val node: PhysicalNode, val modelRef: SModelReference,
                           val targetNodeId: NodeId?, val resolveInfo: String?) : Reference

