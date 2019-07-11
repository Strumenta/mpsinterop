package com.strumenta.mpsinterop.loading

import com.strumenta.mpsinterop.physicalmodel.PhysicalConcept
import com.strumenta.mpsinterop.physicalmodel.PhysicalModel
import com.strumenta.mpsinterop.physicalmodel.PhysicalNode
import java.io.InputStream
import java.util.*

//interface LanguageResolver {
//    fun physicalConceptByName(name: String): PhysicalConcept?
//    fun conceptDeclarationByName(name: String): PhysicalNode?
//}
//
//class LanguageResolverCollector : LanguageResolver {
//
//    private val physicalModels = LinkedList<PhysicalModel>()
//
//    fun loadJar(inputStream: InputStream) {
//        val languageRegistry = LanguageRegistry()
//        val models = languageRegistry.loadJar(inputStream)
//        physicalModels.addAll(models)
//    }
//
//    fun addPhysicalModel(physicalModel: PhysicalModel) {
//        physicalModels.add(physicalModel)
//    }
//
//    override fun physicalConceptByName(name: String): PhysicalConcept? {
//        return physicalModels.map { it.physicalConceptByName(name) }.find { it != null }
//    }
//
//    override fun conceptDeclarationByName(name: String): PhysicalNode? {
//        return physicalModels.map { it.conceptDeclarationByName(name) }.find { it != null }
//    }
//
//}