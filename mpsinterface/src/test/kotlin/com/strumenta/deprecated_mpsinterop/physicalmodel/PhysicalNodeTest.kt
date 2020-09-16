package com.strumenta.deprecated_mpsinterop.physicalmodel

import com.strumenta.deprecated_mpsinterop.logicalmodel.LanguageId
import com.strumenta.deprecated_mpsinterop.logicalmodel.NodeId
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals

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

    @Test
    fun childrenRelationNegativeCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L,
                "MyConcept", "123x")
        val relation = PhysicalRelation(concept, 54534534L, "MyRelation", "23ddd", RelationKind.CONTAINMENT)
        model.registerRelation(relation)
        val node1 = PhysicalNode(null, concept, NodeId.regular(124L))

        assertEquals(emptyList(), node1.children(relation))
    }

    @Test(expected = IllegalArgumentException::class)
    fun childrenRelationPassingReference() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L,
                "MyConcept", "123x")
        val relation = PhysicalRelation(concept, 54534534L, "MyRelation", "23ddd", RelationKind.REFERENCE)
        model.registerRelation(relation)
        val node1 = PhysicalNode(null, concept, NodeId.regular(124L))
        node1.children(relation)
    }

    @Test
    fun childrenRelationPositiveCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L,
                "MyConcept", "123x")
        val relation = PhysicalRelation(concept, 54534534L, "MyRelation", "23ddd", RelationKind.CONTAINMENT)
        model.registerRelation(relation)
        val node1 = PhysicalNode(null, concept, NodeId.regular(124L))
        val node2 = PhysicalNode(null, concept, NodeId.regular(125L))
        val node3 = PhysicalNode(null, concept, NodeId.regular(126L))
        node1.addChild(relation, node2)
        node1.addChild(relation, node3)

        assertEquals(listOf(node2, node3), node1.children(relation))
    }

    @Test
    fun childrenStringNegativeCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L,
                "MyConcept", "123x")
        val relation = PhysicalRelation(concept, 54534534L, "MyRelation", "23ddd", RelationKind.CONTAINMENT)
        model.registerRelation(relation)
        val node1 = PhysicalNode(null, concept, NodeId.regular(124L))
        val node2 = PhysicalNode(null, concept, NodeId.regular(125L))
        val node3 = PhysicalNode(null, concept, NodeId.regular(126L))
        node1.addChild(relation, node2)
        node1.addChild(relation, node3)

        assertEquals(emptyList(), node1.children("anotherRelation"))
    }

    @Test
    fun childrenStringPositiveCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L,
                "MyConcept", "123x")
        val relation = PhysicalRelation(concept, 54534534L, "MyRelation", "23ddd", RelationKind.CONTAINMENT)
        model.registerRelation(relation)
        val node1 = PhysicalNode(null, concept, NodeId.regular(124L))
        val node2 = PhysicalNode(null, concept, NodeId.regular(125L))
        val node3 = PhysicalNode(null, concept, NodeId.regular(126L))
        node1.addChild(relation, node2)
        node1.addChild(relation, node3)

        assertEquals(listOf(node2, node3), node1.children("MyRelation"))
    }

    @Test
    fun numberOfChildrenNegativeCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L,
                "MyConcept", "123x")
        val relation = PhysicalRelation(concept, 54534534L, "MyRelation", "23ddd", RelationKind.CONTAINMENT)
        model.registerRelation(relation)
        val node1 = PhysicalNode(null, concept, NodeId.regular(124L))
        val node2 = PhysicalNode(null, concept, NodeId.regular(125L))
        val node3 = PhysicalNode(null, concept, NodeId.regular(126L))
        node1.addChild(relation, node2)
        node1.addChild(relation, node3)

        assertEquals(0, node1.numberOfChildren("anotherRelation"))
    }

    @Test
    fun numberOfChildrenPositiveCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L,
                "MyConcept", "123x")
        val relation = PhysicalRelation(concept, 54534534L, "MyRelation", "23ddd", RelationKind.CONTAINMENT)
        model.registerRelation(relation)
        val node1 = PhysicalNode(null, concept, NodeId.regular(124L))
        val node2 = PhysicalNode(null, concept, NodeId.regular(125L))
        val node3 = PhysicalNode(null, concept, NodeId.regular(126L))
        node1.addChild(relation, node2)
        node1.addChild(relation, node3)

        assertEquals(2, node1.numberOfChildren("MyRelation"))
    }

    @Test
    fun findNodeByIDNegativeCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L,
                "MyConcept", "123x")
        val relation = PhysicalRelation(concept, 54534534L, "MyRelation", "23ddd", RelationKind.CONTAINMENT)
        model.registerRelation(relation)
        val node1 = PhysicalNode(null, concept, NodeId.regular(124L))
        val node2 = PhysicalNode(null, concept, NodeId.regular(125L))
        val node3 = PhysicalNode(null, concept, NodeId.regular(126L))
        node1.addChild(relation, node2)
        node1.addChild(relation, node3)

        assertEquals(null, node1.findNodeByID(NodeId.regular(12900L)))
    }

    @Test
    fun findNodeByIDRootPositiveCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L,
                "MyConcept", "123x")
        val relation = PhysicalRelation(concept, 54534534L, "MyRelation", "23ddd", RelationKind.CONTAINMENT)
        model.registerRelation(relation)
        val node1 = PhysicalNode(null, concept, NodeId.regular(124L))
        val node2 = PhysicalNode(null, concept, NodeId.regular(125L))
        val node3 = PhysicalNode(null, concept, NodeId.regular(126L))
        node1.addChild(relation, node2)
        node1.addChild(relation, node3)

        assertEquals(node1, node1.findNodeByID(NodeId.regular(124L)))
    }

    @Test
    fun findNodeByIDNonRootPositiveCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L,
                "MyConcept", "123x")
        val relation = PhysicalRelation(concept, 54534534L, "MyRelation", "23ddd", RelationKind.CONTAINMENT)
        model.registerRelation(relation)
        val node1 = PhysicalNode(null, concept, NodeId.regular(124L))
        val node2 = PhysicalNode(null, concept, NodeId.regular(125L))
        val node3 = PhysicalNode(null, concept, NodeId.regular(126L))
        node1.addChild(relation, node2)
        node1.addChild(relation, node3)

        assertEquals(node3, node1.findNodeByID(NodeId.regular(126L)))
    }

    @Test
    fun referenceKeyNegativeCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L,
                "MyConcept", "123x")
        val relation = PhysicalRelation(concept, 54534534L, "MyRelation", "23ddd", RelationKind.REFERENCE)
        model.registerRelation(relation)

        val node1 = PhysicalNode(null, concept, NodeId.regular(124L))

        assertEquals(null, node1.reference(relation))
    }

    @Test
    fun referenceKeyPositiveCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L,
                "MyConcept", "123x")
        val relation = PhysicalRelation(concept, 54534534L, "MyRelation", "23ddd", RelationKind.REFERENCE)
        model.registerRelation(relation)

        val node1 = PhysicalNode(null, concept, NodeId.regular(124L))
        val node2 = PhysicalNode(null, concept, NodeId.regular(125L))
        node1.addReference(relation, PhysicalReferenceValue(InModelReferenceTarget(model, "125"), null))

        assertEquals(PhysicalReferenceValue(InModelReferenceTarget(model, "125"), null), node1.reference(relation))
    }

    @Test
    fun referenceStringNegativeCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L,
                "MyConcept", "123x")
        val relation = PhysicalRelation(concept, 54534534L, "MyRelation", "23ddd", RelationKind.REFERENCE)
        model.registerRelation(relation)

        val node1 = PhysicalNode(null, concept, NodeId.regular(124L))

        assertEquals(null, node1.reference("someRelation"))
    }

    @Test
    fun referenceStringPositiveCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L,
                "MyConcept", "123x")
        val relation = PhysicalRelation(concept, 54534534L, "MyRelation", "23ddd", RelationKind.REFERENCE)
        model.registerRelation(relation)

        val node1 = PhysicalNode(null, concept, NodeId.regular(124L))
        val node2 = PhysicalNode(null, concept, NodeId.regular(125L))
        node1.addReference(relation, PhysicalReferenceValue(InModelReferenceTarget(model, "125"), null))

        assertEquals(PhysicalReferenceValue(InModelReferenceTarget(model, "125"), null), node1.reference("MyRelation"))
    }

    @Test
    fun propertyValueKeyNegativeCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L,
                "MyConcept", "123x")
        val property = PhysicalProperty(concept, 54534534L, "MyProperty", "23ddd")
        model.registerProperty(property)

        val node1 = PhysicalNode(null, concept, NodeId.regular(124L))

        assertEquals(null, node1.propertyValue(property))
    }

    @Test
    fun propertyValueKeyPositiveCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L,
                "MyConcept", "123x")
        val property = PhysicalProperty(concept, 54534534L, "MyProperty", "23ddd")
        model.registerProperty(property)

        val node1 = PhysicalNode(null, concept, NodeId.regular(124L))
        node1[property] = "qwerty"

        assertEquals("qwerty", node1.propertyValue(property))
    }

    @Test
    fun propertyValueKeyWithDefaultNegativeCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L,
                "MyConcept", "123x")
        val property = PhysicalProperty(concept, 54534534L, "MyProperty", "23ddd")
        model.registerProperty(property)

        val node1 = PhysicalNode(null, concept, NodeId.regular(124L))

        assertEquals("my default", node1.propertyValue("MyProperty", "my default"))
    }

    @Test
    fun propertyValueKeyWithDefaultPositiveCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L,
                "MyConcept", "123x")
        val property = PhysicalProperty(concept, 54534534L, "MyProperty", "23ddd")
        model.registerProperty(property)

        val node1 = PhysicalNode(null, concept, NodeId.regular(124L))
        node1[property] = "qwerty"

        assertEquals("qwerty", node1.propertyValue("MyProperty", "my default"))
    }

    @Test
    fun propertyValueStringNegativeCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L,
                "MyConcept", "123x")
        val property = PhysicalProperty(concept, 54534534L, "MyProperty", "23ddd")
        model.registerProperty(property)

        val node1 = PhysicalNode(null, concept, NodeId.regular(124L))

        assertEquals(null, node1.propertyValue("MyProperty"))
    }

    @Test
    fun propertyValueStringPositiveCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L,
                "MyConcept", "123x")
        val property = PhysicalProperty(concept, 54534534L, "MyProperty", "23ddd")
        model.registerProperty(property)

        val node1 = PhysicalNode(null, concept, NodeId.regular(124L))
        node1[property] = "qwerty"

        assertEquals("qwerty", node1.propertyValue("MyProperty"))
    }

    @Test
    fun booleanPropertyValueStringPositiveCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L,
                "MyConcept", "123x")
        val property = PhysicalProperty(concept, 54534534L, "MyProperty", "23ddd")
        model.registerProperty(property)

        val node1 = PhysicalNode(null, concept, NodeId.regular(124L))

        node1[property] = true
        assertEquals(true, node1.booleanPropertyValue("MyProperty"))

        node1[property] = false
        assertEquals(false, node1.booleanPropertyValue("MyProperty"))
    }

    @Test
    fun booleanPropertyValueStringNegativeCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L,
                "MyConcept", "123x")
        val property = PhysicalProperty(concept, 54534534L, "MyProperty", "23ddd")
        model.registerProperty(property)

        val node1 = PhysicalNode(null, concept, NodeId.regular(124L))

        assertEquals(false, node1.booleanPropertyValue("MyProperty"))
    }

    @Test
    fun longPropertyValueStringPositiveCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L,
                "MyConcept", "123x")
        val property = PhysicalProperty(concept, 54534534L, "MyProperty", "23ddd")
        model.registerProperty(property)

        val node1 = PhysicalNode(null, concept, NodeId.regular(124L))

        node1[property] = 120L
        assertEquals(120L, node1.longPropertyValue("MyProperty"))
    }

    @Test
    fun longPropertyValueStringNegativeCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L,
                "MyConcept", "123x")
        val property = PhysicalProperty(concept, 54534534L, "MyProperty", "23ddd")
        model.registerProperty(property)

        val node1 = PhysicalNode(null, concept, NodeId.regular(124L))

        assertEquals(0L, node1.longPropertyValue("MyProperty"))
    }

    @Test
    fun stringPropertyValueStringPositiveCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L,
                "MyConcept", "123x")
        val property = PhysicalProperty(concept, 54534534L, "MyProperty", "23ddd")
        model.registerProperty(property)

        val node1 = PhysicalNode(null, concept, NodeId.regular(124L))

        node1[property] = "foo bar"
        assertEquals("foo bar", node1.stringPropertyValue("MyProperty"))
    }

    @Test
    fun stringPropertyValueStringNegativeCase() {
        val model = PhysicalModel(UUID.randomUUID(), "MyModel")
        val concept = PhysicalConcept(LanguageId(UUID.fromString("c72da2b9-7cce-4447-8389-f407dc1158b7"), "MyLanguage"), 123L,
                "MyConcept", "123x")
        val property = PhysicalProperty(concept, 54534534L, "MyProperty", "23ddd")
        model.registerProperty(property)

        val node1 = PhysicalNode(null, concept, NodeId.regular(124L))

        assertEquals("", node1.stringPropertyValue("MyProperty"))
    }
}
