package com.strumenta.mps

import com.google.gson.annotations.Expose
import com.strumenta.mps.binary.*
import com.strumenta.mps.binary.BinaryPersistence
import com.strumenta.mps.binary.LanguageLoaderHelper
import com.strumenta.mps.binary.NodesReader
import com.strumenta.mps.organization.*
import com.strumenta.mps.physicalmodel.ExplicitReferenceTarget
import com.strumenta.mps.physicalmodel.PhysicalNode
import com.strumenta.mps.utils.Base64
import com.strumenta.utils.child
import com.strumenta.utils.children
import com.strumenta.utils.processAllNodes
import com.strumenta.utils.processChildren
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.lang.IllegalStateException
import java.util.*

data class IndexElement(
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
        try {
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
                                        modelSources.addAll(indexElement.source.listChildrenUnderRecursively(combinedLocation))
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
            return modelSources.filter { it.isFile }.map { loadModel(it) }.filterNotNull().toSet()
        } catch (t: Throwable) {
            throw java.lang.RuntimeException("Issue loading models from $indexElement", t)
        }
    }

    private fun loadModel(source: Source): Model? {
        val extension = source.extension()
        if (extension == "mpb") {
            return loadModelFromBinary(source)
        }
        if (extension == "mps") {
            return loadModelFromXml(source)
        }
        if (extension == "model") {
            return null
        }
        if (extension == "mpsr") {
            return null
        }
        TODO("Extension $extension for source $source")
    }

    private fun loadModelFromBinary(source: Source): Model {
        val mis = ModelInputStream(source.inputStream())
        val modelHeader = loadHeader(mis)
        val name = modelHeader.modelRef!!.name
        val uuid = modelHeader.modelRef!!.id.uuid()!!
        return ModelImpl(source, name, uuid)
    }

    private fun loadModelFromXml(source: Source): Model {
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

private class ModelImpl(
    @Expose(serialize = false)
    override val source: Source,
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
                    if (restRef.startsWith("r:")) {
                        require(restRef.startsWith("r:")) { "Ref value '$ref'" }
                        require(restRef[38] == '(') { "Ref value '$ref'" }
                        require(restRef.endsWith(")")) { "Ref value '$ref'" }
                        val moduleUUID = UUID.fromString(ref.substring(0, 36))
                        val uuid = UUID.fromString(restRef.substring(2, 38))
                        val combinedName = ref.substring(39, restRef.length - 1)
                        val nameParts = combinedName.split("/")
                        require(nameParts.size == 2) { "Ref value '$ref'" }
                        registry.registerModelImport(index, uuid, name, moduleUUID)
                    } else if (restRef.startsWith("java:")) {
                        require(restRef.startsWith("java:")) { "Ref value '$ref'" }
                        require(restRef.endsWith(")")) { "Ref value '$ref'" }
                        val moduleUUID = UUID.fromString(ref.substring(0, 36))
                        val refDetails = ref.substring(37)

                        // TODO process differently these external references
                        registry.registerModelImport(index, moduleUUID, refDetails)
                    } else {
                        throw IllegalStateException("Ref value '$ref'")
                    }
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
        } catch (t: Throwable) {
            throw java.lang.RuntimeException("Issue loading registry for model coming from $source", t)
        }
    }

    private fun loadRoots(): List<Node> {
        if (source.extension() == "mpb") {
            val mis = ModelInputStream(source.inputStream())
            val modelHeader = loadHeader(mis)
            val bp = BinaryPersistence()
            val languageLoaderHelper = LanguageLoaderHelper()
            val rh = bp.loadModelProperties(mis, languageLoaderHelper)

            val reader = NodesReader(modelHeader.getModelReference()!!, mis, rh)
            try {
                val nodes = reader.readNodes()

                return nodes.map { PhysicalNodeWrapper(it) }
            } catch (t: Throwable) {
                throw java.lang.RuntimeException("Issue reading nodes for model $name loaded from $source", t)
            }
        }
        if (source.extension() == "mps") {
            val doc = source.document
            val registry = loadRegistry(doc)
            return doc.documentElement.children("node").map { node ->
                NodeImpl.loadNode(node, registry)
            }
        }
        TODO(source.extension())
    }
}

