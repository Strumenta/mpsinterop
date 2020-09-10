package com.strumenta.mpsinterop

import com.strumenta.mpsinterop.loading.loadMpsModel
import com.strumenta.mpsinterop.logicalmodel.Model
import com.strumenta.mpsinterop.logicalmodel.ModuleType
import com.strumenta.mpsinterop.physicalmodel.PhysicalModel
import com.strumenta.mpsinterop.registries.LanguageRegistry
import com.strumenta.mpsinterop.utils.loadDocument
import com.strumenta.mpsinterop.utils.processAllNodes
import com.strumenta.mpsinterop.utils.processChildren
import org.w3c.dom.Element
import java.io.File
import java.io.FileInputStream
import java.lang.IllegalArgumentException
import java.util.*

class ModelLoadingFacade(projectPath: File) {

    private val languageRegistry = LanguageRegistry()
    private var mpsDir : File? = null
    private var projectDir : File? = null

    private class ModulePlaceholder(moduleFile: File) : AbstractModulePlaceholder(moduleFile) {

        private class ModelPlaceholder(val modelFile: File) {
            fun load(): Model {
                if (model == null) {
                    ensureLangagesAreLoaded()
                    ensureImportsAreLoaded()
                    TODO()
                } else {
                    return model!!
                }
            }

            private fun ensureImportsAreLoaded() {
                // something to do?
            }

            private fun ensureLangagesAreLoaded() {
                // something to do?
            }

            private val physicalModel : PhysicalModel = loadMpsModel(modelFile)
            private var model : Model? = null
            val name : String
                get() = physicalModel.name
        }

        private var models : MutableList<ModelPlaceholder>? = null

        init {
            val doc = loadDocument(FileInputStream(moduleFile))
            name = doc.documentElement.getAttribute("name")
            uuid = UUID.fromString(doc.documentElement.getAttribute("uuid"))
            type = ModuleType.MODULE


        }

        private fun loadModelsDir(modelsDir: File) {
            require(modelsDir.exists())
            require(modelsDir.isDirectory)

            modelsDir.listFiles().forEach {
                models!!.add(ModelPlaceholder(it))
            }
        }

        private fun loadModelsTag(modelsTag: Element) {
            val moduleBaseDir = moduleFile.parentFile
            modelsTag.processChildren {
                when (it.tagName) {
                    "modelRoot" -> {
                        val contentPath = it.getAttribute("contentPath").replace("\${module}", moduleBaseDir.absolutePath)
                        val type = it.getAttribute("type")
                        it.processChildren("sourceRoot") {
                            val location = it.getAttribute("location")
                            val modelsDir = File(contentPath, location)
                            loadModelsDir(modelsDir)
                        }
                    }
                    else -> TODO(it.tagName)
                }
            }
        }

        private fun ensureModelsAreLoaded() {
            if (models == null) {
                models = mutableListOf()
                val doc = loadDocument(FileInputStream(moduleFile))
                doc.documentElement.processChildren("models") {
                    loadModelsTag(it)
                }
            }
        }

        fun loadModel(modelName: String): Model {
            ensureModelsAreLoaded()
            return models!!.find { it.name == modelName || it.name == "${this.name}.${it.name}" }?.load() ?: throw IllegalArgumentException("Model not found: $modelName")
        }
    }

    private class LanguagePlaceholder(moduleFile: File) : AbstractModulePlaceholder(moduleFile) {
        init {
            val doc = loadDocument(FileInputStream(moduleFile))
            name = doc.documentElement.getAttribute("namespace")
            uuid = UUID.fromString(doc.documentElement.getAttribute("uuid"))
            type = ModuleType.LANGUAGE
        }
    }

    private abstract class AbstractModulePlaceholder(val moduleFile: File) {
        lateinit var type : ModuleType
        lateinit var name: String
        lateinit var uuid: UUID

        companion object {
            fun load(moduleFile: File): AbstractModulePlaceholder {
                when (moduleFile.extension) {
                    "mpl" -> {
                        return LanguagePlaceholder(moduleFile)
                    }
                    "msd" -> {
                        return ModulePlaceholder(moduleFile)
                    }
                    else -> TODO(moduleFile.extension)
                }
            }
        }

    }

    private val modules = mutableListOf<AbstractModulePlaceholder>()

    init {
        loadProject(projectPath)
    }

    private fun loadProject(projectPath: File) {
        if (projectPath.name == ".mps") {
            loadProjectMpsDir(projectPath)
        } else {
            val cs = projectPath.listFiles { c -> c.isDirectory && c.name == ".mps" }
            if (projectPath.isDirectory && cs.isNotEmpty()) {
                assert(cs.size == 1)
                loadProjectMpsDir(cs[0])
            } else {
                throw IllegalArgumentException("Not a valid project path: $projectPath")
            }
        }
    }

    private fun loadProjectMpsDir(mpsDir : File) {
        this.projectDir = mpsDir.parentFile
        this.mpsDir = mpsDir
        val modulesFile = File(mpsDir, "modules.xml")
        loadModules(modulesFile)
    }

    private fun loadModules(modulesFile: File) {
        require(modulesFile.exists())
        require(modulesFile.isFile)
        val doc = loadDocument(FileInputStream(modulesFile))
        doc.documentElement.processAllNodes("modulePath") {
            val path = it.getAttribute("path").replace("\$PROJECT_DIR\$", projectDir!!.absolutePath)
            modules.add(AbstractModulePlaceholder.load(File(path)))
        }
    }

    private fun findModule(moduleName: String) : ModulePlaceholder? {
        return modules.filterIsInstance(ModulePlaceholder::class.java).find { it.name == moduleName }
    }

    fun loadModel(moduleName: String, modelName: String) : Model {
        val module = findModule(moduleName) ?: throw IllegalArgumentException("Module not found: $moduleName")
        return module.loadModel(modelName)
    }
}

