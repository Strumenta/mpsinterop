
import com.strumenta.mpsinterop.loading.loadLanguageFromJar
import com.strumenta.mpsinterop.loading.loadLanguageFromMpsInputStream
import com.strumenta.mpsinterop.logicalmodel.Concept
import com.strumenta.mpsinterop.logicalmodel.InterfaceConcept
import com.strumenta.mpsinterop.physicalmodel.PhysicalModule
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
        assertTrue(concept is Concept)
        assertEquals(true, concept.abstract)
        assertEquals(null, concept.extended)
        assertEquals(0, concept.implemented.size)
        assertEquals("", concept.alias)
        assertEquals(2, concept.declaredProperties.size)
    }

    @Test
    fun loadINamedConcept() {
        val languageRegistry = LanguageRegistry()
        languageRegistry.loadLanguageFromJar(LoadingLogicalModelsFromMpsFile::class.java.getResourceAsStream(
                "/jetbrains.mps.lang.core-src.jar"))
        injectIValidIdentifier(languageRegistry)
        languageRegistry.loadLanguageFromJar(LoadingLogicalModelsFromMpsFile::class.java.getResourceAsStream(
                "/jetbrains.mps.lang.structure-src.jar"))
        val concept = languageRegistry.getConcept("jetbrains.mps.lang.core.structure.INamedConcept") as InterfaceConcept
        assertNotNull(concept)
//        assertEquals(true, concept.isInterface)
//        assertEquals(false, concept.abstract)
//        assertEquals(true, concept.final)
//        assertNotNull(concept.extended)
//        assertEquals(1, concept.implemented.size)
//        assertEquals("Concept", concept.alias)
        assertEquals(1, concept.declaredProperties.size)
        assertEquals(1, concept.allProperties.size)
        val nameProp = concept.allProperties.find { it.name == "name" }
        assertNotNull(nameProp)
    }

    @Test
    fun loadConceptDeclaration() {
        val languageRegistry = LanguageRegistry()
        languageRegistry.loadLanguageFromJar(LoadingLogicalModelsFromMpsFile::class.java.getResourceAsStream(
                "/jetbrains.mps.lang.core-src.jar"))
        injectIValidIdentifier(languageRegistry)
        languageRegistry.loadLanguageFromJar(LoadingLogicalModelsFromMpsFile::class.java.getResourceAsStream(
                "/jetbrains.mps.lang.structure-src.jar"))
        val concept = languageRegistry.getConcept("jetbrains.mps.lang.structure.structure.ConceptDeclaration") as Concept
        assertNotNull(concept)
        assertEquals(false, concept.abstract)
        assertEquals(true, concept.final)
        assertNotNull(concept.extended)
        assertEquals(1, concept.implemented.size)
        assertEquals("Concept", concept.alias)
        assertEquals(3, concept.declaredProperties.size)
        assertEquals(14, concept.allProperties.size)
        val nameProp = concept.allProperties.find { it.name == "name" }
        assertNotNull(nameProp)
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
        assertTrue(languageRegistry.knowsLanguageUUID(UUID.fromString("ceab5195-25ea-4f22-9b92-103b95ca8c0c")))

        val module = PhysicalModule("Formats", UUID.fromString("040f4d08-2e19-478e-bafc-1ae65578e650"))
        languageRegistry.loadLanguageFromMpsInputStream(inputStream, module)
        val concept = languageRegistry.getConcept("Formats.structure.Constraint") as Concept
        assertNotNull(concept)
        assertEquals(true, concept.abstract)
        assertNotNull(concept.extended)
        assertEquals(0, concept.implemented.size)
        assertEquals("", concept.alias)

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

        val module = PhysicalModule("Formats", UUID.fromString("040f4d08-2e19-478e-bafc-1ae65578e650"))
        languageRegistry.loadLanguageFromMpsInputStream(inputStream, module)
        val concept = languageRegistry.getConcept("Formats.structure.Constraint") as Concept
        assertNotNull(concept)

        assertEquals(0, concept.declaredProperties.size)
    }

    @Test
    fun loadConceptAllProperties() {
        val inputStream = LanguageLoadingTest::class.java.getResourceAsStream("/formats-structure.mps")
        val languageRegistry = LanguageRegistry()
        languageRegistry.loadLanguageFromJar(LoadingLogicalModelsFromMpsFile::class.java.getResourceAsStream(
                "/jetbrains.mps.lang.core-src.jar"))

        val module = PhysicalModule("Formats", UUID.fromString("040f4d08-2e19-478e-bafc-1ae65578e650"))
        languageRegistry.loadLanguageFromMpsInputStream(inputStream, module)
        val concept = languageRegistry.getConcept("Formats.structure.Constraint") as Concept
        assertNotNull(concept)

        assertEquals(2, concept.allProperties.size)
    }
}
