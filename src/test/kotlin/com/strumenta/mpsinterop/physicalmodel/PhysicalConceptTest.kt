package com.strumenta.mpsinterop.physicalmodel

import com.strumenta.mpsinterop.loading.loadMpsModel
import org.junit.Test
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

    // properties
    // relations
}
