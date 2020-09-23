package com.strumenta.mps

import com.google.gson.annotations.Expose
import com.strumenta.mps.utils.Base64
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.File
import java.io.FileInputStream
import java.io.FilenameFilter
import java.util.LinkedList
import java.util.UUID

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
    abstract fun models(): Set<Model>
    fun findModel(uuid: UUID): Model? = models().find { it.uuid == uuid }
    fun findModel(name: String): Model? = models().find { it.name == name }
}

abstract class Language : Module() {
    abstract val languageVersion: Int
}

abstract class Solution : Module()

abstract class Model : Serializable {
    abstract val name: String
    abstract val uuid: UUID

    abstract fun roots(): List<Node>
    fun roots(conceptName: String): List<Node> = roots().filter { it.conceptName == conceptName }
    fun numberOfRoots(): Int = roots().size
}

typealias NodeID = String

abstract class Reference {
    abstract fun refString(): String

    abstract val linkName: String
    abstract val value: Node?
    abstract val isLocalToModel: Boolean
}

interface Serializable

abstract class Node : Serializable {
    fun child(relationName: String): Node? {
        return children.find { it.containmentLinkName == relationName }
    }

    fun children(relationName: String): List<Node> {
        return children.filter { it.containmentLinkName == relationName }
    }

    fun reference(relationName: String): Node? {
        return references.find { it.linkName == relationName }?.value
    }

    fun isReferenceLocal(relationName: String): Boolean? {
        return references.find { it.linkName == relationName }?.isLocalToModel
    }

    fun property(propertyName: String): String? = properties[propertyName]

    abstract val name: String?
    abstract val conceptName: String
    abstract val id: NodeID
    abstract val properties: Map<String, String>
    abstract val containmentLinkName: String?
    abstract val children: List<Node>
    abstract val references: List<Reference>
}

class MpsProject(val projectDir: File) {

    private val solutions = mutableListOf<Solution>()
    private val languages = mutableListOf<Language>()

    val modules: List<Module>
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
                "devkit" -> null
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

    fun language(languageName: String): Language? {
        return languages.find { it.name == languageName }
    }

