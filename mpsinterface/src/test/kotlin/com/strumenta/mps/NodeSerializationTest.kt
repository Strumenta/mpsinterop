package com.strumenta.mps

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.strumenta.mps.organization.MpsProject
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class NodeSerializationTest {

    @Test
    fun addChildIndexSerialization() {
        val projectDir = File("src/test/resources/mpsserver_2")
        val project = MpsProject(projectDir)
        val model = project.findModel("com.strumenta.mpsserver.logic")!!
        val roots = model.roots("jetbrains.mps.baseLanguage.structure.ClassConcept")
        val addChild = roots.find { it.name == "AddChild" }!!
        val children = addChild.children("member")
        val index = children.find { it.name == "index" }!!
        val json = index.toJsonObject()
        assertEquals(setOf("properties", "children", "references", "conceptName", "id", "containmentLinkName"), json.keySet())
        assertEquals(
            JsonObject().apply {
                addProperty("name", "index")
            },
            json.get("properties")
        )
        assertEquals(JsonArray(), json.get("references"))
        assertEquals(JsonPrimitive("member"), json.get("containmentLinkName"))
        assertEquals(JsonPrimitive("jetbrains.mps.baseLanguage.structure.FieldDeclaration"), json.get("conceptName"))
        assertEquals(JsonPrimitive("5059837838423329956"), json.get("id"))
        assert(json.get("children").isJsonArray)
        assertEquals(3, json.get("children").asJsonArray.size())
    }

    @Test
    fun addChildContainerSerialization() {
        val projectDir = File("src/test/resources/mpsserver_2")
        val project = MpsProject(projectDir)
        val model = project.findModel("com.strumenta.mpsserver.logic")!!
        val roots = model.roots("jetbrains.mps.baseLanguage.structure.ClassConcept")
        val addChild = roots.find { it.name == "AddChild" }!!
        val children = addChild.children("member")
        val container = children.find { it.name == "container" }!!
        val json = container.toJsonObject()
        assertEquals(setOf("properties", "children", "references", "conceptName", "id", "containmentLinkName"), json.keySet())
        assertEquals(
            JsonObject().apply {
                addProperty("name", "container")
            },
            json.get("properties")
        )
        assertEquals(JsonArray(), json.get("references"))
        assertEquals(JsonPrimitive("member"), json.get("containmentLinkName"))
        assertEquals(JsonPrimitive("jetbrains.mps.baseLanguage.structure.FieldDeclaration"), json.get("conceptName"))
        assertEquals(JsonPrimitive("6215511152163644237"), json.get("id"))
        assert(json.get("children").isJsonArray)
        assertEquals(2, json.get("children").asJsonArray.size())
        val containerType = container.child("type")!!
        val containerTypeJson = containerType.toJsonObject()
        assertEquals(setOf("properties", "children", "references", "conceptName", "id", "containmentLinkName"), containerTypeJson.keySet())
        assert(containerTypeJson.get("references").isJsonArray)
        assertEquals(1, containerTypeJson.get("references").asJsonArray.size())
        val referenceJson = containerTypeJson.get("references").asJsonArray.get(0).asJsonObject
        assertEquals(setOf("linkName", "to"), referenceJson.keySet())
        assertEquals(JsonPrimitive("classifier"), referenceJson.get("linkName"))
        assertEquals(JsonPrimitive("int:5270253970128191709"), referenceJson.get("to"))
    }

//    @Test
//    fun modelSerialization() {
//        val projectDir = File("src/test/resources/mpsserver_2")
//        val project = MpsProject(projectDir)
//        val model = project.findModel("com.strumenta.mpsserver.logic")!!
//        val json = model.toJsonObject()
//        assertEquals(setOf("roots", "name", "uuid"), json.keySet())
//        assert(json.get("roots").isJsonArray)
//        val roots = json.get("roots").asJsonArray
//        assertEquals(88, roots.size())
//    }

    @Test
    fun serializationFromBinaryModel() {
        val jar = File("src/test/resources/mps2019_3_1/languages/languageDesign/jetbrains.mps.lang.core-src.jar")
        val language = loadLanguage(JarEntrySource(jar, "module/jetbrains.mps.lang.core.mpl"))
        val models = language.models()
        assertEquals(12, models.size)
        val model = models.iterator().next()
        val structureModel = language.findModel("jetbrains.mps.lang.core.structure")
        assertNotNull(structureModel)
        val roots = structureModel.roots()
        assertEquals(43, roots.size)
        val baseConcept = roots.find { it.name == "BaseConcept" }
        assertNotNull(baseConcept)
        assertEquals("BaseConcept", baseConcept.name)

        val json = baseConcept.toJsonObject()
        assertEquals(setOf("properties", "children", "references", "conceptName", "id"), json.keySet())
        val child = baseConcept.children.first()
        val childJson = child.toJsonObject()
        assertEquals(setOf("properties", "children", "references", "conceptName", "id", "containmentLinkName"), childJson.keySet())
    }

//    @Test
//    fun serializationOfEnvironmentCommon() {
//        val jar = File("src/test/resources/mps2019_3_1/languages/tools/jetbrains.mps.tool.common-src.jar")
//        val module = loadSolution(JarEntrySource(jar, "module/jetbrains.mps.tool.common.msd"))
//        val models = module.models()
//        assertEquals(1, models.size)
//        val model = module.findModel("jetbrains.mps.core.tool.environment.common")
//        assertNotNull(model)
//
//        val json = model.toJsonObject()
//        // We want to verify this does not crash
//    }
}
