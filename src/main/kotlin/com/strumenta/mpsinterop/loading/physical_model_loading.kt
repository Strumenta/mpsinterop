package com.strumenta.mpsinterop.loading

import com.strumenta.mpsinterop.logicalmodel.LanguageId
import com.strumenta.mpsinterop.logicalmodel.NodeId
import com.strumenta.mpsinterop.physicalmodel.*
import com.strumenta.mpsinterop.utils.Base64
import com.strumenta.mpsinterop.utils.loadDocument
import com.strumenta.mpsinterop.utils.processAllNodes
import com.strumenta.mpsinterop.utils.processChildren
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.lang.IllegalArgumentException
import java.lang.RuntimeException
import java.util.*

fun elementToModelNode(physicalModel: PhysicalModel, parent: PhysicalNode?, element: Element): PhysicalNode {
    val conceptIndex = element.getAttribute("concept")
    val id = element.getAttribute("id")
    try {
        val modelNode = PhysicalNode(parent, physicalModel.conceptByIndex(conceptIndex), NodeId.regular(Base64.parseLong(id)))
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
                    if (targetParts[0].isEmpty() && targetParts[1] == "^") {
                        NullReferenceTarget
                    } else {
                        OutsideModelReferenceTarget(physicalModel, targetParts[0], targetParts[1])
                    }
                }
                it.hasAttribute("node") -> InModelReferenceTarget(
                        physicalModel,
                        it.getAttribute("node"))
                else -> throw IllegalArgumentException("A reference should have either the to or node attributes")
            }
            val refValue = PhysicalReferenceValue(target, it.getAttribute("resolve"))
            modelNode.addReference(role, refValue)
        }
        return modelNode
    } catch (e: Exception) {
        throw RuntimeException("Issue loading node with ID $id", e)
    }
}

private fun rawNameToUuid(rawName: String): UUID {
    var nameInParens = rawName.substring(rawName.indexOf('(') + 1, rawName.indexOf(')'))
    if (nameInParens.lastIndexOf('/') != -1) {
        nameInParens = nameInParens.substring(nameInParens.lastIndexOf('/') + 1, nameInParens.length)
    }
    if (nameInParens.lastIndexOf('@') != -1) {
        nameInParens = nameInParens.substring(0, nameInParens.lastIndexOf('@'))
    }
    var uuidStr = rawName
    if (uuidStr.startsWith("r:")) {
        uuidStr = uuidStr.removePrefix("r:")
    }
    if (uuidStr.indexOf('(') != -1) {
        uuidStr = uuidStr.substring(0, uuidStr.indexOf('('))
    }
    if (uuidStr.indexOf('/') != -1) {
        uuidStr = uuidStr.substring(0, uuidStr.indexOf('/'))
    }

    var uuid: UUID?
    try {
        uuid = UUID.fromString(uuidStr)
    } catch (e: RuntimeException) {
        throw RuntimeException("Issue deriving UUID from $uuidStr")
    }
    return uuid
}

private fun rawNameToLanguageName(rawName: String): String {
    var nameInParens = rawName.substring(rawName.indexOf('(') + 1, rawName.indexOf(')'))
    if (nameInParens.lastIndexOf('/') != -1) {
        nameInParens = nameInParens.substring(nameInParens.lastIndexOf('/') + 1, nameInParens.length)
    }
    if (nameInParens.lastIndexOf('@') != -1) {
        nameInParens = nameInParens.substring(0, nameInParens.lastIndexOf('@'))
    }
    return nameInParens
}

fun loadModel(document: Document): PhysicalModel {
    val rawName = document.documentElement.getAttribute("ref")

    val nameInParens = rawNameToLanguageName(rawName)
    val uuid = rawNameToUuid(rawName)

    val physicalModel = PhysicalModel(uuid, nameInParens)
    // physicalModel.putLanguageInRegistry(uuid, nameInParens.removeSuffix(".structure"))
    document.documentElement.processAllNodes("import") {
        val index = it.getAttribute("index")
        val rawName = it.getAttribute("ref")
        val nameInParens = rawNameToLanguageName(rawName)
        val uuid = rawNameToUuid(rawName)
        physicalModel.putLanguageInRegistry(uuid, nameInParens.removeSuffix(".structure"))
        physicalModel.putLanguageIndexInRegistry(uuid, index)
    }
    document.documentElement.processAllNodes("concept") {
        val languageId = UUID.fromString((it.parentNode as Element).getAttribute("id"))
        val languageName = (it.parentNode as Element).getAttribute("name")
        physicalModel.putLanguageInRegistry(languageId, languageName)
        val simpleConceptName = it.getAttribute("name").split(".").last()
        val concept = PhysicalConcept(
                LanguageId(languageId, languageName),
                it.getAttribute("id").toLong(),
                simpleConceptName, it.getAttribute("index"))
        physicalModel.registerConcept(concept)
        it.processChildren("property") {
            val property = PhysicalProperty(concept,
                    it.getAttribute("id").toLong(),
                    it.getAttribute("name"), it.getAttribute("index"))
            concept.addProperty(property)
            physicalModel.registerProperty(property)
        }
        it.processChildren("child") {
            val relation = PhysicalRelation(concept,
                    it.getAttribute("id").toLong(),
                    it.getAttribute("name"),
                    it.getAttribute("index"),
                    RelationKind.CONTAINMENT)
            concept.addRelation(relation)
            physicalModel.registerRelation(relation)
        }
        it.processChildren("reference") {
            val relation = PhysicalRelation(concept,
                    it.getAttribute("id").toLong(),
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

fun loadMpsModel(data: InputStream): PhysicalModel {
    return loadModel(loadDocument(data))
}

fun loadMpsModel(data: ByteArray) = loadMpsModel(ByteArrayInputStream(data))
