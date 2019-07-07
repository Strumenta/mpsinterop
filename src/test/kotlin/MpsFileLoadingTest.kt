import com.strumenta.mpsinterop.CONCEPT_DECLARATION_CONCEPT_NAME
import com.strumenta.mpsinterop.loading.loadMpsModel
import kotlin.test.Test
import kotlin.test.assertEquals

class MpsFileLoadingTest {

    @Test
    fun loadFormatsStructure() {
        val inputStream = MpsFileLoadingTest::class.java.getResourceAsStream("/formats-structure.mps")
        val model = loadMpsModel(inputStream)

        assertEquals("Formats.structure", model.name)

        assertEquals(36, model.numberOfRoots)

        val constraintNode = model.getRootByName("Constraint", model)
        assertEquals(CONCEPT_DECLARATION_CONCEPT_NAME, constraintNode.concept.name)
        //assertEquals(constraintNode.singlePropertyValue())
        // TODO check it is abstract
        // TODO check superclass
        // TODO check it cannot be root
        // TODO check no properties
        // TODO check no references
        // TODO check no children
    }

}
