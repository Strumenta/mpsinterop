
import com.strumenta.mpsinterop.loading.loadLanguageFromJar
import com.strumenta.mpsinterop.loading.loadLanguageFromMpsInputStream
import com.strumenta.mpsinterop.registries.LanguageRegistry
import java.util.*
import kotlin.test.*

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
        assertEquals(2, concept.declaredProperties.size)
    }

    @Test
    fun loadConceptDeclaration() {
        val languageRegistry = LanguageRegistry()
        languageRegistry.loadLanguageFromJar(LoadingLogicalModelsFromMpsFile::class.java.getResourceAsStream(
                "/jetbrains.mps.lang.core-src.jar"))
        languageRegistry.loadLanguageFromJar(LoadingLogicalModelsFromMpsFile::class.java.getResourceAsStream(
                "/jetbrains.mps.lang.structure-src.jar"))
        val concept = languageRegistry.getConcept("jetbrains.mps.lang.structure.structure.ConceptDeclaration")
        assertNotNull(concept)
        assertEquals(false, concept.abstract)
        assertEquals(true, concept.final)
        assertNotNull(concept.extended)
        assertEquals(1, concept.implemented.size)
        assertEquals("Concept", concept.alias)
        assertEquals(3, concept.declaredProperties.size)
        // probably more than 10...
        assertEquals(10, concept.allProperties.size)
    }

    /**
     * In this test we want to load the language from a given file and verify a certain concept looks as expected.
     */
    @Test
    fun loadConstraintConcept() {
        val inputStream = LanguageLoadingTest::class.java.getResourceAsStream("/formats-structure.mps")
        val languageRegistry = LanguageRegistry()
        languageRegistry.loadLanguageFromJar(LoadingLogicalModelsFromMpsFile::class.java.getResourceAsStream(
                "/jetbrains.mps.lang.core-src.jar"))

        // jetbrains.mps.lang.core should be registered
        assertTrue(languageRegistry.knowsLanguageUUID(UUID.fromString("00000000-0000-4000-0000-011c89590288")))

        languageRegistry.loadLanguageFromMpsInputStream(inputStream)
        val concept = languageRegistry.getConcept("Formats.structure.Constraint")
        assertNotNull(concept)
        assertEquals(true, concept.abstract)
        assertNotNull(concept.extended)
        assertEquals(0, concept.implemented.size)
        assertEquals(null, concept.alias)

        assertEquals(0, concept.declaredProperties.size)
        assertEquals(2, concept.extended!!.declaredProperties.size)
        assertEquals(2, concept.allProperties.size)
    }

    @Test
    fun loadConceptDeclaredProperties() {
        val inputStream = LanguageLoadingTest::class.java.getResourceAsStream("/formats-structure.mps")
        val languageRegistry = LanguageRegistry()
        languageRegistry.loadLanguageFromJar(LoadingLogicalModelsFromMpsFile::class.java.getResourceAsStream(
                "/jetbrains.mps.lang.core-src.jar"))

        languageRegistry.loadLanguageFromMpsInputStream(inputStream)
        val concept = languageRegistry.getConcept("Formats.structure.Constraint")
        assertNotNull(concept)

        assertEquals(0, concept.declaredProperties.size)
    }

    @Test
    fun loadConceptAllProperties() {
        val inputStream = LanguageLoadingTest::class.java.getResourceAsStream("/formats-structure.mps")
        val languageRegistry = LanguageRegistry()
        languageRegistry.loadLanguageFromJar(LoadingLogicalModelsFromMpsFile::class.java.getResourceAsStream(
                "/jetbrains.mps.lang.core-src.jar"))

        languageRegistry.loadLanguageFromMpsInputStream(inputStream)
        val concept = languageRegistry.getConcept("Formats.structure.Constraint")
        assertNotNull(concept)

        assertEquals(2, concept.allProperties.size)
    }

}

