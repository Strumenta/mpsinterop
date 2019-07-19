package com.strumenta.mpsinterop.binary

import com.strumenta.mpsinterop.logicalmodel.InterfaceSNode
import com.strumenta.mpsinterop.logicalmodel.SModelReference
import com.strumenta.mpsinterop.logicalmodel.SNode

internal class NodesReader(modelReference: SModelReference, `is`: ModelInputStream, private val myReadHelper: ReadHelper) : BareNodeReader(modelReference, `is`) {
//    private var myExternalRefs: MutableCollection<SNodeId>? = null
//    private var myLocalRefs: MutableCollection<SNodeId>? = null

    override fun instantiate(parent: SNode?): SNode {
        val conceptIndex = modelInputStream.readShort().toInt()
        val concept = myReadHelper.readConcept(conceptIndex)
        //println("Concept $concept (index $conceptIndex)")
        val nodeId = modelInputStream.readNodeId()
        //println("NodeId $nodeId")
        val linkId = modelInputStream.readShort().toInt()
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
    override fun readReferences(node: SNode) {
        var refs = modelInputStream.readShort()
        while (refs-- > 0) {
            val link = myReadHelper.readAssociation(modelInputStream.readShort().toInt())
            readReference(link, node)
        }
    }

    override fun readProperties(node: SNode) {
        var properties = modelInputStream.readShort()
        while (properties-- > 0) {
            val property = myReadHelper.readProperty(modelInputStream.readShort().toInt())
            val value = modelInputStream.readString()
            node.setProperty(property, value!!)
        }
    }
}