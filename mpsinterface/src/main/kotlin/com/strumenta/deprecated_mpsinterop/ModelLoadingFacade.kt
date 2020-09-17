package com.strumenta.deprecated_mpsinterop

import com.strumenta.deprecated_mpsinterop.loading.* // ktlint-disable
import com.strumenta.deprecated_mpsinterop.logicalmodel.Model
import com.strumenta.deprecated_mpsinterop.registries.LanguageRegistry
import java.io.File
import java.lang.RuntimeException
import java.util.* // ktlint-disable

class ModelLoadingFacade(mpsInstallation: File, projectPath: File) {

    private val languageRegistry = LanguageRegistry()
    protected val converter = PhysicalToLogicalConverter(languageRegistry)
    private val physicalToLogicalConverter = PhysicalToLogicalConverter(languageRegistry)

    private val indexer: Indexer = Indexer()

    init {
        indexer.indexMpsDirectory(mpsInstallation)
        indexer.indexMpsProject(projectPath)
    }

    fun loadModel(moduleName: String, modelName: String): Model {
        val solutionIndexElement = indexer.findSolutionByName("com.strumenta.mpsserver.server")
        // first of all we need to find where the model is
        try {
            val modelDocument = solutionIndexElement.findModel(modelName)
            // then we need to load all languages
            val deps = calculateDependenciesForModel(modelDocument)
            ensureLanguagesAreLoaded(deps)
            val physicalModel = com.strumenta.deprecated_mpsinterop.loading.loadModel(modelDocument)
            return physicalToLogicalConverter.toLogical(physicalModel)
        } catch (e: Throwable) {
            throw RuntimeException("Issue loading model $moduleName.$modelName", e)
        }
    }

