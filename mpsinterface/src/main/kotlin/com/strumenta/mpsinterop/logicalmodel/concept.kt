package com.strumenta.mpsinterop.logicalmodel

import java.util.*

abstract class AbstractConcept(open val id: Long, open val name: String) {

    // /////////////////////////////////////
    // Simple fields
    // /////////////////////////////////////

    var final: Boolean = false
    var abstract: Boolean = false
    var alias: String? = null

    // /////////////////////////////////////
    // Properties
    // /////////////////////////////////////

    private val _declaredProperties: MutableList<Property> = LinkedList()

    // We want them to be exposed as an immutable list
    val declaredProperties: List<Property>
        get() = _declaredProperties

    abstract val allProperties: List<Property>

    fun addProperty(property: Property) {
        if (property in _declaredProperties) {
            return
        }
        if (hasPropertyNamed(property.name)) {
            throw IllegalArgumentException("Property with same name already present: ${property.name}")
        }

        _declaredProperties.add(property)
    }

    fun hasPropertyNamed(propertyName: String): Boolean {
        return allProperties.any { it.name == propertyName }
    }

    fun findProperty(propertyName: String): Property? {
        return allProperties.find { it.name == propertyName }
    }

    fun getProperty(propertyName: String): Property {
        return findProperty(propertyName) ?: throw IllegalArgumentException("No property found with name $propertyName")
    }

    fun hasProperty(property: Property) = allProperties.contains(property)

    // /////////////////////////////////////
    // Links
    // /////////////////////////////////////

    private val _declaredLinks: MutableList<Link> = LinkedList()

    // We want them to be exposed as an immutable list
    val declaredLinks: List<Link>
        get() = _declaredLinks

    abstract val allLinks: List<Link>

    fun addLink(link: Link) {
        if (link in _declaredLinks) {
            return
        }
        if (hasLinkNamed(link.name)) {
            throw IllegalArgumentException("Link with same name already present: ${link.name}")
        }

        _declaredLinks.add(link)
    }

    fun addContainmentLink(linkID: Long, linkName: String, multiplicity: Multiplicity) : ContainmentLink {
        val link = ContainmentLink(AbsoluteContainmentLinkId(this.absoluteID!!, linkID), linkName, multiplicity)
        this.addLink(link)
        return link
    }

    fun hasLinkNamed(name: String) = allLinks.any { it.name == name }

    fun hasLink(link: Link) = allLinks.contains(link)

    // /////////////////////////////////////
    // Language
    // /////////////////////////////////////

    var language: Language? = null
        set(value) {
            if (field != null) {
                field?.remove(this)
            }
            field = value
            if (field != null) {
                field?.add(this)
            }
        }

    private fun languageName(): String {
        if (language == null) {
            throw IllegalStateException("The concept is not attached to a language")
        } else {
            return language!!.name
        }
    }

    // /////////////////////////////////////
    // ID
    // /////////////////////////////////////

    val absoluteID: AbsoluteConceptId?
        get() = if (language == null) null else AbsoluteConceptId(language!!.id, id)

    // /////////////////////////////////////
    // Name
    // /////////////////////////////////////

    fun qualifiedName(): String? {
        if (language == null) {
            return null
        }
        val languageName = languageName()
        return "$languageName.structure.$name"
    }
}

data class Concept(override val id: Long, override val name: String) :
    AbstractConcept(id, name) {
    var rootable: Boolean = false
    var extended: Concept? = null
    val implemented: MutableList<InterfaceConcept> = LinkedList()

    override val allProperties: List<Property>
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

    override val allLinks: List<Link>
        get() {
            val links = LinkedList<Link>()
            if (extended != null) {
                links.addAll(extended!!.allLinks)
            }
            implemented.forEach {
                links.addAll(it.allLinks)
            }
            links.addAll(declaredLinks)
            return links
        }
}

data class InterfaceConcept(override val id: Long, override val name: String) :
    AbstractConcept(id, name) {

    var extended: MutableList<InterfaceConcept> = LinkedList()

    override val allProperties: List<Property>
        get() {
            val props = LinkedList<Property>()
            extended.forEach {
                props.addAll(it.allProperties)
            }
            props.addAll(declaredProperties)
            return props
        }

    override val allLinks: List<Link>
        get() {
            val links = LinkedList<Link>()
            extended.forEach {
                links.addAll(it.allLinks)
            }
            links.addAll(declaredLinks)
            return links
        }
}

data class AbsoluteConceptId(val languageId: LanguageUUID, val idValue: Long)
