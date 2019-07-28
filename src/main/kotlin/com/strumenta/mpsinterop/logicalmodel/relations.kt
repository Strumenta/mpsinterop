package com.strumenta.mpsinterop.logicalmodel

import com.strumenta.mpsinterop.physicalmodel.PhysicalNode
import com.strumenta.mpsinterop.physicalmodel.PhysicalRelation

interface AbsoluteLinkId {
    val conceptId: AbsoluteConceptId
    val idValue: Long
}

data class AbsoluteContainmentLinkId(override val conceptId: AbsoluteConceptId,
                                     override val idValue: Long)
    : AbsoluteLinkId

data class AbsoluteReferenceLinkId(override val conceptId: AbsoluteConceptId,
                                   override val idValue: Long)
    : AbsoluteLinkId

interface Link {
    val id : AbsoluteLinkId
}
data class ContainmentLink(override val id: AbsoluteContainmentLinkId, val name: String)
    : Link
data class ReferenceLink(override val id: AbsoluteReferenceLinkId, val name: String)
    : Link

interface Reference
data class StaticReference(val relation: PhysicalRelation,
                           val node: PhysicalNode,
                           val modelRef: SModelReference,
                           val targetNodeId: NodeId?,
                           val resolveInfo: String?) : Reference
