package com.strumenta.mps

import org.w3c.dom.Document
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

/**
 * It can be a Jar file, an entry inside a Jar file, a file or a directory
 */
abstract class Source {
    abstract fun inputStream(): InputStream
    val document : Document by lazy { loadDocument(inputStream()) }
}

class FileSource(val file: File) : Source() {
    override fun inputStream(): InputStream = FileInputStream(file)
}


