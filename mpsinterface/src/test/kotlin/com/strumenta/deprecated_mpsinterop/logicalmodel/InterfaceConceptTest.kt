package com.strumenta.deprecated_mpsinterop.logicalmodel

import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals

class InterfaceConceptTest {

    @Test
    fun addPropertyOk() {
        val language = Language(UUID.randomUUID(), "MyLanguage")
        val intf = InterfaceConcept(124L, "MyConcept")
        language.add(intf)

        val property = Property(
            AbsolutePropertyId(intf.absoluteID!!, 12534L),
            "MyProperty",
            PrimitivePropertyType.STRING
        )
        intf.addProperty(property)

        assertEquals(listOf(property), intf.declaredProperties)
    }

    @Test
    fun addPropertyDuplicateDoesNothing() {
        val language = Language(UUID.randomUUID(), "MyLanguage")
        val intf = InterfaceConcept(124L, "MyConcept")
        language.add(intf)

        val property = Property(
            AbsolutePropertyId(intf.absoluteID!!, 12534L),
            "MyProperty",
            PrimitivePropertyType.STRING
        )
        intf.addProperty(property)
        intf.addProperty(property)
        assertEquals(listOf(property), intf.declaredProperties)
    }

    @Test(expected = IllegalArgumentException::class)
    fun addPropertyWithDuplicateName() {
        val language = Language(UUID.randomUUID(), "MyLanguage")
        val intf = InterfaceConcept(124L, "MyConcept")
        language.add(intf)

        val property = Property(
            AbsolutePropertyId(intf.absoluteID!!, 12534L),
            "MyProperty",
            PrimitivePropertyType.STRING
        )
        intf.addProperty(property)

        val property2 = Property(
            AbsolutePropertyId(intf.absoluteID!!, 1253433L),
            "MyProperty",
            PrimitivePropertyType.STRING
        )
        intf.addProperty(property2)
    }

    @Test
    fun allPropertiesIncludeInheritedProperties() {
        val language = Language(UUID.randomUUID(), "MyLanguage")

        val superIntf = InterfaceConcept(12444L, "MySuperConcept")
        language.add(superIntf)
        val superProperty = Property(
            AbsolutePropertyId(superIntf.absoluteID!!, 12534L),
            "MySuperProperty",
            PrimitivePropertyType.STRING
        )
        superIntf.addProperty(superProperty)

        val intf = InterfaceConcept(124L, "MyConcept")
        intf.extended.add(superIntf)
        language.add(intf)

        val property = Property(
            AbsolutePropertyId(intf.absoluteID!!, 12534L),
            "MyProperty",
            PrimitivePropertyType.STRING
        )
        intf.addProperty(property)

        assertEquals(true, intf.allProperties.contains(superProperty))
        assertEquals(2, intf.allProperties.size)
    }

    @Test
    fun allPropertiesIncludeDeclaredProperties() {
        val language = Language(UUID.randomUUID(), "MyLanguage")

        val superIntf = InterfaceConcept(12444L, "MySuperConcept")
        language.add(superIntf)
        val superProperty = Property(
            AbsolutePropertyId(superIntf.absoluteID!!, 12534L),
            "MySuperProperty",
            PrimitivePropertyType.STRING
        )
        superIntf.addProperty(superProperty)

        val intf = InterfaceConcept(124L, "MyConcept")
        intf.extended.add(superIntf)
        language.add(intf)

        val property = Property(
            AbsolutePropertyId(intf.absoluteID!!, 12534L),
            "MyProperty",
            PrimitivePropertyType.STRING
        )
        intf.addProperty(property)

        assertEquals(true, intf.allProperties.contains(property))
        assertEquals(2, intf.allProperties.size)
    }

    @Test
    fun hasPropertyNamedYes() {
        val language = Language(UUID.randomUUID(), "MyLanguage")
        val intf = InterfaceConcept(124L, "MyConcept")
        language.add(intf)

        val property = Property(
            AbsolutePropertyId(intf.absoluteID!!, 12534L),
            "MyProperty",
            PrimitivePropertyType.STRING
        )
        intf.addProperty(property)

        assertEquals(true, intf.hasPropertyNamed("MyProperty"))
    }

