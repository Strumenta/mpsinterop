package com.strumenta.mpsinterop.registries

import com.strumenta.mpsinterop.binary.loadMpsModelFromBinaryFile
import com.strumenta.mpsinterop.loading.ModelLocator
import com.strumenta.mpsinterop.loading.loadMpsModel
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

// TODO treat languages differently
class Indexer : ModelLocator {

    private abstract class ModelLoadingInfo(val source: ModelSource,
                                            val sourceType: ModelSourceType) {
        open val model : PhysicalModel by lazy { load() }
        protected abstract fun load() : PhysicalModel
        override fun toString(): String {
            return "Model loaded from $source, type $sourceType"
        }
    }

    private class HolderModelLoadingInfo(override val model: PhysicalModel,
                                         source: ModelSource,
                                         sourceType: ModelSourceType) : ModelLoadingInfo(source, sourceType) {
        override fun load() = throw UnsupportedOperationException()
    }

    private val byUUID = HashMap<UUID, ModelLoadingInfo>()

    private fun register(uuid: UUID, modelLoadingInfo: ModelLoadingInfo) {
        if (uuid in byUUID) {
            throw IllegalStateException("ModelLoadingInfo already present for UUID $uuid: ${byUUID[uuid]}. Attempting to replace it with $modelLoadingInfo")
        }
        byUUID[uuid] = modelLoadingInfo
    }

    override fun locate(modelUUID: UUID): PhysicalModel? {
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
        println("SIZE ${byUUID.size}")
        byUUID.entries.forEach {
            println(it.value.model.name)
        }
    }

    private fun indexMps(inputStream: InputStream, modelSource: ModelSource) {
        val model = loadMpsModel(inputStream)
        register(model.uuid, HolderModelLoadingInfo(model, modelSource, ModelSourceType.MPS))
    }

    private fun indexMpl(inputStream: InputStream, modelSource: ModelSource) {
        val mplXML = loadDocument(inputStream)
        val uuid = UUID.fromString(mplXML.documentElement.getAttribute("uuid"))
        val name = mplXML.documentElement.getAttribute("namespace")
        val module = PhysicalModule(name, uuid)
        register(uuid, object : ModelLoadingInfo(modelSource, ModelSourceType.MPL) {
            override fun load(): PhysicalModel {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    private fun indexMpb(inputStream: InputStream, modelSource: ModelSource) {
        val model = loadMpsModelFromBinaryFile(inputStream)
        register(model.uuid, HolderModelLoadingInfo(model, modelSource, ModelSourceType.MPB))
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