    fun solution(solutionName: String): Solution? {
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

    private data class IndexElement(
        val uuid: UUID,
        val name: String,
        val type: ElementType,
        val source: Source,
        val moduleVersion: Int? = null,
        val languageVersion: Int? = null
    ) {
        val document: Document
            get() = source.document
    }

    private class ModuleImpl(val indexElement: IndexElement) {
        private val models: Set<Model> by lazy { loadModels() }

        fun models(): Set<Model> {
            return models
        }

        private fun loadModels(): Set<Model> {
            val document = indexElement.document
            val modelSources = mutableListOf<Source>()
            document.documentElement.child("models").processChildren(
                "modelRoot",
                { modelRoot ->
                    val contentPath = modelRoot.getAttribute("contentPath")
                    val type = modelRoot.getAttribute("type")
                    when (type) {
                        "default" -> {
                            modelRoot.processChildren(
                                "sourceRoot",
                                { sourceRoot ->
                                    val location = sourceRoot.getAttribute("location")
                                    try {
                                        var combinedLocation = "$contentPath/$location"
                                        require(combinedLocation.startsWith("\${module}/"))
                                        combinedLocation = combinedLocation.removePrefix("\${module}/")
                                        modelSources.addAll(indexElement.source.listChildrenUnder(combinedLocation))
                                    } catch (e: Throwable) {
                                        throw RuntimeException("Issue processing location '$location'", e)
                                    }
                                }
                            )
                        }
                        "java_classes" -> {
                            // ignore
                        }
                        else -> {
                            throw UnsupportedOperationException("modelRoot of type $type is not supported")
                        }
                    }
                }
            )
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
        private val indexToContainmentName = HashMap<String, String>()
        private val indexToReferenceName = HashMap<String, String>()
        private val indexToNode = HashMap<String, Node>()
        private val indexToXmlNode = HashMap<String, Element>()
        private val indexToModelUUID = HashMap<String, UUID>()
        private val indexToModuleUUID = HashMap<String, UUID>()
        private val indexToModelName = HashMap<String, String>()

        fun nodeFromIndex(index: String): Node {
            require(index.isNotBlank()) { "a blank index should not be used" }
            if (index !in indexToNode) {
                NodeImpl.loadNode(indexToXmlNode[index] ?: throw IllegalArgumentException("Index unknown '$index'"), this)
            }
            return indexToNode[index]!!
        }

        fun conceptNameFromIndex(index: String): String {
            return indexToConceptName[index]!!
        }

        fun propertyNameFromIndex(index: String): String {
            return indexToPropertyName[index]!!
        }

        fun containmentNameFromIndex(index: String): String {
            return indexToContainmentName[index] ?: throw IllegalArgumentException("No containment with index $index found")
        }

        fun referenceNameFromIndex(index: String): String {
            return indexToReferenceName[index] ?: throw IllegalArgumentException("No reference with index $index found")
        }

        fun modelUUIDFromIndex(index: String) : UUID {
            return indexToModelUUID[index] ?: throw IllegalArgumentException("No model with index $index found")
        }

        fun moduleUUIDFromIndex(index: String) : UUID? {
            return indexToModuleUUID[index]
        }

        fun modelNameFromIndex(index: String) : String {
            return indexToModelName[index] ?: throw IllegalArgumentException("No model with index $index found")
        }

        fun registerConcept(index: String, name: String, id: String) {
            indexToConceptName[index] = name
        }

        fun registerProperty(index: String, name: String, id: String) {
            indexToPropertyName[index] = name
        }

        fun registerContainment(index: String, name: String, id: String) {
            indexToContainmentName[index] = name
        }

        fun registerReference(index: String, name: String, id: String) {
            indexToReferenceName[index] = name
        }

        fun registerXmlNode(index: String, xmlNode: Element) {
            indexToXmlNode[index] = xmlNode
        }

        fun registerNode(index: String, node: Node) {
            indexToNode[index] = node
        }

        fun registerModelImport(index: String, modelUUID: UUID, name: String, moduleUUID: UUID? = null) {
            indexToModelUUID[index] = modelUUID
            indexToModelName[index] = name
            if (moduleUUID != null) {
                indexToModuleUUID[index] = moduleUUID;
            }
        }
    }

    private class ModelImpl(
        @Expose(serialize = false)
        val source: Source,
        override val name: String,
        override val uuid: UUID
    ) : Model() {
        private val roots: List<Node> by lazy { loadRoots() }

        override fun roots(): List<Node> {
            return roots
        }

        private fun loadRegistry(doc: Document): Registry {
            try {
                val registry = Registry()
                doc.documentElement.processAllNodes("node") { node ->
                    registry.registerXmlNode(node.getAttribute("id"), node)
                }
                doc.documentElement.processAllNodes("import") { import ->
                    val index = import.getAttribute("index")
                    val ref = import.getAttribute("ref")
                    if (ref.startsWith("r:")) {
                        // <import index="tpck" ref="r:00000000-0000-4000-0000-011c89590288(jetbrains.mps.lang.core.structure)" implicit="true" />
                        require(ref.startsWith("r:")) { "Ref value '$ref'" }
                        require(ref[38] == '(') { "Ref value '$ref'" }
                        require(ref.endsWith(")")) { "Ref value '$ref'" }
                        val uuid = UUID.fromString(ref.substring(2, 38))
                        val name = ref.substring(39, ref.length - 1)
                        registry.registerModelImport(index, uuid, name)
                    } else {
                        // <import index="kwxp" ref="b4d28e19-7d2d-47e9-943e-3a41f97a0e52/r:4903509f-5416-46ff-9a8b-44b5a178b568(com.mbeddr.mpsutil.plantuml.node/com.mbeddr.mpsutil.plantuml.node.structure)" />
                        require(ref[36] == '/') { "Ref value '$ref'" }
                        val restRef = ref.substring(37)
                        require(restRef.startsWith("r:")) { "Ref value '$ref'" }
                        require(restRef[38] == '(') { "Ref value '$ref'" }
                        require(restRef.endsWith(")")) { "Ref value '$ref'" }
                        val moduleUUID = UUID.fromString(ref.substring(0, 36))
                        val uuid = UUID.fromString(restRef.substring(2, 38))
                        val combinedName = ref.substring(39, restRef.length - 1)
                        val nameParts = combinedName.split("/")
                        require(nameParts.size == 2) { "Ref value '$ref'" }
                        registry.registerModelImport(index, uuid, name, moduleUUID)
                    }
                }
                doc.documentElement.child("registry").processAllNodes("concept") { concept ->
                    registry.registerConcept(concept.getAttribute("index"), concept.getAttribute("name"), concept.getAttribute("id"))
                    concept.processAllNodes("property") { property ->
                        registry.registerProperty(property.getAttribute("index"), property.getAttribute("name"), property.getAttribute("id"))
                    }
                    concept.processAllNodes("child") { child ->
                        registry.registerContainment(child.getAttribute("index"), child.getAttribute("name"), child.getAttribute("id"))
                    }
                    concept.processAllNodes("reference") { reference ->
                        registry.registerReference(reference.getAttribute("index"), reference.getAttribute("name"), reference.getAttribute("id"))
                    }
                }
                return registry
            } catch (t : Throwable) {
                throw java.lang.RuntimeException("Issue loading registry for model coming from $source", t)
            }
        }

        private fun loadRoots(): List<Node> {
            val doc = source.document
            val registry = loadRegistry(doc)
            return doc.documentElement.children("node").map { node ->
                NodeImpl.loadNode(node, registry)
            }
        }
    }

    private class NodeImpl(
        override val conceptName: String,
        override val id: NodeID,
        override val containmentLinkName: String?,
        override val name: String?,
        @Expose(serialize = false)
        val xmlNode: Element,
        @Expose(serialize = false)
        val registry: Registry
    ) : Node() {

        companion object {
            fun loadNode(node: Element, registry: Registry): Node {
                val role = node.getAttribute("role")
                val containmentLinkName: String? = if (role.isNullOrBlank()) null else registry.containmentNameFromIndex(role)
                val conceptName = registry.conceptNameFromIndex(node.getAttribute("concept"))
                val index = node.getAttribute("id")
                val id = Base64.parseLong(index).toString()
                var name: String? = null
                node.processChildren("property") { property ->
                    val role = property.getAttribute("role")
                    val propertyName = registry.propertyNameFromIndex(role)
                    if (propertyName == "name") {
                        name = property.getAttribute("value")
                    }
                }
                val res = NodeImpl(conceptName, id, containmentLinkName, name, node, registry)
                registry.registerNode(index, res)
                return res
            }
        }

        override val properties: Map<String, String> by lazy { loadProperties() }
        override val children: List<Node> by lazy { loadChildren() }
        override val references: List<Reference> by lazy { loadReferences() }

        private fun loadProperties(): Map<String, String> {
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

        private fun loadChildren(): List<Node> {
            val res = LinkedList<Node>()
            xmlNode.processChildren("node") { node ->
                res.add(loadNode(node, registry))
            }
            return res
        }

        private fun loadReferences(): List<Reference> {
            val res = LinkedList<Reference>()
            xmlNode.processChildren("ref") { ref ->
                val role = ref.getAttribute("role")
                val relationName = registry.referenceNameFromIndex(role)
                val index = ref.getAttribute("node")
                if (index.isNullOrBlank()) {
                    val to = ref.getAttribute("to")
                    val parts = to.split(":")
                    require(parts.size == 2)
                    val modelIndex = parts[0]
                    val modelUUID = registry.modelUUIDFromIndex(modelIndex)
                    val moduleUUID = registry.modelUUIDFromIndex(modelIndex)
                    val ref = ExternalReferenceImpl(relationName, registry, modelUUID, parts[1], moduleUUID)
                    res.add(ref)
                } else {
                    val ref = LocalReferenceImpl(relationName, registry, index)
                    res.add(ref)
                }
            }
            return res
        }
    }

    private class LocalReferenceImpl(
        override val linkName: String,
        @Expose(serialize = false)
        val registry: Registry,
        val index: String
    ) : Reference() {
        override fun refString(): String = "int:${value?.id}"

        override val value: Node? by lazy { loadValue() }
        override val isLocalToModel: Boolean
            get() = true

        init {
            require(index.isNotBlank()) { "A valid reference index should not be blank" }
        }

        private fun loadValue(): Node? {
            return registry.nodeFromIndex(index)
        }
    }



    private class ExternalReferenceImpl(
        override val linkName: String,
        @Expose(serialize = false)
        val registry: Registry,
        val modelUUID: UUID,
        val localIndex: String,
        val module: UUID?
    ) : Reference() {
        // TODO use registry to translate those indexes
        override fun refString(): String = "ext:$modelUUID:$localIndex"

        override val value: Node? by lazy { loadValue() }
        override val isLocalToModel: Boolean
            get() = false

        init {
            require(localIndex.isNotBlank())
        }

        private fun loadValue(): Node? {
            TODO()
        }
    }
}
