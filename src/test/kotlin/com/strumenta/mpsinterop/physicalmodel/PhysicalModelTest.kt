package com.strumenta.mpsinterop.physicalmodel

import com.strumenta.mpsinterop.logicalmodel.LanguageId
import com.strumenta.mpsinterop.logicalmodel.NodeId
import org.junit.Test
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.collections.HashSet
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PhysicalModelTest {

    @Test
    fun moduleIsInitiallyNull() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        assertEquals(null, model.module)
    }

    @Test
    fun moduleCanBeSetAndRetrieved() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val module = PhysicalModule(UUID.randomUUID(), "MyModule")
        model.module = module
        assertEquals(module, model.module)
    }

    @Test
    fun settingModuleInsertTheModelInTheModule() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val module = PhysicalModule(UUID.randomUUID(), "MyModule")
        model.module = module
        assertTrue(module.models.contains(model))
    }

    @Test
    fun addingRootsTheyAreRetained() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        assertEquals(true, model.roots.isEmpty())

        val concept = PhysicalConcept(LanguageId(UUID.randomUUID(), "MyLanguage"), 123L, "MyConcept", "123x")
        val node1 = PhysicalNode(null, concept, NodeId.regular(1L))
        val node2 = PhysicalNode(null, concept, NodeId.regular(2L))
        model.addRoot(node1)
        model.addRoot(node2)
        assertEquals(2, model.roots.size)
        assertEquals(2, model.numberOfRoots)
    }

    @Test
    fun addingRootsModelOfWhichIsRootIsSet() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        assertEquals(true, model.roots.isEmpty())

        val concept = PhysicalConcept(LanguageId(UUID.randomUUID(), "MyLanguage"), 123L, "MyConcept", "123x")
        val node1 = PhysicalNode(null, concept, NodeId.regular(1L))
        val node2 = PhysicalNode(null, concept, NodeId.regular(2L))
        assertEquals(null, node1.modelOfWhichIsRoot)
        assertEquals(null, node2.modelOfWhichIsRoot)
        model.addRoot(node1)
        model.addRoot(node2)
        assertEquals(model, node1.modelOfWhichIsRoot)
        assertEquals(model, node2.modelOfWhichIsRoot)
    }

    @Test
    fun onRoots() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        assertEquals(true, model.roots.isEmpty())

        val concept = PhysicalConcept(LanguageId(UUID.randomUUID(), "MyLanguage"), 123L, "MyConcept", "123x")
        val node1 = PhysicalNode(null, concept, NodeId.regular(1L))
        val node2 = PhysicalNode(null, concept, NodeId.regular(2L))
        model.addRoot(node1)
        model.addRoot(node2)

        val calls = HashSet<PhysicalNode>()
        model.onRoots { calls.add(it) }
        assertEquals(2, calls.size)
        assertTrue(calls.contains(node1))
        assertTrue(calls.contains(node2))
    }

    @Test
    fun onRootsWithRightConcept() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        assertEquals(true, model.roots.isEmpty())

        val concept = PhysicalConcept(LanguageId(UUID.randomUUID(), "MyLanguage"), 123L, "MyConcept", "123x")
        val node1 = PhysicalNode(null, concept, NodeId.regular(1L))
        val node2 = PhysicalNode(null, concept, NodeId.regular(2L))
        model.addRoot(node1)
        model.addRoot(node2)

        val calls = HashSet<PhysicalNode>()
        model.onRoots(concept) { calls.add(it) }
        assertEquals(2, calls.size)
        assertTrue(calls.contains(node1))
        assertTrue(calls.contains(node2))
    }

    @Test
    fun onRootsWithWrongConcept() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        assertEquals(true, model.roots.isEmpty())

        val concept = PhysicalConcept(LanguageId(UUID.randomUUID(), "MyLanguage"), 123L, "MyConcept", "123x")
        val anotherConcept = PhysicalConcept(LanguageId(UUID.randomUUID(), "MyLanguage"), 124L, "MyOtherConcept", "124x")
        val node1 = PhysicalNode(null, concept, NodeId.regular(1L))
        val node2 = PhysicalNode(null, concept, NodeId.regular(2L))
        model.addRoot(node1)
        model.addRoot(node2)

        val calls = HashSet<PhysicalNode>()
        model.onRoots(anotherConcept) { calls.add(it) }
        assertEquals(0, calls.size)
    }

    @Test
    fun findRootByNameNegativeCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        assertEquals(true, model.roots.isEmpty())

        val concept = PhysicalConcept(LanguageId(UUID.randomUUID(), "MyLanguage"), 123L, "MyConcept", "123x")
        val node1 = PhysicalNode(null, concept, NodeId.regular(1L))
        val node2 = PhysicalNode(null, concept, NodeId.regular(2L))
        model.addRoot(node1)
        model.addRoot(node2)

        assertEquals(null, model.findRootByName("MyUnexistingRoot"))
    }

    @Test
    fun findRootByNamePositiveCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        assertEquals(true, model.roots.isEmpty())

        val concept = PhysicalConcept(LanguageId(UUID.randomUUID(), "MyLanguage"), 123L, "MyConcept", "123x")
        val iNamedConcept = PhysicalConcept(LanguageId(UUID.randomUUID(), "jetbrains.mps.lang.core"), 123L, "INamedConcept", "123x")
        val nameProp = PhysicalProperty(concept, 789L, NAME_PROPERTY, "89dy")
        iNamedConcept.addProperty(nameProp)
        model.registerConcept(concept)
        model.registerConcept(iNamedConcept)
        assertEquals(nameProp, model.findPropertyByName(INAMED_CONCEPT, NAME_PROPERTY))

        val node1 = PhysicalNode(null, concept, NodeId.regular(1L))
        node1[nameProp] = "MyNode1"
        model.addRoot(node1)
        assertEquals("MyNode1", node1.name())
        val node2 = PhysicalNode(null, concept, NodeId.regular(2L))
        node2[nameProp] = "MyNode2"
        model.addRoot(node2)
        assertEquals("MyNode2", node2.name())

        assertEquals(node1, model.findRootByName("MyNode1"))
        assertEquals(node2, model.findRootByName("MyNode2"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun getRootByNameNegativeCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        assertEquals(true, model.roots.isEmpty())

        val concept = PhysicalConcept(LanguageId(UUID.randomUUID(), "MyLanguage"), 123L, "MyConcept", "123x")
        val node1 = PhysicalNode(null, concept, NodeId.regular(1L))
        val node2 = PhysicalNode(null, concept, NodeId.regular(2L))
        model.addRoot(node1)
        model.addRoot(node2)

        model.getRootByName("MyUnexistingRoot")
    }

    @Test
    fun getRootByNamePositiveCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        assertEquals(true, model.roots.isEmpty())

        val concept = PhysicalConcept(LanguageId(UUID.randomUUID(), "MyLanguage"), 123L, "MyConcept", "123x")
        val iNamedConcept = PhysicalConcept(LanguageId(UUID.randomUUID(), "jetbrains.mps.lang.core"), 123L, "INamedConcept", "123x")
        val nameProp = PhysicalProperty(concept, 789L, NAME_PROPERTY, "89dy")
        iNamedConcept.addProperty(nameProp)
        model.registerConcept(concept)
        model.registerConcept(iNamedConcept)
        assertEquals(nameProp, model.findPropertyByName(INAMED_CONCEPT, NAME_PROPERTY))

        val node1 = PhysicalNode(null, concept, NodeId.regular(1L))
        node1[nameProp] = "MyNode1"
        model.addRoot(node1)
        assertEquals("MyNode1", node1.name())
        val node2 = PhysicalNode(null, concept, NodeId.regular(2L))
        node2[nameProp] = "MyNode2"
        model.addRoot(node2)
        assertEquals("MyNode2", node2.name())

        assertEquals(node1, model.getRootByName("MyNode1"))
        assertEquals(node2, model.getRootByName("MyNode2"))
    }

    @Test
    fun rootsOfConceptWithRightConcept() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        assertEquals(true, model.roots.isEmpty())

        val concept = PhysicalConcept(LanguageId(UUID.randomUUID(), "MyLanguage"), 123L, "MyConcept", "123x")
        val node1 = PhysicalNode(null, concept, NodeId.regular(1L))
        val node2 = PhysicalNode(null, concept, NodeId.regular(2L))
        model.addRoot(node1)
        model.addRoot(node2)

        val roots = model.rootsOfConcept(concept)
        assertEquals(2, roots.size)
        assertTrue(roots.contains(node1))
        assertTrue(roots.contains(node2))
    }

    @Test
    fun rootsOfConceptWithWrongConcept() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        assertEquals(true, model.roots.isEmpty())

        val concept = PhysicalConcept(LanguageId(UUID.randomUUID(), "MyLanguage"), 123L, "MyConcept", "123x")
        val anotherConcept = PhysicalConcept(LanguageId(UUID.randomUUID(), "MyLanguage"), 124L, "MyOtherConcept", "124x")
        val node1 = PhysicalNode(null, concept, NodeId.regular(1L))
        val node2 = PhysicalNode(null, concept, NodeId.regular(2L))
        model.addRoot(node1)
        model.addRoot(node2)

        val roots = model.rootsOfConcept(anotherConcept)
        assertEquals(0, roots.size)
    }

    @Test(expected = IllegalArgumentException::class)
    fun languageUuidFromNameNegativeCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        model.languageUuidFromName("my.imported.language")
    }

    @Test
    fun languageUuidFromNamePositiveCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val uuid = UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7")
        model.putLanguageInRegistry(uuid, "my.imported.language")
        assertEquals(uuid, model.languageUuidFromName("my.imported.language"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun modelUUIDFromIndexNegativeCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        model.modelUUIDFromIndex("my.imported.model")
    }

    @Test
    fun modelUUIDFromIndexPositiveCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val uuid = UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7")
        model.putModelInRegistry(uuid, "my.imported.language", "45678")
        assertEquals(uuid, model.modelUUIDFromIndex("45678"))
    }

    @Test
    fun findConceptByIndexNegativeCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.randomUUID(), "MyLanguage"), 123L, "MyConcept", "123x")
        model.registerConcept(concept)
        assertEquals(null, model.findConceptByIndex("1234x"))
    }

    @Test
    fun findConceptByIndexPositiveCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.randomUUID(), "MyLanguage"), 123L, "MyConcept", "123x")
        model.registerConcept(concept)
        assertEquals(concept, model.findConceptByIndex("123x"))
    }

    @Test
    fun findConceptByIDNegativeCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L, "MyConcept", "123x")
        model.registerConcept(concept)
        assertEquals(null, model.findConceptByID(12345L))
    }

    @Test
    fun findConceptByIDPositiveCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L, "MyConcept", "123x")
        model.registerConcept(concept)
        assertEquals(concept, model.findConceptByID(123L))
    }

    @Test
    fun findConceptByNameNegativeCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L, "MyConcept", "123x")
        model.registerConcept(concept)
        assertEquals(null, model.findConceptByName("MyLanguage.structure.AnotherConcept"))
    }

    @Test
    fun findConceptByNamePositiveCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L, "MyConcept", "123x")
        model.registerConcept(concept)
        assertEquals(concept, model.findConceptByName("MyLanguage.structure.MyConcept"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun getRelationByIndexNegativeCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L, "MyConcept", "123x")
        model.registerConcept(concept)
        val relation = PhysicalRelation(concept, 34234L, "MyRelation", "345y", RelationKind.CONTAINMENT)
        model.registerRelation(relation)
        model.getRelationByIndex("777x")
    }

    @Test
    fun getRelationByIndexPositiveCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L, "MyConcept", "123x")
        model.registerConcept(concept)
        val relation = PhysicalRelation(concept, 34234L, "MyRelation", "345y", RelationKind.CONTAINMENT)
        model.registerRelation(relation)
        assertEquals(relation, model.getRelationByIndex("345y"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun getPropertyByIndexNegativeCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L, "MyConcept", "123x")
        model.registerConcept(concept)
        val property = PhysicalProperty(concept, 34234L, "MyProperty", "345y")
        model.registerProperty(property)
        model.getPropertyByIndex("777x")
    }

    @Test
    fun getPropertyByIndexPositiveCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L, "MyConcept", "123x")
        model.registerConcept(concept)
        val property = PhysicalProperty(concept, 34234L, "MyProperty", "345y")
        model.registerProperty(property)
        assertEquals(property, model.getPropertyByIndex("345y"))
    }

    @Test
    fun findPropertyByNameNegativeCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L, "MyConcept", "123x")
        model.registerConcept(concept)
        val property = PhysicalProperty(concept, 34234L, "MyProperty", "345y")
        model.registerProperty(property)
        assertEquals(null, model.findPropertyByName(concept.qualifiedName, "SomeOtherProperty"))
    }

    @Test
    fun findPropertyByNamePositiveCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L, "MyConcept", "123x")
        model.registerConcept(concept)
        val property = PhysicalProperty(concept, 34234L, "MyProperty", "345y")
        model.registerProperty(property)
        assertEquals(property, model.findPropertyByName(concept.qualifiedName, "MyProperty"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun getPropertyByNameNegativeCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L, "MyConcept", "123x")
        model.registerConcept(concept)
        val property = PhysicalProperty(concept, 34234L, "MyProperty", "345y")
        model.registerProperty(property)
        model.getPropertyByName(concept.qualifiedName, "SomeOtherProperty")
    }

    @Test
    fun getPropertyByNamePositiveCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L, "MyConcept", "123x")
        model.registerConcept(concept)
        val property = PhysicalProperty(concept, 34234L, "MyProperty", "345y")
        model.registerProperty(property)
        assertEquals(property, model.getPropertyByName(concept.qualifiedName, "MyProperty"))
    }

    @Test
    fun findNodeByIDLongNegativeCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L, "MyConcept", "123x")
        model.registerConcept(concept)
        val node = PhysicalNode(null, concept, NodeId.regular(124L))
        model.addRoot(node)
        assertEquals(null, model.findNodeByID(125L))
    }

    @Test
    fun findNodeByIDLongPositiveCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L, "MyConcept", "123x")
        model.registerConcept(concept)
        val node = PhysicalNode(null, concept, NodeId.regular(124L))
        model.addRoot(node)
        assertEquals(node, model.findNodeByID(124L))
    }

    @Test
    fun findNodeByIDNodeIDNegativeCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L, "MyConcept", "123x")
        model.registerConcept(concept)
        val node = PhysicalNode(null, concept, NodeId.regular(124L))
        model.addRoot(node)
        assertEquals(null, model.findNodeByID(NodeId.regular(125L)))
    }

    @Test
    fun findNodeByIDNodeIDPositiveCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L, "MyConcept", "123x")
        model.registerConcept(concept)
        val node = PhysicalNode(null, concept, NodeId.regular(124L))
        model.addRoot(node)
        assertEquals(node, model.findNodeByID(NodeId.regular(124L)))
    }

}
