package com.strumenta.mpsinterop.loading

import com.strumenta.mpsinterop.Indexer
import com.strumenta.mpsinterop.physicalmodel.PhysicalLanguageModule
import com.strumenta.mpsinterop.physicalmodel.PhysicalSolutionModule
import com.strumenta.mpsinterop.processModelsInModule
import com.strumenta.mpsinterop.utils.loadDocument
import com.strumenta.mpsinterop.utils.processAllNodes
import com.strumenta.mpsinterop.utils.processChildren
import org.w3c.dom.Document
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.*
import java.util.function.Consumer

// /////////////////////////////////
// Languages
// /////////////////////////////////

fun loadLanguage(elementLoader: Indexer.ElementLoader, document: Document): PhysicalLanguageModule {
    val uuid = UUID.fromString(document.documentElement.getAttribute("uuid"))
    val name = document.documentElement.getAttribute("namespace")
    val language = PhysicalLanguageModule(uuid, name)
    processModelsInModule(elementLoader, document, Consumer<Document> {
        TODO()
    })
    return language
}

fun loadLanguage(elementLoader: Indexer.ElementLoader, data: InputStream) = loadLanguage(elementLoader, loadDocument(data))

fun loadLanguage(elementLoader: Indexer.ElementLoader, data: ByteArray) = loadLanguage(elementLoader, ByteArrayInputStream(data))

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
