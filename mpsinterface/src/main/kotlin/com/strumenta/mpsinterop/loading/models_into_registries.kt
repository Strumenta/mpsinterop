package com.strumenta.mpsinterop.loading

import com.strumenta.mpsinterop.binary.loadMpsModelFromBinaryFile
import com.strumenta.mpsinterop.logicalmodel.Language
import com.strumenta.mpsinterop.physicalmodel.PhysicalModel
import com.strumenta.mpsinterop.physicalmodel.PhysicalModule
import com.strumenta.mpsinterop.registries.LanguageRegistry
import com.strumenta.mpsinterop.utils.dumpToTempFile
import com.strumenta.mpsinterop.utils.loadDocument
import java.io.File
import java.io.InputStream
import java.util.*
import java.util.jar.JarFile
import java.util.zip.ZipException

fun LanguageRegistry.loadMplFile(inputStream: InputStream) : PhysicalModule {
    val mplXML = loadDocument(inputStream)
    val uuid = UUID.fromString(mplXML.documentElement.getAttribute("uuid"))
    val name = mplXML.documentElement.getAttribute("namespace")
    val module = PhysicalModule(uuid, name)
    return module
}

fun LanguageRegistry.loadLanguageFromJar(inputStream: InputStream) {
    val jarData = loadJar(inputStream)
    val modules = jarData.modules
    jarData.modules.forEach {
        this.add(Language(it.second.uuid, it.second.name))
    }
    jarData.models.forEach { this.loadLanguageFromModel(it, onlyShallow = true) }
    jarData.models.forEach { this.loadLanguageFromModel(it) }
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

private fun LanguageRegistry.loadJar(file: File): JarData {
    val models = LinkedList<PhysicalModel>()
    val modules = LinkedList<Pair<String, PhysicalModule>>()
    try {
        val jarFile = JarFile(file)

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
