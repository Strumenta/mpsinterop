package com.strumenta.mpsinterop.loading

import com.strumenta.mpsinterop.datamodel.*
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.ByteArrayInputStream
import java.io.InputStream

fun elementToModelNode(physicalModel: PhysicalModel, parent: Node?, element: Element) : Node {
    val conceptIndex = element.getAttribute("concept")
    val id = element.getAttribute("id")
    val modelNode = Node(parent, physicalModel.conceptByIndex(conceptIndex), id)
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
        val concept = Concept(it.getAttribute("id"),
                it.getAttribute("name"))
        physicalModel.registerConcept(concept, it.getAttribute("index"))
        it.processChildren("property") {
            val property = Property(concept,
                    it.getAttribute("id"),
                    it.getAttribute("name"))
            concept.addProperty(property)
            physicalModel.registerProperty(property, it.getAttribute("index"))
        }
        it.processChildren("child") {
            val relation = Relation(concept,
                    it.getAttribute("id"),
                    it.getAttribute("name"))
            concept.addRelation(relation)
            physicalModel.registerRelation(relation, it.getAttribute("index"))
        }
    }
    document.documentElement.processChildren("node") {
        val root = elementToModelNode(physicalModel, null, it)
        physicalModel.model.addRoot(root)
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
