package com.strumenta.mpsinterop.registries

import com.strumenta.mpsinterop.physicalmodel.CONCEPT_DECLARATION_CONCEPT_NAME
import com.strumenta.mpsinterop.physicalmodel.PhysicalModel
import com.strumenta.mpsinterop.physicalmodel.PhysicalNode
import com.strumenta.mpsinterop.physicalmodel.name

fun PhysicalModel.findConceptDeclaration(name: String): PhysicalNode? {
    val conceptDeclaration = this.findConceptByName(CONCEPT_DECLARATION_CONCEPT_NAME) ?: return null
    return this.rootsOfConcept(conceptDeclaration).find {
        it.name() == name
    }
}
