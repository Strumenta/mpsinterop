package com.strumenta.mpsinterop.registries

import com.strumenta.mpsinterop.logicalmodel.Language
import com.strumenta.mpsinterop.logicalmodel.LanguageUUID
import com.strumenta.mpsinterop.logicalmodel.SConcept
import com.strumenta.mpsinterop.logicalmodel.SConceptId
import com.strumenta.mpsinterop.physicalmodel.InModelReferenceTarget
import com.strumenta.mpsinterop.physicalmodel.OutsideModelReferenceTarget
import com.strumenta.mpsinterop.physicalmodel.PhysicalModel
import com.strumenta.mpsinterop.physicalmodel.ReferenceTarget
import java.lang.RuntimeException
import java.util.*
import kotlin.collections.HashMap

class LanguageRegistry {

    private val languagesByName = HashMap<String, Language>()
    private val languagesByID = HashMap<LanguageUUID, Language>()

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

    fun loadLanguageFromModel(model: PhysicalModel) {
        model.roots.forEach {
            if (it.concept.qname == "jetbrains.mps.lang.structure.structure.ConceptDeclaration") {
                val languageName = model.name.removeSuffix(".structure")
                val language = if (languageName in this.languagesByName) {
                    this.languagesByName[languageName]!!
                } else {
                    val l = Language(model.languageUuidFromName(languageName), languageName)
                    languagesByID[model.uuid] = l
                    this.add(l)
                    l
                }
                val conceptIdValue : Long = it.propertyValue("conceptId").toLong()
                val conceptId = SConceptId(language.id, conceptIdValue)
                val conceptName = it.propertyValue("name")
                val concept = SConcept(conceptId, conceptName)
                language.concepts.add(concept)

                concept.final = it.booleanPropertyValue("final")
                concept.abstract = it.booleanPropertyValue("abstract")
                concept.rootable = it.booleanPropertyValue("rootable")

                // extends
                val extendsValue = it.reference("extends")
                if (extendsValue != null) {
                    concept.extended = this.resolveAsConcept(extendsValue.target)
                }
            } else if (it.concept.qname == "jetbrains.mps.lang.structure.structure.InterfaceConceptDeclaration") {
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

    private fun resolveAsConcept(target: ReferenceTarget): SConcept? {
        return when (target) {
            is InModelReferenceTarget -> TODO()
            is OutsideModelReferenceTarget -> {
                println(target.nodeID)
                val uuid = target.modelUIID
                if (uuid !in languagesByID) {
                    throw RuntimeException("Unknown language UUID $uuid")
                }
                val language = languagesByID[uuid]!!
                val concept = language.concepts.find { it.id.idValue == target.nodeID }!!
                return concept
            }
        }
    }

    operator fun get(id: LanguageUUID): Language? {
        return languagesByID[id]
    }

    fun getName(id: LanguageUUID): String? {
        return this[id]?.name
    }

    fun knowsLanguageUUID(uuid: UUID) = uuid in languagesByID

//    fun registerLanguage(languageId: UUID, langName: String) {
//        languageNamesByID[languageId] = langName
//    }
}