package com.strumenta.mps

import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.InputStream

private val builderFactory = javax.xml.parsers.DocumentBuilderFactory.newInstance()
private val builder = builderFactory.newDocumentBuilder()

fun loadDocument(data: InputStream): Document {
    return builder.parse(data)
}

fun Element.processAllNodes(op: (Element) -> Unit) {
    op(this)
    (0..(this.childNodes.length))
            .asSequence()
            .map { this.childNodes.item(it) }
            .filterIsInstance<Element>()
            .forEach { it.processAllNodes(op) }
}

fun Element.processChildren(op: (Element) -> Unit) {
    (0..(this.childNodes.length))
            .asSequence()
            .map { this.childNodes.item(it) }
            .filterIsInstance<Element>()
            .forEach { op(it) }
}

fun Element.processAllNodes(tagName: String, op: (Element) -> Unit) {
    this.processAllNodes { if (it.tagName == tagName) op(it) }
}

fun Element.processChildren(tagName: String, op: (Element) -> Unit) {
    this.processChildren { if (it.tagName == tagName) op(it) }
}