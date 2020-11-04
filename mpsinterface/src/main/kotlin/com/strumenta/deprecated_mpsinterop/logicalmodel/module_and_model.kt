package com.strumenta.deprecated_mpsinterop.logicalmodel

import java.util.LinkedList
import java.util.UUID

enum class ModuleType {
    LANGUAGE,
    MODULE
}

open class SModelId {
    companion object {
        fun regular(uuid: UUID): SModelId = RegularSModelId(uuid)
        fun foreign(id: String): SModelId = ForeignSModelId(id)
    }
    fun hasUUID() = uuid() != null
    open fun uuid(): UUID? {
        throw UnsupportedOperationException("No UUID for ${this.javaClass.canonicalName}")
    }
    open fun toSerializedString(): String {
        return uuid().toString() ?: throw java.lang.UnsupportedOperationException()
    }
}

internal class RegularSModelId(val uuid: UUID) : SModelId() {
    override fun uuid() = uuid
}

internal class ForeignSModelId(val id: String) : SModelId() {
    override fun uuid(): UUID? {
        return null
    }

    override fun toSerializedString(): String {
        return id
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
        if (!root.isRoot) {
            throw IllegalArgumentException("The given node is not a isRoot")
        }
        roots.add(root)
        root.modelOfWhichIsRoot = this
    }

    fun removeRoot(root: Node) {
        if (root !in roots) {
            throw IllegalArgumentException("The given node is not a root of this model")
        }
        roots.remove(root)
        root.modelOfWhichIsRoot = null
    }

    fun onRoots(op: (Node) -> Unit) {
        roots.forEach { op(it) }
    }

    fun onRoots(concept: Concept, op: (Node) -> Unit) {
        roots.filter { it.concept == concept }.forEach { op(it) }
    }

    fun getRootByName(name: String): Node {
        return roots.find { it.name == name } ?: throw RuntimeException(
            "No isRoot found with name $name. Roots have these names: ${roots.map {
                it.name
            }.joinToString(", ")}"
        )
    }
}
