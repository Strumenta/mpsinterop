package com.strumenta.mps.exporter

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.strumenta.mps.*
import com.strumenta.mps.organization.Model
import com.strumenta.mps.organization.MpsInstallation
import com.strumenta.mps.organization.MpsProject
import java.io.File
import java.lang.RuntimeException
import kotlin.system.exitProcess

class ExporterTool : CliktCommand() {
    val installation by option("-i", "--installation")
    val destinationPath by option("-d", "--destination", help = "destination for generated files")
    val projectPath by argument("projectPath")
    val models by argument("models", "models to be exported").multiple(required = false)

    private fun serializeModel(model: Model, outputFile: File) {
        try {
            outputFile.writeText(model.toJsonString())
        } catch (t: Throwable) {
            throw RuntimeException("Issue serializing model ${model.name} loaded from ${model.source}", t)
        }
    }

    private fun modulesContainer(): ModulesContainer {
        var partialModulesContainer: MpsInstallation? = null
        if (installation != null) {
            val installationDir = File(installation)
            if (!installationDir.exists() || !installationDir.isDirectory) {
                System.err.println("not a valid installation dir: $installation")
                exitProcess(-1)
            }
            partialModulesContainer = MpsInstallation(installationDir)
        }
        val projectDir = File(projectPath)
        if (!projectDir.exists() || !projectDir.isDirectory) {
            System.err.println("not a valid project dir: $projectPath")
            exitProcess(-1)
        }
        return MpsProject(projectDir, partialModulesContainer)
    }

    private fun exportModel(modulesContainer: ModulesContainer, modelName: String) {
        val model = modulesContainer.findModel(modelName)
        if (model == null) {
            System.err.println("model not found in project: $model")
            exitProcess(-1)
        } else {
            val simpleName = "${model.uuid}-${model.name}.json"
            val outputFile = if (destinationPath == null) File(simpleName) else File(File(destinationPath), simpleName)
            serializeModel(model, outputFile)
            println("model ${model.name} exported to ${outputFile.absolutePath}")
        }
    }

    override fun run() {
        val modulesContainer = modulesContainer()
        if (destinationPath != null && (!File(destinationPath).exists() || !File(destinationPath).isDirectory)) {
            System.err.println("not a valid destination path: $destinationPath")
            exitProcess(-1)
        }
        if (models.isEmpty()) {
            println("no models specified, terminating")
            return
        }
        if (models.contains("all")) {
            val failed = mutableListOf<String>()
            var succedeed = 0
            if (models.size != 1) {
                System.err.println("all should not be used with other models")
                exitProcess(-1)
            }
            modulesContainer.listModels().forEach {
                try {
                    exportModel(modulesContainer, it.name)
                    succedeed++
                } catch (t: Throwable) {
                    failed.add(it.name)
                }
            }
            println("Serialized successfully $succedeed models. Failed ${failed.size}")
            failed.forEach {
                println("FAILED $it")
            }
        } else {
            models.forEach { modelName ->
                exportModel(modulesContainer, modelName)
            }
        }
    }
}

fun main(args: Array<String>) {
    ExporterTool().main(args)
}
