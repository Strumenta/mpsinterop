package com.strumenta.mpsinterop.logicalmodel

import org.junit.Test
import java.util.*
import kotlin.test.assertEquals

class LanguageTest {

    @Test
    fun addConcept() {
        val lang = Language(UUID.randomUUID(), "FooLang")
        val conc = Concept(123L, "FooConcept")

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
        val conc = Concept(123L, "FooConcept")

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

    @Test
    fun hasConceptWithID() {
        val lang = Language(UUID.randomUUID(), "FooLang")
        val conc = Concept(123L, "FooConcept")

        assertEquals(false, lang.hasConceptWithID(123L))

        lang.add(conc)
        assertEquals(true, lang.hasConceptWithID(123L))

        lang.remove(conc)
        assertEquals(false, lang.hasConceptWithID(123L))
    }

    @Test
    fun conceptByID() {
        val lang = Language(UUID.randomUUID(), "FooLang")
        val conc = Concept(123L, "FooConcept")

        assertEquals(null, lang.conceptByID(123L))

        lang.add(conc)
        assertEquals(conc, lang.conceptByID(123L))
        assertEquals(null, lang.conceptByID(124L))

        lang.remove(conc)
        assertEquals(null, lang.conceptByID(123L))
    }

}

