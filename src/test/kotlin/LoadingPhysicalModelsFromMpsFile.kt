import com.strumenta.mpsinterop.loading.loadMpsModel
import com.strumenta.mpsinterop.physicalmodel.CONCEPT_DECLARATION_CONCEPT_NAME
import com.strumenta.mpsinterop.physicalmodel.OutsideModelReferenceTarget
import com.strumenta.mpsinterop.physicalmodel.PhysicalReferenceValue
import com.strumenta.mpsinterop.physicalmodel.name
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

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
        assertEquals(CONCEPT_DECLARATION_CONCEPT_NAME, constraintNode.concept.name)

        // Properties
        assertEquals(true, constraintNode.booleanPropertyValue("abstract"))
        assertEquals(false, constraintNode.booleanPropertyValue("final"))
        assertEquals(false, constraintNode.booleanPropertyValue("rootable"))
        assertEquals(7658651525954277042L, constraintNode.longPropertyValue("conceptId"))
        // TODO language id
        // TODO icon path
        // TODO static scope
        // TODO concept alias
        // TODO concept short description

        // Structure
        val superConcept = constraintNode.reference("extends")
        assertEquals(PhysicalReferenceValue(
                OutsideModelReferenceTarget("tpck", "gw2VY9q"),
                "BaseConcept"),
                superConcept)
        // TODO sourceNode null
        // TODO implements

        // Relations
        // TODO link declarations
        // TODO property declarations
        // TODO help URL

        assertNotNull(superConcept)
    }

}
