import com.strumenta.mpsinterop.loading.loadMpsModel
import com.strumenta.mpsinterop.physicalmodel.CONCEPT_DECLARATION_CONCEPT_NAME
import com.strumenta.mpsinterop.physicalmodel.OutsideModelReferenceTarget
import com.strumenta.mpsinterop.physicalmodel.PhysicalReferenceValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class LoadingPhysicalModelsFromMpsFile {

    @Test
    fun loadPhysicalModelOfFormatsStructure() {
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
        assertEquals("6D8ZJLf0wUM", constraintNode.id.toBase64())
        assertEquals(CONCEPT_DECLARATION_CONCEPT_NAME, constraintNode.concept.name)
        assertEquals("true", constraintNode.singlePropertyValue("abstract"))
        val superConcept = constraintNode.reference("extends")
        assertNotNull(superConcept)
        assertEquals(PhysicalReferenceValue(OutsideModelReferenceTarget("tpck", "gw2VY9q"), "BaseConcept"), superConcept)
    }

}
