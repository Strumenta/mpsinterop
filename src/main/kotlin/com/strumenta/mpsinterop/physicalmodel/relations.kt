package com.strumenta.mpsinterop.physicalmodel

enum class RelationKind {
    CONTAINMENT,
    REFERENCE
}

data class PhysicalRelation(
    val container: PhysicalConcept,
    val id: Long,
    val name: String,
    val index: String,
    val kind: RelationKind
)