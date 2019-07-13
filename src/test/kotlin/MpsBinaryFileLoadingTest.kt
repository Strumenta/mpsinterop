import com.strumenta.mpsinterop.binary.loadMpsModelFromBinaryFile
import kotlin.test.Test

class MpsBinaryFileLoadingTest {

    @Test
    fun loadPhysicalModelFromBinaryFile() {
        val inputStream = MpsBinaryFileLoadingTest::class.java.getResourceAsStream("/jetbrains.mps.lang.core-src-structure.mpb")
        val model = loadMpsModelFromBinaryFile(inputStream)

//        assertEquals("Formats.structure", model.name)
//
//        assertEquals(36, model.numberOfRoots)
//
//        val constraintNode = model.getRootByName("Constraint")
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

        // TODO check no properties
        // TODO check no references
        // TODO check no children
    }

}