private class PhysicalNodeWrapper(
    @Expose(serialize = false)
    val physicalNode: PhysicalNode
) : Node() {
    override val name: String?
        get() = property("name")
    override val conceptName: String by lazy { physicalNode.concept.qualifiedName }
    override val id: NodeID by lazy { physicalNode.id.toString() }
    override val properties: Map<String, String> by lazy { convertProperties() }
    override val containmentLinkName: String? by lazy { calculateContainmentLinkName() }

    private fun calculateContainmentLinkName(): String? {
        val parent = this.physicalNode.parent
        if (parent == null) {
            return null
        } else {
            return parent.containingLinkFor(this.physicalNode)!!
        }
    }

    override val children: List<Node> by lazy { physicalNode.allChildren().map { PhysicalNodeWrapper(it) } }
    override val references: List<Reference> by lazy { physicalNode.allReferenceNames().map { toReference(it) } }

    private fun toReference(refName: String): Reference {
        try {
            val value = physicalNode.reference(refName)
            when (value!!.target) {
                is ExplicitReferenceTarget -> return ExternalReferenceImpl(refName, (value.target as ExplicitReferenceTarget).model, (value.target as ExplicitReferenceTarget).nodeId.toStringRepresentation())
            }
            TODO("Value is $value")
        } catch (t: Throwable) {
            throw java.lang.RuntimeException("Issue loading reference $refName for node ${physicalNode.id} having concept ${physicalNode.concept.qualifiedName} and properties ${physicalNode.propertiesMap()}", t)
        }
    }

    private fun convertProperties(): Map<String, String> = physicalNode.propertyNames().map { it to physicalNode.propertyValue(it) }.filter { it.second != null }.map { it as Pair<String, String> }.toMap()
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

private class NodeImpl(
    override val conceptName: String,
    override val id: NodeID,
    override val containmentLinkName: String?,
    @Expose(serialize = false)
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
                val ref = ExternalReferenceImpl(relationName, modelUUID, parts[1], moduleUUID)
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
    val modelUUID: UUID,
    @Expose(serialize = false)
    val localIndex: String,
    val module: UUID? = null
) : Reference() {
    val nodeID: Long by lazy { Base64.parseLong(localIndex) }

    // TODO use registry to translate those indexes
    override fun refString(): String = "ext:$modelUUID:$nodeID"

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

fun loadSolution(source: Source): Solution {
    val document = source.document
    val uuidStr = document.documentElement.getAttribute("uuid")
    val uuid: UUID
    try {
        uuid = UUID.fromString(uuidStr)
    } catch (t: Throwable) {
        throw java.lang.RuntimeException("Failed to load UUID from $uuidStr, loading solution from $source", t)
    }
    val name = document.documentElement.getAttribute("name")
    val moduleVersion = document.documentElement.getAttribute("moduleVersion").toInt()
    require(name.isNotBlank()) { "name is blank. Source: $source" }
    return SolutionImpl(IndexElement(uuid, name, ElementType.SOLUTION, source, moduleVersion))
}

fun loadLanguage(source: Source): Language {
    try {
        val document = source.document
        val uuid = UUID.fromString(document.documentElement.getAttribute("uuid"))
        val name = document.documentElement.getAttribute("namespace")
        val moduleVersion = document.documentElement.getAttribute("moduleVersion").toInt()
        val languageVersion = document.documentElement.getAttribute("languageVersion").toInt()
        require(name.isNotBlank())
        return LanguageImpl(IndexElement(uuid, name, ElementType.LANGUAGE, source, moduleVersion, languageVersion))
    } catch (t: Throwable) {
        throw java.lang.RuntimeException("Issue loading language from $source", t)
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

    fun modelUUIDFromIndex(index: String): UUID {
        return indexToModelUUID[index] ?: throw IllegalArgumentException("No model with index $index found")
    }

    fun moduleUUIDFromIndex(index: String): UUID? {
        return indexToModuleUUID[index]
    }

    fun modelNameFromIndex(index: String): String {
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
            indexToModuleUUID[index] = moduleUUID
        }
    }
}

abstract class ModulesContainer {
    abstract val modules: List<Module>

    fun findModel(modelName: String): Model? {
        for (m in modules) {
            val model = m.findModel(modelName)
            if (model != null) {
                return model
            }
        }
        return null
    }

    fun findModel(modelUUID: UUID): Model? {
        for (m in modules) {
            val model = m.findModel(modelUUID)
            if (model != null) {
                return model
            }
        }
        return null
    }

    fun findModule(moduleName: String): Module? = modules.find { it.name == moduleName }

    fun listModels(): Set<Model> {
        return modules.fold(emptySet(), { acc, module -> acc + module.models() })
    }
}
