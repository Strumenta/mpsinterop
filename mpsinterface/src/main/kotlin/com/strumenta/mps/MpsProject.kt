package com.strumenta.mps

import com.strumenta.mps.utils.Base64
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.File
import java.io.FileInputStream
import java.io.FilenameFilter
import java.lang.UnsupportedOperationException
import java.rmi.registry.Registry
import java.util.*
import javax.print.Doc
import kotlin.collections.HashMap

enum class ElementType {
    DEVKIT,
    LANGUAGE,
    SOLUTION
}

abstract class Module {
    abstract val name: String
    abstract val uuid: UUID
    abstract val moduleVersion: Int

    fun modelNames(): Set<String> = models().map { it.name }.toSet()
    abstract fun models() : Set<Model>
    fun findModel(uuid: UUID) : Model? = models().find { it.uuid == uuid }
    fun findModel(name: String) : Model? = models().find { it.name == name }
}

abstract class Language : Module() {
    abstract val languageVersion: Int
}

abstract class Solution : Module()

abstract class Model {
    abstract val name: String
    abstract val uuid: UUID

    abstract fun roots() : List<Root>
    fun roots(conceptName: String) : List<Root> = roots().filter { it.conceptName == conceptName }
    fun numberOfRoots(): Int = roots().size
}

typealias NodeID = String

abstract class Root {
    abstract val name: String?
    abstract val conceptName: String
    abstract val id: NodeID
    abstract val properties : Map<String, String>
}

class MpsProject(val projectDir: File) {

    private val solutions = mutableListOf<Solution>()
    private val languages = mutableListOf<Language>()

    val modules : List<Module>
        get() = solutions + languages

    init {
        require(projectDir.exists())
        require(projectDir.isDirectory)
        val mpsDirs = projectDir.listFiles(FilenameFilter { dir, name -> name == ".mps" })
        require(mpsDirs.size == 1)
        val mpsDir = mpsDirs.first()
        val modulesFile = File(mpsDir, "modules.xml")
        loadModules(modulesFile)
    }

    private fun loadModules(modulesFile: File) {
        require(modulesFile.exists())
        require(modulesFile.isFile)
        val doc = loadDocument(FileInputStream(modulesFile))
        doc.documentElement.processAllNodes("modulePath") {
            val path = File(it.getAttribute("path").replace("\$PROJECT_DIR\$", projectDir!!.absolutePath))
            require(path.exists()) { "module $path does not exist" }
            require(path.isFile())
            when (path.extension) {
                "mpl" -> loadLanguage(FileSource(path))
                "msd" -> loadSolution(FileSource(path))
                else -> throw IllegalStateException("Unable to process module file $path")
            }
        }
    }

    private fun loadSolution(source: Source) {
        val document = source.document
        val uuid = UUID.fromString(document.documentElement.getAttribute("uuid"))
        val name = document.documentElement.getAttribute("name")
        val moduleVersionStr = document.documentElement.getAttribute("moduleVersion")
        val moduleVersion = if (moduleVersionStr.isBlank()) null else moduleVersionStr.toInt()
        require(name.isNotBlank()) { "name is blank" }
        solutions.add(SolutionImpl(IndexElement(uuid, name, ElementType.SOLUTION, source, moduleVersion)))
    }

    private fun loadLanguage(source: Source) {
        val document = source.document
        val uuid = UUID.fromString(document.documentElement.getAttribute("uuid"))
        val name = document.documentElement.getAttribute("namespace")
        val moduleVersion = document.documentElement.getAttribute("moduleVersion").toInt()
        val languageVersion = document.documentElement.getAttribute("languageVersion").toInt()
        require(name.isNotBlank())
        languages.add(LanguageImpl(IndexElement(uuid, name, ElementType.LANGUAGE, source, moduleVersion, languageVersion)))
    }

    fun languageNames(): Set<String> {
        return languages.map { it.name }.toSet()
    }

    fun solutionNames(): Set<String> {
        return solutions.map { it.name }.toSet()
    }

    fun hasLanguage(languageName: String): Boolean {
        return languages.any { it.name == languageName }
    }

    fun hasSolution(solutionName: String): Boolean {
        return solutions.any { it.name == solutionName }
    }

    fun language(languageName: String) : Language? {
        return languages.find { it.name == languageName }
    }

    fun solution(solutionName: String) : Solution? {
        return solutions.find { it.name == solutionName }
    }

    fun findModel(modelName: String): Model? {
        for (m in modules) {
            val model = m.findModel(modelName)
            if (model != null) {
                return model
            }
        }
        return null
    }

    private data class IndexElement(val uuid: UUID, val name: String, val type: ElementType,
                            val source: Source,
                            val moduleVersion: Int? = null, val languageVersion: Int? = null) {
        val document: Document
            get() = source.document
    }

    private class ModuleImpl(val indexElement: IndexElement) {
        private val models : Set<Model> by lazy { loadModels() }

        fun models(): Set<Model> {
            return models
        }

