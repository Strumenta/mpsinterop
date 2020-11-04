package com.strumenta.mps.utils

import java.util.jar.JarEntry
import java.util.jar.JarFile

fun JarFile.subDirectory(parentEntry: JarEntry, location: String): JarEntry? {
    val locationCleaned = if (location.endsWith("/")) location else "$location/"
    return this.entries().toList().find { it.isDirectory && it.name.endsWith("/$locationCleaned") && it.isDirectChildOf(parentEntry) }
}

fun JarFile.directChildrenOf(parentEntry: JarEntry): List<JarEntry> {
    return this.entries().toList().filter { it.isDirectChildOf(parentEntry) }
}

fun JarEntry.isDirectChildOf(parentEntry: JarEntry): Boolean {
    return this.name.startsWith(parentEntry.name) && this.level() == (parentEntry.level() + 1)
}

fun JarEntry.level(): Int = this.name.split("/").size - if (this.name.endsWith("/")) 1 else 0

fun JarFile.parent(entry: JarEntry): JarEntry? {
    val index = entry.name.lastIndexOf('/')
    if (index == -1) {
        return null
    }
    return this.getJarEntry(entry.name.substring(0, index))
}
