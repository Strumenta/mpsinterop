import com.strumenta.mpsinterop.CONCEPT_DECLARATION_NAME
import com.strumenta.mpsinterop.loading.loadMpsModel
import kotlin.test.Test
import kotlin.test.assertEquals

class MpsLoadingTest {

    @Test
    fun loadFormatsStructure() {
        val inputStream = MpsLoadingTest::class.java.getResourceAsStream("/formats-structure.mps")
        val model = loadMpsModel(inputStream)
        assertEquals(36, model.model.numberOfRoots)

        val constraintNode = model.model.getRootByName("Constraint", model)
        assertEquals(CONCEPT_DECLARATION_NAME, constraintNode.concept.name)
        // TODO check it is a concept definition
        // TODO check it is abstract
        // TODO check superclass
        // TODO check it cannot be root
        // TODO check no properties
        // TODO check no references
        // TODO check no children
    }

}
