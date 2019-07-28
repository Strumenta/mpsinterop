package com.strumenta.mpsinterop.loading

import com.strumenta.mpsinterop.logicalmodel.NodeId
import com.strumenta.mpsinterop.physicalmodel.*
import com.strumenta.mpsinterop.utils.JavaFriendlyBase64
import java.lang.RuntimeException
import java.util.*

interface ModelLocator {
    fun locate(modelUUID: UUID) : PhysicalModel?
}

interface NodeLocator {

    fun resolve(modelUUID: UUID, nodeID: NodeId): PhysicalNode?
    fun resolve(target: ReferenceTarget): PhysicalNode?
}

class SimpleNodeLocator(val modelLocator: ModelLocator) : NodeLocator {

    override fun resolve(modelUUID: UUID, nodeID: NodeId): PhysicalNode? {
        val model = modelLocator.locate(modelUUID)
                ?: throw RuntimeException("Unknown model with UUID $modelUUID")
        return model.findNodeByID(nodeID)
    }

    override fun resolve(target: ReferenceTarget): PhysicalNode? {
        return when (target) {
            is InModelReferenceTarget -> resolve(target.physicalModel.uuid,
                    NodeId.regular(JavaFriendlyBase64.parseLong(target.nodeID)))
            is OutsideModelReferenceTarget -> resolve(target.modelUIID, NodeId.regular(target.nodeID))
            is NullReferenceTarget -> null
            is ExplicitReferenceTarget -> resolve(target.model, target.nodeId)
            else -> TODO()
        }
    }
}