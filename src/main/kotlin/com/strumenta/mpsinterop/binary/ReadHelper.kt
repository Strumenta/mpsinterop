package com.strumenta.mpsinterop.binary

import com.strumenta.mpsinterop.logicalmodel.*
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
    private val myConcepts = HashMap<Int, SConcept>()
    private val myProperties = HashMap<Int, SProperty>()
    private val myAssociations = HashMap<Int, SReferenceLink>()
    private val myAggregations = HashMap<Int, SContainmentLink>()
    private var currentConcept : SConcept? = null


//    /*package*/ // FIXME could I use myMetaInfo.registry.keySet() instead?
//    val participatingConcepts: List<SConceptId>
//        get() {
//            val rv = ArrayList<SConceptId>(100)
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
//    fun withLanguage(lang: LanguageId, name: String, index: Int) {
//        val langInfo = myMetaInfo.registerLanguage(lang, name)
//        langInfo.setIntIndex(index)
//        myMetaInfoProvider.setLanguageName(lang, name)
//    }


    fun withConcept(conceptIndex: Int, conceptId: SConceptId, conceptName: String, kind: ConceptKind) {
        currentConcept = SConcept(conceptId, conceptName)
        myConcepts[conceptIndex] = currentConcept!!
    }

//
//    // @param stub is optional
//    fun withConcept(concept: SConceptId, name: String, scope: StaticScope, kind: ConceptKind, stub: SConceptId, index: Int) {
//        myActualConcept = myMetaInfo.withConcept(concept, name)
//        myActualConcept!!.setImplementationKind(scope, kind)
//        myActualConcept!!.setIntIndex(index)
//        myConcepts.put(index, MetaAdapterFactory.getConcept(concept, name))
//        myMetaInfoProvider.setConceptName(concept, name)
//        myActualConcept!!.setStubCounterpart(stub)
//        myMetaInfoProvider.setStubConcept(concept, stub)
//    }
//
    fun property(property: SPropertyId, name: String, index: Int) {
        //myActualConcept!!.addProperty(property, name).setIntIndex(index)
        val property = SProperty(property, name)
        currentConcept!!.addProperty(property)
        myProperties[index] = property
        //myMetaInfoProvider.setPropertyName(property, name)
    }
//
    fun association(link: SReferenceLinkId, name: String, index: Int) {

        //myActualConcept!!.addLink(link, name).setIntIndex(index)
       myAssociations[index] = SReferenceLink(link, name)
       //myMetaInfoProvider.setAssociationName(link, name)
    }
//
    fun aggregation(link: SContainmentLinkId, name: String, unordered: Boolean, index: Int) {
//        myActualConcept!!.addLink(link, name, unordered).setIntIndex(index)
        myAggregations[index] = SContainmentLink(link, name)
//        myMetaInfoProvider.setAggregationName(link, name)
    }
//
    fun readConcept(index: Int): SConcept {
        require(index >= 0)
        return myConcepts[index]!!
    }

    fun readProperty(index: Int): SProperty {
        require(index >= 0)
        return myProperties[index]?: throw IllegalArgumentException("Property with index $index not found. Known indexes: ${myProperties.keys.joinToString(separator = ", ")}")
    }

    fun readAssociation(index: Int): SReferenceLink {
        require(index >= 0)
        return myAssociations[index]!!
    }
//
    fun readAggregation(index: Int): SContainmentLink {
        require(index >= 0) { "Index should be equal or greater to 0"}
        return myAggregations[index] ?: throw IllegalArgumentException("Aggregation with index $index not found. Known indexes: ${myAggregations.keys.joinToString(separator = ", ")}")
    }
//
//    fun isInterface(@NotNull concept: SConcept): Boolean {
//        return ConceptKind.INTERFACE === myMetaInfo.find(concept).getKind()
//    }
}