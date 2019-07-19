package com.strumenta.mpsinterop.registries

import com.strumenta.mpsinterop.binary.SModel
import com.strumenta.mpsinterop.logicalmodel.Language
import com.strumenta.mpsinterop.logicalmodel.LanguageId
import com.strumenta.mpsinterop.logicalmodel.SConcept
import java.util.*
import kotlin.collections.HashMap

class LanguageRegistry {

    private val languagesByName = HashMap<String, Language>()
    private val languagesByID = HashMap<LanguageId, Language>()

    companion object {
        val DEFAULT = LanguageRegistry()
    }

    operator fun get(name: String) = languagesByName[name]

    fun add(language: Language) {
        languagesByName[language.name] = language
        languagesByID[language.id] = language
    }

    fun getConcept(conceptName: String): SConcept? {
        TODO("Not implemented: $conceptName")
    }

    fun loadLanguageFromModel(model: SModel) {
        model.roots.forEach {
            println(it)
            if (it.concept.qname(this) == "jetbrains.mps.lang.structure.ConceptDeclaration") {

            }
            println(it.concept.qname(this))
        }
        //val language = model.
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    operator fun get(id: LanguageId): Language? {
        return languagesByID[id]
    }

    fun getName(id: LanguageId): String? {
        return this[id]?.name
    }

//    fun registerLanguage(languageId: UUID, langName: String) {
//        languageNamesByID[languageId] = langName
//    }
}