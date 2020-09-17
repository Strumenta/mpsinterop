package com.strumenta.deprecated_mpsinterop.physicalmodel

import com.strumenta.deprecated_mpsinterop.loading.loadMpsModel
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class PhysicalModelNamingTest {

    @Test
    fun physicalNodeNamePresent() {
        val model = loadMpsModel(
            PhysicalModelNamingTest::class.java.getResourceAsStream("/formats-structure.mps")
        )
        val field = model.findNodeByID(8160220614791809963L)
        assertNotNull(field)
        assertEquals("Field", field.name())
    }

    @Test
    fun physicalNodeNameNotPresent() {
        val model = loadMpsModel(
            PhysicalModelNamingTest::class.java.getResourceAsStream("/formats-structure.mps")
        )
        val anInterfaceConceptReference = model.findNodeByID(7828601061921860417L)
        assertNotNull(anInterfaceConceptReference)
        assertEquals(null, anInterfaceConceptReference.name())
    }
}
