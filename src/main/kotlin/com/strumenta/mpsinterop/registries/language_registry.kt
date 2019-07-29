package com.strumenta.mpsinterop.registries

import com.strumenta.mpsinterop.loading.ModelLocator
import com.strumenta.mpsinterop.loading.NodeLocator
import com.strumenta.mpsinterop.loading.SimpleNodeLocator
import com.strumenta.mpsinterop.logicalmodel.*
import com.strumenta.mpsinterop.physicalmodel.*
import com.strumenta.mpsinterop.utils.JavaFriendlyBase64
import java.lang.RuntimeException
import java.util.*
import kotlin.collections.HashMap

class LanguageRegistry : ModelLocator {
    override fun locate(modelUUID: UUID): PhysicalModel? {
        return modelsByID[modelUUID]
    }

    //private val languagesByName = HashMap<String, Language>()
    private val languagesByID = HashMap<LanguageUUID, Language>()
    private val modelsByID = HashMap<LanguageUUID, PhysicalModel>()
    private val nodeLocator: NodeLocator = SimpleNodeLocator(this)

    companion object {
        val DEFAULT = LanguageRegistry()
    }

    //operator fun get(name: String) = languagesByID[name]

    fun add(language: Language) {
        //languagesByName[language.name] = language
        languagesByID[language.id] = language
    }

    fun getConcept(conceptName: String): AbstractConcept? {
        for (l in languagesByID.values) {
            val concept = l.concepts.find { it.qname() == conceptName }
            if (concept != null) {
                return concept
            }
        }
        return null
    }

    // Wrong, this is the UUID of the structure model of the language,
    // not the UUID of the language
    fun languageIDforConceptNode(it: PhysicalNode) : UUID {
        val module = it.model!!.module ?: throw RuntimeException("No module for model ${it.model!!.name}")
        return module.uuid
    }
    fun conceptIDforConceptNode(it: PhysicalNode) = it.propertyValue("conceptId")?.toLong()

    fun preloadConcept(it: PhysicalNode) : AbstractConcept? {
        val model = it.model!!
        if (it.concept.qname == CONCEPT_DECLARATION_CONCEPT_NAME) {
            val languageName = model.name.removeSuffix(".structure")
            val languageID = languageIDforConceptNode(it)
            val language = if (languageID in this.languagesByID) {
                val l = this.languagesByID[languageID]!!
                l
            } else {
                val l = Language(languageID, languageName)
                this.add(l)
                l
            }
//            val language = if (languageName in this.languagesByName) {
//                val l = this.languagesByName[languageName]!!
//                if (l.id != languageIDforConceptNode(it)) {
//                    throw RuntimeException("We have two languages with same name and different ID")
//                }
//                languagesByID[l.id] = l
//                l
//            } else {
//                val l = Language(model.languageUuidFromName(languageName), languageName)
//                languagesByID[model.uuid] = l
//                this.add(l)
//                l
//            }
            val conceptIdValue : Long = conceptIDforConceptNode(it)!!
            val conceptId = AbsoluteConceptId(language.id, conceptIdValue)
            val conceptName = it.propertyValue("name")!!
            //println(conceptName)
            val concept = Concept(conceptIdValue, conceptName)
            //concepts[it] = concept
            language.add(concept)

//                concept.final = it.booleanPropertyValue("final")
//                concept.abstract = it.booleanPropertyValue("abstract")
//                concept.rootable = it.booleanPropertyValue("rootable")
//
//                // extends
//                val extendsValue = it.reference("extends")
//                if (extendsValue != null) {
//                    concept.extended = this.resolveAsConcept(extendsValue.target)
//                }
            return concept
        } else if (it.concept.qname == INTERFACE_CONCEPT_DECLARATION_CONCEPT_NAME) {
            val languageName = model.name.removeSuffix(".structure")
            val languageID = languageIDforConceptNode(it)
            val language = if (languageID in this.languagesByID) {
                val l = this.languagesByID[languageID]!!
                l
            } else {
                val l = Language(model.languageUuidFromName(languageName), languageName)
                this.add(l)
                l
            }
            val conceptIdValue : Long = it.propertyValue("conceptId")!!.toLong()
            val conceptId = AbsoluteConceptId(language.id, conceptIdValue)
            val conceptName = it.propertyValue("name")!!
            val concept = InterfaceConcept(conceptIdValue, conceptName)
            language.add(concept)
            //concepts[it] = concept

//                concept.final = it.booleanPropertyValue("final")
//                concept.abstract = it.booleanPropertyValue("abstract")
//                concept.rootable = it.booleanPropertyValue("rootable")
            return concept
        }
        return null
    }

