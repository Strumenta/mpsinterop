package com.strumenta.mps

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class SourcesTest {

    @Test
    fun listChildrenUnder() {
        val source = JarEntrySource(File("src/test/resources/mps2019_3_1/languages/languageDesign/jetbrains.mps.lang.core-src.jar"), "module/jetbrains.mps.lang.core.mpl")
        val location = "languageModels"
        val children = source.listChildrenUnder(location)
        assertEquals(12, children.size)
    }
}
