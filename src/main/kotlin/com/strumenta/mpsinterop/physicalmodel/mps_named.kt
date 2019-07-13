package com.strumenta.mpsinterop.physicalmodel

import com.strumenta.mpsinterop.registries.PhysicalModelsRegistry

val INAMED_CONCEPT = "jetbrains.mps.lang.core.structure.INamedConcept"
val NAME_PROPERTY = "name"

fun PhysicalNode.name() : String? {
    return singlePropertyValue(this.model!!.getProperty(INAMED_CONCEPT, NAME_PROPERTY))
}