    private fun ensureLanguagesAreLoaded(deps: Dependencies, loading: Set<UUID> = emptySet()) {
        deps.languages.forEach {
            if (!loading.contains(it.uuid)) {
                if (it.name == "jetbrains.mps.lang.core" || it.name == "jetbrains.mps.lang.structure") {
                    // these languages have to be bootstrapped
                } else {
                    if (!languageRegistry.knowsLanguageUUID(it.uuid)) {
                        println("ensureLanguagesAreLoaded $it")
                        val el = indexer.findElement(it.uuid)
                        if (el == null) {
                            throw RuntimeException("We do now know how to locate this language: $it")
                        } else {
                            val deps = calculateDependenciesForLanguage(el)
                            ensureLanguagesAreLoaded(deps, loading + setOf(it.uuid))
                            try {
                                val physicalLanguageModule = loadLanguage(el.source())
                                languageRegistry.loadLanguageFromModule(physicalLanguageModule)
                                require(languageRegistry.knowsLanguageUUID(it.uuid))
                            } catch (t: Throwable) {
                                throw RuntimeException("Issue loading element for $it from $el", t)
                            }
                        }
                    }
                }
            }
        }
    }

//    private var mpsDir : File? = null
//    private var projectDir : File? = null
//
//    private class ModulePlaceholder(moduleFile: File, val modelLoadingFacade: ModelLoadingFacade) : AbstractModulePlaceholder(moduleFile) {
//
//        private class ModelPlaceholder(val modelFile: File, val modelLoadingFacade: ModelLoadingFacade) {
//            fun load(): Model {
//                if (model == null) {
//                    try {
//                        ensureLanguagesAreLoaded()
//                    } catch (e : Exception) {
//                        throw RuntimeException("Failed to load languages used by model ${physicalModel.name}", e)
//                    }
//                    ensureImportsAreLoaded()
//                    val lm = modelLoadingFacade.converter.toLogical(physicalModel)
//                    return lm
//                } else {
//                    return model!!
//                }
//            }
//
//            private fun ensureImportsAreLoaded() {
//                // something to do?
//            }
//
//            private fun ensureLanguagesAreLoaded() {
//                physicalModel.explicitLanguageUses.forEach {
//                    if (!modelLoadingFacade.converter.languageRegistry.knowsLanguageUUID(it.uuid)) {
//                        modelLoadingFacade.loadLanguage(it)
//                        require(modelLoadingFacade.converter.languageRegistry.knowsLanguageUUID(it.uuid))
//                    }
//                }
//            }
//
//            private val physicalModel : PhysicalModel = loadMpsModel(modelFile)
//            private var model : Model? = null
//            val name : String
//                get() = physicalModel.name
//        }
//
//        private var models : MutableList<ModelPlaceholder>? = null
//
//        init {
//            val doc = loadDocument(FileInputStream(moduleFile))
//            name = doc.documentElement.getAttribute("name")
//            uuid = UUID.fromString(doc.documentElement.getAttribute("uuid"))
//            type = ModuleType.MODULE
//
//
//        }
//
//        private fun loadModelsDir(modelsDir: File) {
//            require(modelsDir.exists())
//            require(modelsDir.isDirectory)
//
//            modelsDir.listFiles().forEach {
//                models!!.add(ModelPlaceholder(it, modelLoadingFacade))
//            }
//        }
//
//        private fun loadModelsTag(modelsTag: Element) {
//            val moduleBaseDir = moduleFile.parentFile
//            modelsTag.processChildren {
//                when (it.tagName) {
//                    "modelRoot" -> {
//                        val contentPath = it.getAttribute("contentPath").replace("\${module}", moduleBaseDir.absolutePath)
//                        val type = it.getAttribute("type")
//                        it.processChildren("sourceRoot") {
//                            val location = it.getAttribute("location")
//                            val modelsDir = File(contentPath, location)
//                            loadModelsDir(modelsDir)
//                        }
//                    }
//                    else -> TODO(it.tagName)
//                }
//            }
//        }
//
//        private fun ensureModelsAreLoaded() {
//            if (models == null) {
//                models = mutableListOf()
//                val doc = loadDocument(FileInputStream(moduleFile))
//                doc.documentElement.processChildren("models") {
//                    loadModelsTag(it)
//                }
//            }
//        }
//
//        fun loadModel(modelName: String): Model {
//            ensureModelsAreLoaded()
//            return models!!.find { it.name == modelName || it.name == "${this.name}.${it.name}" }?.load() ?: throw IllegalArgumentException("Model not found: $modelName")
//        }
//    }
//
//    private fun noActionsDependenciesLoader() : DependenciesLoader {
//        return object : DependenciesLoader {
//            override fun loadDependencies(jarData: JarData) {
//                // nothing to do here
//            }
//        }
//    }
//
//    private fun dependenciesLoader() : DependenciesLoader {
//        return object : DependenciesLoader {
//            override fun loadDependencies(jarData: JarData) {
//                jarData.modules.forEach {
//                    val physicalModule = it.second
//                    it.second.usedLanguages.forEach {
//                        ensureLanguageIsLoaded(it)
//                    }
//                    it.second.dependencies.forEach {
//                        ensureDependencyIsLoaded(it)
//                    }
//                }
//            }
//        }
//    }
//
//    private fun loadLanguage(explicitLanguageUse: PhysicalModel.ExplicitLanguageUse) {
//        if (languagesToJars.containsKey(explicitLanguageUse.name)) {
//            converter.languageRegistry.loadLanguageFromJar(FileInputStream(languagesToJars[explicitLanguageUse.name]), dependenciesLoader())
//        } else {
//            throw IllegalArgumentException("No JAR to load this language from: $explicitLanguageUse")
//        }
//    }
//
//    private fun loadLanguage(languageName: String) {
//        if (languagesToJars.containsKey(languageName)) {
//            converter.languageRegistry.loadLanguageFromJar(FileInputStream(languagesToJars[languageName]), dependenciesLoader())
//        } else {
//            throw IllegalArgumentException("No JAR to load this language from: $languageName")
//        }
//    }
//
//    private fun ensureLanguageIsLoaded(languageName: String) {
//        if (!languageRegistry.knowsLanguageName(languageName)) {
//            if (languagesToJars.containsKey(languageName)) {
//                val jarFile = languagesToJars[languageName]!!
//                try {
//                    languageRegistry.loadLanguageFromJar(FileInputStream(jarFile))
//                } catch (e : Exception) {
//                    throw RuntimeException("Failed to load language from JAR $jarFile", e)
//                }
//                require(languageRegistry.knowsLanguageName(languageName))
//            } else {
//                throw IllegalArgumentException("We do not know how to load language $languageName")
//            }
//        }
//    }
//
//    private fun ensureLanguageIsLoaded(dependency: PhysicalModule.UsedLanguage) {
//        if (!languageRegistry.knowsLanguageUUID(dependency.uuid)) {
//            if (languagesToJars.containsKey(dependency.name)) {
//                val jarFile = languagesToJars[dependency.name]!!
//                try {
//                    languageRegistry.loadLanguageFromJar(FileInputStream(jarFile), dependenciesLoader())
//                } catch (e : Exception) {
//                    throw RuntimeException("Failed to load language from JAR $jarFile", e)
//                }
//                require(languageRegistry.knowsLanguageUUID(dependency.uuid))
//            } else {
//                throw IllegalArgumentException("We do not know how to load language $dependency")
//            }
//        }
//    }
//
//    private fun ensureDependencyIsLoaded(dependency: PhysicalModule.Dependency) {
//        if (!modulesToJars.containsKey(dependency.name)) {
//            throw IllegalArgumentException("We do not know where the module is: $dependency")
//        }
//        val physicalModule = modulesToJars[dependency.name]!!
//        physicalModule.usedLanguages.forEach { ensureLanguageIsLoaded(it) }
//        physicalModule.dependencies.forEach { ensureDependencyIsLoaded(it) }
//        //TODO("Not yet implemented $dependency")
//    }
//
//    private class LanguagePlaceholder(moduleFile: File) : AbstractModulePlaceholder(moduleFile) {
//        init {
//            val doc = loadDocument(FileInputStream(moduleFile))
//            name = doc.documentElement.getAttribute("namespace")
//            uuid = UUID.fromString(doc.documentElement.getAttribute("uuid"))
//            type = ModuleType.LANGUAGE
//        }
//    }
//
//    private abstract class AbstractModulePlaceholder(val moduleFile: File) {
//        lateinit var type : ModuleType
//        lateinit var name: String
//        lateinit var uuid: UUID
//
//        companion object {
//            fun load(moduleFile: File, modelLoadingFacade: ModelLoadingFacade): AbstractModulePlaceholder {
//                when (moduleFile.extension) {
//                    "mpl" -> {
//                        return LanguagePlaceholder(moduleFile)
//                    }
//                    "msd" -> {
//                        return ModulePlaceholder(moduleFile, modelLoadingFacade)
//                    }
//                    else -> TODO(moduleFile.extension)
//                }
//            }
//        }
//
//    }
//
//    private val modules = mutableListOf<AbstractModulePlaceholder>()
//    private val languagesToJars = mutableMapOf<String, File>()
//    private val modulesToJars = mutableMapOf<String, PhysicalModule>()
//
//    init {
//        indexMpsJars(mpsInstallation)
//        ensureLanguageIsLoaded("jetbrains.mps.lang.core")
//        loadProject(projectPath)
//    }
//
//    private fun processFile(it: File) {
//        if (it.isFile && it.name.endsWith("-src.jar")) {
//            val languageName = it.name.removeSuffix("-src.jar")
//            languagesToJars[languageName] = it
//        } else if (it.isFile && it.name.endsWith(".jar")) {
//            val srcJar = it.name.removeSuffix(".jar") + "-src.jar"
//            if (!File(it.parentFile, srcJar).exists()) {
//                //println(it.absolutePath)
//                this.converter.languageRegistry.loadElementFromJar(FileInputStream(it), object : DependenciesLoader {
//                    override fun loadDependencies(jarData: JarData) {
//                        jarData.modules.forEach {
//                            modulesToJars[it.second.name] = it.second
//                        }
//                    }
//
//                })
//            }
//        }
//    }
//
//    private fun indexMpsJars(mpsInstallation: File) {
//        val languagesDir = File(mpsInstallation, "languages")
//        languagesDir.walkTopDown().forEach {
//            processFile(it)
//        }
//        val pluginsDir = File(mpsInstallation, "plugins")
//        pluginsDir.walkTopDown().forEach {
//            processFile(it)
//        }
//    }
//
//    private fun loadProject(projectPath: File) {
//        if (projectPath.name == ".mps") {
//            loadProjectMpsDir(projectPath)
//        } else {
//            val cs = projectPath.listFiles { c -> c.isDirectory && c.name == ".mps" }
//            if (projectPath.isDirectory && cs.isNotEmpty()) {
//                assert(cs.size == 1)
//                loadProjectMpsDir(cs[0])
//            } else {
//                throw IllegalArgumentException("Not a valid project path: $projectPath")
//            }
//        }
//    }
//
//    private fun loadProjectMpsDir(mpsDir : File) {
//        this.projectDir = mpsDir.parentFile
//        this.mpsDir = mpsDir
//        val modulesFile = File(mpsDir, "modules.xml")
//        loadModules(modulesFile)
//    }
//
//    private fun loadModules(modulesFile: File) {
//        require(modulesFile.exists())
//        require(modulesFile.isFile)
//        val doc = loadDocument(FileInputStream(modulesFile))
//        doc.documentElement.processAllNodes("modulePath") {
//            val path = it.getAttribute("path").replace("\$PROJECT_DIR\$", projectDir!!.absolutePath)
//            modules.add(AbstractModulePlaceholder.load(File(path), this))
//        }
//    }
//
//    private fun findModule(moduleName: String) : ModulePlaceholder? {
//        return modules.filterIsInstance(ModulePlaceholder::class.java).find { it.name == moduleName }
//    }
//
//    fun loadModel(moduleName: String, modelName: String) : Model {
//        val module = findModule(moduleName) ?: throw IllegalArgumentException("Module not found: $moduleName")
//        return module.loadModel(modelName)
//    }
}
