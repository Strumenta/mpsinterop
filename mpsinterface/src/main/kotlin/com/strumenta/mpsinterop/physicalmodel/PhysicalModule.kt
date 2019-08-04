package com.strumenta.mpsinterop.physicalmodel

import java.util.*

open class PhysicalModule(val uuid: UUID, val name: String) {
    val models = LinkedList<PhysicalModel>()
}