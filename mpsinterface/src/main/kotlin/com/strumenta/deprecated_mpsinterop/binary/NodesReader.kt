package com.strumenta.deprecated_mpsinterop.binary

import com.strumenta.deprecated_mpsinterop.logicalmodel.SModelReference
import com.strumenta.deprecated_mpsinterop.physicalmodel.PhysicalNode

internal class NodesReader(modelReference: SModelReference, `is`: ModelInputStream, private val myReadHelper: ReadHelper) : BareNodeReader(modelReference, `is`) {
//    private var myExternalRefs: MutableCollection<NodeId>? = null
//    private var myLocalRefs: MutableCollection<NodeId>? = null

    override fun instantiate(parent: PhysicalNode?): PhysicalNode {
        val conceptIndex = modelInputStream.readShort().toInt()
        val concept = myReadHelper.readConcept(conceptIndex)
        // println("Concept $concept (index $conceptIndex)")
        val nodeId = modelInputStream.readNodeId()
        // println("NodeId $nodeId")
        val linkId = modelInputStream.readShort().toInt()
        val link = if (linkId == -1) null else myReadHelper.readAggregation(linkId)

        var interfaceNode = false
//        if (myReadHelper.isRequestedInterfaceOnly()) {
//            interfaceNode = myReadHelper.isInterface(concept) || link == null
//        }
//        // TODO report if (nodeInfo != 0 && myEnv != null) .. myEnv.nodeRoleRead/conceptRead();
//
        val node = PhysicalNode(parent, concept, nodeId!!)
//
        if (parent == null) {
            return node
        }
        parent!!.addChild(link!!, node)
//        if (parent !is InterfaceSNode || node is InterfaceSNode) {
//            parent!!.addChild(link!!, node)
//        } else {
//            (parent as InterfaceSNode).skipRole(link!!)
//            //hasSkippedNodes = true
//        }
        return node
    }
//
//    protected fun localNodeReferenceRead(nodeId: NodeId?) {
//        if (nodeId != null && myLocalRefs != null) {
//            myLocalRefs!!.add(nodeId)
//        }
//    }
//
//    protected fun externalNodeReferenceRead(targetModel: SModelReference, targetNodeId: NodeId?) {
//        if (targetNodeId != null && myExternalRefs != null) {
//            myExternalRefs!!.add(targetNodeId)
//        }
//    }
//
    override fun readReferences(node: PhysicalNode) {
        var refs = modelInputStream.readShort()
        while (refs-- > 0) {
            val link = myReadHelper.readAssociation(modelInputStream.readShort().toInt())
            readReference(link, node)
        }
    }

    override fun readProperties(node: PhysicalNode) {
        var properties = modelInputStream.readShort()
        while (properties-- > 0) {
            val property = myReadHelper.readProperty(modelInputStream.readShort().toInt())
            val value = modelInputStream.readString()
            node[property] = value!!
        }
    }
}
