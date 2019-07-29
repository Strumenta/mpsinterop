package com.strumenta.mpsinterop.loading

import com.strumenta.mpsinterop.binary.loadMpsModelFromBinaryFile
import com.strumenta.mpsinterop.logicalmodel.Language
import com.strumenta.mpsinterop.physicalmodel.PhysicalModel
import com.strumenta.mpsinterop.physicalmodel.PhysicalModule
import com.strumenta.mpsinterop.registries.LanguageRegistry
import com.strumenta.mpsinterop.registries.PhysicalModelsRegistry
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.jar.JarFile
import java.io.FileOutputStream
import java.util.*
import java.util.zip.ZipException

fun PhysicalModelsRegistry.loadMpsFile(file: File) : PhysicalModel {
    val model = loadMpsModel(FileInputStream(file))
    this.add(model)
    return model
}


fun PhysicalModelsRegistry.loadMpsFile(inputStream: InputStream) : PhysicalModel {
    val model = loadMpsModel(inputStream)
    this.add(model)
    return model
}

fun PhysicalModelsRegistry.loadJar(file: File) : List<PhysicalModel> {
    val models = LinkedList<PhysicalModel>()
    val jarFile = JarFile(file)
    val entries = jarFile.entries()
    while (entries.hasMoreElements()) {
        val entry = entries.nextElement()
        if (entry.name.endsWith(".mps")) {
            val model = loadMpsModel(jarFile.getInputStream(entry))
            models.add(model)
            this.add(model)
        }
    }
    return models
}

fun PhysicalModelsRegistry.loadJar(inputStream: InputStream) = loadJar(dumpToTempFile(inputStream))

private fun dumpToTempFile(inputStream: InputStream): File {
    val tempFile = File.createTempFile("jar_file_from_input_stream", ".jar")
    tempFile.deleteOnExit()

    val buffer = ByteArray(8 * 1024)

    inputStream.use { inputStream ->
        FileOutputStream(tempFile).use { output ->
            var bytesRead: Int
            do {
                bytesRead = inputStream.read(buffer)
                if (bytesRead != -1) {
                    output.write(buffer, 0, bytesRead)
                }
            } while (bytesRead != -1)
        }
    }
    return tempFile
}

fun LanguageRegistry.loadLanguageFromJar(inputStream: InputStream)  {
    loadJar(inputStream).forEach { this.loadLanguageFromModel(it) }
}

fun LanguageRegistry.loadMpsFile(inputStream: InputStream): PhysicalModel {
    val model = loadMpsModel(inputStream)
    return model
}

private fun LanguageRegistry.loadJar(inputStream: InputStream) : List<PhysicalModel> {
    val file = dumpToTempFile(inputStream)
    try {
        return loadJar(file)
    } catch (e: RuntimeException) {
        throw RuntimeException("Issue loading JAR from inputstream", e)
    }
}
// TODO return also the language for each model
private fun LanguageRegistry.loadJar(file: File) : List<PhysicalModel> {
    val models = LinkedList<PhysicalModel>()
    val modules = LinkedList<Pair<String, PhysicalModule>>()
    try {
        val jarFile = JarFile(file)
        val entries = jarFile.entries()
        while (entries.hasMoreElements()) {
            val entry = entries.nextElement()
            when {
                entry.name.endsWith(".mps") -> {
                    val model = loadMpsModel(jarFile.getInputStream(entry))
                    models.add(model)
                    val module = modules.find { entry.name.startsWith(it.first) }
                    if (module != null) {
                        model.module = module.second
                    }
                }
                entry.name.endsWith(".mpl") -> {
                    val mplXML = loadDocument(jarFile.getInputStream(entry))
                    val uuid = UUID.fromString(mplXML.documentElement.getAttribute("uuid"))
                    val name = mplXML.documentElement.getAttribute("namespace")
                    val module = PhysicalModule(name, uuid)
                    var parts = entry.name.split("/")
                    parts = parts.dropLast(1)
                    // TODO read path from XML
                    val pathCovered = parts.joinToString("/") + "/languageModels/"
                    modules.add(Pair(pathCovered, module))
                }
                entry.name.endsWith(".mpb") -> {
                    val model = loadMpsModelFromBinaryFile(jarFile.getInputStream(entry))
                    models.add(model)
                    val module = modules.find { entry.name.startsWith(it.first) }
                    if (module != null) {
                        model.module = module.second
                    }
                }
            }
        }
    } catch (e: ZipException) {
        throw RuntimeException("Problem loading JAR from file $file", e)
    }
    return models
}

fun LanguageRegistry.loadLanguageFromMpsInputStream(inputStream: InputStream,
                                                    module: PhysicalModule) {
    val model = loadMpsModel(inputStream)
    model.module = module
    this.loadLanguageFromModel(model)
}
