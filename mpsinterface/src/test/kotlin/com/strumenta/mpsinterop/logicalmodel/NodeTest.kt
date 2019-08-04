package com.strumenta.mpsinterop.logicalmodel

import org.junit.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NodeTest {

    @Test
    fun isRootNegativeCase() {
        val language = Language(UUID.randomUUID(), "MyLanguage")

        val concept1 = language.createConcept(1234234L, "MyConcept")
        val link = concept1.createContainmentLink(2323L, "MyLink", Multiplicity.ZERO_OR_MORE)

        val node1 = Node(concept1, NodeId.regular(124L))
        val node2 = Node(concept1, NodeId.regular(125L))
        node1.addChild(link, node2)
        assertEquals(false, node2.isRoot)
    }

    @Test
    fun isRootPositiveCase() {
        val language = Language(UUID.randomUUID(), "MyLanguage")

        val concept1 = language.createConcept(1234234L, "MyConcept")
        val link = concept1.createContainmentLink(2323L, "MyLink", Multiplicity.ZERO_OR_MORE)

        val node1 = Node(concept1, NodeId.regular(124L))
        val node2 = Node(concept1, NodeId.regular(125L))
        node1.addChild(link, node2)
        assertEquals(true, node1.isRoot)
    }

    @Test
    fun modelOfWhichIsRootNull() {
        val language = Language(UUID.randomUUID(), "MyLanguage")

        val concept1 = language.createConcept(1234234L, "MyConcept")
        val link = concept1.createContainmentLink(2323L, "MyLink", Multiplicity.ZERO_OR_MORE)

        val node1 = Node(concept1, NodeId.regular(124L))
        val node2 = Node(concept1, NodeId.regular(125L))
        node1.addChild(link, node2)

        assertEquals(null, node1.modelOfWhichIsRoot)
        assertEquals(null, node2.modelOfWhichIsRoot)
    }

    @Test
    fun modelOfWhichIsRootNotNull() {
        val language = Language(UUID.randomUUID(), "MyLanguage")

        val concept1 = language.createConcept(1234234L, "MyConcept")
        val link = concept1.createContainmentLink(2323L, "MyLink", Multiplicity.ZERO_OR_MORE)

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

        val concept1 = language.createConcept(1234234L, "MyConcept")
        val link = concept1.createContainmentLink(2323L, "MyLink", Multiplicity.ZERO_OR_MORE)

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

        val concept1 = language.createConcept(1234234L, "MyConcept")
        val link = concept1.createContainmentLink(2323L, "MyLink", Multiplicity.ZERO_OR_MORE)

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

        val concept1 = language.createConcept(1234234L, "MyConcept")
        val link = concept1.createContainmentLink(2323L, "MyLink", Multiplicity.ZERO_OR_MORE)

        val node1 = Node(concept1, NodeId.regular(124L))
        val node2 = Node(concept1, NodeId.regular(125L))
        node1.addChild(link, node2)

        val model = Model("MyModel")

        model.addRoot(node1)

        assertEquals(model, node2.model)
    }

    @Test
    fun parentIsNull() {
        val language = Language(UUID.randomUUID(), "MyLanguage")

        val concept1 = language.createConcept(1234234L, "MyConcept")
        val link = concept1.createContainmentLink(2323L, "MyLink", Multiplicity.ZERO_OR_MORE)

        val node1 = Node(concept1, NodeId.regular(124L))
        val node2 = Node(concept1, NodeId.regular(125L))
        node1.addChild(link, node2)

        assertEquals(null, node1.parent)
    }

    @Test
    fun parentIsNotNull() {
        val language = Language(UUID.randomUUID(), "MyLanguage")

        val concept1 = language.createConcept(1234234L, "MyConcept")
        val link = concept1.createContainmentLink(2323L, "MyLink", Multiplicity.ZERO_OR_MORE)

        val node1 = Node(concept1, NodeId.regular(124L))
        val node2 = Node(concept1, NodeId.regular(125L))
        node1.addChild(link, node2)

        assertEquals(node1, node2.parent)
    }

    @Test
    fun settingParentNotNullWhenChild() {
        val language = Language(UUID.randomUUID(), "MyLanguage")

        val concept1 = language.createConcept(1234234L, "MyConcept")
        val link = concept1.createContainmentLink(2323L, "MyLink", Multiplicity.ZERO_OR_MORE)

        val node1 = Node(concept1, NodeId.regular(124L))
        val node2 = Node(concept1, NodeId.regular(125L))
        node1.addChild(link, node2)

        // this does nothing
        node2.parent = node1

        assertEquals(node1, node2.parent)
    }

    @Test(expected = RuntimeException::class)
    fun settingParentNotNullWhenNotChild() {
        val language = Language(UUID.randomUUID(), "MyLanguage")

        val concept1 = language.createConcept(1234234L, "MyConcept")
        val link = concept1.createContainmentLink(2323L, "MyLink", Multiplicity.ZERO_OR_MORE)

        val node1 = Node(concept1, NodeId.regular(124L))
        val node2 = Node(concept1, NodeId.regular(125L))

        // this explodes
        node2.parent = node1
    }

    @Test
    fun settingParentNullWhenNull() {
        val language = Language(UUID.randomUUID(), "MyLanguage")

        val concept1 = language.createConcept(1234234L, "MyConcept")
        val link = concept1.createContainmentLink(2323L, "MyLink", Multiplicity.ZERO_OR_MORE)

        val node1 = Node(concept1, NodeId.regular(124L))
        val node2 = Node(concept1, NodeId.regular(125L))

        // this does nothing
        node2.parent = null
        assertEquals(null, node2.parent)
    }

    @Test
    fun settingParentNullWhenNotNull() {
        val language = Language(UUID.randomUUID(), "MyLanguage")

        val concept1 = language.createConcept(1234234L, "MyConcept")
        val link = concept1.createContainmentLink(2323L, "MyLink", Multiplicity.ZERO_OR_MORE)

        val node1 = Node(concept1, NodeId.regular(124L))
        val node2 = Node(concept1, NodeId.regular(125L))
        node1.addChild(link, node2)

        assertTrue(node1.hasChild(node2))

        node2.parent = null

        assertEquals(null, node2.parent)
        assertFalse(node1.hasChild(node2))
    }

    @Test
    fun children() {
        val language = Language(UUID.randomUUID(), "MyLanguage")

        val concept1 = language.createConcept(1234234L, "MyConcept")
        val link = concept1.createContainmentLink(2323L, "MyLink", Multiplicity.ZERO_OR_MORE)

        val node1 = Node(concept1, NodeId.regular(124L))
        val node2 = Node(concept1, NodeId.regular(125L))
        val node3 = Node(concept1, NodeId.regular(126L))

        assertEquals(emptyList(), node1.children)
        node1.addChild(link, node2)
        assertEquals(listOf(node2), node1.children)
        node1.addChild(link, node3)
        assertEquals(listOf(node2, node3), node1.children)
    }

    @Test
    fun numberOfChildren() {
        val language = Language(UUID.randomUUID(), "MyLanguage")

        val concept1 = language.createConcept(1234234L, "MyConcept")
        val link = concept1.createContainmentLink(2323L, "MyLink", Multiplicity.ZERO_OR_MORE)

        val node1 = Node(concept1, NodeId.regular(124L))
        val node2 = Node(concept1, NodeId.regular(125L))
        val node3 = Node(concept1, NodeId.regular(126L))

        assertEquals(0, node1.numberOfChildren)
        node1.addChild(link, node2)
        assertEquals(1, node1.numberOfChildren)
        node1.addChild(link, node3)
        assertEquals(2, node1.numberOfChildren)
    }

    @Test
    fun getChildren() {
        val language = Language(UUID.randomUUID(), "MyLanguage")

        val concept1 = language.createConcept(1234234L, "MyConcept")
        val link = concept1.createContainmentLink(2323L, "MyLink", Multiplicity.ZERO_OR_MORE)

        val node1 = Node(concept1, NodeId.regular(124L))
        val node2 = Node(concept1, NodeId.regular(125L))
        val node3 = Node(concept1, NodeId.regular(126L))

        assertEquals(emptyList(), node1.getChildren(link))
        node1.addChild(link, node2)
        assertEquals(listOf(node2), node1.getChildren(link))
        node1.addChild(link, node3)
        assertEquals(listOf(node2, node3), node1.getChildren(link))
    }

    @Test
    fun hasChild() {
        val language = Language(UUID.randomUUID(), "MyLanguage")

        val concept1 = language.createConcept(1234234L, "MyConcept")
        val link = concept1.createContainmentLink(2323L, "MyLink", Multiplicity.ZERO_OR_MORE)

        val node1 = Node(concept1, NodeId.regular(124L))
        val node2 = Node(concept1, NodeId.regular(125L))
        val node3 = Node(concept1, NodeId.regular(126L))

        assertEquals(false, node1.hasChild(node1))
        assertEquals(false, node1.hasChild(node2))
        assertEquals(false, node1.hasChild(node3))
        node1.addChild(link, node2)
        assertEquals(false, node1.hasChild(node1))
        assertEquals(true, node1.hasChild(node2))
        assertEquals(false, node1.hasChild(node3))
        node1.addChild(link, node3)
        assertEquals(false, node1.hasChild(node1))
        assertEquals(true, node1.hasChild(node2))
        assertEquals(true, node1.hasChild(node3))
    }

    @Test
    fun getReference() {
        val language = Language(UUID.randomUUID(), "MyLanguage")

        val concept1 = language.createConcept(1234234L, "MyConcept")
        val link = concept1.createReferenceLink(2323L, "MyLink", Multiplicity.OPTIONAL)

        val node1 = Node(concept1, NodeId.regular(124L))
        val node2 = Node(concept1, NodeId.regular(125L))
        val node3 = Node(concept1, NodeId.regular(126L))

        assertEquals(null, node1.getReference(link))
        node1.setReference(link, node2)
        assertEquals(node2, node1.getReference(link))
        node1.setReference(link, node3)
        assertEquals(node3, node1.getReference(link))
        node1.setReference(link, null)
        assertEquals(null, node1.getReference(link))
    }

    @Test
    fun numberOfProperties() {
        val language = Language(UUID.randomUUID(), "MyLanguage")

        val concept1 = language.createConcept(1234234L, "MyConcept")
        val prop1 = concept1.createProperty(12344L, "Prop1", PrimitivePropertyType.STRING)
        val prop2 = concept1.createProperty(123334L, "Prop2", PrimitivePropertyType.STRING)

        val node1 = Node(concept1, NodeId.regular(124L))

        assertEquals(2, node1.numberOfProperties)
        node1.setProperty(prop1, "foo")
        assertEquals(2, node1.numberOfProperties)
        node1.setProperty(prop2, "bar")
        assertEquals(2, node1.numberOfProperties)
    }

    @Test
    fun propertyValueWhenNotPresent() {
        val language = Language(UUID.randomUUID(), "MyLanguage")

        val concept1 = language.createConcept(1234234L, "MyConcept")
        val prop1 = concept1.createProperty(12344L, "Prop1", PrimitivePropertyType.STRING)
        val prop2 = concept1.createProperty(123334L, "Prop2", PrimitivePropertyType.STRING)

        val node1 = Node(concept1, NodeId.regular(124L))

        assertEquals("", node1.propertyValue("Prop1"))
        assertEquals("", node1.propertyValue("Prop2"))
    }

    @Test
    fun propertyValueWhenPresent() {
        val language = Language(UUID.randomUUID(), "MyLanguage")

        val concept1 = language.createConcept(1234234L, "MyConcept")
        val prop1 = concept1.createProperty(12344L, "Prop1", PrimitivePropertyType.STRING)
        val prop2 = concept1.createProperty(123334L, "Prop2", PrimitivePropertyType.STRING)

        val node1 = Node(concept1, NodeId.regular(124L))

        node1.setProperty(prop1, "foo")
        node1.setProperty(prop2, "bar")

        assertEquals("foo", node1.propertyValue("Prop1"))
        assertEquals("bar", node1.propertyValue("Prop2"))
    }

    @Test
    fun booleanPropertyValue() {
        val language = Language(UUID.randomUUID(), "MyLanguage")

        val concept1 = language.createConcept(1234234L, "MyConcept")
        val prop1 = concept1.createProperty(12344L, "Prop1", PrimitivePropertyType.BOOLEAN)
        val prop2 = concept1.createProperty(123334L, "Prop2", PrimitivePropertyType.STRING)
        val prop3 = concept1.createProperty(1233224L, "Prop3", PrimitivePropertyType.INTEGER)

        val node1 = Node(concept1, NodeId.regular(124L))

        assertEquals(false, node1.booleanPropertyValue("Prop1"))
        node1.setProperty(prop1, true)
        assertEquals(true, node1.booleanPropertyValue("Prop1"))
        node1.setProperty(prop1, false)
        assertEquals(false, node1.booleanPropertyValue("Prop1"))
    }

    @Test
    fun longPropertyValue() {
        val language = Language(UUID.randomUUID(), "MyLanguage")

        val concept1 = language.createConcept(1234234L, "MyConcept")
        val prop1 = concept1.createProperty(12344L, "Prop1", PrimitivePropertyType.BOOLEAN)
        val prop2 = concept1.createProperty(123334L, "Prop2", PrimitivePropertyType.STRING)
        val prop3 = concept1.createProperty(1233224L, "Prop3", PrimitivePropertyType.INTEGER)

        val node1 = Node(concept1, NodeId.regular(124L))

        assertEquals(0L, node1.longPropertyValue("Prop3"))
        node1.setProperty(prop3, 888L)
        assertEquals(888L, node1.longPropertyValue("Prop3"))
    }

    @Test
    fun stringPropertyValue() {
        val language = Language(UUID.randomUUID(), "MyLanguage")

        val concept1 = language.createConcept(1234234L, "MyConcept")
        val prop1 = concept1.createProperty(12344L, "Prop1", PrimitivePropertyType.BOOLEAN)
        val prop2 = concept1.createProperty(123334L, "Prop2", PrimitivePropertyType.STRING)
        val prop3 = concept1.createProperty(1233224L, "Prop3", PrimitivePropertyType.INTEGER)

        val node1 = Node(concept1, NodeId.regular(124L))

        assertEquals("", node1.stringPropertyValue("Prop2"))
        node1.setProperty(prop2, "fff")
        assertEquals("fff", node1.stringPropertyValue("Prop2"))
    }

    @Test
    fun nameForConceptWithName() {
        val coreLang = Language(UUID.fromString("ceab5195-25ea-4f22-9b92-103b95ca8c0c"), "jetbrains.mps.lang.core")
        val iNamedConcept = InterfaceConcept(1169194658468L, "INamedConcept")
        coreLang.add(iNamedConcept)
        val nameProp = Property(AbsolutePropertyId(iNamedConcept.absoluteID!!, 1169194664001L), "name", PrimitivePropertyType.STRING)
        iNamedConcept.addProperty(nameProp)

        val language = Language(UUID.randomUUID(), "MyLanguage")

        val concept1 = Concept(1234234L, "MyConcept")
        concept1.implemented.add(iNamedConcept)
        language.add(concept1)

        val node1 = Node(concept1, NodeId.regular(124L))
        assertEquals("", node1.name)
        node1.setProperty(nameProp, "MyNode")
        assertEquals("MyNode", node1.name)
    }

    @Test
    fun nameForConceptWithoutName() {
        val coreLang = Language(UUID.fromString("ceab5195-25ea-4f22-9b92-103b95ca8c0c"), "jetbrains.mps.lang.core")
        val iNamedConcept = InterfaceConcept(1169194658468L, "INamedConcept")
        coreLang.add(iNamedConcept)
        val nameProp = Property(AbsolutePropertyId(iNamedConcept.absoluteID!!, 1169194664001L), "name", PrimitivePropertyType.STRING)
        iNamedConcept.addProperty(nameProp)

        val language = Language(UUID.randomUUID(), "MyLanguage")

        val concept1 = Concept(1234234L, "MyConcept")
        language.add(concept1)

        val node1 = Node(concept1, NodeId.regular(124L))
        assertEquals(null, node1.name)
    }

    @Test
    fun toStringForConceptWithName() {
        val coreLang = Language(UUID.fromString("ceab5195-25ea-4f22-9b92-103b95ca8c0c"), "jetbrains.mps.lang.core")
        val iNamedConcept = InterfaceConcept(1169194658468L, "INamedConcept")
        coreLang.add(iNamedConcept)
        val nameProp = Property(AbsolutePropertyId(iNamedConcept.absoluteID!!, 1169194664001L), "name", PrimitivePropertyType.STRING)
        iNamedConcept.addProperty(nameProp)

        val language = Language(UUID.randomUUID(), "MyLanguage")

        val concept1 = Concept(1234234L, "MyConcept")
        concept1.implemented.add(iNamedConcept)
        language.add(concept1)

        val node1 = Node(concept1, NodeId.regular(124L))
        assertEquals("() [MyConcept] (124)", node1.toString())
        node1.setProperty(nameProp, "MyNode")
        assertEquals("(MyNode) [MyConcept] (124)", node1.toString())
    }

    @Test
    fun toStringForConceptWithoutName() {
        val coreLang = Language(UUID.fromString("ceab5195-25ea-4f22-9b92-103b95ca8c0c"), "jetbrains.mps.lang.core")
        val iNamedConcept = InterfaceConcept(1169194658468L, "INamedConcept")
        coreLang.add(iNamedConcept)
        val nameProp = Property(AbsolutePropertyId(iNamedConcept.absoluteID!!, 1169194664001L), "name", PrimitivePropertyType.STRING)
        iNamedConcept.addProperty(nameProp)

        val language = Language(UUID.randomUUID(), "MyLanguage")

        val concept1 = Concept(1234234L, "MyConcept")
        language.add(concept1)

        val node1 = Node(concept1, NodeId.regular(124L))
        assertEquals("null [MyConcept] (124)", node1.toString())
    }
}
