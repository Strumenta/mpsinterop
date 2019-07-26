package com.strumenta.mpsinterop.registries

import com.strumenta.mpsinterop.logicalmodel.*
import com.strumenta.mpsinterop.physicalmodel.*
import com.strumenta.mpsinterop.utils.JavaFriendlyBase64
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
        // We solve stuff in two rounds, because there could be dependency between concepts
        val concepts = HashMap<PhysicalNode, SConcept>()
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
                //println(conceptName)
                val concept = SConcept(conceptId, conceptName)
                concepts[it] = concept
                language.concepts.add(concept)

//                concept.final = it.booleanPropertyValue("final")
//                concept.abstract = it.booleanPropertyValue("abstract")
//                concept.rootable = it.booleanPropertyValue("rootable")
//
//                // extends
//                val extendsValue = it.reference("extends")
//                if (extendsValue != null) {
//                    concept.extended = this.resolveAsConcept(extendsValue.target)
//                }
            } else if (it.concept.qname == "jetbrains.mps.lang.structure.structure.InterfaceConceptDeclaration") {
                val languageName = model.name.removeSuffix(".structure")
                val language = this.languagesByName[languageName]!!
                val conceptIdValue : Long = it.propertyValue("conceptId").toLong()
                val conceptId = SConceptId(language.id, conceptIdValue)
                val conceptName = it.propertyValue("name")
                val concept = SConcept(conceptId, conceptName, true)
                language.concepts.add(concept)
                concepts[it] = concept

//                concept.final = it.booleanPropertyValue("final")
//                concept.abstract = it.booleanPropertyValue("abstract")
//                concept.rootable = it.booleanPropertyValue("rootable")
            }
            //println(it.concept.qname(this))
        }


        model.roots.forEach {
            if (it.concept.qname == "jetbrains.mps.lang.structure.structure.ConceptDeclaration") {

                val concept = concepts[it]!!

                concept.final = it.booleanPropertyValue("final")
                concept.abstract = it.booleanPropertyValue("abstract")
                concept.rootable = it.booleanPropertyValue("rootable")

                // extends
                val extendsValue = it.reference("extends")
                if (extendsValue != null) {
                    concept.extended = this.resolveAsConcept(extendsValue.target)
                }

                it.children("propertyDeclaration").forEach {
                    val name = it.propertyValue("name")
                    val conceptId = concept.id
                    val idValue: Long = it.propertyValue("propertyId").toLong()
                    val dataType = it.reference("dataType")
                            ?: throw RuntimeException("Reference dataType not found in node $name of type $conceptId, in concept ${concept.name}")
                    dataType.target
                    val propertyType = TODO()
                    concept.addProperty(SProperty(SPropertyId(conceptId, idValue), name, propertyType))
                }
                it.children("linkDeclaration").forEach {
                    //TODO()
                }
            } else if (it.concept.qname == "jetbrains.mps.lang.structure.structure.InterfaceConceptDeclaration") {
                val concept = concepts[it]!!

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
            is InModelReferenceTarget -> {
                val uuid = target.physicalModel.uuid
                if (uuid !in languagesByID) {
                    throw RuntimeException("Unknown language UUID $uuid")
                }
                val language = languagesByID[uuid]!!
                val concept = language.concepts.find {
                    it.id.idValue == JavaFriendlyBase64.parseLong(target.nodeID)
                } ?: throw RuntimeException("Concept not found (ID ${target.nodeID})")
                return concept
            }
            is OutsideModelReferenceTarget -> {
                val uuid = target.modelUIID
                if (uuid !in languagesByID) {
                    throw RuntimeException("Unknown language UUID $uuid")
                }
                val language = languagesByID[uuid]!!
                val concept = language.concepts.find { it.id.idValue == target.nodeID }!!
                return concept
            }
            is NullReferenceTarget -> null
            else -> TODO()
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