    fun loadLanguageFromModel(model: PhysicalModel) {
        // We solve stuff in two rounds, because there could be dependency between concepts
        val concepts = HashMap<PhysicalNode, AbstractConcept>()
        modelsByID[model.uuid] = model
        model.roots.forEach {
            val concept = preloadConcept(it)
            if (concept != null) {
                concepts[it] = concept
            }
            //println(it.concept.qname(this))
        }

        model.roots.forEach {
            loadConceptFromNode(it)
            //println(it.concept.qname(this))
        }
        //val language = model.
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    private fun loadPropertyTypeFromNode(node: PhysicalNode) : PropertyType {
        when {
            node.concept.qname == PRIMITIVE_DATA_TYPE_DECLARATION_CONCEPT_NAME -> {
                val name = node.name()!!
                return PrimitivePropertyType.valueOf(name.toUpperCase())
            }
            node.concept.qname == ENUMERATION_DATA_TYPE_DECLARATION_CONCEPT_NAME -> {
                val baseTypeNode = nodeLocator.resolve(node.reference("memberDataType")!!.target)!!
                val alternatives = node.children("member").map {
                    loadEnumerationAlternative(it)
                }.toList()
                val enumerationType = EnumerationPropertyType(
                        node.name()!!,
                        loadPropertyTypeFromNode(baseTypeNode) as PrimitivePropertyType,
                        alternatives
                )
                return enumerationType
            }
            node.concept.qname == CONSTRAINED_DATA_TYPE_DECLARATION_CONCEPT_NAME -> {
                val qname = node.qname()
                return ConstrainedPropertyType(qname)
            }
            else -> TODO(node.concept.qname)
        }
    }

    private fun loadEnumerationAlternative(node: PhysicalNode) : EnumerationAlternative {
        return EnumerationAlternative(node.propertyValue("externalValue")!!,
                node.propertyValue("internalValue", null))
    }

    private fun loadConceptFromNode(it: PhysicalNode) : AbstractConcept? {
        val conceptID = conceptIDforConceptNode(it)
        if (conceptID == null) {
            return null
        }
        val langUUID = languageIDforConceptNode(it)
        var concept = languagesByID[langUUID]?.conceptByID(conceptID)
        if (concept == null) {
            concept = preloadConcept(it)
            if (concept == null) {
                return null
            }
            val l = languagesByID[langUUID]
                    ?: throw RuntimeException("Language with ID $langUUID not found")
            l.add(concept)
        }

        when {
            it.concept.qname == CONCEPT_DECLARATION -> {

                (concept as Concept).final = it.booleanPropertyValue("final")
                concept.abstract = it.booleanPropertyValue("abstract")
                concept.rootable = it.booleanPropertyValue("rootable")
                concept.alias = it.stringPropertyValue("conceptAlias")

                // extends
                val extendsValue = it.reference("extends")
                if (extendsValue != null) {
                    try {
                        concept.extended = this.resolveAsConcept(extendsValue.target) as Concept
                    } catch (e: Exception) {

                    }
                }

                // implements
                val implementsValue = it.children("implements")
                concept.implemented.clear()
                concept.implemented.addAll(implementsValue.map {
                    val interfaceDeclaration = this.resolveAsConcept(it.reference("intfc")!!.target) as InterfaceConcept
                    interfaceDeclaration!!
                })

                it.children("propertyDeclaration").forEach {
                    val name = it.propertyValue("name")!!
                    val conceptId = concept.id
                    val idValue: Long = it.propertyValue("propertyId")!!.toLong()
                    val dataType = it.reference("dataType")
                            ?: throw RuntimeException("Reference dataType not found in node $name of type $conceptId, in concept ${concept.name}")
                    val propertyTypeNode = nodeLocator.resolve(dataType.target)!!
                    val propertyType = loadPropertyTypeFromNode(propertyTypeNode)
                    concept.addProperty(Property(AbsolutePropertyId(concept.absoluteID!!, idValue), name, propertyType))
                }
                it.children("linkDeclaration").forEach {
                    //TODO()
                }
            }
            it.concept.qname == INTERFACE_CONCEPT_DECLARATION_CONCEPT_NAME -> {

                (concept as InterfaceConcept).final = it.booleanPropertyValue("final")
                concept.abstract = it.booleanPropertyValue("abstract")

                it.children("propertyDeclaration").forEach {
                    val name = it.propertyValue("name")!!
                    val conceptId = concept.id
                    val idValue: Long = it.propertyValue("propertyId")!!.toLong()
                    val dataType = it.reference("dataType")
                            ?: throw RuntimeException("Reference dataType not found in node $name of type $conceptId, in concept ${concept.name}")
                    val propertyTypeNode = nodeLocator.resolve(dataType.target)!!
                    val propertyType = loadPropertyTypeFromNode(propertyTypeNode)
                    concept.addProperty(Property(AbsolutePropertyId(concept.absoluteID!!, idValue), name, propertyType))
                }
            }
        }
        return concept
    }

    private fun resolveAsConcept(target: ReferenceTarget): AbstractConcept? {
        return when (target) {
            is InModelReferenceTarget -> {
//                val uuid = target.physicalModel.uuid
//                if (uuid !in languagesByID) {
//                    throw RuntimeException("Unknown language UUID $uuid")
//                }
//                val language = languagesByID[uuid]!!
//                val concept = language.concepts.find {
//                    it.id.idValue == JavaFriendlyBase64.parseLong(target.nodeID)
//                } ?: throw RuntimeException("Concept not found (ID ${target.nodeID})")
//                return concept
                return this.findConceptWithID(JavaFriendlyBase64.parseLong(target.nodeID))!!
            }
            is OutsideModelReferenceTarget -> {
                // Note that this is the model ID, not the language ID
                val uuid = target.modelUIID
//                if (uuid !in languagesByID) {
//                    throw RuntimeException("Unknown language UUID $uuid")
//                }
//                val language = languagesByID[uuid]!!
                //val concept = language.concepts.find { it.id.idValue == target.nodeID }!!
                return this.findConceptWithID(target.nodeID)!!
            }
            is NullReferenceTarget -> null
            is ExplicitReferenceTarget -> {
                val uuid = target.model
                if (uuid !in languagesByID) {
                    if (uuid in modelsByID) {
                        val conceptNode = modelsByID[uuid]!!.findNodeByID(target.nodeId)
                        return loadConceptFromNode(conceptNode!!)
                    }
                    throw RuntimeException("Unknown language UUID $uuid (looking for node ${target.nodeId})")
                }
                val language = languagesByID[uuid]!!
                val concept = language.concepts.find { target.nodeId.isCompatibleWith(it.id) }!!
                return concept
            }
            else -> TODO()
        }
    }

    private fun findConceptWithID(nodeID: Long): AbstractConcept? {
        for (l in languagesByID.values) {
            for (c in l.concepts) {
                if (c.id == nodeID) {
                    return c
                }
            }
        }
        return null
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