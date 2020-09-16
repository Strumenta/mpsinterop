package com.strumenta.mpsinterop.loading

import com.strumenta.mpsinterop.Indexer
import com.strumenta.mpsinterop.physicalmodel.PhysicalLanguageModule
import com.strumenta.mpsinterop.physicalmodel.PhysicalSolutionModule
import com.strumenta.mpsinterop.utils.loadDocument
import com.strumenta.mpsinterop.utils.processChildren
import org.w3c.dom.Document
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.lang.RuntimeException
import java.util.*
import java.util.function.Consumer

// /////////////////////////////////
// Languages
// /////////////////////////////////

interface Source {
    fun document(): Document
    fun listChildrenUnder(combinedLocation: String): List<Source>
}

fun processModelsInModule(source: Source, consumer: Consumer<Document>) {
    val document = source.document()
    document.documentElement.processChildren("models", { models ->
        models.processChildren("modelRoot", { modelRoot ->
            val contentPath = modelRoot.getAttribute("contentPath")
            val type = modelRoot.getAttribute("type")
            if (type == "default") {
                modelRoot.processChildren("sourceRoot", { sourceRoot ->
                    val location = sourceRoot.getAttribute("location")
                    try {
                        var combinedLocation = "$contentPath/$location"
                        require(combinedLocation.startsWith("\${module}/"))
                        combinedLocation = combinedLocation.removePrefix("\${module}/")
                        val models = source.listChildrenUnder(combinedLocation)
                        for (model in models) {
                            consumer.accept(model.document())
                        }
                    } catch (e: Throwable) {
                        throw RuntimeException("Issue processing location '$location'", e)
                    }
                })
            } else if (type == "java_classes") {
                // ignore
            } else {
                TODO(type)
            }
        })
    })
}

fun loadLanguage(source: Source): PhysicalLanguageModule {
    val document = source.document()
    val uuid = UUID.fromString(document.documentElement.getAttribute("uuid"))
    val name = document.documentElement.getAttribute("namespace")
    val language = PhysicalLanguageModule(uuid, name)
    processModelsInModule(source, Consumer<Document> {
        TODO()
    })
    return language
}

fun loadLanguage(elementLoader: Indexer.ElementLoader, data: InputStream) : PhysicalLanguageModule = TODO()//loadLanguage(elementLoader, loadDocument(data))

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
