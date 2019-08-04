package com.strumenta.mpsinterop.physicalmodel

import java.util.*

open class PhysicalModule(val uuid: UUID, val name: String) {
    val models = LinkedList<PhysicalModel>()
}

class PhysicalLanguageModule(uuid: UUID, name: String) : PhysicalModule(uuid, name)

class PhysicalSolutionModule(uuid: UUID, name: String) : PhysicalModule(uuid, name)
