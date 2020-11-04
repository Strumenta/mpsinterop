package com.strumenta.mps.binary

import com.strumenta.deprecated_mpsinterop.binary.ExternalReference
import com.strumenta.deprecated_mpsinterop.binary.ForeignReference
import com.strumenta.deprecated_mpsinterop.logicalmodel.* // ktlint-disable
import com.strumenta.deprecated_mpsinterop.physicalmodel.* // ktlint-disable
import java.io.IOException
import java.util.*

class ExternalReference(val modelUUID: UUID, val nodeId: String) : Reference

class ForeignReference(val modelUUID: String, val nodeId: String) : Reference

/**
 * Lightweight, straightforward binary serialization of individual [org.jetbrains.mps.openapi.model.SNode]s.
 * @see jetbrains.mps.persistence.binary.BareNodeWriter
 *
 * @author Artem Tikhomirov
 */
internal abstract class BareNodeReader(
    private val modelReference: SModelReference,
    protected val modelInputStream: ModelInputStream
) {

    companion object {
        private const val REF_THIS_MODEL: Byte = 17
        private const val REF_OTHER_MODEL: Byte = 18
    }

    fun readNodes(): List<PhysicalNode> {
        return readChildren(null)
    }

    private fun readChildren(parent: PhysicalNode?): List<PhysicalNode> {
        var size = modelInputStream.readInt()
        val rv = ArrayList<PhysicalNode>(size)
        while (size-- > 0) {
            rv.add(readNode(parent))
        }
        return rv
    }

    private fun readNode(parent: PhysicalNode?): PhysicalNode {
        val node = instantiate(parent)

        if (modelInputStream.readByte() != '{'.toByte()) {
            throw IOException("bad stream, no '{'")
        }

        readProperties(node)
        readUserObjects(node)
        readReferences(node)
        readChildren(node)

        if (modelInputStream.readByte() != '}'.toByte()) {
            throw IOException("bad stream, no '}'")
        }
        return node
    }

    protected abstract fun instantiate(parent: PhysicalNode?): PhysicalNode

    protected abstract fun readProperties(node: PhysicalNode)

    protected abstract fun readReferences(node: PhysicalNode)

    protected fun readReference(sref: PhysicalRelation, node: PhysicalNode): Reference {
        val kind = modelInputStream.readByte().toInt()
        assert(kind in 1..3)
        val targetNodeId = if (kind == 1) modelInputStream.readNodeId() else null
        val origin = if (kind == 3) TODO() else null
        val targetModelKind = modelInputStream.readByte().toInt()
        assert(targetModelKind == REF_OTHER_MODEL.toInt() || targetModelKind == REF_THIS_MODEL.toInt())
        val modelRef: SModelReference?
        var result: Reference? = null
        if (targetModelKind == REF_OTHER_MODEL.toInt()) {
            modelRef = modelInputStream.readModelReference()!!
            result = if (modelRef.id.hasUUID()) {
                ExternalReference(modelRef.id.uuid()!!, targetNodeId!!.toStringRepresentation())
            } else {
                ForeignReference(modelRef.id.toSerializedString(), targetNodeId!!.toStringRepresentation())
            }
        } else {
            modelRef = modelReference
            localNodeReferenceRead(targetNodeId)
        }
        val resolveInfo = modelInputStream.readString()
        if (kind == 1) {
            val reference = StaticReference(
                sref,
                node,
                modelRef,
                targetNodeId,
                resolveInfo
            )
            try {
                val value = when (targetModelKind) {
                    REF_OTHER_MODEL.toInt() -> ExplicitReferenceTarget(
                        modelRef.id.uuid()!!,
                        targetNodeId!!
                    )
                    REF_THIS_MODEL.toInt() -> ExplicitReferenceTarget(
                        modelRef.id.uuid()!!,
                        targetNodeId!!
                    )
                    else -> throw UnsupportedOperationException()
                }
                node.addReference(sref, PhysicalReferenceValue(value, resolveInfo))
            } catch (e: Throwable) {
                if (result != null) {
                    // node.addReference(sref, PhysicalReferenceValue(result!!, resolveInfo))
                } else {
                    node.addReference(sref, PhysicalReferenceValue(FailedLoadingReferenceTarget(e), resolveInfo))
                }
            }
            return result ?: reference
        } else if (kind == 2 || kind == 3) {
            TODO()
        } else {
            throw IOException("unknown reference type")
        }
    }

    protected fun localNodeReferenceRead(nodeId: NodeId?) {
        // no-op, left for subclasses  to override
    }

    protected fun readUserObjects(node: PhysicalNode) {
        val userObjectCount = modelInputStream.readShort().toInt()
        var i = 0
        while (i < userObjectCount) {
            val key = readUserObject()
            val value = readUserObject()
            if (key != null && value != null) {
                // node.putUserObject(key, value)
            }
            i += 2
        }
    }

    private fun readUserObject(): Any? {
        TODO("Reading user objects is not supported")
    }
}
