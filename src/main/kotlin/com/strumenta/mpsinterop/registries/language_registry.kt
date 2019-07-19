package com.strumenta.mpsinterop.registries

import com.strumenta.mpsinterop.binary.SModel
import com.strumenta.mpsinterop.logicalmodel.Language
import com.strumenta.mpsinterop.logicalmodel.LanguageId
import com.strumenta.mpsinterop.logicalmodel.SConcept
import com.strumenta.mpsinterop.logicalmodel.SConceptId
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
        for (l in languagesByID.values) {
            val concept = l.concepts.find { it.qname(this) == conceptName }
            if (concept != null) {
                return concept
            }
        }
        return null
    }

    fun loadLanguageFromModel(model: SModel) {
        model.roots.forEach {
            if (it.concept.qname(this) == "jetbrains.mps.lang.structure.ConceptDeclaration") {
                val languageName = model.name.removeSuffix(".structure")
                val language = this.languagesByName[languageName]!!
                val conceptIdValue : Long = it.propertyValue("conceptId").toLong()
                val conceptId = SConceptId(language.id, conceptIdValue)
                val conceptName = it.propertyValue("name")
                val concept = SConcept(conceptId, conceptName)
                language.concepts.add(concept)

                concept.final = it.booleanPropertyValue("final")
                concept.abstract = it.booleanPropertyValue("abstract")
                concept.rootable = it.booleanPropertyValue("rootable")
            } else if (it.concept.qname(this) == "jetbrains.mps.lang.structure.InterfaceConceptDeclaration") {
                val languageName = model.name.removeSuffix(".structure")
                val language = this.languagesByName[languageName]!!
                val conceptIdValue : Long = it.propertyValue("conceptId").toLong()
                val conceptId = SConceptId(language.id, conceptIdValue)
                val conceptName = it.propertyValue("name")
                val concept = SConcept(conceptId, conceptName, true)
                language.concepts.add(concept)

                concept.final = it.booleanPropertyValue("final")
                concept.abstract = it.booleanPropertyValue("abstract")
                concept.rootable = it.booleanPropertyValue("rootable")
            }
            //println(it.concept.qname(this))
        }
        //val language = model.
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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