package com.strumenta.deprecated_mpsinterop

import com.strumenta.deprecated_mpsinterop.utils.loadDocument
import com.strumenta.deprecated_mpsinterop.utils.processAllNodes
import com.strumenta.deprecated_mpsinterop.utils.processChildren
import org.w3c.dom.Document
import java.io.InputStream
import java.lang.RuntimeException
import java.util.* // ktlint-disable

data class LanguageDep(val name: String, val uuid: UUID, val version: Int) {
    init {
        require(version >= -1)
    }
}

class Dependencies {
    val languages = mutableListOf<LanguageDep>()
}

fun calculateDependenciesForModel(document: Document): Dependencies {
    val deps = Dependencies()
    document.documentElement.processChildren(
        "languages",
        { languages ->
            languages.processChildren(
                "use",
                { use ->
                    val uuid = UUID.fromString(use.getAttribute("id"))
                    val name = use.getAttribute("name")
                    val version = use.getAttribute("version").toInt()
                    deps.languages.add(LanguageDep(name, uuid, version))
                }
            )
        }
    )
    return deps
}

fun calculateDependenciesForSolution(indexElement: Indexer.IndexElement): Dependencies {
    return calculateDependenciesForSolution(indexElement.inputStream())
}

fun calculateDependenciesForLanguage(indexElement: Indexer.IndexElement): Dependencies {
    try {
        return calculateDependenciesForLanguage(indexElement.inputStream())
    } catch (t: Throwable) {
        throw RuntimeException("Issue calculating dependencies for $indexElement", t)
    }
}

fun calculateDependenciesForSolution(inputStream: InputStream): Dependencies {
    val deps = Dependencies()
    val doc = loadDocument(inputStream)
    doc.documentElement.processAllNodes("language") {
        var slang = it.getAttribute("slang")
        slang = slang.removePrefix("l:")
        val parts = slang.split(":")
        require(parts.size == 2)
        val version = it.getAttribute("version").toInt()
        require(version >= 0)
        deps.languages.add(LanguageDep(parts[1], UUID.fromString(parts[0]), version))
    }
    return deps
}

fun calculateDependenciesForLanguage(inputStream: InputStream): Dependencies {
    val deps = Dependencies()
    val doc = loadDocument(inputStream)
    val thisUUID = UUID.fromString(doc.documentElement.getAttribute("uuid"))
    val thisName = doc.documentElement.getAttribute("namespace")
    val thisLanguageVersion = doc.documentElement.getAttribute("languageVersion").toInt()
    val languageVersions = mutableListOf<LanguageDep>()
    doc.documentElement.processChildren("languageVersions") {
        it.processAllNodes("language") {
            var slang = it.getAttribute("slang")
            slang = slang.removePrefix("l:")
            val parts = slang.split(":")
            require(parts.size == 2)
            val version = it.getAttribute("version").toInt()
            require(version >= -1)
            val name = parts[1]
            val uuid = UUID.fromString(parts[0])
            if (thisUUID == uuid) {
                require(thisName == name)
                require(thisLanguageVersion == version)
                // we do not consider dependencies on the language itself
            } else {
                languageVersions.add(LanguageDep(name, uuid, version))
            }
        }
    }
    doc.documentElement.processChildren("dependencyVersions") {
        it.processAllNodes("module") {
            var reference = it.getAttribute("reference")

            val uuid = UUID.fromString(reference.substring(0, 36))
            reference = reference.substring(36)
            require(reference.startsWith("("))
            require(reference.endsWith(")"))
            val name = reference.substring(1, reference.length - 1)
            val version = it.getAttribute("version").toInt()
            require(version >= -1)
            val isLanguage = languageVersions.any { it.uuid == uuid }
            if (isLanguage) {
                if (thisUUID == uuid) {
                    require(thisName == name)
                    require(thisLanguageVersion == version)
                    // we do not consider dependencies on the language itself
                } else {
                    deps.languages.add(LanguageDep(name, uuid, version))
                }
            }
        }
    }
    return deps
}