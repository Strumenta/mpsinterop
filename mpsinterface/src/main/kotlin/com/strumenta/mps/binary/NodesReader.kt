package com.strumenta.mps.binary

import com.strumenta.mps.logicalmodel.SModelReference
import com.strumenta.mps.physicalmodel.PhysicalNode

internal class NodesReader(modelReference: SModelReference, `is`: ModelInputStream, private val myReadHelper: ReadHelper) : BareNodeReader(modelReference, `is`) {

    override fun instantiate(parent: PhysicalNode?): PhysicalNode {
        val conceptIndex = modelInputStream.readShort().toInt()
        val concept = myReadHelper.readConcept(conceptIndex)
        // println("Concept $concept (index $conceptIndex)")
        val nodeId = modelInputStream.readNodeId()
        // println("NodeId $nodeId")
        val linkId = modelInputStream.readShort().toInt()
        val link = if (linkId == -1) null else myReadHelper.readAggregation(linkId)

        var interfaceNode = false
        val node = PhysicalNode(parent, concept, nodeId!!)
        if (parent == null) {
            return node
        }
        parent!!.addChild(link!!, node)
        return node
    }

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
