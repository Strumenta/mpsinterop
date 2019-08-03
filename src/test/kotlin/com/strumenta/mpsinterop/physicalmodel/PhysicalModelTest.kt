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
        assertEquals(nameProp, model.findProperty(INAMED_CONCEPT, NAME_PROPERTY))

        val node1 = PhysicalNode(null, concept, NodeId.regular(1L))
        node1.setProperty(nameProp, "MyNode1")
        model.addRoot(node1)
        assertEquals("MyNode1", node1.name())
        val node2 = PhysicalNode(null, concept, NodeId.regular(2L))
        node2.setProperty(nameProp, "MyNode2")
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
        assertEquals(nameProp, model.findProperty(INAMED_CONCEPT, NAME_PROPERTY))

        val node1 = PhysicalNode(null, concept, NodeId.regular(1L))
        node1.setProperty(nameProp, "MyNode1")
        model.addRoot(node1)
        assertEquals("MyNode1", node1.name())
        val node2 = PhysicalNode(null, concept, NodeId.regular(2L))
        node2.setProperty(nameProp, "MyNode2")
        model.addRoot(node2)
        assertEquals("MyNode2", node2.name())

        assertEquals(node1, model.getRootByName("MyNode1"))
        assertEquals(node2, model.getRootByName("MyNode2"))
    }
}
