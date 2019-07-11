package com.strumenta.mpsinterop.registries

import com.strumenta.mpsinterop.logicalmodel.Language

class LanguageRegistry {

    companion object {
        val DEFAULT = LanguageRegistry()
    }

    operator fun get(name: String) {
        TODO()
    }

    operator fun set(name: String, language: Language) {
        TODO()
    }
}