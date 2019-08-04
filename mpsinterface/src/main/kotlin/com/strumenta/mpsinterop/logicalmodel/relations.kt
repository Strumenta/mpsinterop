package com.strumenta.mpsinterop.logicalmodel

import com.strumenta.mpsinterop.physicalmodel.PhysicalNode
import com.strumenta.mpsinterop.physicalmodel.PhysicalRelation

interface AbsoluteLinkId {
    val conceptId: AbsoluteConceptId
    val idValue: Long
}

data class AbsoluteContainmentLinkId(
    override val conceptId: AbsoluteConceptId,
    override val idValue: Long
) :
    AbsoluteLinkId

data class AbsoluteReferenceLinkId(
    override val conceptId: AbsoluteConceptId,
    override val idValue: Long
) :
    AbsoluteLinkId

interface Link {
    val id: AbsoluteLinkId
    val name: String
}

enum class Multiplicity {
    OPTIONAL,
    ONE,
    ZERO_OR_MORE,
    ONE_OR_MORE
}

data class ContainmentLink(override val id: AbsoluteContainmentLinkId, override val name: String, val multiplicity: Multiplicity) :
    Link
data class ReferenceLink(override val id: AbsoluteReferenceLinkId, override val name: String) :
    Link

interface Reference
data class StaticReference(
    val relation: PhysicalRelation,
    val node: PhysicalNode,
    val modelRef: SModelReference,
    val targetNodeId: NodeId?,
    val resolveInfo: String?
) : Reference
