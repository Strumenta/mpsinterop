package com.strumenta.mpsinterop.loading

import com.strumenta.mpsinterop.physicalmodel.*
import com.strumenta.mpsinterop.utils.processAllNodes
import com.strumenta.mpsinterop.utils.processChildren
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.lang.IllegalArgumentException
import java.lang.RuntimeException

fun elementToModelNode(physicalModel: PhysicalModel, parent: PhysicalNode?, element: Element) : PhysicalNode {
    val conceptIndex = element.getAttribute("concept")
    val id = element.getAttribute("id")
    try {
        val modelNode = PhysicalNode(parent, physicalModel.conceptByIndex(conceptIndex), id)
        element.processChildren("property") {
            val value = it.getAttribute("value")
            val property = physicalModel.propertyByIndex(it.getAttribute("role"))
            modelNode.addProperty(property, value)
        }
        element.processChildren("node") {
            val childModelNode = elementToModelNode(physicalModel, modelNode, it)
            val role = physicalModel.relationByIndex(it.getAttribute("role"))
            modelNode.addChild(role, childModelNode)
        }
        element.processChildren("ref") {
            val roleIndex = it.getAttribute("role")
            val role = physicalModel.relationByIndex(roleIndex)
            val target = when {
                it.hasAttribute("to") -> {
                    val to = it.getAttribute("to")
                    val targetParts = to.split(":")
                    if (targetParts.size != 2) {
                        throw IllegalArgumentException("Illegal target: $to in reference with role index $roleIndex")
                    }
                    OutsideModelReferenceTarget(targetParts[0], targetParts[1])
                }
                it.hasAttribute("node") -> InModelReferenceTarget(element.getAttribute("node"))
                else -> throw IllegalArgumentException("A reference should have either the to or node attributes")
            }
            val refValue = PhysicalReferenceValue(target, it.getAttribute("resolve"))
            modelNode.addReference(role, refValue)
        }
        return modelNode
    } catch (e : Exception) {
        throw RuntimeException("Issue loading node with ID $id", e)
    }
}

fun loadModel(document: Document) : PhysicalModel {
    val rawName = document.documentElement.getAttribute("ref")

    // TODO make it more robust
    var nameInParens = rawName.substring(rawName.indexOf('(') + 1, rawName.indexOf(')'))
    if (nameInParens.lastIndexOf('/') != -1) {
        nameInParens = nameInParens.substring(nameInParens.lastIndexOf('/') + 1, nameInParens.length)
    }
    if (nameInParens.lastIndexOf('@') != -1) {
        nameInParens = nameInParens.substring(0, nameInParens.lastIndexOf('@'))
    }

    val physicalModel = PhysicalModel(nameInParens)
    document.documentElement.processAllNodes("concept") {
        val concept = PhysicalConcept(it.getAttribute("id"),
                it.getAttribute("name"), it.getAttribute("index"))
        physicalModel.registerConcept(concept)
        it.processChildren("property") {
            val property = PhysicalProperty(concept,
                    it.getAttribute("id"),
                    it.getAttribute("name"), it.getAttribute("index"))
            concept.addProperty(property)
            physicalModel.registerProperty(property)
        }
        it.processChildren("child") {
            val relation = PhysicalRelation(concept,
                    it.getAttribute("id"),
                    it.getAttribute("name"),
                    it.getAttribute("index"),
                    RelationKind.CONTAINMENT)
            concept.addRelation(relation)
            physicalModel.registerRelation(relation)
        }
        it.processChildren("reference") {
            val relation = PhysicalRelation(concept,
                    it.getAttribute("id"),
                    it.getAttribute("name"),
                    it.getAttribute("index"),
                    RelationKind.REFERENCE)
            concept.addRelation(relation)
            physicalModel.registerRelation(relation)
        }
    }
    document.documentElement.processChildren("node") {
        val root = elementToModelNode(physicalModel, null, it)
        physicalModel.addRoot(root)
    }
    return physicalModel
}

fun loadMpsModel(data: InputStream) : PhysicalModel {
    val builderFactory = javax.xml.parsers.DocumentBuilderFactory.newInstance()
    val builder = builderFactory.newDocumentBuilder()

    val document = builder.parse(data)
    return loadModel(document)
}

fun loadMpsModel(data: ByteArray) = loadMpsModel(ByteArrayInputStream(data))