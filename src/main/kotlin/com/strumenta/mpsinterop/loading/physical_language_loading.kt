package com.strumenta.mpsinterop.loading

import com.strumenta.mpsinterop.physicalmodel.PhysicalLanguage
import com.strumenta.mpsinterop.utils.loadDocument
import org.w3c.dom.Document
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.*


fun loadLanguage(document: Document) : PhysicalLanguage {
    val uuid = UUID.fromString(document.documentElement.getAttribute("uuid"))
    val name = document.documentElement.getAttribute("namespace")
    val language = PhysicalLanguage(name, uuid)
    return language
}

fun loadLanguage(data: InputStream) : PhysicalLanguage {
    return loadLanguage(loadDocument(data))
}

fun loadLanguage(data: ByteArray) = loadLanguage(ByteArrayInputStream(data))
