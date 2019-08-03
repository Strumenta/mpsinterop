package com.strumenta.mpsinterop.registries

import com.strumenta.mpsinterop.binary.loadMpsModelFromBinaryFile
import com.strumenta.mpsinterop.loading.ModelLocator
import com.strumenta.mpsinterop.loading.loadMpsModel
import com.strumenta.mpsinterop.physicalmodel.PhysicalLanguage
import com.strumenta.mpsinterop.physicalmodel.PhysicalModel
import com.strumenta.mpsinterop.physicalmodel.PhysicalModule
import com.strumenta.mpsinterop.utils.loadDocument
import java.io.File
import java.io.InputStream
import java.lang.IllegalStateException
import java.lang.UnsupportedOperationException
import java.util.*
import java.util.jar.JarFile
import java.util.zip.ZipException

interface ModelSource

class JarModelSource(val jarFile: File, val entry: String) : ModelSource {
    override fun toString(): String {
        return "Jar ${jarFile.absolutePath}#$entry"
    }
}

enum class ModelSourceType {
    MPS,
    MPB,
    MPL
}

abstract class LoadingInfo<E>(val source: ModelSource,
                                      val sourceType: ModelSourceType) {
    open val element : E by lazy { load() }
    protected abstract fun load() : E
    override fun toString(): String {
        return "Element loaded from $source, type $sourceType"
    }
}

typealias ModelLoadingInfo = LoadingInfo<PhysicalModel>
typealias LanguageLoadingInfo = LoadingInfo<PhysicalLanguage>

// TODO treat languages differently
class Indexer : ModelLocator {

    private class HolderLoadingInfo<E>(override val element: E,
                                         source: ModelSource,
                                         sourceType: ModelSourceType) : LoadingInfo<E>(source, sourceType) {
        override fun load() = throw UnsupportedOperationException()
    }


    private val modelsByUUID = HashMap<UUID, ModelLoadingInfo>()
    private val languagesByUUID = HashMap<UUID, LoadingInfo<PhysicalLanguage>>()

    private fun registerModel(uuid: UUID, modelLoadingInfo: ModelLoadingInfo) {
        if (uuid in modelsByUUID) {
            throw IllegalStateException("ModelLoadingInfo already present for UUID $uuid: ${modelsByUUID[uuid]}. Attempting to replace it with $modelLoadingInfo")
        }
        modelsByUUID[uuid] = modelLoadingInfo
    }

    private fun registerLanguage(uuid: UUID, loadingInfo: LanguageLoadingInfo) {
        if (uuid in languagesByUUID) {
            throw IllegalStateException("LoadingInfo already present for UUID $uuid: ${languagesByUUID[uuid]}. Attempting to replace it with $loadingInfo")
        }
        languagesByUUID[uuid] = loadingInfo
    }

    override fun locateModel(modelUUID: UUID): PhysicalModel? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun locateLanguage(languageUUID: UUID): PhysicalLanguage? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
        //println("SIZE ${byUUID.size}")
        modelsByUUID.entries.forEach {
            println("Model ${it.value.element.name} ${it.value.element.uuid}")
        }
        languagesByUUID.entries.forEach {
            println("Language ${it.value.element.name} ${it.value.element.uuid}")
        }
    }

    private fun indexMps(inputStream: InputStream, modelSource: ModelSource) {
        val model = loadMpsModel(inputStream)
        registerModel(model.uuid, HolderLoadingInfo(model, modelSource, ModelSourceType.MPS))
    }

    private fun indexMpl(inputStream: InputStream, modelSource: ModelSource) {
        val mplXML = loadDocument(inputStream)
        val uuid = UUID.fromString(mplXML.documentElement.getAttribute("uuid"))
        val name = mplXML.documentElement.getAttribute("namespace")
        val module = PhysicalModule(name, uuid)
        val language = PhysicalLanguage(uuid, name)
        registerLanguage(uuid, HolderLoadingInfo(language, modelSource, ModelSourceType.MPL))
    }

    private fun indexMpb(inputStream: InputStream, modelSource: ModelSource) {
        val model = loadMpsModelFromBinaryFile(inputStream)
        registerModel(model.uuid, HolderLoadingInfo(model, modelSource, ModelSourceType.MPB))
    }

    private fun indexJar(file: File) {
        //println("[JAR] ${file.absolutePath}")
        try {
            val jarFile = JarFile(file)
            val entries = jarFile.entries()
            while (entries.hasMoreElements()) {
                val entry = entries.nextElement()
                val source = JarModelSource(file, entry.name)
                when {
                    entry.name.endsWith(".mps") -> {
                        if (!entry.name.endsWith("descriptorclasses.mps")
                                && !entry.name.contains("/aspectcps")) {
                            //println("[MPS] ${entry.name}")
                            indexMps(jarFile.getInputStream(entry), source)
                        }
                    }
                    entry.name.endsWith(".mpl") -> {
                        //println("[MPL] ${entry.name}")
                        indexMpl(jarFile.getInputStream(entry), source)
                    }
                    entry.name.endsWith(".mpb") -> {
                        //println("[MPB] ${entry.name}")
                        indexMpb(jarFile.getInputStream(entry), source)
                    }
                }
            }
        } catch (e: ZipException) {
            throw RuntimeException("Problem loading JAR from file $file", e)
        }

    }
}

fun main() {
    val indexer = Indexer()
    indexer.explore(File("/Applications/MPS2019.1.4.app/Contents/languages/"))
}