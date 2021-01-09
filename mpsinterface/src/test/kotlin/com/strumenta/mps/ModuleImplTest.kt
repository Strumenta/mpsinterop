package com.strumenta.mps

import org.junit.Test
import java.io.File
import kotlin.test.assertTrue

class ModuleImplTest {

    @Test
    fun modelsForLangCore() {
        val langCoreJar = File("src/test/resources/mps2019_3_1/languages/languageDesign/jetbrains.mps.lang.core-src.jar")
        val language = loadLanguage(JarEntrySource(langCoreJar, "module/jetbrains.mps.lang.core.mpl"))
        assertTrue(language.models().isNotEmpty())
    }

    @Test
    fun modelsForToolCommon() {
        val jar = File("src/test/resources/mps2019_3_1/languages/tools/jetbrains.mps.tool.common-src.jar")
        val solution = loadSolution(JarEntrySource(jar, "module/jetbrains.mps.tool.common.msd"))
        assertTrue(solution.models().isNotEmpty())
    }
}
