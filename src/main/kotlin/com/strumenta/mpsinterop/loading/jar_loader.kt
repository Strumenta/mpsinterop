package com.strumenta.mpsinterop.loading

import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.jar.JarFile
import java.io.FileOutputStream
import java.io.IOException

class LanguageRegistry {
    fun loadJar(file: File) {
        val jarFile = JarFile(file)
        val entries = jarFile.entries()
        while (entries.hasMoreElements()) {
            val entry = entries.nextElement()
            if (entry.name.endsWith(".mps")) {
                val model = loadMpsModel(jarFile.getInputStream(entry))
                println(model)
            }
        }
    }

    fun loadJar(inputStream: InputStream) = loadJar(dumpToTempFile(inputStream))
}


private fun dumpToTempFile(inputStream: InputStream): File {
    val tempFile = File.createTempFile("jar_file_from_input_stream", ".jar")
    tempFile.deleteOnExit()

    val buffer = ByteArray(8 * 1024)

    inputStream.use { inputStream ->
        FileOutputStream(tempFile).use { output ->
            var bytesRead: Int
            do {
                bytesRead = inputStream.read(buffer)
                if (bytesRead != -1) {
                    output.write(buffer, 0, bytesRead)
                }
            } while (bytesRead != -1)
        }
    }
    return tempFile
}
