
import com.strumenta.mpsinterop.registries.Indexer
import com.strumenta.mpsinterop.registries.PhysicalModelsRegistry
import kotlin.test.Test
import kotlin.test.assertEquals

// Perhaps not so useful
class LoadingPhysicalModelsFromJar {

    @Test
    fun langCoreJarIsLoaded() {
        val inputStream = LoadingPhysicalModelsFromJar::class.java.getResourceAsStream("/jetbrains.mps.lang.core-src.jar")
        val physicalModelsRegistry = Indexer()
        physicalModelsRegistry.indexJar(inputStream)
        val structure = physicalModelsRegistry.locateModel("jetbrains.mps.lang.core.structure")!!
    }

    @Test
    fun loadStructureJarIsLoaded() {
        val inputStream = LoadingPhysicalModelsFromJar::class.java.getResourceAsStream("/jetbrains.mps.lang.structure-src.jar")
        val physicalModelsRegistry = Indexer()
        physicalModelsRegistry.indexJar(inputStream)
        val structure = physicalModelsRegistry.locateModel("jetbrains.mps.lang.structure.structure")!!
    }

}
