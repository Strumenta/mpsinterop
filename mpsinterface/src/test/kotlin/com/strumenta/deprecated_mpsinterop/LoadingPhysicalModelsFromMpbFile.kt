package com.strumenta.deprecated_mpsinterop

import com.strumenta.deprecated_mpsinterop.binary.loadMpsModelFromBinaryFile
import com.strumenta.deprecated_mpsinterop.physicalmodel.name
import kotlin.test.Test
import kotlin.test.assertEquals

class LoadingPhysicalModelsFromMpbFile {

    @Test
    fun loadPhysicalModelOfLangCoreStructureBasicSanityCheck() {
        val inputStream = LoadingPhysicalModelsFromMpbFile::class.java.getResourceAsStream("/jetbrains.mps.lang.core-src-structure.mpb")
        val model = loadMpsModelFromBinaryFile(inputStream)

        assertEquals("jetbrains.mps.lang.core.structure", model.name)
        assertEquals(42, model.numberOfRoots)
    }

    @Test
    fun loadPhysicalModelOfINamedConcept() {
        val inputStream = LoadingPhysicalModelsFromMpbFile::class.java.getResourceAsStream("/jetbrains.mps.lang.core-src-structure.mpb")
        val model = loadMpsModelFromBinaryFile(inputStream)

        val iNamedConceptNode = model.getRootByName("INamedConcept")

        // Basis
        assertEquals("INamedConcept", iNamedConceptNode.name())
        assertEquals(true, iNamedConceptNode.id.isCompatibleWith(1169194658468L))
        assertEquals("jetbrains.mps.lang.structure.structure.InterfaceConceptDeclaration", iNamedConceptNode.concept.qualifiedName)

        // Properties
        assertEquals(false, iNamedConceptNode.booleanPropertyValue("abstract"))
        assertEquals(false, iNamedConceptNode.booleanPropertyValue("final"))
        assertEquals(1169194658468L, iNamedConceptNode.longPropertyValue("conceptId"))
        assertEquals("", iNamedConceptNode.stringPropertyValue("languageId"))
        assertEquals("", iNamedConceptNode.stringPropertyValue("conceptAlias"))
        assertEquals("", iNamedConceptNode.stringPropertyValue("conceptShortDescription"))

        // Structure
        val sourceNode = iNamedConceptNode.reference("sourceNode")
        assertEquals(null, sourceNode)
        assertEquals(0, iNamedConceptNode.numberOfChildren("extends"))

        // Relations
        assertEquals(0, iNamedConceptNode.numberOfChildren("linkDeclaration"))
        assertEquals(1, iNamedConceptNode.numberOfChildren("propertyDeclaration"))
        assertEquals(0, iNamedConceptNode.numberOfChildren("helpURL"))
    }
}
