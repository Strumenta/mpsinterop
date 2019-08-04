package com.strumenta.mpsinterop.loading

import com.strumenta.mpsinterop.physicalmodel.PhysicalLanguageModule
import com.strumenta.mpsinterop.physicalmodel.PhysicalSolutionModule
import com.strumenta.mpsinterop.utils.loadDocument
import org.w3c.dom.Document
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.*

// /////////////////////////////////
// Languages
// /////////////////////////////////

fun loadLanguage(document: Document): PhysicalLanguageModule {
    val uuid = UUID.fromString(document.documentElement.getAttribute("uuid"))
    val name = document.documentElement.getAttribute("namespace")
    val language = PhysicalLanguageModule(uuid, name)
    return language
}

fun loadLanguage(data: InputStream) = loadLanguage(loadDocument(data))

fun loadLanguage(data: ByteArray) = loadLanguage(ByteArrayInputStream(data))

// /////////////////////////////////
// Solutions
// /////////////////////////////////

fun loadSolution(document: Document): PhysicalSolutionModule {
    val uuid = UUID.fromString(document.documentElement.getAttribute("uuid"))
    val name = document.documentElement.getAttribute("name")
    val module = PhysicalSolutionModule(uuid, name)
    return module
}

fun loadSolution(data: InputStream) = loadSolution(loadDocument(data))

fun loadSolution(data: ByteArray) = loadSolution(ByteArrayInputStream(data))
