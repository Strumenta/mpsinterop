package com.strumenta.mpsinterop.registries

import com.strumenta.mpsinterop.binary.SModel
import com.strumenta.mpsinterop.logicalmodel.Language
import com.strumenta.mpsinterop.logicalmodel.SConcept

class LanguageRegistry {

    private val languagesByName = HashMap<String, Language>()

    companion object {
        val DEFAULT = LanguageRegistry()
    }

    operator fun get(name: String) = languagesByName[name]

    fun add(language: Language) {
        languagesByName[language.name] = language
    }

    fun getConcept(conceptName: String): SConcept? {
        TODO("Not implemented: $conceptName")
    }

    fun loadLanguageFromModel(model: SModel) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}