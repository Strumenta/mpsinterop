
import com.strumenta.mpsinterop.binary.loadMpsModelFromBinaryFile
import com.strumenta.mpsinterop.loading.*
import com.strumenta.mpsinterop.registries.LanguageRegistry
import com.strumenta.mpsinterop.registries.PhysicalModelsRegistry
import kotlin.test.*

class LoadingLogicalModelsFromMpsFile {

    fun loadBasicLanguageRegistry() : LanguageRegistry {
        val languageRegistry = LanguageRegistry()

        val inputStream = LoadingLogicalModelsFromMpsFile::class.java.getResourceAsStream(
                "/jetbrains.mps.lang.core-src-structure.mpb")
        val model = loadMpsModelFromBinaryFile(inputStream, languageRegistry)
        //languageRegistry.loadLanguageFromModel(model)

        //return languageRegistry
        TODO()
    }

    @Test
    @Ignore
    fun loadLangStructureIntoLanguageRegistryConceptsAreLoaded() {
        val languageRegistry = loadBasicLanguageRegistry()
        assertNotNull(languageRegistry.getConcept("jetbrains.mps.lang.core.BaseConcept"))
    }

    @Test
    @Ignore
    fun loadLangStructureIntoLanguageRegistryInterfaceConceptsAreLoaded() {
        val languageRegistry = loadBasicLanguageRegistry()
        assertNotNull(languageRegistry.getConcept("jetbrains.mps.lang.core.INamedConcept"))
    }

    @Test
    @Ignore
    fun loadBaseConceptIntoLanguageRegistry() {
        val languageRegistry = loadBasicLanguageRegistry()
        val baseConcept = languageRegistry.getConcept("jetbrains.mps.lang.core.BaseConcept")!!
        assertEquals("BaseConcept", baseConcept.name)
        assertEquals(false, baseConcept.rootable)
        assertEquals(false, baseConcept.final)
        assertEquals(true, baseConcept.abstract)
//        assertEquals("AbstractConceptDeclaration", baseConcept.extended!!.name)
    }

    @Test
    @Ignore
    fun loadLogicalModelOfConstraint() {
        val languageRegistry = loadBasicLanguageRegistry()
        val physicalModelRegistry = PhysicalModelsRegistry()
        val formatsStructurePhysicalModel = physicalModelRegistry.loadMpsFile(
                LoadingLogicalModelsFromMpsFile::class.java.getResourceAsStream("/formats-structure.mps"))
        physicalModelRegistry.loadJar(LoadingLogicalModelsFromMpsFile::class.java.getResourceAsStream(
                "/jetbrains.mps.lang.structure.jar"))

        val converter = PhysicalToLogicalConverter(languageRegistry, physicalModelRegistry)
        val logicalModel = converter.toLogical(formatsStructurePhysicalModel)

        assertEquals("Formats.structure", logicalModel.name)

        assertEquals(36, logicalModel.numberOfRoots)
//
//        val constraintNode = logicalModel.getRootByName("Constraint")
//        assertEquals("6D8ZJLf0wUM", constraintNode.id)
//        assertEquals(CONCEPT_DECLARATION_CONCEPT_NAME, constraintNode.concept.name)
//        assertEquals("true", constraintNode.singlePropertyValue("abstract"))
//        val superConcept = constraintNode.reference("extends")
//        assertNotNull(superConcept)
//        assertEquals(PhysicalReferenceValue(OutsideModelReferenceTarget("tpck", "gw2VY9q"), "BaseConcept"), superConcept)
//
//        // TODO check it cannot be root
//        // It is not present because it has the default value
//        //assertEquals("false", constraintNode.singlePropertyValue("rootable"))
//
//        // TODO check no properties
//        // TODO check no references
//        // TODO check no children
        TODO()
    }

}
