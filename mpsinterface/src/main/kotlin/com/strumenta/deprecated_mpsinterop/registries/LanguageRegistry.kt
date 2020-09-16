package com.strumenta.deprecated_mpsinterop.registries

import com.strumenta.deprecated_mpsinterop.loading.ModelLocator
import com.strumenta.deprecated_mpsinterop.loading.NodeLocator
import com.strumenta.deprecated_mpsinterop.loading.SimpleNodeLocator
import com.strumenta.deprecated_mpsinterop.logicalmodel.*
import com.strumenta.deprecated_mpsinterop.physicalmodel.*
import com.strumenta.deprecated_mpsinterop.utils.Base64
import java.lang.RuntimeException
import java.util.*
import kotlin.collections.HashMap

class LanguageRegistry : ModelLocator {
    override fun locateModel(name: String): PhysicalModel? {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun locateLanguage(languageUUID: UUID): PhysicalLanguageModule? {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun locateModel(modelUUID: UUID): PhysicalModel? {
        return modelsByUUID[modelUUID]
    }

    private val languagesByUUID = HashMap<LanguageUUID, Language>()
    private val modelsByUUID = HashMap<LanguageUUID, PhysicalModel>()
    private val nodeLocator: NodeLocator = SimpleNodeLocator(this)

    companion object {
        val DEFAULT = LanguageRegistry()
    }

    fun add(language: Language) {
        languagesByUUID[language.id] = language
    }

    fun getConcept(conceptName: String): AbstractConcept? {
        for (l in languagesByUUID.values) {
            val concept = l.concepts.find { it.qualifiedName() == conceptName }
            if (concept != null) {
                return concept
            }
        }
        return null
    }

    // Wrong, this is the UUID of the structure model of the language,
    // not the UUID of the language
    fun languageIDforConceptNode(it: PhysicalNode): UUID {
        val module = it.model!!.module ?: throw RuntimeException("No module for model ${it.model!!.name}")
        return module.uuid
    }
    fun conceptIDforConceptNode(it: PhysicalNode) = it.propertyValue("conceptId")?.toLong()

    fun preloadConcept(it: PhysicalNode): AbstractConcept? {
        val model = it.model!!
        if (it.concept.qualifiedName == CONCEPT_DECLARATION_CONCEPT_NAME) {
            val languageName = model.name.removeSuffix(".structure")
            val languageID = languageIDforConceptNode(it)
            val language = if (languageID in this.languagesByUUID) {
                val l = this.languagesByUUID[languageID]!!
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
//                languagesByUUID[l.id] = l
//                l
//            } else {
//                val l = Language(model.languageUuidFromName(languageName), languageName)
//                languagesByUUID[model.uuid] = l
//                this.add(l)
//                l
//            }
            val conceptIdValue: Long = conceptIDforConceptNode(it)!!
            val conceptId = AbsoluteConceptId(language.id, conceptIdValue)
            val conceptName = it.propertyValue("name")!!
            // println(conceptName)
            val concept = Concept(conceptIdValue, conceptName)
            // concepts[it] = concept
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
        } else if (it.concept.qualifiedName == INTERFACE_CONCEPT_DECLARATION_CONCEPT_NAME) {
            val languageName = model.name.removeSuffix(".structure")
            val languageID = languageIDforConceptNode(it)
            val language = if (languageID in this.languagesByUUID) {
                val l = this.languagesByUUID[languageID]!!
                l
            } else {
                val l = Language(model.languageUuidFromName(languageName), languageName)
                this.add(l)
                l
            }
            val conceptIdValue: Long = it.propertyValue("conceptId")!!.toLong()
            val conceptId = AbsoluteConceptId(language.id, conceptIdValue)
            val conceptName = it.propertyValue("name")!!
            val concept = InterfaceConcept(conceptIdValue, conceptName)
            language.add(concept)
            // concepts[it] = concept

//                concept.final = it.booleanPropertyValue("final")
//                concept.abstract = it.booleanPropertyValue("abstract")
//                concept.rootable = it.booleanPropertyValue("rootable")
            return concept
        }
        return null
    }

    fun loadLanguageFromModuleAndModel(module: PhysicalLanguageModule, structureModel: PhysicalModel) {
        this.loadLanguageFromModule(module)
        structureModel.module = module
        this.loadLanguageFromModel(structureModel)
    }

    fun loadLanguageFromModule(module: PhysicalLanguageModule) {
        add(Language(module.uuid, module.name, module))
    }

    fun loadLanguageFromModel(model: PhysicalModel, onlyShallow: Boolean = false) {
        // We solve stuff in two rounds, because there could be dependency between concepts
        val concepts = HashMap<PhysicalNode, AbstractConcept>()
        modelsByUUID[model.uuid] = model

        model.roots.forEach {
            val concept = preloadConcept(it)
            if (concept != null) {
                concepts[it] = concept
            }
            // println(it.concept.qualifiedName(this))
        }

        if (onlyShallow) {
            return
        }

        model.roots.forEach {
            loadConceptFromNode(it)
            // println(it.concept.qualifiedName(this))
        }
        // val language = model.
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun loadPropertyTypeFromNode(node: PhysicalNode): PropertyType {
        when {
            node.concept.qualifiedName == PRIMITIVE_DATA_TYPE_DECLARATION_CONCEPT_NAME -> {
                val name = node.name()!!
                return PrimitivePropertyType.valueOf(name.toUpperCase())
            }
            node.concept.qualifiedName == ENUMERATION_DATA_TYPE_DECLARATION_CONCEPT_NAME -> {
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
            node.concept.qualifiedName == CONSTRAINED_DATA_TYPE_DECLARATION_CONCEPT_NAME -> {
                val qname = node.qualifiedName()
                return ConstrainedPropertyType(qname)
            }
            node.concept.qualifiedName == ENUMERATION_DECLARATION_CONCEPT_NAME -> {
                return EnumerationDeclarationType()
            }
            else -> TODO(node.concept.qualifiedName)
        }
    }

    private fun loadEnumerationAlternative(node: PhysicalNode): EnumerationAlternative {
        return EnumerationAlternative(node.propertyValue("externalValue")!!,
                node.propertyValue("internalValue", null))
    }

    private fun loadConceptFromNode(it: PhysicalNode): AbstractConcept? {
        val conceptID = conceptIDforConceptNode(it) ?: return null
        val langUUID = languageIDforConceptNode(it)
        var concept = languagesByUUID[langUUID]?.conceptByID(conceptID)
        if (concept == null) {
            concept = preloadConcept(it)
            if (concept == null) {
                return null
            }
            val l = languagesByUUID[langUUID]
                    ?: throw RuntimeException("Language with ID $langUUID not found")
            l.add(concept)
        }

        when {
            it.concept.qualifiedName == CONCEPT_DECLARATION_CONCEPT_NAME -> {

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
                    try {
                        val interfaceDeclaration = this.resolveAsConcept(it.reference("intfc")!!.target) as InterfaceConcept
                        interfaceDeclaration!!
                    } catch (e: RuntimeException) {
                        throw RuntimeException("Issue while loading for interface for concept ${concept.qualifiedName()}", e)
                    }
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
                    // TODO()
                }
            }
            it.concept.qualifiedName == INTERFACE_CONCEPT_DECLARATION_CONCEPT_NAME -> {

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
//                if (uuid !in languagesByUUID) {
//                    throw RuntimeException("Unknown language UUID $uuid")
//                }
//                val language = languagesByUUID[uuid]!!
//                val concept = language.concepts.find {
//                    it.id.idValue == Base64.parseLong(target.nodeID)
//                } ?: throw RuntimeException("Concept not found (ID ${target.nodeID})")
//                return concept
                return this.findConceptWithID(Base64.parseLong(target.nodeID))!!
            }
            is OutsideModelReferenceTarget -> {
                // Note that this is the model ID, not the language ID
                val uuid = target.modelUIID
//                if (uuid !in languagesByUUID) {
//                    throw RuntimeException("Unknown language UUID $uuid")
//                }
//                val language = languagesByUUID[uuid]!!
                // val concept = language.concepts.find { it.id.idValue == target.nodeID }!!
                return this.findConceptWithID(target.nodeID) ?: throw RuntimeException("Concept with ID ${target.nodeID} was not found. Looking in model $uuid")
            }
            is NullReferenceTarget -> null
            is ExplicitReferenceTarget -> {
                val uuid = target.model
                if (uuid !in languagesByUUID) {
                    if (uuid in modelsByUUID) {
                        val conceptNode = modelsByUUID[uuid]!!.findNodeByID(target.nodeId)
                        return loadConceptFromNode(conceptNode!!)
                    }
                    throw RuntimeException("Unknown language UUID $uuid (looking for node ${target.nodeId}). Note that it could be the structure model. Model $uuid")
                }
                val language = languagesByUUID[uuid]!!
                val concept = language.concepts.find { target.nodeId.isCompatibleWith(it.id) }!!
                return concept
            }
            else -> TODO()
        }
    }

    private fun findConceptWithID(nodeID: Long): AbstractConcept? {
        for (l in languagesByUUID.values) {
            for (c in l.concepts) {
                if (c.id == nodeID) {
                    return c
                }
            }
        }
        return null
    }

    operator fun get(id: LanguageUUID): Language? {
        return languagesByUUID[id]
    }

    fun getName(id: LanguageUUID): String? {
        return this[id]?.name
    }

    fun knowsLanguageUUID(uuid: UUID) = uuid in languagesByUUID

    fun languageByUUID(uuid: UUID) = languagesByUUID[uuid]

    fun knowsLanguageName(name: String) = languagesByUUID.values.any { it.name == name }
}