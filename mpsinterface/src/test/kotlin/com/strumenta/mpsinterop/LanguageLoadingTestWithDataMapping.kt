package com.strumenta.mpsinterop

import com.strumenta.mpsinterop.loading.PhysicalToLogicalConverter
import com.strumenta.mpsinterop.loading.loadLanguageFromJar
import com.strumenta.mpsinterop.loading.loadMplFile
import com.strumenta.mpsinterop.loading.loadMpsFile
import com.strumenta.mpsinterop.registries.LanguageRegistry
import org.junit.Test
import java.io.File
import java.io.FileInputStream

class LanguageLoadingTestWithDataMapping {

    @Test
    fun loadDataMappingLanguage() {
        val lr = LanguageRegistry()

        loadBaseStuff(lr)

        val module = lr.loadMplFile(FileInputStream(File("/Users/federico/repos/SigiDsl/languages/gescomplus.datamapping/gescomplus.datamapping.mpl")))
        val structureModel = lr.loadMpsFile(FileInputStream(File("/Users/federico/repos/SigiDsl/languages/gescomplus.datamapping/models/gescomplus.datamapping.structure.mps")))
        val converter = PhysicalToLogicalConverter(lr)
        val logicalModel = converter.toLogical(structureModel)
        println("FOO")
    }

    private fun loadBaseStuff(languageRegistry: LanguageRegistry) {
        loadBaseLanguage(languageRegistry, "jetbrains.mps.lang.core")
        loadBaseLanguage(languageRegistry, "jetbrains.mps.lang.structure")
    }

    private fun loadBaseLanguage(languageRegistry: LanguageRegistry, name: String) {
        val f = File("/Applications/MPS2019.1.4.app/Contents/languages/languageDesign/$name-src.jar")
        languageRegistry.loadLanguageFromJar(FileInputStream(f))
    }
}