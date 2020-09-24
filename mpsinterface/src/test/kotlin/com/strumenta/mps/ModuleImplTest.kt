package com.strumenta.mps

import org.junit.Test
import java.io.File
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ModuleImplTest {

    @Test
    fun modelsForLangCore() {
        val langCoreJar = File("src/test/resources/mps2019_3_1/languages/languageDesign/jetbrains.mps.lang.core-src.jar")
        val language = loadLanguage(JarEntrySource(langCoreJar, "module/jetbrains.mps.lang.core.mpl"))
        assertTrue(language.models().isNotEmpty())
    }
}
