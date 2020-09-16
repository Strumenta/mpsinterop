package com.strumenta.deprecated_mpsinterop.loading

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class PhysicalModuleLoadingTest {

    @Test
    fun loadASimpleSolution() {
        val module = loadSolution(PhysicalModuleLoadingTest::class.java.getResourceAsStream(
                "/com.strumenta.data.stdlib.msd"))
        assertEquals(UUID.fromString("252065ef-9fba-45ef-9478-0581a0595ac3"), module.uuid)
        assertEquals("com.strumenta.data.stdlib", module.name)
    }
}
