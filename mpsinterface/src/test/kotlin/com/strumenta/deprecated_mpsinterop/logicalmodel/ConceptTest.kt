package com.strumenta.deprecated_mpsinterop.logicalmodel

import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals

class ConceptTest {

    @Test
    fun addPropertyOk() {
        val language = Language(UUID.randomUUID(), "MyLanguage")
        val concept = Concept(124L, "MyConcept")
        language.add(concept)

        val property = Property(
            AbsolutePropertyId(concept.absoluteID!!, 12534L),
            "MyProperty",
            PrimitivePropertyType.STRING
        )
        concept.addProperty(property)

        assertEquals(listOf(property), concept.declaredProperties)
    }

    @Test
    fun addPropertyDuplicateDoesNothing() {
        val language = Language(UUID.randomUUID(), "MyLanguage")
        val concept = Concept(124L, "MyConcept")
        language.add(concept)

        val property = Property(
            AbsolutePropertyId(concept.absoluteID!!, 12534L),
            "MyProperty",
            PrimitivePropertyType.STRING
        )
        concept.addProperty(property)
        concept.addProperty(property)
        assertEquals(listOf(property), concept.declaredProperties)
    }

    @Test(expected = IllegalArgumentException::class)
    fun addPropertyWithDuplicateName() {
        val language = Language(UUID.randomUUID(), "MyLanguage")
        val concept = Concept(124L, "MyConcept")
        language.add(concept)

        val property = Property(
            AbsolutePropertyId(concept.absoluteID!!, 12534L),
            "MyProperty",
            PrimitivePropertyType.STRING
        )
        concept.addProperty(property)

        val property2 = Property(
            AbsolutePropertyId(concept.absoluteID!!, 1253433L),
            "MyProperty",
            PrimitivePropertyType.STRING
        )
        concept.addProperty(property2)
    }

    @Test
    fun allPropertiesIncludeInheritedProperties() {
        val language = Language(UUID.randomUUID(), "MyLanguage")

        val superConcept = Concept(12444L, "MySuperConcept")
        language.add(superConcept)
        val superProperty = Property(
            AbsolutePropertyId(superConcept.absoluteID!!, 12534L),
            "MySuperProperty",
            PrimitivePropertyType.STRING
        )
        superConcept.addProperty(superProperty)

        val concept = Concept(124L, "MyConcept")
        concept.extended = superConcept
        language.add(concept)

        val property = Property(
            AbsolutePropertyId(concept.absoluteID!!, 12534L),
            "MyProperty",
            PrimitivePropertyType.STRING
        )
        concept.addProperty(property)

        assertEquals(true, concept.allProperties.contains(superProperty))
        assertEquals(2, concept.allProperties.size)
    }

    @Test
    fun allPropertiesIncludeDeclaredProperties() {
        val language = Language(UUID.randomUUID(), "MyLanguage")

        val superConcept = Concept(12444L, "MySuperConcept")
        language.add(superConcept)
        val superProperty = Property(
            AbsolutePropertyId(superConcept.absoluteID!!, 12534L),
            "MySuperProperty",
            PrimitivePropertyType.STRING
        )
        superConcept.addProperty(superProperty)

        val concept = Concept(124L, "MyConcept")
        concept.extended = superConcept
        language.add(concept)

        val property = Property(
            AbsolutePropertyId(concept.absoluteID!!, 12534L),
            "MyProperty",
            PrimitivePropertyType.STRING
        )
        concept.addProperty(property)

        assertEquals(true, concept.allProperties.contains(property))
        assertEquals(2, concept.allProperties.size)
    }

    @Test
    fun hasPropertyNamedYes() {
        val language = Language(UUID.randomUUID(), "MyLanguage")
        val concept = Concept(124L, "MyConcept")
        language.add(concept)

        val property = Property(
            AbsolutePropertyId(concept.absoluteID!!, 12534L),
            "MyProperty",
            PrimitivePropertyType.STRING
        )
        concept.addProperty(property)

        assertEquals(true, concept.hasPropertyNamed("MyProperty"))
    }

