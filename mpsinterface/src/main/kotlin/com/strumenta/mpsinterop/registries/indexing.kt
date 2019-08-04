package com.strumenta.mpsinterop.registries

import com.strumenta.mpsinterop.binary.loadMpsModelFromBinaryFile
import com.strumenta.mpsinterop.loading.ModelLocator
import com.strumenta.mpsinterop.loading.loadLanguage
import com.strumenta.mpsinterop.loading.loadMpsModel
import com.strumenta.mpsinterop.physicalmodel.PhysicalLanguageModule
import com.strumenta.mpsinterop.physicalmodel.PhysicalModel
import com.strumenta.mpsinterop.physicalmodel.PhysicalNode
import com.strumenta.mpsinterop.utils.dumpToTempFile
import java.io.File
import java.io.InputStream
import java.util.*
import java.util.jar.JarFile
import java.util.zip.ZipException

private val String.parentName: String
    get() {
        val parts = this.split(".").dropLast(1)
        return parts.joinToString(".")
    }

interface Source

class JarSource(val jarFile: File, val entry: String) : Source {
    override fun toString(): String {
        return "Jar ${jarFile.absolutePath}#$entry"
    }
}

enum class SourceType {
    MPS,
    MPB,
    MPL
}

abstract class LoadingInfo<E>(
    val source: Source,
    val sourceType: SourceType
) {
    open val element: E by lazy { load() }

    protected abstract fun load(): E
    override fun toString(): String {
        return "Element loaded from $source, type $sourceType"
    }
}

typealias ModelLoadingInfo = LoadingInfo<PhysicalModel>
typealias LanguageLoadingInfo = LoadingInfo<PhysicalLanguageModule>

class Indexer : ModelLocator {

    companion object {
        val DEFAULT = Indexer()
    }

    override fun locateModel(name: String): PhysicalModel? {
        return modelsByName[name]?.element
    }

    private class HolderLoadingInfo<E>(
        override val element: E,
        source: Source,
        sourceType: SourceType
    ) : LoadingInfo<E>(source, sourceType) {
        override fun load() = throw UnsupportedOperationException()
    }

    private val modelsByUUID = HashMap<UUID, ModelLoadingInfo>()
    private val modelsByName = HashMap<String, ModelLoadingInfo>()
    private val languagesByUUID = HashMap<UUID, LoadingInfo<PhysicalLanguageModule>>()

    private fun registerModel(uuid: UUID, loadingInfo: ModelLoadingInfo) {
        if (uuid in modelsByUUID) {
            throw IllegalStateException(
                    "LoadingInfo already present for model with UUID $uuid: ${modelsByUUID[uuid]}. Attempting to replace it with $loadingInfo")
        }
        val name = loadingInfo.element.name
        if (name in modelsByName) {
            throw IllegalStateException(
                    "LoadingInfo already present for model with name $name: ${modelsByName[name]}. Attempting to replace it with $loadingInfo")
        }
        modelsByName[name] = loadingInfo
        modelsByUUID[uuid] = loadingInfo
    }

    private fun registerLanguage(uuid: UUID, loadingInfo: LanguageLoadingInfo) {
        if (uuid in languagesByUUID) {
            throw IllegalStateException(
                    "LoadingInfo already present for language with UUID $uuid: ${languagesByUUID[uuid]}. Attempting to replace it with $loadingInfo")
        }
        languagesByUUID[uuid] = loadingInfo
    }

    override fun locateModel(modelUUID: UUID): PhysicalModel? {
        return modelsByUUID[modelUUID]?.element
    }

    override fun locateLanguage(languageUUID: UUID): PhysicalLanguageModule? {
        return languagesByUUID[languageUUID]?.element
    }

    fun explore(file: File) {
        when {
            file.isDirectory -> {
                file.listFiles().forEach {
                    explore(it)
                }
            }
            file.isFile -> when (file.extension) {
                "jar" -> indexJar(file)
                else -> println(file.extension)
            }
        }
    }

    private fun indexMps(inputStream: InputStream, modelSource: Source) {
        val model = loadMpsModel(inputStream)
        registerModel(model.uuid, HolderLoadingInfo(model, modelSource, SourceType.MPS))
    }

    private fun indexMpl(inputStream: InputStream, modelSource: Source) {
        val language = loadLanguage(inputStream)
        registerLanguage(language.uuid, HolderLoadingInfo(language, modelSource, SourceType.MPL))
    }

    private fun indexMpb(inputStream: InputStream, modelSource: Source) {
        val model = loadMpsModelFromBinaryFile(inputStream)
        registerModel(model.uuid, HolderLoadingInfo(model, modelSource, SourceType.MPB))
    }

    fun indexJar(inputStream: InputStream) {
        indexJar(dumpToTempFile(inputStream))
    }

    fun indexJar(file: File) {
        try {
            val jarFile = JarFile(file)
            val entries = jarFile.entries()
            while (entries.hasMoreElements()) {
                val entry = entries.nextElement()
                val source = JarSource(file, entry.name)
                when {
                    entry.name.endsWith(".mps") -> {
                        if (!entry.name.endsWith("descriptorclasses.mps") &&
                                !entry.name.contains("/aspectcps")) {
                            // println("[MPS] ${entry.name}")
                            indexMps(jarFile.getInputStream(entry), source)
                        }
                    }
                    entry.name.endsWith(".mpl") -> {
                        // println("[MPL] ${entry.name}")
                        indexMpl(jarFile.getInputStream(entry), source)
                    }
                    entry.name.endsWith(".mpb") -> {
                        // println("[MPB] ${entry.name}")
                        indexMpb(jarFile.getInputStream(entry), source)
                    }
                }
            }
        } catch (e: ZipException) {
            throw RuntimeException("Problem loading JAR from file $file", e)
        }
    }
}

fun ModelLocator.findConceptDeclaration(qname: String): PhysicalNode? {
    val modelName = qname.parentName
    val model = this.locateModel(modelName)
    return model?.findConceptDeclaration(qname)
}

fun main() {
    val indexer = Indexer()
    indexer.explore(File("/Applications/MPS2019.1.4.app/Contents/languages/"))
}