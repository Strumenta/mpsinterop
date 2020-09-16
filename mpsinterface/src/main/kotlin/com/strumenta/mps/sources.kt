package com.strumenta.mps

import org.w3c.dom.Document
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.lang.RuntimeException

/**
 * It can be a Jar file, an entry inside a Jar file, a file or a directory
 */
abstract class Source {
    abstract fun inputStream(): InputStream
    abstract fun listChildrenUnder(location: String): List<Source>
    val document : Document by lazy { loadDocument(inputStream()) }
}

class FileSource(val file: File) : Source() {
    override fun inputStream(): InputStream = FileInputStream(file)
    override fun listChildrenUnder(location: String): List<Source> {
        val parent = file.parentFile
        val locationDir = File(parent, location)
        if (!locationDir.exists()) {
            // this is legal, for examples, models is often listed even if it does not exists
            // maybe because the module imports just jars
            return emptyList()
        }
        val childrenFiles = (locationDir.listFiles() ?: throw RuntimeException("no children found for $locationDir")).filter { it.isFile }
        return childrenFiles.map { FileSource(it) }
    }
}


