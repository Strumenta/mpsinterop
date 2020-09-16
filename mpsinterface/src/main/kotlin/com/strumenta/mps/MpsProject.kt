package com.strumenta.mps

import java.io.File
import java.io.FileInputStream
import java.io.FilenameFilter
import java.util.*

enum class ElementType {
    DEVKIT,
    LANGUAGE,
    SOLUTION
}

class MpsProject(val projectDir: File) {

    private val solutions = mutableListOf<IndexElement>()
    private val languages = mutableListOf<IndexElement>()

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
        solutions.add(IndexElement(uuid, name, ElementType.SOLUTION, source, moduleVersion))
    }

    private fun loadLanguage(source: Source) {
        val document = source.document
        val uuid = UUID.fromString(document.documentElement.getAttribute("uuid"))
        val name = document.documentElement.getAttribute("namespace")
        val moduleVersion = document.documentElement.getAttribute("moduleVersion").toInt()
        val languageVersion = document.documentElement.getAttribute("languageVersion").toInt()
        require(name.isNotBlank())
        languages.add(IndexElement(uuid, name, ElementType.LANGUAGE, source, moduleVersion, languageVersion))
    }

    fun languageNames(): Set<String> {
        return languages.map { it.name }.toSet()
    }

    fun hasSolution(solutionName: String): Boolean {
        return solutions.any { it.name == solutionName }
    }

    private data class IndexElement(val uuid: UUID, val name: String, val type: ElementType,
                            val source: Source,
                            val moduleVersion: Int? = null, val languageVersion: Int? = null)
}