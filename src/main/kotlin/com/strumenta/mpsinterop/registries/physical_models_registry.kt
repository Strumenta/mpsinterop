package com.strumenta.mpsinterop.registries

import com.strumenta.mpsinterop.loading.ModelLocator
import com.strumenta.mpsinterop.physicalmodel.PhysicalLanguage
import com.strumenta.mpsinterop.physicalmodel.PhysicalModel
import com.strumenta.mpsinterop.physicalmodel.PhysicalNode
import com.strumenta.mpsinterop.physicalmodel.name
import java.util.*

class PhysicalModelsRegistry : ModelLocator {
    override fun locateModel(name: String): PhysicalModel? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun locateModel(modelUUID: UUID): PhysicalModel? {
        return models.find { it.uuid == modelUUID }
    }

    override fun locateLanguage(languageUUID: UUID): PhysicalLanguage? {
        return null
    }

    private val models = LinkedList<PhysicalModel>()

    fun findConceptDeclaration(name: String): PhysicalNode? {
        for (m: PhysicalModel in models) {
            val res = m.findConceptDeclaration(name)
            if (res != null) {
                return res
            }
        }
        return null
    }

    companion object {
        val DEFAULT = PhysicalModelsRegistry()
    }

    fun add(physicalModel: PhysicalModel) {
        models.add(physicalModel)
    }

//    operator fun get(name: String) {
//        TODO()
//    }
//
//    operator fun set(name: String, language: Language) {
//        TODO()
//    }
}

val CONCEPT_DECLARATION = "jetbrains.mps.lang.structure.structure.ConceptDeclaration"

fun PhysicalModel.findConceptDeclaration(name: String): PhysicalNode? {
    val conceptDeclaration = this.conceptByName(CONCEPT_DECLARATION) ?: return null
    return this.allRoots(conceptDeclaration).find {
        it.name() == name
    }
}
