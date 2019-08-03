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

        val internalValue = enumerationMemberDeclaration.propertyByName("internalValue")
        assertEquals("internalValue", internalValue.name)
        assertEquals("1uS6qv", internalValue.index)
        assertEquals(1083923523171L, internalValue.id)
    }

    // properties
    // relations
}
