package com.strumenta.mpsinterop.utils

import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.InputStream

fun Element.processAllNodes(op: (Element)->Unit) {
    op(this)
    (0..(this.childNodes.length))
            .asSequence()
            .map { this.childNodes.item(it) }
            .filterIsInstance<Element>()
            .forEach { it.processAllNodes(op) }
}

fun Element.processChildren(op: (Element)->Unit) {
    (0..(this.childNodes.length))
            .asSequence()
            .map { this.childNodes.item(it) }
            .filterIsInstance<Element>()
            .forEach { op(it) }
}

fun Element.processAllNodes(tagName: String, op: (Element)->Unit) {
    this.processAllNodes { if (it.tagName == tagName) op(it) }
}

fun Element.processChildren(tagName: String, op: (Element)->Unit) {
    this.processChildren { if (it.tagName == tagName) op(it) }
}

fun loadDocument(data: InputStream) : Document {
    val builderFactory = javax.xml.parsers.DocumentBuilderFactory.newInstance()
    val builder = builderFactory.newDocumentBuilder()

    val document = builder.parse(data)
    return document
}

