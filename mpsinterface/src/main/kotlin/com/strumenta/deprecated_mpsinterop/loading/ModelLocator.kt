package com.strumenta.deprecated_mpsinterop.loading

import com.strumenta.deprecated_mpsinterop.logicalmodel.NodeId
import com.strumenta.deprecated_mpsinterop.physicalmodel.*
import com.strumenta.deprecated_mpsinterop.utils.Base64
import java.lang.RuntimeException
import java.util.*

interface ModelLocator {
    fun locateModel(modelUUID: UUID): PhysicalModel?
    fun locateModel(name: String): PhysicalModel?
    fun locateLanguage(languageUUID: UUID): PhysicalLanguageModule?
}

interface NodeLocator {
    fun resolve(modelUUID: UUID, nodeID: NodeId): PhysicalNode?
    fun resolve(target: ReferenceTarget): PhysicalNode?
}

class SimpleNodeLocator(val modelLocator: ModelLocator) : NodeLocator {

    override fun resolve(modelUUID: UUID, nodeID: NodeId): PhysicalNode? {
        val model = modelLocator.locateModel(modelUUID)
                ?: throw RuntimeException("Unknown model with UUID $modelUUID")
        return model.findNodeByID(nodeID)
    }

    override fun resolve(target: ReferenceTarget): PhysicalNode? {
        return when (target) {
            is InModelReferenceTarget -> resolve(target.physicalModel.uuid,
                    NodeId.regular(Base64.parseLong(target.nodeID)))
            is OutsideModelReferenceTarget -> resolve(target.modelUIID, NodeId.regular(target.nodeID))
            is NullReferenceTarget -> null
            is ExplicitReferenceTarget -> resolve(target.model, target.nodeId)
            else -> TODO()
        }
    }
}
