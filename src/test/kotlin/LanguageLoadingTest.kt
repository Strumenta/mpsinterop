
import com.strumenta.mpsinterop.loading.loadLanguageFromJar
import com.strumenta.mpsinterop.loading.loadLanguageFromMpsInputStream
import com.strumenta.mpsinterop.registries.LanguageRegistry
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class LanguageLoadingTest {

    @Test
    fun loadBaseConcept() {
        val languageRegistry = LanguageRegistry()
        languageRegistry.loadLanguageFromJar(LoadingLogicalModelsFromMpsFile::class.java.getResourceAsStream(
                "/jetbrains.mps.lang.core-src.jar"))
        val concept = languageRegistry.getConcept("jetbrains.mps.lang.core.structure.BaseConcept")
        assertNotNull(concept)
        assertEquals(true, concept.abstract)
        assertEquals(null, concept.extended)
        assertEquals(0, concept.implemented.size)
        assertEquals(null, concept.alias)
    }

    /**
     * In this test we want to load the language from a given file and verify a certain concept looks as expected.
     */
    @Test
    fun loadConstraintConcept() {
        val inputStream = LanguageLoadingTest::class.java.getResourceAsStream("/formats-structure.mps")
        val languageRegistry = LanguageRegistry()
        languageRegistry.loadLanguageFromMpsInputStream(inputStream)
        val concept = languageRegistry.getConcept("Formats.structure.Constraint")
        assertNotNull(concept)
        assertEquals(true, concept.abstract)
        assertEquals(null, concept.extended)
        assertEquals(0, concept.implemented.size)
        assertEquals(null, concept.alias)
    }

}

