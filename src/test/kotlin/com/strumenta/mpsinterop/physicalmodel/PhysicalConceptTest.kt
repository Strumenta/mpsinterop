package com.strumenta.mpsinterop.physicalmodel

import com.strumenta.mpsinterop.loading.loadMpsModel
import org.junit.Test
import java.lang.IllegalArgumentException
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class PhysicalConceptTest {

    @Test
    fun conceptSimpleName() {
        val model = loadMpsModel(
                PhysicalConceptTest::class.java.getResourceAsStream("/formats-structure.mps"))
        val enumerationMemberDeclaration = model.findConceptByID(1083171877298L)
        assertNotNull(enumerationMemberDeclaration)
        assertEquals("EnumerationMemberDeclaration", enumerationMemberDeclaration.name)
    }

    @Test
    fun conceptQualifiedName() {
        val model = loadMpsModel(
                PhysicalConceptTest::class.java.getResourceAsStream("/formats-structure.mps"))
        val enumerationMemberDeclaration = model.findConceptByID(1083171877298L)
        assertNotNull(enumerationMemberDeclaration)
        assertEquals("jetbrains.mps.lang.structure.structure.EnumerationMemberDeclaration", enumerationMemberDeclaration.qualifiedName)
    }

    @Test
    fun conceptIndex() {
        val model = loadMpsModel(
                PhysicalConceptTest::class.java.getResourceAsStream("/formats-structure.mps"))
        val enumerationMemberDeclaration = model.findConceptByID(1083171877298L)
        assertNotNull(enumerationMemberDeclaration)
        assertEquals("M4N5e", enumerationMemberDeclaration.index)
    }

    @Test(expected = IllegalArgumentException::class)
    fun getUnexistingProperty() {
        val model = loadMpsModel(
                PhysicalConceptTest::class.java.getResourceAsStream("/formats-structure.mps"))
        val enumerationMemberDeclaration = model.findConceptByID(1083171877298L)
        assertNotNull(enumerationMemberDeclaration)
        enumerationMemberDeclaration.propertyByName("MyUnexistingProperty")
    }

    @Test
    fun getExistingProperty() {
        val model = loadMpsModel(
                PhysicalConceptTest::class.java.getResourceAsStream("/formats-structure.mps"))
        val enumerationMemberDeclaration = model.findConceptByID(1083171877298L)
        assertNotNull(enumerationMemberDeclaration)

        val externalValue = enumerationMemberDeclaration.propertyByName("externalValue")
        assertEquals("externalValue", externalValue.name)
        assertEquals("1uS6qo", externalValue.index)
        assertEquals(1083923523172L, externalValue.id)
        assertEquals(enumerationMemberDeclaration, externalValue.container)

        val internalValue = enumerationMemberDeclaration.propertyByName("internalValue")
        assertEquals("internalValue", internalValue.name)
        assertEquals("1uS6qv", internalValue.index)
        assertEquals(1083923523171L, internalValue.id)
        assertEquals(enumerationMemberDeclaration, internalValue.container)
    }

    @Test(expected = IllegalArgumentException::class)
    fun getUnexistingRelation() {
        val model = loadMpsModel(
                PhysicalConceptTest::class.java.getResourceAsStream("/formats-structure.mps"))
        val enumerationMemberDeclaration = model.findConceptByID(1083171877298L)
        assertNotNull(enumerationMemberDeclaration)
        enumerationMemberDeclaration.relationByName("MyUnexistingRelation")
    }

    @Test
    fun getExistingContainment() {
        val model = loadMpsModel(
                PhysicalConceptTest::class.java.getResourceAsStream("/formats-structure.mps"))
        val abstractConceptDeclaration = model.findConceptByID(1169125787135L)
        assertNotNull(abstractConceptDeclaration)

        val linkDeclaration = abstractConceptDeclaration.relationByName("linkDeclaration")
        assertEquals("linkDeclaration", linkDeclaration.name)
        assertEquals("1TKVEi", linkDeclaration.index)
        assertEquals(1071489727083L, linkDeclaration.id)
        assertEquals(RelationKind.CONTAINMENT, linkDeclaration.kind)
        assertEquals(abstractConceptDeclaration, linkDeclaration.container)

        val propertyDeclaration = abstractConceptDeclaration.relationByName("propertyDeclaration")
        assertEquals("propertyDeclaration", propertyDeclaration.name)
        assertEquals("1TKVEl", propertyDeclaration.index)
        assertEquals(1071489727084L, propertyDeclaration.id)
        assertEquals(RelationKind.CONTAINMENT, propertyDeclaration.kind)
        assertEquals(abstractConceptDeclaration, propertyDeclaration.container)
    }

    @Test
    fun getExistingReference() {
        val model = loadMpsModel(
                PhysicalConceptTest::class.java.getResourceAsStream("/formats-structure.mps"))
        val linkDeclaration = model.findConceptByID(1071489288298L)
        assertNotNull(linkDeclaration)

        val target = linkDeclaration.relationByName("target")
        assertEquals("target", target.name)
        assertEquals("20lvS9", target.index)
        assertEquals(1071599976176L, target.id)
        assertEquals(RelationKind.REFERENCE, target.kind)
        assertEquals(linkDeclaration, target.container)
    }
}
