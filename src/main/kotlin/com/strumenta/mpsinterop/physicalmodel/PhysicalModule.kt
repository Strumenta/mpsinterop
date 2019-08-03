package com.strumenta.mpsinterop.physicalmodel

import java.util.*

open class PhysicalModule(val name: String, val uuid: UUID) {
    val models = LinkedList<PhysicalModel>()
}