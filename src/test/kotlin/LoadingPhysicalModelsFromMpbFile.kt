import com.strumenta.mpsinterop.binary.loadMpsModelFromBinaryFile
import com.strumenta.mpsinterop.physicalmodel.CONCEPT_DECLARATION_CONCEPT_NAME
import com.strumenta.mpsinterop.physicalmodel.OutsideModelReferenceTarget
import com.strumenta.mpsinterop.physicalmodel.PhysicalReferenceValue
import com.strumenta.mpsinterop.physicalmodel.name
import kotlin.test.Test
import kotlin.test.assertEquals

class LoadingPhysicalModelsFromMpbFile {

    @Test
    fun loadPhysicalModelOfLangCoreStructureBasicSanityCheck() {
        val inputStream = LoadingPhysicalModelsFromMpbFile::class.java.getResourceAsStream("/jetbrains.mps.lang.core-src-structure.mpb")
        val model = loadMpsModelFromBinaryFile(inputStream)

        assertEquals("jetbrains.mps.lang.core.structure", model.name)
        assertEquals(42, model.numberOfRoots)
    }

    @Test
    fun loadPhysicalModelOfINamedConcept() {
        val inputStream = LoadingPhysicalModelsFromMpbFile::class.java.getResourceAsStream("/jetbrains.mps.lang.core-src-structure.mpb")
        val model = loadMpsModelFromBinaryFile(inputStream)

        val iNamedConceptNode = model.getRootByName("INamedConcept")

        // Basis
//        assertEquals("INamedConcept", iNamedConceptNode.name())
//        assertEquals("6D8ZJLf0wUM", iNamedConceptNode.id.toBase64())
//        assertEquals(CONCEPT_DECLARATION_CONCEPT_NAME, iNamedConceptNode.concept.name)

        // Properties
//        assertEquals(true, iNamedConceptNode.booleanPropertyValue("abstract"))
//        assertEquals(false, iNamedConceptNode.booleanPropertyValue("final"))
//        assertEquals(false, iNamedConceptNode.booleanPropertyValue("rootable"))
//        assertEquals(1169194658468L, iNamedConceptNode.longPropertyValue("conceptId"))
//        assertEquals("", iNamedConceptNode.stringPropertyValue("languageId"))
//        assertEquals("", iNamedConceptNode.stringPropertyValue("iconPath"))
//        // static scope global has the value null (empty string)
//        assertEquals("", iNamedConceptNode.stringPropertyValue("staticScope"))
//        assertEquals("", iNamedConceptNode.stringPropertyValue("conceptAlias"))
//        assertEquals("", iNamedConceptNode.stringPropertyValue("conceptShortDescription"))
//
//        // Structure
//        val superConcept = iNamedConceptNode.reference("extends")
//        assertEquals(PhysicalReferenceValue(
//                OutsideModelReferenceTarget("tpck", "gw2VY9q"),
//                "BaseConcept"),
//                superConcept)
//        val sourceNode = iNamedConceptNode.reference("sourceNode")
//        assertEquals(null, sourceNode)
//        assertEquals(0, iNamedConceptNode.numberOfChildren("implements"))
//
//        // Relations
//        assertEquals(0, iNamedConceptNode.numberOfChildren("linkDeclaration"))
//        assertEquals(0, iNamedConceptNode.numberOfChildren("propertyDeclaration"))
//        assertEquals(0, iNamedConceptNode.numberOfChildren("helpURL"))
    }

//    @Test
//    @Ignore
//    fun loadPhysicalModelFromBinaryFile() {
//        val inputStream = LoadingPhysicalModelsFromMpbFile::class.java.getResourceAsStream("/jetbrains.mps.lang.core-src-structure.mpb")
//        val model = loadMpsModelFromBinaryFile(inputStream)
//
//        assertEquals("jetbrains.mps.lang.core.structure", model.name)
//
//        assertEquals(42, model.numberOfRoots)
//
//        TODO()
//        //val INamedConcept = model.named("INamedConcept")!!
//
////        assertEquals("INamedConcept", INamedConcept.name)
////        assertEquals(SNodeId.regular(1169194658468L), INamedConcept.nodeId)
////        assertEquals(1169125989551L, INamedConcept.concept.id.idValue)
////        assertEquals(2, INamedConcept.numberOfProperties)
////
////        assertEquals(1, INamedConcept.numberOfChildren)
////
////        val propertyName = INamedConcept.children.first()
////        assertEquals("PropertyDeclaration", propertyName.concept.name)
////        assertEquals("name", propertyName.name!!)
//    }

}
