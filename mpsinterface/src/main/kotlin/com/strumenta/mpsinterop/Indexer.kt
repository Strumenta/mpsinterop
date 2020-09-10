package com.strumenta.mpsinterop

import com.strumenta.mpsinterop.utils.loadDocument
import com.strumenta.mpsinterop.utils.processAllNodes
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.lang.IllegalArgumentException
import java.lang.RuntimeException
import java.util.*
import java.util.jar.JarEntry
import java.util.jar.JarFile

enum class ElementType {
    DEVKIT,
    LANGUAGE,
    SOLUTION
}

/**
 * The only goal of this class it to build an index, without having memory occupied to explode
 */
class Indexer {

    private val elementsByUUID = mutableMapOf<UUID, IndexElement>()
    private val elementsByName = mutableMapOf<String, IndexElement>()

    data class IndexElement(val uuid: UUID, val name: String, val type: ElementType, val moduleVersion: Int? = null, val languageVersion: Int? = null)

    private fun addIndexElement(indexElement: IndexElement) {
        //println(" * add $indexElement")
        require(!elementsByUUID.containsKey(indexElement.uuid))
        require(!elementsByName.containsKey(indexElement.name))
        elementsByUUID[indexElement.uuid] = indexElement
        elementsByName[indexElement.name] = indexElement
    }

    private fun loadDevKit(inputStreamProvider: () -> InputStream) {
        val inputStream = inputStreamProvider()
        val document = loadDocument(inputStream)
        val uuid = UUID.fromString(document.documentElement.getAttribute("uuid"))
        val name = document.documentElement.getAttribute("name")
        require(name.isNotBlank())
        addIndexElement(IndexElement(uuid, name, ElementType.DEVKIT))
    }

    fun jarEntryLoader(jarFile: JarFile, entry: JarEntry) : () -> InputStream {
        return { -> jarFile.getInputStream(entry) }
    }

    private fun loadSolution(inputStreamProvider: () -> InputStream) {
        val inputStream = inputStreamProvider()
        val document = loadDocument(inputStream)
        val uuid = UUID.fromString(document.documentElement.getAttribute("uuid"))
        val name = document.documentElement.getAttribute("name")
        val moduleVersionStr = document.documentElement.getAttribute("moduleVersion")
        val moduleVersion = if (moduleVersionStr.isBlank()) null else moduleVersionStr.toInt()
        require(name.isNotBlank()) { "name is blank" }
        addIndexElement(IndexElement(uuid, name, ElementType.SOLUTION, moduleVersion))
    }

    private fun loadLanguage(inputStreamProvider: () -> InputStream) {
        val inputStream = inputStreamProvider()
        val document = loadDocument(inputStream)
        val uuid = UUID.fromString(document.documentElement.getAttribute("uuid"))
        val name = document.documentElement.getAttribute("namespace")
        val moduleVersion = document.documentElement.getAttribute("moduleVersion").toInt()
        val languageVersion = document.documentElement.getAttribute("languageVersion").toInt()
        require(name.isNotBlank())
        addIndexElement(IndexElement(uuid, name, ElementType.LANGUAGE, moduleVersion, languageVersion))
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
        languagesDir.walkTopDown().forEach {
            processFileInMpsInstallation(it)
        }
        val pluginsDir = File(mpsInstallation, "plugins")
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
                "mpl" -> loadLanguage { FileInputStream(path) }
                "msd" -> loadSolution { FileInputStream(path) }
                else -> TODO()
            }
        }
    }
}