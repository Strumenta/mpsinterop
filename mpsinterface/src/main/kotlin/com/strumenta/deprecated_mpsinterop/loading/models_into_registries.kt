package com.strumenta.deprecated_mpsinterop.loading

import com.strumenta.deprecated_mpsinterop.binary.loadMpsModelFromBinaryFile
import com.strumenta.deprecated_mpsinterop.logicalmodel.Language
import com.strumenta.deprecated_mpsinterop.physicalmodel.PhysicalLanguageModule
import com.strumenta.deprecated_mpsinterop.physicalmodel.PhysicalModel
import com.strumenta.deprecated_mpsinterop.physicalmodel.PhysicalModule
import com.strumenta.deprecated_mpsinterop.physicalmodel.PhysicalSolutionModule
import com.strumenta.deprecated_mpsinterop.registries.LanguageRegistry
import com.strumenta.deprecated_mpsinterop.utils.dumpToTempFile
import com.strumenta.deprecated_mpsinterop.utils.loadDocument
import com.strumenta.deprecated_mpsinterop.utils.processAllNodes
import org.w3c.dom.Element
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.*
import java.util.jar.JarFile
import java.util.zip.ZipException

fun LanguageRegistry.loadMplFile(inputStream: InputStream) : PhysicalLanguageModule {
    val mplXML = loadDocument(inputStream)
    val uuid = UUID.fromString(mplXML.documentElement.getAttribute("uuid"))
    val name = mplXML.documentElement.getAttribute("namespace")
    val module = PhysicalLanguageModule(uuid, name)
    mplXML.documentElement.processAllNodes { if (it.tagName == "dependency") {
        module.dependencies.add(processDependency(it))
        }
    }
    return module
}

private fun processDependency(element: Element) : PhysicalModule.Dependency {
    val s = element.textContent
    require(s[36] == '(')
    require(s.last() == ')')
    val uuid = UUID.fromString(s.substring(0, 36))
    val name = s.substring(37, s.length - 1)
    val reexport = element.getAttribute("reexport").toBoolean()
    return PhysicalModule.Dependency(uuid, name, reexport)
}

private fun processUsedLanguage(element: Element) : PhysicalModule.UsedLanguage {
    val s = element.textContent
    require(s[36] == '(')
    require(s.last() == ')')
    val uuid = UUID.fromString(s.substring(0, 36))
    val name = s.substring(37, s.length - 1)
    return PhysicalModule.UsedLanguage(uuid, name)
}

fun LanguageRegistry.loadMsdFile(inputStream: InputStream) : PhysicalSolutionModule {
    val mplXML = loadDocument(inputStream)
    val uuid = UUID.fromString(mplXML.documentElement.getAttribute("uuid"))
    val name = mplXML.documentElement.getAttribute("name")
    val module = PhysicalSolutionModule(uuid, name)
    mplXML.documentElement.processAllNodes { if (it.tagName == "dependency") {
        module.dependencies.add(processDependency(it))
        }
    }
    mplXML.documentElement.processAllNodes { if (it.tagName == "usedLanguage") {
        module.usedLanguages.add(processUsedLanguage(it))
    }
    }
    return module
}

interface DependenciesLoader {
    fun loadDependencies(jarData: JarData)
}

fun LanguageRegistry.loadLanguageFromJar(inputStream: InputStream, dependenciesLoader: DependenciesLoader? = null) {
    val jarData = loadJar(inputStream)
    dependenciesLoader?.loadDependencies(jarData)
    loadLanguageFromJarData(jarData)
}

private fun LanguageRegistry.loadLanguageFromJarData(jarData: JarData) {
    val modules = jarData.modules
    jarData.modules.forEach {
        this.add(Language(it.second.uuid, it.second.name))
    }
    jarData.models.forEach { this.loadLanguageFromModel(it, onlyShallow = true) }
    jarData.models.forEach { this.loadLanguageFromModel(it) }
}

fun LanguageRegistry.loadLanguageFromDirectory(directory: File) {
    val jarData = loadJarFromDirectory(directory)
    loadLanguageFromJarData(jarData)
}

fun LanguageRegistry.loadMpsFile(inputStream: InputStream): PhysicalModel {
    val model = loadMpsModel(inputStream)
    return model
}

private fun LanguageRegistry.loadJar(inputStream: InputStream): JarData {
    val file = dumpToTempFile(inputStream)
    try {
        return loadJar(file)
    } catch (e: RuntimeException) {
        throw RuntimeException("Issue loading JAR from inputstream", e)
    }
}

// TODO return also the language for each model
data class JarData(val models: List<PhysicalModel>, val modules : List<Pair<String, PhysicalModule>>)

