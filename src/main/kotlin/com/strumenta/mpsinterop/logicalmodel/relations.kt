package com.strumenta.mpsinterop.logicalmodel

import com.strumenta.mpsinterop.physicalmodel.PhysicalNode
import com.strumenta.mpsinterop.physicalmodel.PhysicalRelation

data class ContainmentLink(val link: AbsoluteContainmentLinkId, val name: String)
data class AbsoluteContainmentLinkId(val conceptId: AbsoluteConceptId, val idValue: Long)

data class ReferenceLink(val link: AbsoluteReferenceLinkId, val name: String)
data class AbsoluteReferenceLinkId(val conceptId: AbsoluteConceptId, val idValue: Long)
interface Reference
data class StaticReference(val sref: PhysicalRelation, val node: PhysicalNode, val modelRef: SModelReference,
                           val targetNodeId: NodeId?, val resolveInfo: String?) : Reference
