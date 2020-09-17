package com.strumenta.deprecated_mpsinterop

import com.strumenta.deprecated_mpsinterop.loading.loadMpsModel
import com.strumenta.deprecated_mpsinterop.physicalmodel.CONCEPT_DECLARATION_CONCEPT_NAME
import com.strumenta.deprecated_mpsinterop.physicalmodel.InModelReferenceTarget
import com.strumenta.deprecated_mpsinterop.physicalmodel.OutsideModelReferenceTarget
import com.strumenta.deprecated_mpsinterop.physicalmodel.PhysicalReferenceValue
import com.strumenta.deprecated_mpsinterop.physicalmodel.name
import kotlin.test.Test
import kotlin.test.assertEquals

class LoadingPhysicalModelsFromMpsFile {

    @Test
    fun loadPhysicalModelOfFormatsStructureBasicSanityCheck() {
        val inputStream = LoadingPhysicalModelsFromMpsFile::class.java.getResourceAsStream(
            "/formats-structure.mps"
        )
        val model = loadMpsModel(inputStream)

        assertEquals("Formats.structure", model.name)
        assertEquals(36, model.numberOfRoots)
    }

    @Test
    fun loadPhysicalModelOfConstraint() {
        val inputStream = LoadingPhysicalModelsFromMpsFile::class.java.getResourceAsStream(
            "/formats-structure.mps"
        )
        val model = loadMpsModel(inputStream)

        val constraintNode = model.getRootByName("Constraint")

        // Basis
        assertEquals("Constraint", constraintNode.name())
        assertEquals("6D8ZJLf0wUM", constraintNode.id.toStringRepresentation())
        assertEquals(CONCEPT_DECLARATION_CONCEPT_NAME, constraintNode.concept.qualifiedName)

        // Properties
        assertEquals(true, constraintNode.booleanPropertyValue("abstract"))
        assertEquals(false, constraintNode.booleanPropertyValue("final"))
        assertEquals(false, constraintNode.booleanPropertyValue("rootable"))
        assertEquals(7658651525954277042L, constraintNode.longPropertyValue("conceptId"))
        assertEquals("", constraintNode.stringPropertyValue("languageId"))
        assertEquals("", constraintNode.stringPropertyValue("iconPath"))
        // static scope global has the value null (empty string)
        assertEquals("", constraintNode.stringPropertyValue("staticScope"))
        assertEquals("", constraintNode.stringPropertyValue("conceptAlias"))
        assertEquals("", constraintNode.stringPropertyValue("conceptShortDescription"))

        // Structure
        val superConcept = constraintNode.reference("extends")
        assertEquals(
            PhysicalReferenceValue(
                OutsideModelReferenceTarget(model, "tpck", "gw2VY9q"),
                "BaseConcept"
            ),
            superConcept
        )
        val sourceNode = constraintNode.reference("sourceNode")
        assertEquals(null, sourceNode)
        assertEquals(0, constraintNode.numberOfChildren("implements"))

        // Relations
        assertEquals(0, constraintNode.numberOfChildren("linkDeclaration"))
        assertEquals(0, constraintNode.numberOfChildren("propertyDeclaration"))
        assertEquals(0, constraintNode.numberOfChildren("helpURL"))
    }

    @Test
    fun loadPhysicalModelOfEqualTo() {
        val inputStream = LoadingPhysicalModelsFromMpsFile::class.java.getResourceAsStream(
            "/formats-structure.mps"
        )
        val model = loadMpsModel(inputStream)

        val equalTo = model.getRootByName("EqualTo")

        // Basis
        assertEquals("EqualTo", equalTo.name())
        assertEquals(CONCEPT_DECLARATION_CONCEPT_NAME, equalTo.concept.qualifiedName)

        // Properties
        assertEquals("=", equalTo.stringPropertyValue("conceptAlias"))

        // Structure
        val superConcept = equalTo.reference("extends")
        assertEquals(
            PhysicalReferenceValue(
                InModelReferenceTarget(
                    model,
                    "6D8ZJLf0wUM"
                ),
                "Constraint"
            ),
            superConcept
        )

        // Relations
        assertEquals(1, equalTo.numberOfChildren("linkDeclaration"))
        assertEquals(0, equalTo.numberOfChildren("propertyDeclaration"))
        assertEquals(0, equalTo.numberOfChildren("helpURL"))
    }
}
