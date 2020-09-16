package com.strumenta.mpsinterop

import com.strumenta.mpsinterop.utils.loadDocument
import com.strumenta.mpsinterop.utils.processAllNodes
import com.strumenta.mpsinterop.utils.processChildren
import org.w3c.dom.Document
import java.io.File
import java.io.FileInputStream
import java.io.FilenameFilter
import java.io.InputStream
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.lang.RuntimeException
import java.util.*
import java.util.function.Consumer
import java.util.jar.JarEntry
import java.util.jar.JarFile

enum class ElementType {
    DEVKIT,
    LANGUAGE,
    SOLUTION
}

fun processModelsInModule(elementLoader: Indexer.ElementLoader, document: Document, consumer: Consumer<Document>) {
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
                        val models = elementLoader.listModelsUnder(combinedLocation)
                        for (model in models) {
                            val doc = loadDocument(model)
                            consumer.accept(doc)
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

/**
 * The only goal of this class it to build an index, without having memory occupied to explode
 */
class Indexer {

    private val elementsByUUID = mutableMapOf<UUID, IndexElement>()
    private val elementsByName = mutableMapOf<String, IndexElement>()

    interface ElementLoader {
        fun inputStream() : InputStream
        fun listModelsUnder(location: String) : List<InputStream>
    }

    class JarEntryElementLoader(val jarFile: JarFile, val entry: JarEntry) : ElementLoader {
        override fun inputStream(): InputStream {
            return jarFile.getInputStream(entry)
        }

        override fun listModelsUnder(location: String): List<InputStream> {
            val entries = entry.parent().child(location).children(jarFile)
            return entries.map { jarFile.getInputStream(it) }
        }

        override fun toString(): String {
            return "JarEntry(file=$jarFile,entry=${entry.name})"
        }
    }

    class FileElementLoader(val file: File) : ElementLoader {
        override fun inputStream(): InputStream = FileInputStream(file)

        override fun listModelsUnder(location: String): List<InputStream> {
            val locationDir = File(file.parent, location)
            require(locationDir.exists()) { "${locationDir.absolutePath} does not exit" }
            require(locationDir.isDirectory)
            return locationDir.listFiles { dir, name -> name!!.endsWith(".mps") }.map { FileInputStream(it) }
        }

    }

    data class IndexElement(val uuid: UUID, val name: String, val type: ElementType,
                            val elementLoader: ElementLoader,
                            val moduleVersion: Int? = null, val languageVersion: Int? = null) {
        fun inputStream(): InputStream = elementLoader.inputStream()
        fun findModel(modelName: String): Document {
            val inputStream = this.inputStream()
            val document = loadDocument(inputStream)
            var res : Document? = null
            processModelsInModule(elementLoader, document, Consumer<Document> { doc ->
                var ref = doc.documentElement.getAttribute("ref")
                require(ref.startsWith("r:"))
                ref = ref.removePrefix("r:")
                val uuid = UUID.fromString(ref.substring(0, 36))
                ref = ref.substring(36)
                require(ref.startsWith("("))
                require(ref.endsWith(")"))
                val name = ref.substring(1, ref.length - 1)
                if (modelName == name) {
                    res = doc
                }
            })
            return res ?: throw RuntimeException("Model not found")
        }

        fun loader(): Indexer.ElementLoader {
            return elementLoader
        }
    }

    fun findElement(elementName: String) : IndexElement? = elementsByName[elementName]
    fun findElement(elementUUID: UUID) : IndexElement? = elementsByUUID[elementUUID]

    fun findSolutionByName(solutionName: String) : IndexElement {
        val indexElement = elementsByName[solutionName] ?: throw IllegalArgumentException("Solution $solutionName not found")
        require(indexElement.type == ElementType.SOLUTION)
        return indexElement
    }

    private fun addIndexElement(indexElement: IndexElement) {
        //println(" * add $indexElement")
        require(!elementsByUUID.containsKey(indexElement.uuid))
        require(!elementsByName.containsKey(indexElement.name))
        elementsByUUID[indexElement.uuid] = indexElement
        elementsByName[indexElement.name] = indexElement
    }

    private fun loadDevKit(elementLoader: ElementLoader) {
        val inputStream = elementLoader.inputStream()
        val document = loadDocument(inputStream)
        val uuid = UUID.fromString(document.documentElement.getAttribute("uuid"))
        val name = document.documentElement.getAttribute("name")
        require(name.isNotBlank())
        addIndexElement(IndexElement(uuid, name, ElementType.DEVKIT, elementLoader))
    }

    fun jarEntryLoader(jarFile: JarFile, entry: JarEntry) : JarEntryElementLoader = JarEntryElementLoader(jarFile, entry)

    private fun loadSolution(elementLoader: ElementLoader) {
        val inputStream = elementLoader.inputStream()
        val document = loadDocument(inputStream)
        val uuid = UUID.fromString(document.documentElement.getAttribute("uuid"))
        val name = document.documentElement.getAttribute("name")
        val moduleVersionStr = document.documentElement.getAttribute("moduleVersion")
        val moduleVersion = if (moduleVersionStr.isBlank()) null else moduleVersionStr.toInt()
        require(name.isNotBlank()) { "name is blank" }
        addIndexElement(IndexElement(uuid, name, ElementType.SOLUTION, elementLoader, moduleVersion))
    }

    private fun loadLanguage(elementLoader: ElementLoader) {
        val inputStream = elementLoader.inputStream()
        val document = loadDocument(inputStream)
        val uuid = UUID.fromString(document.documentElement.getAttribute("uuid"))
        val name = document.documentElement.getAttribute("namespace")
        val moduleVersion = document.documentElement.getAttribute("moduleVersion").toInt()
        val languageVersion = document.documentElement.getAttribute("languageVersion").toInt()
        require(name.isNotBlank())
        addIndexElement(IndexElement(uuid, name, ElementType.LANGUAGE, elementLoader, moduleVersion, languageVersion))
    }

    private fun processJarFile(it: File) {
        val jarFile = JarFile(it)
        try {
            jarFile.stream().forEach { entry ->
                when {
                    entry.name.endsWith(".devkit") -> loadDevKit(jarEntryLoader(jarFile, entry))
                    entry.name.endsWith(".msd") -> loadSolution(jarEntryLoader(jarFile, entry))
                    entry.name.endsWith(".mpl") -> loadLanguage(jarEntryLoader(jarFile, entry))
                    entry.name.endsWith("/MANIFEST.MF") -> "nothing to do"
                    entry.name.endsWith("/module.xml") -> "nothing to do"
                    entry.name.endsWith(".java") -> "nothing to do"
                    entry.name.endsWith(".mpb") -> "nothing to do"
                    entry.name.endsWith(".mps") -> "nothing to do"
                    entry.name.endsWith(".class") -> "nothing to do"
                    entry.name.endsWith(".xml") -> "nothing to do"
                    entry.name.endsWith(".png") -> "nothing to do"
                    entry.name.endsWith(".svg") -> "nothing to do"
                    entry.name.endsWith(".txt") -> "nothing to do"
                    entry.name.endsWith(".html") -> "nothing to do"
                    entry.name.endsWith(".properties") -> "nothing to do"
                    entry.name.endsWith(".kotlin_module") -> "nothing to do"
                    entry.name.endsWith("/trace.info") -> "nothing to do"
                    entry.name.endsWith("/checkpoints") -> "nothing to do"
                    entry.isDirectory -> "nothing to do"
                    //else -> println("ENTRY ${entry.name}")
                }
            }
        } catch (e: Throwable) {
            throw RuntimeException("Issue processing jar $it", e)
        }
    }

    private fun processFileInMpsInstallation(it: File) {
        if (it.isFile) {
            //println("(process ${it.absolutePath})")
            when {
                it.extension == "jar" -> processJarFile(it)
                it.extension == "xml" -> "nothing to do"
                it.extension == "properties" -> "nothing to do"
                //else -> println("PATH ${it.absolutePath}")
            }
        }
    }

    fun indexMpsDirectory(mpsInstallation: File) {
        val languagesDir = File(mpsInstallation, "languages")
        require(languagesDir.exists())
        require(languagesDir.isDirectory)
        languagesDir.walkTopDown().forEach {
            processFileInMpsInstallation(it)
        }
        val pluginsDir = File(mpsInstallation, "plugins")
        require(pluginsDir.exists())
        require(pluginsDir.isDirectory)
        pluginsDir.walkTopDown().forEach {
            processFileInMpsInstallation(it)
        }
    }

    fun indexMpsProject(projectPath: File) {
        if (projectPath.name == ".mps") {
            loadProjectMpsDir(projectPath)
        } else {
            val cs = projectPath.listFiles { c -> c.isDirectory && c.name == ".mps" }
            if (projectPath.isDirectory && cs.isNotEmpty()) {
                assert(cs.size == 1)
                loadProjectMpsDir(cs[0])
            } else {
                throw IllegalArgumentException("Not a valid project path: $projectPath")
            }
        }
    }

    private fun loadProjectMpsDir(mpsDir : File) {
        val projectDir = mpsDir.parentFile
        val modulesFile = File(mpsDir, "modules.xml")
        loadModules(modulesFile, projectDir)
    }

    private fun loadModules(modulesFile: File, projectDir: File) {
        require(modulesFile.exists())
        require(modulesFile.isFile)
        val doc = loadDocument(FileInputStream(modulesFile))
        doc.documentElement.processAllNodes("modulePath") {
            val path = File(it.getAttribute("path").replace("\$PROJECT_DIR\$", projectDir!!.absolutePath))
            require(path.exists())
            require(path.isFile())
            when (path.extension) {
                "mpl" -> loadLanguage(FileElementLoader(path))
                "msd" -> loadSolution(FileElementLoader(path))
                else -> TODO()
            }
        }
    }

    fun verifyCanSatisfyLanguages(deps: Dependencies, recursively: Boolean = false, alreadyVerified : MutableSet<LanguageDep> = mutableSetOf()) {
        deps.languages.forEach {
            verifyCanSatisfyLanguage(it, recursively, alreadyVerified)
        }
    }

    private fun verifyCanSatisfyLanguage(languageDep: LanguageDep, recursively: Boolean = false, alreadyVerified : MutableSet<LanguageDep> = mutableSetOf()) {
        if (alreadyVerified.contains(languageDep)) {
            return
        }
        println("dep $languageDep")
        val el = findElement(languageDep.uuid)
        if (el == null) {
            //throw RuntimeException("Cannot satisfy $languageDep")
            println("UNSATISFIED $languageDep")
        } else {
            require(el.name == languageDep.name)
            require(languageDep.version == -1 || el.languageVersion == languageDep.version) { "Version for dependency is $languageDep, while actual version is $el" }
            alreadyVerified.add(languageDep)
            if (recursively) {
                val deps = calculateDependenciesForLanguage(el)
                try {
                    verifyCanSatisfyLanguages(deps, true, alreadyVerified)
                } catch (t : Throwable) {
                    throw RuntimeException("Deps of $el not satisfied", t)
                }
            }
        }
    }
}

private fun JarEntry.child(location: String): JarEntry {
    return JarEntry("${this.name}/$location")
}

private fun JarEntry.children(jarFile: JarFile): List<JarEntry> {
    return jarFile.entries().toList().filter {
        it.isChildOf(this)
    }.toList()
}

private fun JarEntry.isChildOf(parent: JarEntry): Boolean {
    return if (this.name.startsWith("${parent.name}/")) {
        val remaining = this.name.substring("${parent.name}/".length)
        remaining.indexOf('/') == -1
    } else {
        false
    }
}

private fun JarEntry.parent(): JarEntry {
    val index = this.name.lastIndexOf('/')
    if (index == -1) {
        throw IllegalStateException()
    } else {
        val parentName = this.name.substring(0, index)
        return JarEntry(parentName)
    }
}
