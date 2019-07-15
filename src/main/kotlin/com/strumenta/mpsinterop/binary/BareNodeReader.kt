package com.strumenta.mpsinterop.binary
import com.strumenta.mpsinterop.binary.SModelReference
import com.strumenta.mpsinterop.binary.ModelInputStream


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
//import com.strumenta.mpsinterop.binary.ModelInputStream
//import com.strumenta.mpsinterop.binary.SModelReference
//import jetbrains.mps.extapi.model.SModelData
//import jetbrains.mps.smodel.DynamicReference
//import jetbrains.mps.smodel.DynamicReference.DynamicReferenceOrigin
//import jetbrains.mps.smodel.StaticReference
//import jetbrains.mps.util.io.ModelInputStream
//import org.jetbrains.mps.openapi.language.SConcept
//import org.jetbrains.mps.openapi.language.SContainmentLink
//import org.jetbrains.mps.openapi.language.SProperty
//import org.jetbrains.mps.openapi.language.SReferenceLink
//import org.jetbrains.mps.openapi.model.SModelReference
//import org.jetbrains.mps.openapi.model.SNode
//import org.jetbrains.mps.openapi.model.SNodeId
//import org.jetbrains.mps.openapi.model.SReference

import java.io.IOException
import java.io.ObjectInputStream
import java.util.ArrayList

/**
 * Lightweight, straightforward binary serialization of individual [org.jetbrains.mps.openapi.model.SNode]s.
 * @see jetbrains.mps.persistence.binary.BareNodeWriter
 *
 * @author Artem Tikhomirov
 */
open class BareNodeReader(protected val myModelReference: SModelReference, protected val myIn: ModelInputStream) {

//    /**
//     * Read nodes and register them as roots into supplied ModelData
//     */
//    @Throws(IOException::class)
    fun readNodesInto(modelData: SModel) {
        for (r in readChildren(null)) {
            //modelData.addRootNode(r)
        }
    }
//
//    /**
//     * Read nodes and register them as children into supplied parent SNode (if any)
//     * @return list of nodes read
//     */
//    @Throws(IOException::class)
    fun readChildren(parent: SNode?): List<SNode> {
        var size = myIn.readInt()
        println("  readChildren, size $size")
        val rv = ArrayList<SNode>(size)
        while (size-- > 0) {
            rv.add(readNode(parent))
        }
        return rv
    }
//
//    /**
//     * Read a single node and register it with optional parent
//     */
//    @Throws(IOException::class)
    fun readNode(parent: SNode?): SNode {
        println("Reading node with parent $parent")

        val node = instantiate(parent)

        if (myIn.readByte() != '{'.toByte()) {
            throw IOException("bad stream, no '{'")
        }

        readProperties(node)

        readUserObjects(node)

        readReferences(node)

        readChildren(node)

        if (myIn.readByte() != '}'.toByte()) {
            throw IOException("bad stream, no '}'")
        }
        return node
    }
//
//    @Throws(IOException::class)
    protected open fun instantiate(parent: SNode?): SNode {
        val c = myIn.readConcept()
        val nid = myIn.readNodeId()
        val link = myIn.readContainmentLink()
        println("Instantiate concept $c, id $nid, link $link")
//        val node = jetbrains.mps.smodel.SNode(c, nid)
//        if (parent != null && link != null) {
//            parent!!.addChild(link, node)
//        }
//        return node
        TODO()
    }
//
//    @Throws(IOException::class)
    protected open fun readProperties(node: SNode) {
        var properties = myIn.readShort().toInt()
        println("  properties $properties")
        while (properties-- > 0) {
            val property = myIn.readProperty()
            val value = myIn.readString()
            //node.setProperty(property, value)
        }
    }

    @Throws(IOException::class)
    protected fun readReferences(node: SNode) {
        var refs = myIn.readShort().toInt()
        while (refs-- > 0) {
            readReference(myIn.readReferenceLink()!!, node)
        }
    }
//
//    @Throws(IOException::class)
    protected fun readReference(sref: SReferenceLink, node: SNode)/*: SReference*/ {
        val kind = myIn.readByte().toInt()
        TODO()
//        assert(kind >= 1 && kind <= 3)
//        val targetNodeId = if (kind == 1) myIn.readNodeId() else null
//        val origin = if (kind == 3) DynamicReferenceOrigin(myIn.readNodePointer(), myIn.readNodePointer()) else null
//        val targetModelKind = myIn.readByte().toInt()
//        assert(targetModelKind == BareNodeWriter.REF_OTHER_MODEL || targetModelKind == BareNodeWriter.REF_THIS_MODEL)
//        val modelRef: SModelReference?
//        if (targetModelKind == BareNodeWriter.REF_OTHER_MODEL) {
//            modelRef = myIn.readModelReference()
//            externalNodeReferenceRead(modelRef, targetNodeId)
//        } else {
//            modelRef = myModelReference
//            localNodeReferenceRead(targetNodeId)
//        }
//        val resolveInfo = myIn.readString()
        if (kind == 1) {
//            val reference = StaticReference(
//                    sref,
//                    node,
//                    modelRef,
//                    targetNodeId,
//                    resolveInfo)
            //node.setReference(reference.getLink(), reference)
            //return reference
        } else
            if (kind == 2 || kind == 3) {
//                val reference = DynamicReference(
//                        sref,
//                        node,
//                        modelRef,
//                        resolveInfo)
//                if (origin != null) {
//                    reference.setOrigin(origin)
//                }
//                node.setReference(sref, reference)
                //return reference
            } else {
                throw IOException("unknown reference type")
            }
    }
//
//    protected fun localNodeReferenceRead(nodeId: SNodeId?) {
//        // no-op, left for subclasses  to override
//    }
//
//    protected fun externalNodeReferenceRead(targetModel: SModelReference?, nodeId: SNodeId?) {
//        // no-op, left for subclasses  to override
//    }
//
//    @Throws(IOException::class)
    protected fun readUserObjects(node: SNode) {
        val userObjectCount = myIn.readShort().toInt()
        var i = 0
        while (i < userObjectCount) {
            val key = readUserObject()
            val value = readUserObject()
            if (key != null && value != null) {
                //node.putUserObject(key, value)
            }
            i += 2
        }
    }
//
//    @Throws(IOException::class)
    private fun readUserObject(): Any? {
        val id = myIn.readByte().toInt()
        when (id) {
//            BareNodeWriter.USER_NODE_POINTER -> return myIn.readNodePointer()
//            BareNodeWriter.USER_STRING -> return myIn.readString()
//            BareNodeWriter.USER_NULL -> return null
//            BareNodeWriter.USER_NODE_ID -> return myIn.readNodeId()
//            BareNodeWriter.USER_MODEL_ID -> return myIn.readModelID()
//            BareNodeWriter.USER_MODEL_REFERENCE -> return myIn.readModelReference()
//            BareNodeWriter.USER_SERIALIZABLE -> {
//                val stream = ObjectInputStream(myIn)
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