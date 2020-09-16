package com.strumenta.deprecated_mpsinterop.utils

import org.junit.Test
import kotlin.test.assertEquals

class NamingTest {

    @Test
    fun simpleNameIsSimple() {
        assertEquals(true, "myName".isSimpleName)
    }

    @Test
    fun simpleNameIsNotQualified() {
        assertEquals(false, "myName".isQualifiedName)
    }

    @Test
    fun qualifiedNameIsNotSimple() {
        assertEquals(false, "my.qualifiedName".isSimpleName)
        assertEquals(false, "my.very.qualifiedName".isSimpleName)
    }

    @Test
    fun qualifiedNameIsQualified() {
        assertEquals(true, "my.qualifiedName".isQualifiedName)
        assertEquals(true, "my.very.qualifiedName".isQualifiedName)
    }
}
