package com.strumenta.mpsinterop.loading

import com.strumenta.mpsinterop.loading.loading.physicalmodel.*
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.ByteArrayInputStream
import java.io.InputStream

fun elementToModelNode(physicalModel: PhysicalModel, parent: PhysicalNode?, element: Element) : PhysicalNode {
    val conceptIndex = element.getAttribute("concept")
    val id = element.getAttribute("id")
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
        // TODO modelNode.addReference
    }
    return modelNode
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
                    it.getAttribute("name"), it.getAttribute("index"))
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
