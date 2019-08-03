package com.strumenta.mpsinterop.physicalmodel

import java.lang.IllegalStateException

const val INAMED_CONCEPT = "jetbrains.mps.lang.core.structure.INamedConcept"
const val NAME_PROPERTY = "name"

fun PhysicalNode.name(): String? {
    val model = this.model ?: throw IllegalStateException("Node not attached into a physical model")
    return model.findProperty(INAMED_CONCEPT, NAME_PROPERTY)?.let {
        propertyValue(it)
    }
}
