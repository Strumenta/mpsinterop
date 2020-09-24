package com.strumenta.mps

import org.junit.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class BinaryLoadingTest {

    @Test
    fun loadLangCoreStructure() {
        val jar = File("src/test/resources/mps2019_3_1/languages/languageDesign/jetbrains.mps.lang.core-src.jar")
        val language = loadLanguage(JarEntrySource(jar, "module/jetbrains.mps.lang.core.mpl"))
        val models = language.models()
        assertEquals(12, models.size)
        val model = models.iterator().next()
        val structureModel = language.findModel("jetbrains.mps.lang.core.structure")
        assertNotNull(structureModel)
        val roots = structureModel.roots()
        assertEquals(43, roots.size)
        val baseConcept = roots.find { it.name == "BaseConcept" }
        assertNotNull(baseConcept)
        assertEquals("BaseConcept", baseConcept.name)
        assertEquals("1133920641626", baseConcept.id)
    }

    @Test
    fun loadToolCommon() {
        val jar = File("src/test/resources/mps2019_3_1/languages/tools/jetbrains.mps.tool.common-src.jar")
        val module = loadSolution(JarEntrySource(jar, "module/jetbrains.mps.tool.common.msd"))
        val models = module.models()
        assertEquals(1, models.size)
        val model = module.findModel("jetbrains.mps.core.tool.environment.common")
        assertNotNull(model)

        val problematicNode = model.findNodeByID("2326999218710315906")
        assertNotNull(problematicNode)
        val refVariableDeclaration = problematicNode.reference("variableDeclaration")
    }
}