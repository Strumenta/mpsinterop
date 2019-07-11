import com.strumenta.mpsinterop.loading.LanguageRegistry
import kotlin.test.Test
import kotlin.test.assertEquals

class MpsJarLoadingTest {

    @Test
    fun loadConceptsStructure() {
        val inputStream = MpsFileLoadingTest::class.java.getResourceAsStream("/jetbrains.mps.lang.structure.jar")
        val languageRegistry = LanguageRegistry()
        val models = languageRegistry.loadJar(inputStream)
        assertEquals(3, models.size)
        val structure = models.find { it.name == "jetbrains.mps.lang.structure.structure" }!!
        println(structure)
    }

}
