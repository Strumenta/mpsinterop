import com.strumenta.mpsinterop.loading.loadMpsModel
import com.strumenta.mpsinterop.physicalmodel.*
import kotlin.test.Test
import kotlin.test.assertEquals

class LoadingPhysicalModelsFromMpsFile {

    @Test
    fun loadPhysicalModelOfFormatsStructureBasicSanityCheck() {
        val inputStream = LoadingPhysicalModelsFromMpsFile::class.java.getResourceAsStream(
                "/formats-structure.mps")
        val model = loadMpsModel(inputStream)

        assertEquals("Formats.structure", model.name)
        assertEquals(36, model.numberOfRoots)
    }

    @Test
    fun loadPhysicalModelOfConstraint() {
        val inputStream = LoadingPhysicalModelsFromMpsFile::class.java.getResourceAsStream(
                "/formats-structure.mps")
        val model = loadMpsModel(inputStream)

        val constraintNode = model.getRootByName("Constraint")

        // Basis
        assertEquals("Constraint", constraintNode.name())
        assertEquals("6D8ZJLf0wUM", constraintNode.id.toBase64())
        assertEquals(CONCEPT_DECLARATION_CONCEPT_NAME, constraintNode.concept.qname)

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
        assertEquals(PhysicalReferenceValue(
                OutsideModelReferenceTarget("tpck", "gw2VY9q"),
                "BaseConcept"),
                superConcept)
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
                "/formats-structure.mps")
        val model = loadMpsModel(inputStream)

        val equalTo = model.getRootByName("EqualTo")

        // Basis
        assertEquals("EqualTo", equalTo.name())
        assertEquals(CONCEPT_DECLARATION_CONCEPT_NAME, equalTo.concept.qname)

        // Properties
        assertEquals("=", equalTo.stringPropertyValue("conceptAlias"))

        // Structure
        val superConcept = equalTo.reference("extends")
        assertEquals(PhysicalReferenceValue(
                InModelReferenceTarget("6D8ZJLf0wUM"),
                "Constraint"),
                superConcept)

        // Relations
        assertEquals(1, equalTo.numberOfChildren("linkDeclaration"))
        assertEquals(0, equalTo.numberOfChildren("propertyDeclaration"))
        assertEquals(0, equalTo.numberOfChildren("helpURL"))
    }

}
