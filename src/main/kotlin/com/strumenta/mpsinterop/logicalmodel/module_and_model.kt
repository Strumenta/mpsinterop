package com.strumenta.mpsinterop.logicalmodel

import java.util.*

open class SModelId {
    companion object {
        fun regular(uuid: UUID) : SModelId = RegularSModelId(uuid)
        fun foreign(id: String) : SModelId {
            TODO()
        }
    }
}

internal class RegularSModelId(val uuid: UUID) : SModelId()

class IntegerSModelId(val id: Int) : SModelId() {

}

open class ModuleId {
    companion object {
        fun regular(uuid: UUID) : ModuleId = RegularModuleId(uuid)
        fun foreign(id: String) : ModuleId {
            TODO()
        }
    }
}

internal class RegularModuleId(val uuid: UUID) : ModuleId()

data class SModelReference(val moduleRef : SModuleReference?,
                           val id: SModelId, val name: String)

data class SModuleReference(val name: String, val id: ModuleId)

class Model(val name: String) {
    private val roots = LinkedList<SNode>()

    val numberOfRoots: Int
        get() = this.roots.size

    fun addRoot(root: SNode) {
        if (!root.root) {
            throw IllegalArgumentException("The given node is not a root")
        }
        roots.add(root)
    }

    fun onRoots(op: (SNode) -> Unit) {
        roots.forEach { op(it) }
    }

    fun onRoots(concept: SConcept, op: (SNode) -> Unit) {
        roots.filter { it.concept == concept }.forEach { op(it) }
    }

    fun getRootByName(name: String): SNode {
        return roots.find { it.name == name }!!
    }
}