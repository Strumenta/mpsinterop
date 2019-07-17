package com.strumenta.mpsinterop.binary

import com.strumenta.mpsinterop.logicalmodel.InterfaceSNode
import com.strumenta.mpsinterop.logicalmodel.SModelReference
import com.strumenta.mpsinterop.logicalmodel.SNode


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


//import com.strumenta.mpsinterop.binary.BinaryPersistence.ReadHelper
//import com.strumenta.mpsinterop.binary.ModelInputStream
//import com.strumenta.mpsinterop.binary.SModelReference
//import jetbrains.mps.smodel.InterfaceSNode
//import jetbrains.mps.util.io.ModelInputStream
//import org.jetbrains.mps.openapi.language.SConcept
//import org.jetbrains.mps.openapi.language.SContainmentLink
//import org.jetbrains.mps.openapi.language.SProperty
//import org.jetbrains.mps.openapi.language.SReferenceLink
//import org.jetbrains.mps.openapi.model.SModelReference
//import org.jetbrains.mps.openapi.model.SNode
//import org.jetbrains.mps.openapi.model.SNodeId

class NodesReader(modelReference: SModelReference, `is`: ModelInputStream, private val myReadHelper: ReadHelper) : BareNodeReader(modelReference, `is`) {
//    private var hasSkippedNodes = false
//    private var myExternalRefs: MutableCollection<SNodeId>? = null
//    private var myLocalRefs: MutableCollection<SNodeId>? = null
//
//    /*package*/ internal fun collectExternalTargets(store: MutableCollection<SNodeId>?) {
//        myExternalRefs = store
//    }
//
//    /*package*/ internal fun collectLocalTargets(store: MutableCollection<SNodeId>?) {
//        myLocalRefs = store
//    }
//
//    fun hasSkippedNodes(): Boolean {
//        return hasSkippedNodes
//    }
//
//    @Throws(IOException::class)
    protected override fun instantiate(parent: SNode?): SNode {
        val conceptIndex = myIn.readShort().toInt()
        val concept = myReadHelper.readConcept(conceptIndex)
        println("Concept $concept (index $conceptIndex)")
        val nodeId = myIn.readNodeId()
        println("NodeId $nodeId")
        val linkId = myIn.readShort().toInt()
        val link = if (linkId == -1) null else myReadHelper.readAggregation(linkId)

        var interfaceNode = false
//        if (myReadHelper.isRequestedInterfaceOnly()) {
//            interfaceNode = myReadHelper.isInterface(concept) || link == null
//        }
//        // TODO report if (nodeInfo != 0 && myEnv != null) .. myEnv.nodeRoleRead/conceptRead();
//
        val node = if (interfaceNode) InterfaceSNode(concept, nodeId) else SNode(concept, nodeId)
//
        if (parent == null) {
            return node
        }
        if (parent !is InterfaceSNode || node is InterfaceSNode) {
            parent!!.addChild(link!!, node)
        } else {
            (parent as InterfaceSNode).skipRole(link!!)
            //hasSkippedNodes = true
        }
        return node
    }
//
//    protected fun localNodeReferenceRead(nodeId: SNodeId?) {
//        if (nodeId != null && myLocalRefs != null) {
//            myLocalRefs!!.add(nodeId)
//        }
//    }
//
//    protected fun externalNodeReferenceRead(targetModel: SModelReference, targetNodeId: SNodeId?) {
//        if (targetNodeId != null && myExternalRefs != null) {
//            myExternalRefs!!.add(targetNodeId)
//        }
//    }
//
    protected override fun readReferences(node: SNode) {
        var refs = myIn.readShort()
        while (refs-- > 0) {
            val link = myReadHelper.readAssociation(myIn.readShort().toInt())
            readReference(link, node)
        }
    }
//
//    @Throws(IOException::class)
    protected override fun readProperties(node: SNode) {
        var properties = myIn.readShort()
        while (properties-- > 0) {
            val property = myReadHelper.readProperty(myIn.readShort().toInt())
            val value = myIn.readString()
            node.setProperty(property, value!!)
        }
    }
}