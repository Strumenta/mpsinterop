package com.strumenta.mpsinterop.logicalmodel

import java.lang.RuntimeException
import java.lang.UnsupportedOperationException
import java.util.*

open class SModelId {
    companion object {
        fun regular(uuid: UUID): SModelId = RegularSModelId(uuid)
        fun foreign(id: String): SModelId = ForeignSModelId(id)
    }
    open fun uuid(): UUID {
        throw UnsupportedOperationException("No UUID for ${this.javaClass.canonicalName}")
    }
}

internal class RegularSModelId(val uuid: UUID) : SModelId() {
    override fun uuid() = uuid
}

internal class ForeignSModelId(val id: String) : SModelId() {
    override fun uuid(): UUID {
        TODO("FOREIGN SModelID $id")
    }
}

class IntegerSModelId(val id: Int) : SModelId()

open class ModuleId {
    companion object {
        fun regular(uuid: UUID): ModuleId = RegularModuleId(uuid)
        fun foreign(id: String): ModuleId {
            TODO()
        }
    }
}

internal class RegularModuleId(val uuid: UUID) : ModuleId()

data class SModelReference(
    val moduleRef: SModuleReference?,
    val id: SModelId,
    val name: String
)

data class SModuleReference(val name: String, val id: ModuleId)

class Model(val name: String) {
    private val roots = LinkedList<Node>()

    val numberOfRoots: Int
        get() = this.roots.size

    fun addRoot(root: Node) {
        if (!root.root) {
            throw IllegalArgumentException("The given node is not a root")
        }
        roots.add(root)
    }

    fun onRoots(op: (Node) -> Unit) {
        roots.forEach { op(it) }
    }

    fun onRoots(concept: Concept, op: (Node) -> Unit) {
        roots.filter { it.concept == concept }.forEach { op(it) }
    }

    fun getRootByName(name: String): Node {
        return roots.find { it.name == name } ?: throw RuntimeException(
                "No root found with name $name. Roots have these names: ${roots.map { it.name
        }.joinToString(", ")}")
    }
}
