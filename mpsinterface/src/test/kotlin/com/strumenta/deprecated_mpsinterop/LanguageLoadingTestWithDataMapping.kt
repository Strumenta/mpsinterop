package com.strumenta.deprecated_mpsinterop

import com.strumenta.deprecated_mpsinterop.loading.*
import com.strumenta.deprecated_mpsinterop.registries.LanguageRegistry
import org.junit.Test
import java.io.File
import java.io.FileInputStream
import java.lang.RuntimeException

class LanguageLoadingTestWithDataMapping {

    @Test
    fun loadDataMappingLanguage() {
//        val indexer = Indexer()
//        indexer.justPrinting = true
//        indexer.explore(File("/Applications/MPS2019.1.4.app/Contents/languages/languageDesign/"))
//        indexer.explore(File("/Users/federico/repos/SigiDsl/artifacts"))
//        val uuid = UUID.fromString("00000000-0000-4000-0000-011c895902ca")
//        val l1 = indexer.locateLanguage(uuid)
//        val m1 = indexer.locateModel(uuid)
//
        val lr = LanguageRegistry()

        loadBaseStuff(lr)

        val module = lr.loadMplFile(FileInputStream(File("/Users/federico/repos/SigiDsl/languages/gescomplus.datamapping/gescomplus.datamapping.mpl")))
        val structureModel = lr.loadMpsFile(FileInputStream(File("/Users/federico/repos/SigiDsl/languages/gescomplus.datamapping/models/gescomplus.datamapping.structure.mps")))
        val converter = PhysicalToLogicalConverter(lr)
        val logicalModel = converter.toLogical(structureModel)
//        lr.loadLanguageFromModule(module)
//        lr.loadLanguageFromModel(structureModel)
        lr.loadLanguageFromModuleAndModel(module, structureModel)

        loadGescomplusLanguage(lr,"gescomplus.dsl.core")

        val physicalModel = loadMpsModel(FileInputStream(File("/Users/federico/repos/SigiDsl/solutions/gescomplus.dsl.sandbox/models/gescomplus.dsl.mapping.mps")))
        val lm = converter.toLogical(physicalModel)
        println("FOO")
    }

    private fun loadBaseStuff(languageRegistry: LanguageRegistry) {
        loadBaseLanguage(languageRegistry, "jetbrains.mps.lang.core")
        loadBaseLanguage(languageRegistry, "jetbrains.mps.lang.traceable")
        loadBaseLanguage(languageRegistry, "jetbrains.mps.baseLanguage")
        loadBaseLanguage(languageRegistry, "jetbrains.mps.lang.structure")
        loadMbeddrLanguage(languageRegistry, "de.slisson.mps.richtext")
        loadMbeddrLanguage(languageRegistry, "com.mbeddr.mpsutil.plantuml.node")
        loadMbeddrLanguage(languageRegistry, "com.mbeddr.mpsutil.jung")
        loadMbeddrLanguage(languageRegistry, "com.mbeddr.core.base")
        loadMbeddrLanguage(languageRegistry, "com.mbeddr.mpsutil.interpreter")
        loadIets3Language(languageRegistry, "org.iets3.analysis.base")
        loadIets3Language(languageRegistry, "org.iets3.core.base")
        loadIets3Language(languageRegistry, "org.iets3.core.expr.base")
        loadMbeddrLanguage(languageRegistry, "com.mbeddr.mpsutil.varscope")
        loadIets3Language(languageRegistry, "org.iets3.core.expr.lambda")
        loadIets3Language(languageRegistry, "org.iets3.core.expr.path")
        loadIets3Language(languageRegistry, "org.iets3.core.expr.collections")
        loadIets3Language(languageRegistry, "org.iets3.core.expr.toplevel")
    }

    private fun loadBaseLanguage(languageRegistry: LanguageRegistry, name: String) {
        var f = File("/Applications/MPS2019.1.4.app/Contents/languages/languageDesign/$name-src.jar")
        if (!f.exists()) {
            f = File("/Applications/MPS2019.1.4.app/Contents/languages/baseLanguage/$name-src.jar")
        }
        languageRegistry.loadLanguageFromJar(FileInputStream(f))
    }

    private fun loadGescomplusLanguage(languageRegistry: LanguageRegistry, name: String) {
        val d = File("/Users/federico/repos/SigiDsl/languages/$name")
        languageRegistry.loadLanguageFromDirectory(d)
    }

    private fun loadMbeddrLanguage(languageRegistry: LanguageRegistry, name: String) {
        if (!lookForLanguageIn(languageRegistry, name, File("/Users/federico/repos/SigiDsl/artifacts/com.mbeddr.platform"))) {
            throw RuntimeException("Language not found $name")
        }
    }

    private fun lookForLanguageIn(languageRegistry: LanguageRegistry, name: String, dir: File) : Boolean {
        dir.listFiles().forEach {
            if (it.isFile && it.name == "$name-src.jar") {
                languageRegistry.loadLanguageFromJar(FileInputStream(it))
                return true
            }
            if (it.isDirectory) {
                val res = lookForLanguageIn(languageRegistry, name, it)
                if (res) {
                    return true
                }
            }
        }
        return false
    }

    private fun loadIets3Language(languageRegistry: LanguageRegistry, name: String) {
        var f = File("/Users/federico/repos/SigiDsl/artifacts/org.iets3.opensource/org.iets3.core.os/languages/iets3.core.os/$name-src.jar")
        languageRegistry.loadLanguageFromJar(FileInputStream(f))
    }
}