import com.strumenta.mpsinterop.logicalmodel.SNodeId
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

        TODO()
        //val INamedConcept = model.named("INamedConcept")!!

//        assertEquals("INamedConcept", INamedConcept.name)
//        assertEquals(SNodeId.regular(1169194658468L), INamedConcept.nodeId)
//        assertEquals(1169125989551L, INamedConcept.concept.id.idValue)
//        assertEquals(2, INamedConcept.numberOfProperties)
//
//        assertEquals(1, INamedConcept.numberOfChildren)
//
//        val propertyName = INamedConcept.children.first()
//        assertEquals("PropertyDeclaration", propertyName.concept.name)
//        assertEquals("name", propertyName.name!!)
    }

}
