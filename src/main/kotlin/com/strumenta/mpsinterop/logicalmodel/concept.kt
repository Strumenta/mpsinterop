package com.strumenta.mpsinterop.logicalmodel

import java.util.*

abstract class AbstractConcept(open val id: Long, open val name: String) {
    var final: Boolean = false
    var abstract: Boolean = false
    var alias: String? = null

    private val _declaredProperties : MutableList<Property> = LinkedList()

    // We want them to be exposed as an immutable list
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
        if (hasPropertyNamed(property.name)) {
            throw java.lang.IllegalStateException("Property with same name already present: ${property.name}")
        }

        _declaredProperties.add(property)
    }

    fun hasPropertyNamed(propertyName: String): Boolean {
        return allProperties.any { it.name == propertyName }
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
    var rootable: Boolean = false
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

