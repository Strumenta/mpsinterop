package com.strumenta.mpsinterop.registries

import com.strumenta.mpsinterop.logicalmodel.Language
import com.strumenta.mpsinterop.physicalmodel.PhysicalModel
import com.strumenta.mpsinterop.physicalmodel.PhysicalNode

class PhysicalModelsRegistry {

    fun findConceptDeclaration(name: String): PhysicalNode? {
        TODO()
    }

    companion object {
        val DEFAULT = PhysicalModelsRegistry()
    }

//    operator fun get(name: String) {
//        TODO()
//    }
//
//    operator fun set(name: String, language: Language) {
//        TODO()
//    }
}