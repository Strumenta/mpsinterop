package com.strumenta.mps

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import org.junit.Test
import java.io.File
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

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
        assertEquals(JsonObject().apply {
            addProperty("name", "index")
        }, json.get("properties"))
        assertEquals(JsonArray(), json.get("references"))
        assertEquals(JsonPrimitive("member"), json.get("containmentLinkName"))
        assertEquals(JsonPrimitive("jetbrains.mps.baseLanguage.structure.FieldDeclaration"), json.get("conceptName"))
        assertEquals(JsonPrimitive("5059837838423329956"), json.get("id"))
        assert(json.get("children").isJsonArray)
        assertEquals(3, json.get("children").asJsonArray.size())
        println(index.toJsonString())
    }

    @Test
    fun modelSerialization() {
        val projectDir = File("src/test/resources/mpsserver_2")
        val project = MpsProject(projectDir)
        val model = project.findModel("com.strumenta.mpsserver.logic")!!
        val json = model.toJsonObject()
        assertEquals(setOf("roots", "name", "uuid"), json.keySet())
        assert(json.get("roots").isJsonArray)
        val roots = json.get("roots").asJsonArray
        assertEquals(88, roots.size())
    }

}