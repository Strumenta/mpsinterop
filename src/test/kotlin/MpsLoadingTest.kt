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

    }

}
