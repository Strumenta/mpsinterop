package com.strumenta.deprecated_mpsinterop.registries

import com.strumenta.deprecated_mpsinterop.physicalmodel.CONCEPT_DECLARATION_CONCEPT_NAME
import com.strumenta.deprecated_mpsinterop.physicalmodel.PhysicalModel
import com.strumenta.deprecated_mpsinterop.physicalmodel.PhysicalNode
import com.strumenta.deprecated_mpsinterop.physicalmodel.name

fun PhysicalModel.findConceptDeclaration(name: String): PhysicalNode? {
    val conceptDeclaration = this.findConceptByName(CONCEPT_DECLARATION_CONCEPT_NAME) ?: return null
    return this.rootsOfConcept(conceptDeclaration).find {
        it.name() == name
    }
}
