package com.strumenta.mps

import org.junit.Test
import java.io.File
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class MpsProjectTest {

    @Test
    fun languageNames() {
        val projectDir = File("src/test/resources/mpsserver_1")
        val project = MpsProject(projectDir)
        assertEquals(setOf("com.strumenta.businessorg", "com.strumenta.financialcalc"), project.languageNames())
    }

    @Test
    fun solutionNames() {
        val projectDir = File("src/test/resources/mpsserver_1")
        val project = MpsProject(projectDir)
        assertEquals(setOf("com.strumenta.businessorg.sandbox", "com.strumenta.financialcalc.sandbox",
        "com.strumenta.mpsserver.build", "com.strumenta.mpsserver.buildmeta", "com.strumenta.mpsserver.deps",
                "com.strumenta.mpsserver.extensionkit", "com.strumenta.mpsserver.ide", "com.strumenta.mpsserver.launcher",
                "com.strumenta.mpsserver.operations", "com.strumenta.mpsserver.server", "com.strumenta.mpsserver.server.tests"), project.solutionNames())
    }

    @Test
    fun hasSolutionPositive() {
        val projectDir = File("src/test/resources/mpsserver_1")
        val project = MpsProject(projectDir)
        assertEquals(true, project.hasSolution("com.strumenta.mpsserver.build"))
    }

    @Test
    fun hasSolutionNegative() {
        val projectDir = File("src/test/resources/mpsserver_1")
        val project = MpsProject(projectDir)
        assertEquals(false, project.hasSolution("com.strumenta.mpsserver.build.foo"))
    }

    @Test
    fun hasLanguagePositive() {
        val projectDir = File("src/test/resources/mpsserver_1")
        val project = MpsProject(projectDir)
        assertEquals(true, project.hasLanguage("com.strumenta.businessorg"))
    }

    @Test
    fun hasLanguageNegative() {
        val projectDir = File("src/test/resources/mpsserver_1")
        val project = MpsProject(projectDir)
        assertEquals(false, project.hasLanguage("com.strumenta.foo"))
    }

    @Test
    fun language() {
        val projectDir = File("src/test/resources/mpsserver_1")
        val project = MpsProject(projectDir)
        val language = project.language("com.strumenta.businessorg")
        assertNotNull(language)
        assertEquals("com.strumenta.businessorg", language.name)
        assertEquals(0, language.moduleVersion)
        assertEquals(0, language.languageVersion)
        assertEquals(UUID.fromString("96ad5b8f-04fe-4e16-a7d6-0e014b8726e4"), language.uuid)
    }

    @Test
    fun solution() {
        val projectDir = File("src/test/resources/mpsserver_1")
        val project = MpsProject(projectDir)
        val solution = project.solution("com.strumenta.businessorg.sandbox")
        assertNotNull(solution)
        assertEquals("com.strumenta.businessorg.sandbox", solution.name)
        assertEquals(0, solution.moduleVersion)
        assertEquals(UUID.fromString("304d28bd-2c3c-4fbd-b987-dbce2813a938"), solution.uuid)
    }

    @Test
    fun languageModelNames() {
        val projectDir = File("src/test/resources/mpsserver_1")
        val project = MpsProject(projectDir)
        val language = project.language("com.strumenta.businessorg")
        assertNotNull(language)
        assertEquals(setOf("com.strumenta.businessorg.behavior", "com.strumenta.businessorg.constraints",
                "com.strumenta.businessorg.editor", "com.strumenta.businessorg.intentions",
                "com.strumenta.businessorg.structure", "com.strumenta.businessorg.typesystem"), language.modelNames())
    }

    @Test
    fun languageFindModelByNamePositive() {
        val projectDir = File("src/test/resources/mpsserver_1")
        val project = MpsProject(projectDir)
        val language = project.language("com.strumenta.businessorg")
        assertNotNull(language)
        val model = language.findModel("com.strumenta.businessorg.structure")
        assertNotNull(model)
        assertEquals("com.strumenta.businessorg.structure", model.name)
        assertEquals(UUID.fromString("7b9b5e1d-3054-41f7-a62a-e0116b0952e3"), model.uuid)
    }

    @Test
    fun languageFindModelByNameNegative() {
        val projectDir = File("src/test/resources/mpsserver_1")
        val project = MpsProject(projectDir)
        val language = project.language("com.strumenta.businessorg")
        assertNotNull(language)
        val model = language.findModel("com.strumenta.businessorg.foo")
        assertNull(model)
    }

    @Test
    fun languageFindModelByUUIDPositive() {
        val projectDir = File("src/test/resources/mpsserver_1")
        val project = MpsProject(projectDir)
        val language = project.language("com.strumenta.businessorg")
        assertNotNull(language)
        val model = language.findModel(UUID.fromString("7b9b5e1d-3054-41f7-a62a-e0116b0952e3"))
        assertNotNull(model)
        assertEquals("com.strumenta.businessorg.structure", model.name)
        assertEquals(UUID.fromString("7b9b5e1d-3054-41f7-a62a-e0116b0952e3"), model.uuid)
    }

    @Test
    fun languageFindModelByUUIDNegative() {
        val projectDir = File("src/test/resources/mpsserver_1")
        val project = MpsProject(projectDir)
        val language = project.language("com.strumenta.businessorg")
        assertNotNull(language)
        val model = language.findModel(UUID.fromString("7b9b5e1d-9999-41f7-a62a-e0116b0952e3"))
        assertNull(model)
    }

    @Test
    fun modelRootNodes() {
        val projectDir = File("src/test/resources/mpsserver_1")
        val project = MpsProject(projectDir)
        val solution = project.solution("com.strumenta.businessorg.sandbox")!!
        val model = solution.findModel("com.strumenta.businessorg.sandbox.acmeinc")!!
        assertEquals(2, model.numberOfRoots())
    }

    @Test
    fun modelRoots() {
        val projectDir = File("src/test/resources/mpsserver_1")
        val project = MpsProject(projectDir)
        val solution = project.solution("com.strumenta.businessorg.sandbox")!!
        val model = solution.findModel("com.strumenta.businessorg.sandbox.acmeinc")!!
        assertEquals(2, model.roots().size)
        val root1 = model.roots()[0]
        assertEquals("com.strumenta.businessorg.structure.Organization", root1.conceptName)
        assertEquals("5270253970127314084", root1.id)
        val root2 = model.roots()[1]
        assertEquals("com.strumenta.businessorg.structure.Process", root2.conceptName)
        assertEquals("5270253970127368335", root2.id)
    }

    @Test
    fun findModel() {
        val projectDir = File("src/test/resources/mpsserver_2")
        val project = MpsProject(projectDir)
        val model = project.findModel("com.strumenta.mpsserver.logic")
        assertNotNull(model)
        assertEquals("com.strumenta.mpsserver.logic", model.name)
    }

    @Test
    fun modelRootsOfSpecificConcept() {
        val projectDir = File("src/test/resources/mpsserver_2")
        val project = MpsProject(projectDir)
        val model = project.findModel("com.strumenta.mpsserver.logic")!!
        val roots = model.roots("jetbrains.mps.baseLanguage.structure.ClassConcept")
        assertEquals(86, roots.size)
        assertNotNull(roots.find { it.name == "ConceptInfo" })
        assertNotNull(roots.find { it.name == "ConceptRoutes" })
        assertNotNull(roots.find { it.name == "AddChild" })
        assertNotNull(roots.find { it.name == "AddChildAnswer" })
        assertNull(roots.find { it.name == "Zuzzurellone" })
    }

    @Test
    fun modelProperties() {
        val projectDir = File("src/test/resources/mpsserver_2")
        val project = MpsProject(projectDir)
        val model = project.findModel("com.strumenta.mpsserver.logic")!!
        val roots = model.roots("jetbrains.mps.baseLanguage.structure.ClassConcept")
        assertEquals(86, roots.size)
        val conceptInfo = roots.find { it.name == "ConceptInfo" }
        assertNotNull(conceptInfo)
        assertEquals("data.metamodel", conceptInfo.properties["virtualPackage"])
        assertEquals("ConceptInfo", conceptInfo.properties["name"])
    }

    @Test
    fun rootContainmentLinkName() {
        val projectDir = File("src/test/resources/mpsserver_2")
        val project = MpsProject(projectDir)
        val model = project.findModel("com.strumenta.mpsserver.logic")!!
        val roots = model.roots("jetbrains.mps.baseLanguage.structure.ClassConcept")
        assertEquals(86, roots.size)
        val conceptInfo = roots.find { it.name == "ConceptInfo" }!!
        assertEquals(null, conceptInfo.containmentLinkName)
    }

    @Test
    fun nodeChildren() {
        val projectDir = File("src/test/resources/mpsserver_2")
        val project = MpsProject(projectDir)
        val model = project.findModel("com.strumenta.mpsserver.logic")!!
        val roots = model.roots("jetbrains.mps.baseLanguage.structure.ClassConcept")
        assertEquals(86, roots.size)
        val addChild = roots.find { it.name == "AddChild" }!!
        val children = addChild.children
        assertEquals(7, children.size)
    }

    @Test
    fun nodeReferences() {
        val projectDir = File("src/test/resources/mpsserver_2")
        val project = MpsProject(projectDir)
        val model = project.findModel("com.strumenta.mpsserver.logic")!!
        val roots = model.roots("jetbrains.mps.baseLanguage.structure.ClassConcept")
        assertEquals(86, roots.size)
        val addChild = roots.find { it.name == "AddChild" }!!
        val superclassChild = addChild.child("superclass")!!
        val references = superclassChild.references
        assertEquals(1, references.size)
    }

    @Test
    fun nodeReferenceByName() {
        val projectDir = File("src/test/resources/mpsserver_2")
        val project = MpsProject(projectDir)
        val model = project.findModel("com.strumenta.mpsserver.logic")!!
        val roots = model.roots("jetbrains.mps.baseLanguage.structure.ClassConcept")
        assertEquals(86, roots.size)
        val addChild = roots.find { it.name == "AddChild" }!!
        val superclassClassifier = addChild.child("superclass")!!.reference("classifier")
        assertNotNull(superclassClassifier)
        assertEquals("RequestMessage", superclassClassifier.name)
    }

}