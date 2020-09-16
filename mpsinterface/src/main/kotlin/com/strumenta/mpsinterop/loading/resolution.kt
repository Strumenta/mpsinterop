package com.strumenta.mpsinterop.loading

import com.strumenta.mpsinterop.logicalmodel.AbstractConcept
import com.strumenta.mpsinterop.logicalmodel.Concept
import com.strumenta.mpsinterop.logicalmodel.Model
import com.strumenta.mpsinterop.logicalmodel.Node
import com.strumenta.mpsinterop.physicalmodel.PhysicalConcept
import com.strumenta.mpsinterop.physicalmodel.PhysicalModel
import com.strumenta.mpsinterop.physicalmodel.PhysicalNode
import com.strumenta.mpsinterop.registries.Indexer
import com.strumenta.mpsinterop.registries.LanguageRegistry
import com.strumenta.mpsinterop.registries.findConceptDeclaration

class PhysicalToLogicalConverter(
    val languageRegistry: LanguageRegistry = LanguageRegistry.DEFAULT,
    val physicalModelsRegistry: ModelLocator = Indexer.DEFAULT
) {
    private val convertedConcepts = HashMap<PhysicalConcept, AbstractConcept>()
    private val convertedNodes = HashMap<PhysicalNode, Node>()

    fun toLogical(physicalModel: PhysicalModel): Model {
        val logicalModel = Model(physicalModel.name)
        physicalModel.onRoots { logicalModel.addRoot(this.toLogical(it)) }
        return logicalModel
    }

    fun toLogical(physicalNode: PhysicalNode): Node {
        return convertedNodes.computeIfAbsent(physicalNode) { physicalNode ->
            val id = physicalNode.id // NodeId.regular(Base64.parseLong(physicalNode.id))
            val concept = this.toLogical(physicalNode.concept) as Concept
            val logicalNode = Node(
                    /*physicalNode.parent?.toLogical(this),*/
                    concept,
                    id)
            concept.allProperties.forEach {
                val value = physicalNode.propertyValue(it.name, null)
                if (value != null) {
                    logicalNode.setProperty(it, value)
                }
            }

            logicalNode
        }
    }

    fun toLogical(physicalConcept: PhysicalConcept): AbstractConcept {
        return convertedConcepts.computeIfAbsent(physicalConcept) { physicalConcept ->
            val language = languageRegistry.languageByUUID(physicalConcept.languageId.id)
            require(language != null) { "Language not found ${physicalConcept.languageId} for concept $physicalConcept" }
            val concept = language!!.conceptByID(physicalConcept.id)
            TODO(concept?.toString() ?: "NULL")
//            if (concept != null) {
//                concept
//            } else {
//                val thisConceptDeclarationPhysical =
//                        physicalModelsRegistry.findConceptDeclaration(physicalConcept.qualifiedName)
//                                ?: throw RuntimeException("Concept declaration for ${physicalConcept.qualifiedName} not found")
//                val thisConceptDeclarationLogical = toLogical(thisConceptDeclarationPhysical)
//                loadConceptFromConceptDeclaration(thisConceptDeclarationLogical)
//            }
        }
    }

    private fun loadConceptFromConceptDeclaration(conceptDeclaration: Node): Concept {
        TODO()
    }
}

fun PhysicalNode.toLogical(physicalToLogicalConverter: PhysicalToLogicalConverter) =
        physicalToLogicalConverter.toLogical(this)
