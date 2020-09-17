package com.strumenta.mps.exporter

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.strumenta.mps.Model
import com.strumenta.mps.MpsProject
import com.strumenta.mps.toJsonString
import java.io.File
import kotlin.system.exitProcess

class ExporterTool : CliktCommand() {
    val destinationPath by option("-d", "--destination", help = "destination for generated files")
    val projectPath by argument("projectPath")
    val models by argument("models", "models to be exported").multiple(required = false)

    fun exportModel(model: Model, outputFile: File) {
        outputFile.writeText(model.toJsonString())
    }

    override fun run() {
        val projectDir = File(projectPath)
        if (!projectDir.exists() || !projectDir.isDirectory) {
            System.err.println("not a valid project dir: $projectPath")
            exitProcess(-1)
        }
        if (destinationPath != null && (!File(destinationPath).exists() || !File(destinationPath).isDirectory)) {
            System.err.println("not a valid destination path: $destinationPath")
            exitProcess(-1)
        }
        val mpsProject = MpsProject(projectDir)
        if (models.isEmpty()) {
            println("no models specified, terminating")
            return
        }
        models.forEach { modelName ->
            val model = mpsProject.findModel(modelName)
            if (model == null) {
                System.err.println("model not found in project: $model")
                exitProcess(-1)
            } else {
                val outputFile = if (destinationPath == null) File("${model.name}.json") else File(File(destinationPath), "${model.name}.json")
                exportModel(model, outputFile)
                println("model ${model.name} exported to ${outputFile.absolutePath}")
            }
        }
    }
}

fun main(args: Array<String>) {
    ExporterTool().main(args)
}
