package com.strumenta.mps

import com.strumenta.mps.utils.directChildrenOf
import java.io.File
import java.lang.IllegalArgumentException
import java.util.jar.JarFile

class MpsInstallation(val rootDir: File) : ModulesContainer() {
    private val solutions = mutableListOf<Solution>()
    private val languages = mutableListOf<Language>()

    override val modules: List<Module>
        get() = solutions + languages

    init {
        require(rootDir.exists())
        require(rootDir.isDirectory)
        loadModules(File(rootDir, "languages"))
        loadModules(File(rootDir, "plugins"))
    }

    private fun loadModuleFromJar(file: File) {
        val jarFile = JarFile(file)

        // JAR files other than the -src.jar files have the META-INF/module.xml
        // but to support them we cannot count on this

        val entry = jarFile.getJarEntry("META-INF/module.xml")
        // The module.xml is a simplified version of the msd or mpl file
        // we want to look for those files
        if (entry != null) {
            println("module found $file")
            val doc = loadDocument(jarFile.getInputStream(entry))
            val type = doc.documentElement.getAttribute("type")
            when (type) {
                "language" -> {
                    val mplEntry = jarFile.entries().toList().find { it.name.endsWith(".mpl") }
                    if (mplEntry == null) {
                        // nothing to load
                    } else {
                        languages.add(loadLanguage(JarEntrySource(file, mplEntry.name)))
                    }
                }
                "solution" -> {
                    val msdEntry = jarFile.entries().toList().find { it.name.endsWith(".msd") }
                    if (msdEntry == null) {
                        // nothing to load
                    } else {
                        solutions.add(loadSolution(JarEntrySource(file, msdEntry.name)))
                    }
                }
                "devkit" -> null // Ignore for now
                "generator" -> null // Ignore for now
                else -> throw IllegalArgumentException("Unknown module type: $type")
            }
        } else {
            val moduleEntry = jarFile.getJarEntry("module")
            if (moduleEntry != null) {
                jarFile.directChildrenOf(moduleEntry).forEach {
                    when {
                        it.name.endsWith(".mpl") -> {
                            languages.add(loadLanguage(JarEntrySource(file, it.name)))
                        }
                        it.name.endsWith(".msd") -> {
                            solutions.add(loadSolution(JarEntrySource(file, it.name)))
                        }
                    }
                }
            }
        }
    }

    private fun loadModules(modulesDir: File) {
        require(modulesDir.exists())
        require(modulesDir.isDirectory)
        modulesDir.listFiles().forEach {
            if (it.isDirectory) {
                loadModules(it)
            } else if (it.isFile) {
                when (it.extension) {
                    "jar" -> loadModuleFromJar(it)
                }
            }
        }
//        val doc = loadDocument(FileInputStream(modulesFile))
//        doc.documentElement.processAllNodes("modulePath") {
//            val path = File(it.getAttribute("path").replace("\$PROJECT_DIR\$", projectDir!!.absolutePath))
//            require(path.exists()) { "module $path does not exist" }
//            require(path.isFile())
//            when (path.extension) {
//                "mpl" -> loadLanguage(FileSource(path))
//                "msd" -> loadSolution(FileSource(path))
//                "devkit" -> null
//                else -> throw IllegalStateException("Unable to process module file $path")
//            }
//        }
    }
}
