
import com.strumenta.mpsinterop.binary.loadMpsModelFromBinaryFile
import com.strumenta.mpsinterop.loading.*
import com.strumenta.mpsinterop.logicalmodel.Concept
import com.strumenta.mpsinterop.physicalmodel.PhysicalModule
import com.strumenta.mpsinterop.registries.LanguageRegistry
import java.util.*
import kotlin.test.*

class LoadingLogicalModelsFromMpsFile {

    fun loadBasicLanguageRegistry(): LanguageRegistry {
        val languageRegistry = LanguageRegistry()

        val inputStream = LoadingLogicalModelsFromMpsFile::class.java.getResourceAsStream(
                "/jetbrains.mps.lang.core-src-structure.mpb")
        val model = loadMpsModelFromBinaryFile(inputStream, languageRegistry)
        val module = PhysicalModule("jetbrains.mps.lang.core",
                UUID.fromString("ceab5195-25ea-4f22-9b92-103b95ca8c0c"))
        model.module = module
        languageRegistry.loadLanguageFromModel(model)

        injectIValidIdentifier(languageRegistry)

        return languageRegistry
    }

    @Test
    fun loadLangStructureIntoLanguageRegistryConceptsAreLoaded() {
        val languageRegistry = loadBasicLanguageRegistry()
        assertNotNull(languageRegistry.getConcept("jetbrains.mps.lang.core.structure.BaseConcept"))
    }

    @Test
    fun loadLangStructureIntoLanguageRegistryInterfaceConceptsAreLoaded() {
        val languageRegistry = loadBasicLanguageRegistry()
        assertNotNull(languageRegistry.getConcept("jetbrains.mps.lang.core.structure.INamedConcept"))
    }

    @Test
    fun loadBaseConceptIntoLanguageRegistry() {
        val languageRegistry = loadBasicLanguageRegistry()
        val baseConcept = languageRegistry.getConcept("jetbrains.mps.lang.core.structure.BaseConcept")!! as Concept
        assertEquals("BaseConcept", baseConcept.name)
        assertEquals(false, baseConcept.rootable)
        assertEquals(false, baseConcept.final)
        assertEquals(true, baseConcept.abstract)
    }

    @Test
    fun loadBaseConceptHasRightLanguageID() {
        val languageRegistry = loadBasicLanguageRegistry()
        // This is the UUID of the structure model
        // val expectedLangUUID = UUID.fromString("00000000-0000-4000-0000-011c89590288")
        val expectedLangUUID = UUID.fromString("ceab5195-25ea-4f22-9b92-103b95ca8c0c")
        val langCore = languageRegistry[expectedLangUUID]
        assertNotNull(langCore)
        assertEquals("jetbrains.mps.lang.core", langCore.name)
    }

    @Test
    fun loadLogicalModelOfConstraint() {
        // The right language ID for jetbrains.mps.lang.core.structure is 00000000-0000-4000-0000-011c89590288
        // however it has been loaded with the wrong language ID. Why is this the case?
        val languageRegistry = loadBasicLanguageRegistry()
        // val physicalModelRegistry = PhysicalModelsRegistry()
        val formatsStructurePhysicalModel = languageRegistry.loadMpsFile(
                LoadingLogicalModelsFromMpsFile::class.java.getResourceAsStream("/formats-structure.mps"))
        languageRegistry.loadLanguageFromJar(LoadingLogicalModelsFromMpsFile::class.java.getResourceAsStream(
                "/jetbrains.mps.lang.structure-src.jar"))

        // At this point we expect the language registry to contain the basic concepts needed for the conversion
        // of the Formats language
        assertNotNull(languageRegistry.getConcept("jetbrains.mps.lang.structure.structure.ConceptDeclaration"))

        val converter = PhysicalToLogicalConverter(languageRegistry)
        val logicalModel = converter.toLogical(formatsStructurePhysicalModel)

        assertEquals("Formats.structure", logicalModel.name)

        assertEquals(36, logicalModel.numberOfRoots)

        val constraintNode = logicalModel.getRootByName("Constraint")
//        assertEquals("6D8ZJLf0wUM", constraintNode.id)
//        assertEquals(CONCEPT_DECLARATION_CONCEPT_NAME, constraintNode.concept.name)
//        assertEquals("true", constraintNode.propertyValue("abstract"))
//        val superConcept = constraintNode.reference("extends")
//        assertNotNull(superConcept)
//        assertEquals(PhysicalReferenceValue(OutsideModelReferenceTarget("tpck", "gw2VY9q"), "BaseConcept"), superConcept)
//
//        // TODO check it cannot be root
//        // It is not present because it has the default value
//        //assertEquals("false", constraintNode.propertyValue("rootable"))
//
//        // TODO check no declaredProperties
//        // TODO check no references
//        // TODO check no children
//        TODO()
    }
}
