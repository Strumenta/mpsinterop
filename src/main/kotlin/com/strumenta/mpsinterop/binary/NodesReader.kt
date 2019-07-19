package com.strumenta.mpsinterop.binary

import com.strumenta.mpsinterop.logicalmodel.InterfaceSNode
import com.strumenta.mpsinterop.logicalmodel.SModelReference
import com.strumenta.mpsinterop.logicalmodel.SNode

internal class NodesReader(modelReference: SModelReference, `is`: ModelInputStream, private val myReadHelper: ReadHelper) : BareNodeReader(modelReference, `is`) {
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
        //println("Concept $concept (index $conceptIndex)")
        val nodeId = myIn.readNodeId()
        //println("NodeId $nodeId")
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