package com.strumenta.mpsinterop.physicalmodel

const val INAMED_CONCEPT = "jetbrains.mps.lang.core.structure.INamedConcept"
const val NAME_PROPERTY = "name"

fun PhysicalNode.name(): String? {
    return propertyValue(this.model!!.getProperty(INAMED_CONCEPT, NAME_PROPERTY))
}
