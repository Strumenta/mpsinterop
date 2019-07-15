import com.strumenta.mpsinterop.binary.SConceptId
import com.strumenta.mpsinterop.binary.SNodeId
import com.strumenta.mpsinterop.binary.loadMpsModelFromBinaryFile
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

class MpsBinaryFileLoadingTest {

    @Test
    @Ignore
    fun loadPhysicalModelFromBinaryFile() {
        val inputStream = MpsBinaryFileLoadingTest::class.java.getResourceAsStream("/jetbrains.mps.lang.core-src-structure.mpb")
        val model = loadMpsModelFromBinaryFile(inputStream)

        assertEquals("jetbrains.mps.lang.core.structure", model.name)

        assertEquals(42, model.numberOfRoots)
        //model.roots.forEach { println(it.name) }

        val INamedConcept = model.named("INamedConcept")!!

        assertEquals("INamedConcept", INamedConcept.name)
        assertEquals(SNodeId.regular(1169194658468L), INamedConcept.nodeId)
        assertEquals(1169125989551L, INamedConcept.concept.id.idValue)
        assertEquals(1, INamedConcept.numberOfProperties)
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
