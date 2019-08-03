package com.strumenta.mpsinterop.physicalmodel

import com.strumenta.mpsinterop.logicalmodel.LanguageId
import com.strumenta.mpsinterop.logicalmodel.NodeId
import org.junit.Test
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.lang.UnsupportedOperationException
import java.util.*
import kotlin.collections.HashSet
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PhysicalNodeTest {

    @Test
    fun isRootNegativeCase() {
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L, "MyConcept", "123x")
        val node1 = PhysicalNode(null, concept, NodeId.regular(124L))
        val node2 = PhysicalNode(node1, concept, NodeId.regular(125L))
        assertEquals(false, node2.isRoot)
    }

    @Test
    fun isRootPositiveCase() {
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L, "MyConcept", "123x")
        val node1 = PhysicalNode(null, concept, NodeId.regular(124L))
        val node2 = PhysicalNode(node1, concept, NodeId.regular(125L))
        assertEquals(true, node1.isRoot)
    }

    @Test
    fun modelOfWhichIsRootNull() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L, "MyConcept", "123x")
        val node1 = PhysicalNode(null, concept, NodeId.regular(124L))
        val node2 = PhysicalNode(node1, concept, NodeId.regular(125L))
        assertEquals(null, node1.modelOfWhichIsRoot)
    }

    @Test
    fun modelOfWhichIsRootNotNull() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L, "MyConcept", "123x")
        val node1 = PhysicalNode(null, concept, NodeId.regular(124L))
        val node2 = PhysicalNode(node1, concept, NodeId.regular(125L))
        model.addRoot(node1)
        assertEquals(model, node1.modelOfWhichIsRoot)
        assertEquals(null, node2.modelOfWhichIsRoot)
    }

    @Test
    fun modelNull() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L, "MyConcept", "123x")
        val node1 = PhysicalNode(null, concept, NodeId.regular(124L))
        val node2 = PhysicalNode(node1, concept, NodeId.regular(125L))
        assertEquals(null, node1.model)
        assertEquals(null, node2.model)
    }

    @Test
    fun modelNotNullForRoot() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L, "MyConcept", "123x")
        val node1 = PhysicalNode(null, concept, NodeId.regular(124L))
        val node2 = PhysicalNode(node1, concept, NodeId.regular(125L))
        model.addRoot(node1)
        assertEquals(model, node1.model)
    }

    @Test
    fun modelNotNullForRootDescendant() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L, "MyConcept", "123x")
        val node1 = PhysicalNode(null, concept, NodeId.regular(124L))
        val node2 = PhysicalNode(node1, concept, NodeId.regular(125L))
        model.addRoot(node1)
        assertEquals(model, node2.model)
    }

    @Test(expected = IllegalStateException::class)
    fun qualifiedNameDangling() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L, "MyConcept", "123x")
        val node1 = PhysicalNode(null, concept, NodeId.regular(124L))
        val node2 = PhysicalNode(node1, concept, NodeId.regular(125L))
        node1.qualifiedName()
    }

    @Test(expected = UnsupportedOperationException::class)
    fun qualifiedNameNonRoot() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L, "MyConcept", "123x")
        val node1 = PhysicalNode(null, concept, NodeId.regular(124L))
        val node2 = PhysicalNode(node1, concept, NodeId.regular(125L))
        model.addRoot(node1)
        node2.qualifiedName()
    }

    @Test(expected = IllegalStateException::class)
    fun qualifiedNameRootNoName() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L, "MyConcept", "123x")
        val node1 = PhysicalNode(null, concept, NodeId.regular(124L))
        val node2 = PhysicalNode(node1, concept, NodeId.regular(125L))
        model.addRoot(node1)
        node1.qualifiedName()
    }

    @Test
    fun qualifiedNamePositiveCase() {

        val model = PhysicalModel(UUID.randomUUID(), "MyModel")

        val iNamedConcept = PhysicalConcept(LanguageId(UUID.randomUUID(), "jetbrains.mps.lang.core"), 123L, "INamedConcept", "123x")
        val nameProp = PhysicalProperty(iNamedConcept, 789L, NAME_PROPERTY, "89dy")
        iNamedConcept.addProperty(nameProp)

        model.registerConcept(iNamedConcept)
        assertEquals(nameProp, model.findPropertyByName(INAMED_CONCEPT, NAME_PROPERTY))

        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L, "MyConcept", "123x")
        val node1 = PhysicalNode(null, concept, NodeId.regular(124L))
        val node2 = PhysicalNode(node1, concept, NodeId.regular(125L))

        node1[nameProp] = "MyNode1"
        model.addRoot(node1)
        assertEquals("MyNode1", node1.name())

        node2[nameProp] = "MyNode2"
        assertEquals("MyNode2", node2.name())

        assertEquals("MyModel.MyNode1", node1.qualifiedName())
    }
}
