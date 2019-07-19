package com.strumenta.mpsinterop.loading

import com.strumenta.mpsinterop.logicalmodel.*
import com.strumenta.mpsinterop.physicalmodel.PhysicalConcept
import com.strumenta.mpsinterop.physicalmodel.PhysicalModel
import com.strumenta.mpsinterop.physicalmodel.PhysicalNode
import com.strumenta.mpsinterop.registries.LanguageRegistry
import com.strumenta.mpsinterop.registries.PhysicalModelsRegistry
import com.strumenta.mpsinterop.utils.JavaFriendlyBase64

class PhysicalToLogicalConverter(
        val languageRegistry: LanguageRegistry = LanguageRegistry.DEFAULT,
        val physicalModelsRegistry: PhysicalModelsRegistry = PhysicalModelsRegistry.DEFAULT) {
    private val convertedConcepts = HashMap<PhysicalConcept, SConcept>()
    private val convertedNodes = HashMap<PhysicalNode, SNode>()

    fun toLogical(physicalModel: PhysicalModel) : Model {
        val logicalModel = Model(physicalModel.name)
        physicalModel.onRoots { logicalModel.addRoot(this.toLogical(it)) }
        return logicalModel
    }

    fun toLogical(physicalNode: PhysicalNode) : SNode {
        return convertedNodes.computeIfAbsent(physicalNode) { physicalNode ->
            val id = physicalNode.id//SNodeId.regular(JavaFriendlyBase64.parseLong(physicalNode.id))
            val logicalNode = SNode(
                    /*physicalNode.parent?.toLogical(this),*/
                    this.toLogical(physicalNode.concept),
                    id)
            logicalNode
        }
    }

    fun toLogical(physicalConcept: PhysicalConcept) : SConcept {
        return convertedConcepts.computeIfAbsent(physicalConcept) { physicalConcept ->
            val concept = languageRegistry.getConcept(physicalConcept.name)
            if (concept != null) {
                concept
            } else {
                val thisConceptDeclarationPhysical =
                        physicalModelsRegistry.findConceptDeclaration(physicalConcept.name)
                                ?: throw RuntimeException("Concept declaration for ${physicalConcept.name} not found")
                val thisConceptDeclarationLogical = toLogical(thisConceptDeclarationPhysical)
                //            val thisConcept
                //            val superConcept = constraintNode.reference("extends")
                //            val implemented = LinkedList<Concept>()
                //            val logicalConcept = Concept(physicalConcept.id, physicalConcept.name,
                //                    superConcept, implemented)
                //            logicalConcept
                loadConceptFromConceptDeclaration(thisConceptDeclarationLogical)
            }
        }
    }

    private fun loadConceptFromConceptDeclaration(conceptDeclaration: SNode) : SConcept {
        TODO()
    }

}

fun PhysicalNode.toLogical(physicalToLogicalConverter: PhysicalToLogicalConverter) =
        physicalToLogicalConverter.toLogical(this)
