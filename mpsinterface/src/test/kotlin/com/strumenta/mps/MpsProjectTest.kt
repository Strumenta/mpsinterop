package com.strumenta.mps

import org.junit.Test
import java.io.File
import kotlin.test.assertEquals

class MpsProjectTest {

    @Test
    fun languageNames() {
        val projectDir = File("src/test/resources/mpsserver_1")
        val project = MpsProject(projectDir)
        assertEquals(setOf("com.strumenta.businessorg", "com.strumenta.financialcalc"), project.languageNames())
    }

    @Test
    fun hasSolutionPositive() {
        val projectDir = File("src/test/resources/mpsserver_1")
        val project = MpsProject(projectDir)
        assertEquals(true, project.hasSolution("com.strumenta.mpsserver.build"))
    }

    @Test
    fun hasSolutionNegative() {
        val projectDir = File("src/test/resources/mpsserver_1")
        val project = MpsProject(projectDir)
        assertEquals(false, project.hasSolution("com.strumenta.mpsserver.build.foo"))
    }

}