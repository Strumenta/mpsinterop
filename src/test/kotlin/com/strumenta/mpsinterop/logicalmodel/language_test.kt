package com.strumenta.mpsinterop.logicalmodel

import org.junit.Test
import java.util.*
import kotlin.test.assertEquals

class LanguageTest {

    @Test
    fun addConcept() {
        val lang = Language(UUID.randomUUID(), "FooLang")
        val conc = SConcept(123L, "FooConcept")

        assertEquals(0, lang.concepts.size)
        assertEquals(null, conc.language)
        lang.add(conc)
        assertEquals(1, lang.concepts.size)
        assertEquals(lang, conc.language)
        lang.add(conc)
        assertEquals(1, lang.concepts.size)
        assertEquals(lang, conc.language)
    }

    @Test
    fun removeConcept() {
        val lang = Language(UUID.randomUUID(), "FooLang")
        val conc = SConcept(123L, "FooConcept")

        assertEquals(0, lang.concepts.size)
        assertEquals(null, conc.language)
        lang.remove(conc)
        assertEquals(0, lang.concepts.size)
        assertEquals(null, conc.language)
        lang.add(conc)
        assertEquals(1, lang.concepts.size)
        assertEquals(lang, conc.language)
        lang.remove(conc)
        assertEquals(0, lang.concepts.size)
        assertEquals(null, conc.language)
    }

}

