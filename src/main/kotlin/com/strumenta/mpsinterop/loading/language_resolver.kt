package com.strumenta.mpsinterop.loading

import com.strumenta.mpsinterop.datamodel.Concept

interface LanguageResolver {
    fun conceptByName(name: String): Concept
}