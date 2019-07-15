package com.strumenta.mpsinterop.binary

import java.util.*
import kotlin.collections.HashMap

open class SNode(concept: SConcept, nodeId: SNodeId?) {
    private val children = HashMap<SContainmentLink, MutableList<SNode>>()

    fun addChild(link: SContainmentLink, node: SNode) {
        children.computeIfAbsent(link) {
            LinkedList<SNode>()
        }.add(node)
    }
}
