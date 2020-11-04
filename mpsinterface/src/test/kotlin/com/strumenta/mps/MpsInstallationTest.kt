package com.strumenta.mps

import com.strumenta.mps.organization.MpsInstallation
import java.io.File
import java.util.*
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MpsInstallationTest {

    @Test
    fun loadModules() {
        val installation = MpsInstallation(File("src/test/resources/mps2019_3_1"))
        assertTrue(installation.modules.size > 200)
    }

    @Test
    fun loadModulesLangCore() {
        val installation = MpsInstallation(File("src/test/resources/mps2019_3_1"))
        val module = installation.findModule("jetbrains.mps.lang.core")
        assertNotNull(module)
    }

    @Test
    fun findModelLangCoreStructure() {
        val installation = MpsInstallation(File("src/test/resources/mps2019_3_1"))
        val uuid = UUID.fromString("00000000-0000-4000-0000-011c89590288")
        val model = installation.findModel(uuid)
        assertNotNull(model)
    }
}