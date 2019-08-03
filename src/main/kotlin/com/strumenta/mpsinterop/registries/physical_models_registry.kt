package com.strumenta.mpsinterop.registries

import com.strumenta.mpsinterop.physicalmodel.PhysicalModel
import com.strumenta.mpsinterop.physicalmodel.PhysicalNode
import com.strumenta.mpsinterop.physicalmodel.name
import java.util.*

val CONCEPT_DECLARATION = "jetbrains.mps.lang.structure.structure.ConceptDeclaration"

fun PhysicalModel.findConceptDeclaration(name: String): PhysicalNode? {
    val conceptDeclaration = this.conceptByName(CONCEPT_DECLARATION) ?: return null
    return this.allRoots(conceptDeclaration).find {
        it.name() == name
    }
}
