package com.strumenta.mps

import com.strumenta.mps.utils.directChildrenOf
import com.strumenta.mps.utils.parent
import com.strumenta.mps.utils.subDirectory
import com.strumenta.utils.loadDocument
import org.w3c.dom.Document
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.lang.RuntimeException
import java.util.jar.JarFile

/**
 * It can be a Jar file, an entry inside a Jar file, a file or a directory
 */
abstract class Source {
    abstract fun inputStream(): InputStream
    abstract fun listChildrenUnder(location: String, includingDir: Boolean = false): List<Source>
    abstract fun listChildren() : List<Source>
    fun listChildrenRecursively(): List<Source> {
        val sources = mutableListOf<Source>()
        for (c in listChildren()) {
            if (c.isFile) {
                sources.add(c)
            } else {
                sources.addAll(c.listChildrenRecursively())
            }
        }
        return sources
    }
    fun listChildrenUnderRecursively(location: String): List<Source> {
        val sources = mutableListOf<Source>()
        for (c in listChildrenUnder(location, includingDir = true)) {
            if (c.isFile) {
                sources.add(c)
            } else {
                sources.addAll(c.listChildrenRecursively())
            }
        }
        return sources
    }

    abstract val isFile: Boolean
    val document: Document by lazy {
        try {
            loadDocument(inputStream())
        } catch (t: Throwable) {
            throw RuntimeException("Issue while loading XML document from $this", t)
        }
    }

    abstract fun extension() : String
}

class FileSource(val file: File) : Source() {
    override fun inputStream(): InputStream = FileInputStream(file)
    override fun listChildrenUnder(location: String, includingDir: Boolean): List<Source> {
        val parent = file.parentFile
        val locationDir = File(parent, location)
        if (!locationDir.exists()) {
            // this is legal, for examples, models is often listed even if it does not exists
            // maybe because the module imports just jars
            return emptyList()
        }
        val childrenFiles = (locationDir.listFiles() ?: throw RuntimeException("no children found for $locationDir")).filter { it.isFile || includingDir }
        return childrenFiles.map { FileSource(it) }
    }

    override fun listChildren(): List<Source> {
        val childrenFiles = (file.listFiles() ?: throw RuntimeException("no children found for $file"))
        return childrenFiles.map { FileSource(it) }
    }

    override val isFile: Boolean
        get() = file.isFile

    override fun extension(): String = file.extension

    override fun toString(): String {
        return "file(${file.path})"
    }
}

class JarEntrySource(val file: File, val entryPath: String) : Source() {

    override fun inputStream(): InputStream {
        val jarFile = JarFile(file)
        val entry = jarFile.getJarEntry(entryPath)
        if (entry == null) {
            throw RuntimeException("Entry $entryPath not found in JAR $file")
        }
        return jarFile.getInputStream(entry)
    }
    override fun listChildrenUnder(location: String, includingDir: Boolean): List<Source> {
        val jarFile = JarFile(file)
        val entry = jarFile.getJarEntry(entryPath)
        val parentEntry = jarFile.parent(entry)
        val locationEntry = if (parentEntry != null) jarFile.subDirectory(parentEntry, location) else null
        if (locationEntry == null) {
            // this is legal, for examples, models is often listed even if it does not exists
            // maybe because the module imports just jars
            return emptyList()
        }
        val childrenEntries = jarFile.directChildrenOf(locationEntry)
        return childrenEntries.map { JarEntrySource(file, it.name) }
    }

    override fun listChildren(): List<Source> {
        val jarFile = JarFile(file)
        val entry = jarFile.getJarEntry(entryPath)
        return jarFile.directChildrenOf(entry).map { JarEntrySource(file, it.name) }
    }

    override val isFile: Boolean
        get() {
            val jarFile = JarFile(file)
            val entry = jarFile.getJarEntry(entryPath)
            return !entry.isDirectory
        }

    override fun extension(): String {
        val simpleName = entryPath.split('/').last()
        val parts = simpleName.split('.')
        return if (parts.size == 1) {
            ""
        } else {
            parts.last()
        }
    }

    override fun toString(): String {
        return "jarEntry(${file.path}#$entryPath)"
    }
}

