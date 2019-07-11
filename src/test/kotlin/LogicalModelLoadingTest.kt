import com.strumenta.mpsinterop.loading.LanguageResolver
import com.strumenta.mpsinterop.loading.LanguageResolverCollector
import com.strumenta.mpsinterop.loading.PhysicalToLogicalConverter
import com.strumenta.mpsinterop.loading.loadMpsModel
import kotlin.test.Test
import kotlin.test.assertEquals

class LogicalModelLoadingTest {

    @Test
    fun loadLogicalModelOfConstraint() {
        val inputStream = LogicalModelLoadingTest::class.java.getResourceAsStream("/formats-structure.mps")
        val physicalModel = loadMpsModel(inputStream)
        val languageResolver = LanguageResolverCollector()
        val converter = PhysicalToLogicalConverter(languageResolver)
        val logicalModel = converter.toLogical(physicalModel)

        assertEquals("Formats.structure", logicalModel.name)

        assertEquals(36, physicalModel.numberOfRoots)

        val constraintNode = logicalModel.getRootByName("Constraint")
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
    }

}
