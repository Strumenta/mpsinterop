import com.strumenta.mpsinterop.loading.loading.physicalmodel.CONCEPT_DECLARATION_CONCEPT_NAME
import com.strumenta.mpsinterop.loading.loadMpsModel
import com.strumenta.mpsinterop.loading.loading.physicalmodel.OutsideModelReferenceTarget
import com.strumenta.mpsinterop.loading.loading.physicalmodel.PhysicalReferenceValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class MpsFileLoadingTest {

    @Test
    fun loadFormatsStructure() {
        val inputStream = MpsFileLoadingTest::class.java.getResourceAsStream("/formats-structure.mps")
        val model = loadMpsModel(inputStream)

        assertEquals("Formats.structure", model.name)

        assertEquals(36, model.numberOfRoots)

        val constraintNode = model.getRootByName("Constraint", model)
        assertEquals("6D8ZJLf0wUM", constraintNode.id)
        assertEquals(CONCEPT_DECLARATION_CONCEPT_NAME, constraintNode.concept.name)
        assertEquals("true", constraintNode.singlePropertyValue("abstract"))
        val superConcept = constraintNode.reference("extends")
        assertNotNull(superConcept)
        assertEquals(PhysicalReferenceValue(OutsideModelReferenceTarget("tpck", "gw2VY9q"), "BaseConcept"), superConcept)
        
        // TODO check it cannot be root
        // TODO check no properties
        // TODO check no references
        // TODO check no children
    }

}