        private fun loadModels(): Set<Model> {
            val document = indexElement.document
            val modelSources = mutableListOf<Source>()
            document.documentElement.child("models").processChildren("modelRoot", { modelRoot ->
                val contentPath = modelRoot.getAttribute("contentPath")
                val type = modelRoot.getAttribute("type")
                when (type) {
                    "default" -> {
                        modelRoot.processChildren("sourceRoot", { sourceRoot ->
                            val location = sourceRoot.getAttribute("location")
                            try {
                                var combinedLocation = "$contentPath/$location"
                                require(combinedLocation.startsWith("\${module}/"))
                                combinedLocation = combinedLocation.removePrefix("\${module}/")
                                modelSources.addAll(indexElement.source.listChildrenUnder(combinedLocation))
                            } catch (e: Throwable) {
                                throw RuntimeException("Issue processing location '$location'", e)
                            }
                        })
                    }
                    "java_classes" -> {
                        // ignore
                    }
                    else -> {
                        throw UnsupportedOperationException("modelRoot of type $type is not supported")
                    }
                }
            })
            return modelSources.map { loadModel(it) }.toSet()
        }

        private fun loadModel(source: Source): Model {
            val doc = source.document
            require(doc.documentElement.tagName == "model")
            val ref = doc.documentElement.getAttribute("ref")
            val refPrefix = "r:"
            val uuidLength = 36
            require(ref.startsWith(refPrefix))
            val uuid = UUID.fromString(ref.substring(refPrefix.length, refPrefix.length + uuidLength))
            require(ref.elementAt(refPrefix.length + uuidLength) == '(')
            require(ref.endsWith(")"))
            val name = ref.substring(refPrefix.length + uuidLength + 1, ref.length - 1)
            return ModelImpl(source, name, uuid)
        }
    }

    private class LanguageImpl(val indexElement: IndexElement) : Language() {
        override val name: String
            get() = indexElement.name
        override val uuid: UUID
            get() = indexElement.uuid
        override val languageVersion: Int
            get() = indexElement.languageVersion!!
        override val moduleVersion: Int
            get() = indexElement.moduleVersion!!
        private val module = ModuleImpl(indexElement)

        override fun models(): Set<Model> {
            return module.models()
        }
    }

    private class SolutionImpl(val indexElement: IndexElement) : Solution() {
        override val name: String
            get() = indexElement.name
        override val uuid: UUID
            get() = indexElement.uuid
        override val moduleVersion: Int
            get() = indexElement.moduleVersion!!
        private val module = ModuleImpl(indexElement)

        override fun models(): Set<Model> {
            return module.models()
        }
    }

    private class Registry {
        private val indexToConceptName = HashMap<String, String>()
        private val indexToPropertyName = HashMap<String, String>()

        fun conceptNameFromIndex(index: String): String {
            return indexToConceptName[index]!!
        }

        fun propertyNameFromIndex(index: String): String {
            return indexToPropertyName[index]!!
        }

        fun registerConcept(index: String, name: String, id: String) {
            indexToConceptName[index] = name
        }

        fun registerProperty(index: String, name: String, id: String) {
            indexToPropertyName[index] = name
        }

    }

    private class ModelImpl(val source: Source, override val name: String, override val uuid: UUID) : Model() {
        private val roots : List<Root> by lazy { loadRoots() }

        override fun roots(): List<Root> {
            return roots
        }


        private fun loadRegistry(doc: Document) : Registry {
            val registry = Registry()
            doc.documentElement.child("registry").processAllNodes("concept") { concept ->
                registry.registerConcept(concept.getAttribute("index"), concept.getAttribute("name"), concept.getAttribute("id"))
                concept.processAllNodes("property") { property ->
                    registry.registerProperty(property.getAttribute("index"), property.getAttribute("name"), property.getAttribute("id"))
                }
            }
            return registry
        }

        private fun loadRoots(): List<Root> {
            val doc = source.document
            val registry = loadRegistry(doc)
            return doc.documentElement.children("node").map { node ->
                val conceptName = registry.conceptNameFromIndex(node.getAttribute("concept"))
                val id = Base64.parseLong(node.getAttribute("id")).toString()
                var name : String? = null
                node.processChildren("property") { property ->
                    val role = property.getAttribute("role")
                    val propertyName = registry.propertyNameFromIndex(role)
                    if (propertyName == "name") {
                        name = property.getAttribute("value")
                    }
                }
                RootImpl(conceptName, id, name, node, registry)
            }
        }
    }

    private class RootImpl(override val conceptName: String, override val id: NodeID, override val name: String?,
        val xmlNode: Element, val registry: Registry) : Root() {

        override val properties : Map<String, String> by lazy { loadProperties() }

        private fun loadProperties() : Map<String, String> {
            val res = HashMap<String, String>()
            xmlNode.processChildren("property") { property ->
                val role = property.getAttribute("role")
                val propertyName = registry.propertyNameFromIndex(role)
                val value = property.getAttribute("value")
                res[propertyName] = value
            }
            require(res["name"] == name)
            return res
        }
    }
}
