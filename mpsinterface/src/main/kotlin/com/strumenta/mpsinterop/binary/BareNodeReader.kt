package com.strumenta.mpsinterop.binary
import com.strumenta.mpsinterop.logicalmodel.*
import com.strumenta.mpsinterop.physicalmodel.*

/*
 * Copyright 2003-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//
// import ModelInputStream
// import com.strumenta.mpsinterop.binary.SModelReference
// import jetbrains.mps.extapi.model.SModelData
// import jetbrains.mps.smodel.DynamicReference
// import jetbrains.mps.smodel.DynamicReference.DynamicReferenceOrigin
// import jetbrains.mps.smodel.StaticReference
// import jetbrains.mps.util.io.ModelInputStream
// import org.jetbrains.mps.openapi.language.Concept
// import org.jetbrains.mps.openapi.language.ContainmentLink
// import org.jetbrains.mps.openapi.language.Property
// import org.jetbrains.mps.openapi.language.ReferenceLink
// import org.jetbrains.mps.openapi.model.SModelReference
// import org.jetbrains.mps.openapi.model.Node
// import org.jetbrains.mps.openapi.model.NodeId
// import org.jetbrains.mps.openapi.model.Reference

import java.io.IOException
import java.lang.UnsupportedOperationException
import java.util.ArrayList

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

    fun readNodesInto(modelData: PhysicalModel) {
        readChildren(null).forEach { modelData.addRoot(it) }
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
            // val origin = if (kind == 3) DynamicReferenceOrigin(modelInputStream.readNodePointer(), modelInputStream.readNodePointer()) else null
            val origin = if (kind == 3) TODO() else null
            val targetModelKind = modelInputStream.readByte().toInt()
            assert(targetModelKind == REF_OTHER_MODEL.toInt() || targetModelKind == REF_THIS_MODEL.toInt())
            val modelRef: SModelReference?
            if (targetModelKind == REF_OTHER_MODEL.toInt()) {
                modelRef = modelInputStream.readModelReference()
            } else {
                modelRef = modelReference
                localNodeReferenceRead(targetNodeId)
            }
            val resolveInfo = modelInputStream.readString()
            if (kind == 1) {
                val reference = StaticReference(
                        sref,
                        node,
                        modelRef!!,
                        targetNodeId,
                        resolveInfo)
                try {
                    val value = when (targetModelKind) {
                        REF_OTHER_MODEL.toInt() -> ExplicitReferenceTarget(
                                modelRef.id.uuid(), targetNodeId!!)
                        REF_THIS_MODEL.toInt() -> ExplicitReferenceTarget(
                                modelRef.id.uuid(), targetNodeId!!)
                        else -> throw UnsupportedOperationException()
                    }
                    node.addReference(sref, PhysicalReferenceValue(value, resolveInfo!!))
                } catch (e: Throwable) {
                    node.addReference(sref, PhysicalReferenceValue(FailedLoadingReferenceTarget(e), resolveInfo))
                }
                return reference
            } else if (kind == 2 || kind == 3) {
                    TODO()
//                val reference = DynamicReference(
//                        relation,
//                        node,
//                        modelRef,
//                        resolveInfo)
//                if (origin != null) {
//                    reference.setOrigin(origin)
//                }
//                node.setReference(relation, reference)
                    // return reference
                } else {
                    throw IOException("unknown reference type")
                }
    }
//
    protected fun localNodeReferenceRead(nodeId: NodeId?) {
        // no-op, left for subclasses  to override
    }
//
//    protected fun externalNodeReferenceRead(targetModel: SModelReference?, nodeId: NodeId?) {
//        // no-op, left for subclasses  to override
//    }
//
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
        val id = modelInputStream.readByte().toInt()
        when (id) {
//            BareNodeWriter.USER_NODE_POINTER -> return modelInputStream.readNodePointer()
//            BareNodeWriter.USER_STRING -> return modelInputStream.readString()
//            BareNodeWriter.USER_NULL -> return null
//            BareNodeWriter.USER_NODE_ID -> return modelInputStream.readNodeId()
//            BareNodeWriter.USER_MODEL_ID -> return modelInputStream.readModelID()
//            BareNodeWriter.USER_MODEL_REFERENCE -> return modelInputStream.readModelReference()
//            BareNodeWriter.USER_SERIALIZABLE -> {
//                val stream = ObjectInputStream(modelInputStream)
//                try {
//                    return stream.readObject()
//                } catch (ignore: ClassNotFoundException) {
//                    // class could be loaded by the other classloader
//                    return null
//                }
//
//            }
            else -> TODO()
        }
        throw IOException("Could not read user object")
    }
}