package com.strumenta.mpsinterop.registries

import com.strumenta.mpsinterop.binary.loadMpsModelFromBinaryFile
import com.strumenta.mpsinterop.loading.loadDocument
import com.strumenta.mpsinterop.loading.loadMpsModel
import com.strumenta.mpsinterop.physicalmodel.PhysicalModel
import com.strumenta.mpsinterop.physicalmodel.PhysicalModule
import java.io.File
import java.io.InputStream
import java.util.*
import java.util.jar.JarFile
import java.util.zip.ZipException

class Indexer {
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

    fun indexMps(inputStream: InputStream) {
        TODO()
    }

    fun indexMpl(inputStream: InputStream) {
        TODO()
    }

    fun indexMpb(inputStream: InputStream) {
        TODO()
    }

    fun indexJar(file: File) {
        println("[JAR] ${file.absolutePath}")
        try {
            val jarFile = JarFile(file)
            val entries = jarFile.entries()
            while (entries.hasMoreElements()) {
                val entry = entries.nextElement()
                when {
                    entry.name.endsWith(".mps") -> {
                        println("[MPS] ${entry.name}")
                        indexMps(jarFile.getInputStream(entry))
                    }
                    entry.name.endsWith(".mpl") -> {
                        println("[MPL] ${entry.name}")
                        indexMpl(jarFile.getInputStream(entry))
                    }
                    entry.name.endsWith(".mpb") -> {
                        println("[MPB] ${entry.name}")
                        indexMpb(jarFile.getInputStream(entry))
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