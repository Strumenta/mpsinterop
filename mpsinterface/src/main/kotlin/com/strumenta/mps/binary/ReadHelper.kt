package com.strumenta.mps.binary

import com.strumenta.mps.logicalmodel.AbsoluteConceptId
import com.strumenta.mps.logicalmodel.AbsoluteContainmentLinkId
import com.strumenta.mps.logicalmodel.AbsolutePropertyId
import com.strumenta.mps.logicalmodel.AbsoluteReferenceLinkId
import com.strumenta.mps.logicalmodel.ConceptKind
import com.strumenta.mps.logicalmodel.LanguageId
import com.strumenta.mps.physicalmodel.PhysicalConcept
import com.strumenta.mps.physicalmodel.PhysicalProperty
import com.strumenta.mps.physicalmodel.PhysicalRelation
import com.strumenta.mps.physicalmodel.RelationKind

/**
 * [jetbrains.mps.smodel.persistence.def.v9.IdInfoReadHelper] counterpart for binary persistence.
 * FIXME consider refactoring to remove duplicating code (e.g. #isInterface or #isRequestedInterfaceOnly)
 * @author Artem Tikhomirov
 */
internal class ReadHelper() {
    var isRequestedInterfaceOnly: Boolean = false
        private set
    private val myConcepts = HashMap<Int, PhysicalConcept>()
    private val myProperties = HashMap<Int, PhysicalProperty>()
    private val myAssociations = HashMap<Int, PhysicalRelation>()
    private val myAggregations = HashMap<Int, PhysicalRelation>()
    private var currentConcept: PhysicalConcept? = null

    fun requestInterfaceOnly(interfaceOnly: Boolean) {
        isRequestedInterfaceOnly = interfaceOnly
    }

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
                conceptId.idValue,
                conceptName,
                conceptIndex.toString()
            )
            myConcepts[conceptIndex] = currentConcept!!
            return currentConcept!!
        }

    fun property(property: AbsolutePropertyId, name: String, index: Int) {
        val property = PhysicalProperty(currentConcept!!, property.idValue, name, index.toString())
        currentConcept!!.addProperty(property)
        myProperties[index] = property
    }

    fun association(link: AbsoluteReferenceLinkId, name: String, index: Int) {
        myAssociations[index] = PhysicalRelation(currentConcept!!, link.idValue, name, index.toString(), RelationKind.REFERENCE)
    }

    fun aggregation(link: AbsoluteContainmentLinkId, name: String, unordered: Boolean, index: Int) {
        myAggregations[index] = PhysicalRelation(currentConcept!!, link.idValue, name, index.toString(), RelationKind.CONTAINMENT)
    }

    fun readConcept(index: Int): PhysicalConcept {
        require(index >= 0)
        return myConcepts[index] ?: throw IllegalArgumentException("Concept index $index not found")
    }

    fun readProperty(index: Int): PhysicalProperty {
        require(index >= 0)
        return myProperties[index] ?: throw IllegalArgumentException("Property with index $index not found. Known indexes: ${myProperties.keys.joinToString(separator = ", ")}")
    }

    fun readAssociation(index: Int): PhysicalRelation {
        require(index >= 0)
        return myAssociations[index]!!
    }

    fun readAggregation(index: Int): PhysicalRelation {
        require(index >= 0) { "Index should be equal or greater to 0" }
        return myAggregations[index] ?: throw IllegalArgumentException("Aggregation with index $index not found. Known indexes: ${myAggregations.keys.joinToString(separator = ", ")}")
    }
}