    @Test
    fun hasPropertyNamedNo() {
        val language = Language(UUID.randomUUID(), "MyLanguage")
        val concept = Concept(124L, "MyConcept")
        language.add(concept)

        val property = Property(
            AbsolutePropertyId(concept.absoluteID!!, 12534L),
            "MyProperty",
            PrimitivePropertyType.STRING
        )
        concept.addProperty(property)

        assertEquals(false, concept.hasPropertyNamed("SomeWeirdName"))
    }

    @Test
    fun findPropertyYes() {
        val language = Language(UUID.randomUUID(), "MyLanguage")
        val concept = Concept(124L, "MyConcept")
        language.add(concept)

        val property = Property(
            AbsolutePropertyId(concept.absoluteID!!, 12534L),
            "MyProperty",
            PrimitivePropertyType.STRING
        )
        concept.addProperty(property)

        assertEquals(property, concept.findProperty("MyProperty"))
    }

    @Test
    fun findPropertyNo() {
        val language = Language(UUID.randomUUID(), "MyLanguage")
        val concept = Concept(124L, "MyConcept")
        language.add(concept)

        val property = Property(
            AbsolutePropertyId(concept.absoluteID!!, 12534L),
            "MyProperty",
            PrimitivePropertyType.STRING
        )
        concept.addProperty(property)

        assertEquals(null, concept.findProperty("SomeWeirdName"))
    }

    @Test
    fun getPropertyYes() {
        val language = Language(UUID.randomUUID(), "MyLanguage")
        val concept = Concept(124L, "MyConcept")
        language.add(concept)

        val property = Property(
            AbsolutePropertyId(concept.absoluteID!!, 12534L),
            "MyProperty",
            PrimitivePropertyType.STRING
        )
        concept.addProperty(property)

        assertEquals(property, concept.getProperty("MyProperty"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun getPropertyNo() {
        val language = Language(UUID.randomUUID(), "MyLanguage")
        val concept = Concept(124L, "MyConcept")
        language.add(concept)

        val property = Property(
            AbsolutePropertyId(concept.absoluteID!!, 12534L),
            "MyProperty",
            PrimitivePropertyType.STRING
        )
        concept.addProperty(property)

        concept.getProperty("SomeWeirdName")
    }

    @Test
    fun settingLanguageTheConceptAppearsInTheLanguage() {
        val language = Language(UUID.randomUUID(), "MyLanguage")
        val concept = Concept(124L, "MyConcept")

        assertEquals(null, concept.language)
        assertEquals(false, language.concepts.contains(concept))
        concept.language = language
        assertEquals(language, concept.language)
        assertEquals(true, language.concepts.contains(concept))
    }

    @Test
    fun unsettingLanguageTheConceptDisappearsFromTheLanguage() {
        val language = Language(UUID.randomUUID(), "MyLanguage")
        val concept = Concept(124L, "MyConcept")

        concept.language = language
        assertEquals(language, concept.language)
        assertEquals(true, language.concepts.contains(concept))
        concept.language = null
        assertEquals(null, concept.language)
        assertEquals(false, language.concepts.contains(concept))
    }

    @Test
    fun absoluteIDforDangling() {
        val concept = Concept(124L, "MyConcept")

        assertEquals(null, concept.absoluteID)
    }

    @Test
    fun absoluteIDforNotDangling() {
        val languageUUID = UUID.fromString("3819ba36-98f4-49ac-b779-34f3a458c09b")
        val language = Language(languageUUID, "MyLanguage")
        val concept = Concept(124L, "MyConcept")

        concept.language = language
        assertEquals(AbsoluteConceptId(languageUUID, 124L), concept.absoluteID)
    }

    @Test
    fun qualifiedNameForDangling() {
        val concept = Concept(124L, "MyConcept")

        assertEquals(null, concept.qualifiedName())
    }

    @Test
    fun qualifiedNameForNotDangling() {
        val languageUUID = UUID.fromString("3819ba36-98f4-49ac-b779-34f3a458c09b")
        val language = Language(languageUUID, "MyLanguage")
        val concept = Concept(124L, "MyConcept")

        concept.language = language
        assertEquals("MyLanguage.structure.MyConcept", concept.qualifiedName())
    }
}
