package com.strumenta.mps.utils

import java.io.File
import java.util.jar.JarFile
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class JarTest {

    @Test
    fun directChildrenOf() {
        val file = File("src/test/resources/mps2019_3_1/languages/languageDesign/jetbrains.mps.lang.core-src.jar")
        val jarFile = JarFile(file)
        val moduleEntry = jarFile.getJarEntry("module")
        val moduleEntryChildren = jarFile.directChildrenOf(moduleEntry)
        assertEquals(3, moduleEntryChildren.size)
    }

    @Test
    fun isDirectChildOf() {
        val file = File("src/test/resources/mps2019_3_1/languages/languageDesign/jetbrains.mps.lang.core-src.jar")
        val jarFile = JarFile(file)
        val moduleEntry = jarFile.getJarEntry("module")
        val moduleMplFileEntry = jarFile.getJarEntry("module/jetbrains.mps.lang.core.mpl")
        assertTrue(moduleMplFileEntry.isDirectChildOf(moduleEntry))
        assertFalse(moduleEntry.isDirectChildOf(moduleMplFileEntry))
    }

    @Test
    fun level() {
        val file = File("src/test/resources/mps2019_3_1/languages/languageDesign/jetbrains.mps.lang.core-src.jar")
        val jarFile = JarFile(file)
        val moduleEntry = jarFile.getJarEntry("module")
        val moduleMplFileEntry = jarFile.getJarEntry("module/jetbrains.mps.lang.core.mpl")
        assertEquals(1, moduleEntry.level())
        assertEquals(2, moduleMplFileEntry.level())
    }

    @Test
    fun name() {
        val file = File("src/test/resources/mps2019_3_1/languages/languageDesign/jetbrains.mps.lang.core-src.jar")
        val jarFile = JarFile(file)
        val moduleEntry = jarFile.getJarEntry("module")
        val moduleMplFileEntry = jarFile.getJarEntry("module/jetbrains.mps.lang.core.mpl")
        assertEquals("module/", moduleEntry.name)
        assertEquals("module/jetbrains.mps.lang.core.mpl", moduleMplFileEntry.name)
    }
}