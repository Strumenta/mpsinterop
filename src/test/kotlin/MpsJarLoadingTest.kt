
import com.strumenta.mpsinterop.loading.loadJar
import com.strumenta.mpsinterop.loading.loadLanguageFromDescriptors
import com.strumenta.mpsinterop.registries.LanguageRegistry
import com.strumenta.mpsinterop.registries.PhysicalModelsRegistry
import kotlin.test.Test
import kotlin.test.assertEquals

class MpsJarLoadingTest {

    @Test
    fun loadINamedConcept() {
        val inputStream = MpsFileLoadingTest::class.java.getResourceAsStream("/jetbrains.mps.lang.core.jar")
        val physicalModelsRegistry = PhysicalModelsRegistry()
        val models = physicalModelsRegistry.loadJar(inputStream)
        val structure = models.find { it.name == "jetbrains.mps.lang.core.structure" }!!
        val language = loadLanguageFromDescriptors(structure)
    }

    @Test
    fun loadConceptsStructure() {
        val inputStream = MpsFileLoadingTest::class.java.getResourceAsStream("/jetbrains.mps.lang.structure.jar")
        val physicalModelsRegistry = PhysicalModelsRegistry()
        val models = physicalModelsRegistry.loadJar(inputStream)
        assertEquals(3, models.size)
        val structure = models.find { it.name == "jetbrains.mps.lang.structure.structure" }!!
//        println(structure)
    }

}
