package com.strumenta.mpsinterop.binary

import com.strumenta.mpsinterop.logicalmodel.*
import com.strumenta.mpsinterop.physicalmodel.PhysicalConcept
import com.strumenta.mpsinterop.physicalmodel.PhysicalProperty
import com.strumenta.mpsinterop.physicalmodel.PhysicalRelation
import com.strumenta.mpsinterop.physicalmodel.RelationKind
import java.lang.IllegalArgumentException

/**
 * [jetbrains.mps.smodel.persistence.def.v9.IdInfoReadHelper] counterpart for binary persistence.
 * FIXME consider refactoring to remove duplicating code (e.g. #isInterface or #isRequestedInterfaceOnly)
 * @author Artem Tikhomirov
 */
internal class ReadHelper() {
//    private val myMetaInfo: IdInfoRegistry
    var isRequestedInterfaceOnly: Boolean = false
        private set
//    private var myActualConcept: ConceptInfo? = null
//    // TODO with indices being just a persistence position, shall use arrays instead
    private val myConcepts = HashMap<Int, PhysicalConcept>()
    private val myProperties = HashMap<Int, PhysicalProperty>()
    private val myAssociations = HashMap<Int, PhysicalRelation>()
    private val myAggregations = HashMap<Int, PhysicalRelation>()
    private var currentConcept: PhysicalConcept? = null

//    /*package*/ // FIXME could I use myMetaInfo.registry.keySet() instead?
//    val participatingConcepts: List<AbsoluteConceptId>
//        get() {
//            val rv = ArrayList<AbsoluteConceptId>(100)
//            for (li in myMetaInfo.getLanguagesInUse()) {
//                for (ci in li.getConceptsInUse()) {
//                    rv.add(ci.getConceptId())
//                }
//            }
//            return rv
//        }
//
//    init {
//        myMetaInfo = IdInfoRegistry()
//    }
//
    /**/ fun requestInterfaceOnly(interfaceOnly: Boolean) {
        isRequestedInterfaceOnly = interfaceOnly
    }
//
//    fun withLanguage(lang: LanguageUUID, name: String, index: Int) {
//        val langInfo = myMetaInfo.registerLanguage(lang, name)
//        langInfo.setIntIndex(index)
//        myMetaInfoProvider.setLanguageName(lang, name)
//    }

    fun withConcept(
        conceptIndex: Int,
        conceptId: AbsoluteConceptId,
        languageName: String,
        conceptName: String,
        kind: ConceptKind
    ):
            PhysicalConcept {
        currentConcept = PhysicalConcept(
                LanguageId(conceptId.languageId, languageName),
                conceptId.idValue, conceptName, conceptIndex.toString())
        myConcepts[conceptIndex] = currentConcept!!
        return currentConcept!!
    }

//
//    // @param stub is optional
//    fun withConcept(concept: AbsoluteConceptId, name: String, scope: StaticScope, kind: ConceptKind, stub: AbsoluteConceptId, index: Int) {
//        myActualConcept = myMetaInfo.withConcept(concept, name)
//        myActualConcept!!.setImplementationKind(scope, kind)
//        myActualConcept!!.setIntIndex(index)
//        myConcepts.put(index, MetaAdapterFactory.getConcept(concept, name))
//        myMetaInfoProvider.setConceptName(concept, name)
//        myActualConcept!!.setStubCounterpart(stub)
//        myMetaInfoProvider.setStubConcept(concept, stub)
//    }
//
    fun property(property: AbsolutePropertyId, name: String, index: Int) {
        // myActualConcept!!.createProperty(property, name).setIntIndex(index)
        val property = PhysicalProperty(currentConcept!!, property.idValue, name, index.toString())
        currentConcept!!.addProperty(property)
        myProperties[index] = property
        // myMetaInfoProvider.setPropertyName(property, name)
    }
//
    fun association(link: AbsoluteReferenceLinkId, name: String, index: Int) {

        // myActualConcept!!.addLink(link, name).setIntIndex(index)
        myAssociations[index] = PhysicalRelation(currentConcept!!, link.idValue, name, index.toString(), RelationKind.REFERENCE)
        // myMetaInfoProvider.setAssociationName(link, name)
    }
//
    fun aggregation(link: AbsoluteContainmentLinkId, name: String, unordered: Boolean, index: Int) {
//        myActualConcept!!.addLink(link, name, unordered).setIntIndex(index)
        myAggregations[index] = PhysicalRelation(currentConcept!!, link.idValue, name, index.toString(), RelationKind.CONTAINMENT)
//        myMetaInfoProvider.setAggregationName(link, name)
    }
//
    fun readConcept(index: Int): PhysicalConcept {
        require(index >= 0)
        return myConcepts[index]!!
    }

    fun readProperty(index: Int): PhysicalProperty {
        require(index >= 0)
        return myProperties[index] ?: throw IllegalArgumentException("Property with index $index not found. Known indexes: ${myProperties.keys.joinToString(separator = ", ")}")
    }

    fun readAssociation(index: Int): PhysicalRelation {
        require(index >= 0)
        return myAssociations[index]!!
    }
//
    fun readAggregation(index: Int): PhysicalRelation {
        require(index >= 0) { "Index should be equal or greater to 0" }
        return myAggregations[index] ?: throw IllegalArgumentException("Aggregation with index $index not found. Known indexes: ${myAggregations.keys.joinToString(separator = ", ")}")
    }
//
//    fun isInterface(@NotNull concept: Concept): Boolean {
//        return ConceptKind.INTERFACE === myMetaInfo.find(concept).getKind()
//    }
}