private fun LanguageRegistry.loadJarFromDirectory(directory: File): JarData {
    val models = LinkedList<PhysicalModel>()
    val modules = LinkedList<Pair<String, PhysicalModule>>()

    val dirToExplores = LinkedList<File>()
    val files = LinkedList<File>()
    dirToExplores.add(directory)
    var i = 0
    while (i<dirToExplores.size) {
        dirToExplores[i].listFiles().forEach {
            if (it.isFile) {
                files.add(it)
            } else if (it.isDirectory) {
                dirToExplores.add(it)
            }
        }
        i++
    }

    files.filter { it.name.endsWith(".mpl") }.forEach {
        val module = loadMplFile(FileInputStream(it))
        var parts = it.name.split("/")
        parts = parts.dropLast(1)
        // TODO read path from XML
        val pathCovered = parts.joinToString("/") + "/languageModels/"
        modules.add(Pair(pathCovered, module))
    }
    files.filter { it.name.endsWith(".mps") }.forEach {file ->
        val model = loadMpsModel(FileInputStream(file))
        models.add(model)
        val module = modules.find { file.name.startsWith(it.first) }
        if (module != null) {
            model.module = module.second
        } else {
            //throw java.lang.RuntimeException()
            if (modules.size == 1) {
                model.module = modules[0].second
            }
        }
    }
    files.filter { it.name.endsWith(".mpb") }.forEach {file ->
        val model = loadMpsModelFromBinaryFile(FileInputStream(file))
        models.add(model)
        val module = modules.find {
            var entryName = file.name
            if (entryName.startsWith("module/models/")) {
                entryName = entryName.removePrefix("module/models/")
            }
            entryName = entryName.replace("/", ".")
            entryName.startsWith(it.first) }
        if (module != null) {
            model.module = module.second
        } else {
            //throw java.lang.RuntimeException()
            //println("A")
            if (modules.size == 1) {
                model.module = modules[0].second
            }
        }
    }
    return JarData(models, modules)
}

private fun LanguageRegistry.loadJar(file: File): JarData {
    val models = LinkedList<PhysicalModel>()
    val modules = LinkedList<Pair<String, PhysicalModule>>()
    try {
        val jarFile = JarFile(file)

        // TODO consider devkits

        // first load modules
        var entries = jarFile.entries()
        while (entries.hasMoreElements()) {
            val entry = entries.nextElement()
            when {
                entry.name.endsWith(".mpl") -> {
                    val module = loadMplFile(jarFile.getInputStream(entry))
                    var parts = entry.name.split("/")
                    parts = parts.dropLast(1)
                    // TODO read path from XML
                    val pathCovered = parts.joinToString("/") + "/languageModels/"
                    modules.add(Pair(pathCovered, module))
                }
                entry.name.endsWith(".msd") -> {
                    val module = loadMsdFile(jarFile.getInputStream(entry))
                    var parts = entry.name.split("/")
                    parts = parts.dropLast(1)
                    // TODO read path from XML
                    val pathCovered = parts.joinToString("/") + "/modules/"
                    modules.add(Pair(pathCovered, module))
                }
            }
        }
        entries = jarFile.entries()
        while (entries.hasMoreElements()) {
            val entry = entries.nextElement()
            when {
                entry.name.endsWith(".mps") -> {
                    val model = loadMpsModel(jarFile.getInputStream(entry))
                    models.add(model)
                    val module = modules.find { entry.name.startsWith(it.first) }
                    if (module != null) {
                        model.module = module.second
                    } else {
                        //throw java.lang.RuntimeException()
                        if (modules.size == 1) {
                            model.module = modules[0].second
                        }
                    }
                }
                entry.name.endsWith(".mpb") -> {
                    val model = loadMpsModelFromBinaryFile(jarFile.getInputStream(entry))
                    models.add(model)
                    val module = modules.find {
                        var entryName = entry.name
                        if (entryName.startsWith("module/models/")) {
                            entryName = entryName.removePrefix("module/models/")
                        }
                        entryName = entryName.replace("/", ".")
                        entryName.startsWith(it.first) }
                    if (module != null) {
                        model.module = module.second
                    } else {
                        //throw java.lang.RuntimeException()
                        //println("A")
                        if (modules.size == 1) {
                            model.module = modules[0].second
                        }
                    }
                }
            }
        }
    } catch (e: ZipException) {
        throw RuntimeException("Problem loading JAR from file $file", e)
    }
    return JarData(models, modules)
}

fun LanguageRegistry.loadLanguageFromMpsInputStream(
    inputStream: InputStream,
    module: PhysicalModule
) {
    val model = loadMpsModel(inputStream)
    model.module = module
    this.loadLanguageFromModel(model)
}

fun LanguageRegistry.loadElementFromJar(inputStream: InputStream, dependenciesLoader: DependenciesLoader? = null) {
    val jarData = loadJar(inputStream)
    dependenciesLoader?.loadDependencies(jarData)
}