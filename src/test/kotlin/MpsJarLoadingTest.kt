import com.strumenta.mpsinterop.CONCEPT_DECLARATION_CONCEPT_NAME
import com.strumenta.mpsinterop.loading.LanguageRegistry
import com.strumenta.mpsinterop.loading.loadMpsModel
import kotlin.test.Test
import kotlin.test.assertEquals

class MpsJarLoadingTest {

    @Test
    fun loadConceptsStructure() {
        val inputStream = MpsFileLoadingTest::class.java.getResourceAsStream("/jetbrains.mps.lang.structure.jar")
        val languageRegistry = LanguageRegistry()
        languageRegistry.loadJar(inputStream)
    }

}
