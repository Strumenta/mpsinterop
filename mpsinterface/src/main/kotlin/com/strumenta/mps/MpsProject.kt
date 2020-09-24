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
    abstract val source: Source

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

class MpsProject(val projectDir: File, val mpsInstallation: MpsInstallation? = null) : ModulesContainer() {

    private val solutions = mutableListOf<Solution>()
    private val languages = mutableListOf<Language>()

    val projectModules: List<Module>
        get() = solutions + languages

    override val modules: List<Module>
        get() = projectModules + (mpsInstallation?.modules ?: emptyList())

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
                "mpl" -> loadLanguageInt(FileSource(path))
                "msd" -> loadSolutionInt(FileSource(path))
                "devkit" -> null
                else -> throw IllegalStateException("Unable to process module file $path")
            }
        }
    }

    private fun loadSolutionInt(source: Source) {
        val document = source.document
        val uuid = UUID.fromString(document.documentElement.getAttribute("uuid"))
        val name = document.documentElement.getAttribute("name")
        val moduleVersionStr = document.documentElement.getAttribute("moduleVersion")
        val moduleVersion = if (moduleVersionStr.isBlank()) null else moduleVersionStr.toInt()
        require(name.isNotBlank()) { "name is blank" }
        solutions.add(loadSolution(source))
    }

    private fun loadLanguageInt(source: Source) {
        val document = source.document
        val uuid = UUID.fromString(document.documentElement.getAttribute("uuid"))
        val name = document.documentElement.getAttribute("namespace")
        val moduleVersion = document.documentElement.getAttribute("moduleVersion").toInt()
        val languageVersion = document.documentElement.getAttribute("languageVersion").toInt()
        require(name.isNotBlank())
        languages.add(loadLanguage(source))
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


}
