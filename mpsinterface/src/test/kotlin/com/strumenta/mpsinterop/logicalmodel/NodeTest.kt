package com.strumenta.mpsinterop.logicalmodel

import com.strumenta.mpsinterop.physicalmodel.PhysicalConcept
import com.strumenta.mpsinterop.physicalmodel.PhysicalModel
import com.strumenta.mpsinterop.physicalmodel.PhysicalNode
import org.junit.Before
import org.junit.Test
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.test.assertEquals

class NodeTest {

    @Test
    fun isRootNegativeCase() {
        val language = Language(UUID.randomUUID(), "MyLanguage")

        val concept1 = language.addConcept(1234234L, "MyConcept")
        val link = concept1.addContainmentLink(2323L, "MyLink")

        val node1 = Node(concept1, NodeId.regular(124L))
        val node2 = Node(concept1, NodeId.regular(125L))
        node1.addChild(link, node2)
        assertEquals(false, node2.isRoot)
    }

    @Test
    fun isRootPositiveCase() {
        val language = Language(UUID.randomUUID(), "MyLanguage")

        val concept1 = language.addConcept(1234234L, "MyConcept")
        val link = concept1.addContainmentLink(2323L, "MyLink")

        val node1 = Node(concept1, NodeId.regular(124L))
        val node2 = Node(concept1, NodeId.regular(125L))
        node1.addChild(link, node2)
        assertEquals(true, node1.isRoot)
    }

    @Test
    fun modelOfWhichIsRootNull() {
        val language = Language(UUID.randomUUID(), "MyLanguage")

        val concept1 = language.addConcept(1234234L, "MyConcept")
        val link = concept1.addContainmentLink(2323L, "MyLink")

        val node1 = Node(concept1, NodeId.regular(124L))
        val node2 = Node(concept1, NodeId.regular(125L))
        node1.addChild(link, node2)

        assertEquals(null, node1.modelOfWhichIsRoot)
        assertEquals(null, node2.modelOfWhichIsRoot)
    }

    @Test
    fun modelOfWhichIsRootNotNull() {
        val language = Language(UUID.randomUUID(), "MyLanguage")

        val concept1 = language.addConcept(1234234L, "MyConcept")
        val link = concept1.addContainmentLink(2323L, "MyLink")

        val node1 = Node(concept1, NodeId.regular(124L))
        val node2 = Node(concept1, NodeId.regular(125L))
        node1.addChild(link, node2)

        val model = Model("MyModel")

        model.addRoot(node1)

        assertEquals(model, node1.modelOfWhichIsRoot)
        assertEquals(null, node2.modelOfWhichIsRoot)
    }

    @Test
    fun modelNull() {
        val language = Language(UUID.randomUUID(), "MyLanguage")

        val concept1 = language.addConcept(1234234L, "MyConcept")
        val link = concept1.addContainmentLink(2323L, "MyLink")

        val node1 = Node(concept1, NodeId.regular(124L))
        val node2 = Node(concept1, NodeId.regular(125L))
        node1.addChild(link, node2)

        val model = Model("MyModel")

        assertEquals(null, node1.model)
        assertEquals(null, node2.model)
    }

    @Test
    fun modelNotNullForRoot() {
        val language = Language(UUID.randomUUID(), "MyLanguage")

        val concept1 = language.addConcept(1234234L, "MyConcept")
        val link = concept1.addContainmentLink(2323L, "MyLink")

        val node1 = Node(concept1, NodeId.regular(124L))
        val node2 = Node(concept1, NodeId.regular(125L))
        node1.addChild(link, node2)

        val model = Model("MyModel")

        model.addRoot(node1)

        assertEquals(model, node1.model)
    }

    @Test
    fun modelNotNullForRootDescendant() {
        val language = Language(UUID.randomUUID(), "MyLanguage")

        val concept1 = language.addConcept(1234234L, "MyConcept")
        val link = concept1.addContainmentLink(2323L, "MyLink")

        val node1 = Node(concept1, NodeId.regular(124L))
        val node2 = Node(concept1, NodeId.regular(125L))
        node1.addChild(link, node2)

        val model = Model("MyModel")

        model.addRoot(node1)
        
        assertEquals(model, node2.model)
    }
}