    @Test
    fun hasPropertyNamedNo() {
        val language = Language(UUID.randomUUID(), "MyLanguage")
        val intf = InterfaceConcept(124L, "MyConcept")
        language.add(intf)

        val property = Property(
            AbsolutePropertyId(intf.absoluteID!!, 12534L),
            "MyProperty",
            PrimitivePropertyType.STRING
        )
        intf.addProperty(property)

        assertEquals(false, intf.hasPropertyNamed("SomeWeirdName"))
    }

    @Test
    fun findPropertyYes() {
        val language = Language(UUID.randomUUID(), "MyLanguage")
        val intf = InterfaceConcept(124L, "MyConcept")
        language.add(intf)

        val property = Property(
            AbsolutePropertyId(intf.absoluteID!!, 12534L),
            "MyProperty",
            PrimitivePropertyType.STRING
        )
        intf.addProperty(property)

        assertEquals(property, intf.findProperty("MyProperty"))
    }

    @Test
    fun findPropertyNo() {
        val language = Language(UUID.randomUUID(), "MyLanguage")
        val intf = InterfaceConcept(124L, "MyConcept")
        language.add(intf)

        val property = Property(
            AbsolutePropertyId(intf.absoluteID!!, 12534L),
            "MyProperty",
            PrimitivePropertyType.STRING
        )
        intf.addProperty(property)

        assertEquals(null, intf.findProperty("SomeWeirdName"))
    }

    @Test
    fun getPropertyYes() {
        val language = Language(UUID.randomUUID(), "MyLanguage")
        val intf = InterfaceConcept(124L, "MyConcept")
        language.add(intf)

        val property = Property(
            AbsolutePropertyId(intf.absoluteID!!, 12534L),
            "MyProperty",
            PrimitivePropertyType.STRING
        )
        intf.addProperty(property)

        assertEquals(property, intf.getProperty("MyProperty"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun getPropertyNo() {
        val language = Language(UUID.randomUUID(), "MyLanguage")
        val intf = InterfaceConcept(124L, "MyConcept")
        language.add(intf)

        val property = Property(
            AbsolutePropertyId(intf.absoluteID!!, 12534L),
            "MyProperty",
            PrimitivePropertyType.STRING
        )
        intf.addProperty(property)

        intf.getProperty("SomeWeirdName")
    }

    @Test
    fun settingLanguageTheConceptAppearsInTheLanguage() {
        val language = Language(UUID.randomUUID(), "MyLanguage")
        val intf = InterfaceConcept(124L, "MyConcept")

        assertEquals(null, intf.language)
        assertEquals(false, language.concepts.contains(intf))
        intf.language = language
        assertEquals(language, intf.language)
        assertEquals(true, language.concepts.contains(intf))
    }

    @Test
    fun unsettingLanguageTheConceptDisappearsFromTheLanguage() {
        val language = Language(UUID.randomUUID(), "MyLanguage")
        val intf = InterfaceConcept(124L, "MyConcept")

        intf.language = language
        assertEquals(language, intf.language)
        assertEquals(true, language.concepts.contains(intf))
        intf.language = null
        assertEquals(null, intf.language)
        assertEquals(false, language.concepts.contains(intf))
    }

    @Test
    fun absoluteIDforDangling() {
        val intf = InterfaceConcept(124L, "MyConcept")

        assertEquals(null, intf.absoluteID)
    }

    @Test
    fun absoluteIDforNotDangling() {
        val languageUUID = UUID.fromString("3819ba36-98f4-49ac-b779-34f3a458c09b")
        val language = Language(languageUUID, "MyLanguage")
        val intf = InterfaceConcept(124L, "MyConcept")

        intf.language = language
        assertEquals(AbsoluteConceptId(languageUUID, 124L), intf.absoluteID)
    }

    @Test
    fun qualifiedNameForDangling() {
        val intf = InterfaceConcept(124L, "MyConcept")

        assertEquals(null, intf.qualifiedName())
    }

    @Test
    fun qualifiedNameForNotDangling() {
        val languageUUID = UUID.fromString("3819ba36-98f4-49ac-b779-34f3a458c09b")
        val language = Language(languageUUID, "MyLanguage")
        val intf = InterfaceConcept(124L, "MyConcept")

        intf.language = language
        assertEquals("MyLanguage.structure.MyConcept", intf.qualifiedName())
    }
}
