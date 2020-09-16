package com.strumenta.deprecated_mpsinterop.physicalmodel

import com.strumenta.deprecated_mpsinterop.logicalmodel.NodeId
import com.strumenta.deprecated_mpsinterop.utils.Base64
import java.util.*

sealed class ReferenceTarget

data class InModelReferenceTarget(
    val physicalModel: PhysicalModel,
    val nodeID: String
) : ReferenceTarget()

data class OutsideModelReferenceTarget(
    val physicalModel: PhysicalModel,
    val importIndex: String,
    val nodeIndex: String
) : ReferenceTarget() {
    val modelUIID: UUID = physicalModel.modelUUIDFromIndex(importIndex)
    val nodeID: Long
        get() {
            return Base64.parseLong(nodeIndex)
        }
}
class ExplicitReferenceTarget(val model: UUID, val nodeId: NodeId) : ReferenceTarget()
class FailedLoadingReferenceTarget(val e: Throwable) : ReferenceTarget()
object NullReferenceTarget : ReferenceTarget()

data class PhysicalReferenceValue(val target: ReferenceTarget, val resolve: String?)
