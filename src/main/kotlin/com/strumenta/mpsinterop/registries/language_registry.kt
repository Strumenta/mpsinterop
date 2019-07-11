package com.strumenta.mpsinterop.registries

import com.strumenta.mpsinterop.logicalmodel.Language

class LanguageRegistry {

    private val languagesByName = HashMap<String, Language>()

    companion object {
        val DEFAULT = LanguageRegistry()
    }

    operator fun get(name: String) = languagesByName[name]

    fun add(language: Language) {
        languagesByName[language.name] = language
    }
}