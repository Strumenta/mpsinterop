package com.strumenta.deprecated_mpsinterop.logicalmodel

import org.junit.Test
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.test.assertEquals

class ModelTest {

    @Test
    fun numberOfRoots() {
        val concept = Concept(1234234L, "MyConcept")

        val model = Model("myModel")

        val node1 = Node(concept, NodeId.regular(1223343L))
        val node2 = Node(concept, NodeId.regular(122322223L))

        assertEquals(0, model.numberOfRoots)
        model.addRoot(node1)
        assertEquals(1, model.numberOfRoots)
        model.addRoot(node2)
        assertEquals(2, model.numberOfRoots)
    }

    @Test(expected = IllegalArgumentException::class)
    fun addNonRootFail() {
        val language = Language(UUID.randomUUID(), "MyLanguage")
        val concept = Concept(1234234L, "MyConcept")
        language.add(concept)
        val link = ContainmentLink(AbsoluteContainmentLinkId(concept.absoluteID!!, 1244L), "myLink", Multiplicity.ZERO_OR_MORE)

        val model = Model("myModel")

        val node1 = Node(concept, NodeId.regular(1223343L))
        val node2 = Node(concept, NodeId.regular(122322223L))

        node1.addChild(link, node2)

        model.addRoot(node2)
    }

    @Test
    fun addRootSetTheModelForNode() {
        val language = Language(UUID.randomUUID(), "MyLanguage")
        val concept = Concept(1234234L, "MyConcept")
        language.add(concept)
        val link = ContainmentLink(AbsoluteContainmentLinkId(concept.absoluteID!!, 1244L), "myLink", Multiplicity.ZERO_OR_MORE)
        concept.addLink(link)

        val model = Model("myModel")

        val node1 = Node(concept, NodeId.regular(1223343L))
        val node2 = Node(concept, NodeId.regular(122322223L))

        node1.addChild(link, node2)

        model.addRoot(node1)

        assertEquals(model, node1.model)
        assertEquals(model, node1.modelOfWhichIsRoot)
        assertEquals(model, node2.model)
        assertEquals(null, node2.modelOfWhichIsRoot)
    }

    @Test
    fun removingRootUnsetTheModelForNode() {
        val language = Language(UUID.randomUUID(), "MyLanguage")
        val concept = Concept(1234234L, "MyConcept")
        language.add(concept)
        val link = ContainmentLink(AbsoluteContainmentLinkId(concept.absoluteID!!, 1244L), "myLink", Multiplicity.ZERO_OR_MORE)
        concept.addLink(link)

        val model = Model("myModel")

        val node1 = Node(concept, NodeId.regular(1223343L))
        val node2 = Node(concept, NodeId.regular(122322223L))

        node1.addChild(link, node2)

        model.addRoot(node1)

        assertEquals(model, node1.model)
        assertEquals(model, node1.modelOfWhichIsRoot)
        assertEquals(model, node2.model)
        assertEquals(null, node2.modelOfWhichIsRoot)

        model.removeRoot(node1)

        assertEquals(null, node1.model)
        assertEquals(null, node1.modelOfWhichIsRoot)
        assertEquals(null, node2.model)
        assertEquals(null, node2.modelOfWhichIsRoot)
    }

    @Test
    fun onRootsWithoutConcept() {
        val language = Language(UUID.randomUUID(), "MyLanguage")
        val concept = Concept(1234234L, "MyConcept")
        language.add(concept)
        val link = ContainmentLink(AbsoluteContainmentLinkId(concept.absoluteID!!, 1244L), "myLink", Multiplicity.ZERO_OR_MORE)

        val model = Model("myModel")

        val node1 = Node(concept, NodeId.regular(1223343L))
        val node2 = Node(concept, NodeId.regular(122322223L))

        model.addRoot(node1)
        model.addRoot(node2)

        val ns = LinkedList<Node>()
        model.onRoots { ns.add(it) }
        assertEquals(listOf(node1, node2), ns)
    }

    @Test
    fun onRootsWithConcept() {
        val language = Language(UUID.randomUUID(), "MyLanguage")
        val concept1 = Concept(1234234L, "MyConcept")
        language.add(concept1)
        val concept2 = Concept(123334234L, "MyOtherConcept")
        language.add(concept2)

        val model = Model("myModel")

        val node1 = Node(concept1, NodeId.regular(1223343L))
        val node2 = Node(concept1, NodeId.regular(122322223L))
        val node3 = Node(concept2, NodeId.regular(122622223L))
        val node4 = Node(concept2, NodeId.regular(122822223L))
        val node5 = Node(concept1, NodeId.regular(128822223L))

        model.addRoot(node1)
        model.addRoot(node2)
        model.addRoot(node3)
        model.addRoot(node4)
        model.addRoot(node5)

        val ns = LinkedList<Node>()

        ns.clear()
        model.onRoots(concept1) { ns.add(it) }
        assertEquals(listOf(node1, node2, node5), ns)

        ns.clear()
        model.onRoots(concept2) { ns.add(it) }
        assertEquals(listOf(node3, node4), ns)
    }

    @Test
    fun getRootByName() {
        val coreLang = Language(UUID.fromString("ceab5195-25ea-4f22-9b92-103b95ca8c0c"), "jetbrains.mps.lang.core")
        val iNamedConcept = InterfaceConcept(1169194658468L, "INamedConcept")
        coreLang.add(iNamedConcept)
        val nameProp = Property(AbsolutePropertyId(iNamedConcept.absoluteID!!, 1169194664001L), "name", PrimitivePropertyType.STRING)
        iNamedConcept.addProperty(nameProp)

        val language = Language(UUID.randomUUID(), "MyLanguage")

        val concept1 = Concept(1234234L, "MyConcept")
        concept1.implemented.add(iNamedConcept)
        language.add(concept1)
        val concept2 = Concept(123334234L, "MyOtherConcept")
        concept2.implemented.add(iNamedConcept)
        language.add(concept2)

        val model = Model("myModel")

        val node1 = Node(concept1, NodeId.regular(1223343L))
        val node2 = Node(concept1, NodeId.regular(122322223L))
        val node3 = Node(concept2, NodeId.regular(122622223L))
        val node4 = Node(concept2, NodeId.regular(122822223L))
        val node5 = Node(concept1, NodeId.regular(128822223L))

        node1.setProperty(nameProp, "Node1")
        node2.setProperty(nameProp, "Node2")
        node3.setProperty(nameProp, "Node3")
        node4.setProperty(nameProp, "Node4")
        node5.setProperty(nameProp, "Node5")

        model.addRoot(node1)
        model.addRoot(node2)
        model.addRoot(node3)
        model.addRoot(node4)
        model.addRoot(node5)

        require("Node1" == node1.name)
        require("Node2" == node2.name)
        require("Node3" == node3.name)
        require("Node4" == node4.name)
        require("Node5" == node5.name)

        assertEquals(node3, model.getRootByName("Node3"))
    }
}
