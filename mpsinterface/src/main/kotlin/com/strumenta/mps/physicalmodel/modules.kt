package com.strumenta.mps.physicalmodel

import java.io.InputStream
import java.util.LinkedList
import java.util.UUID

open class PhysicalModule(val uuid: UUID, val name: String) {
    val models = LinkedList<PhysicalModel>()
    val modelsLocators = mutableListOf<Function<InputStream>>()

    data class Dependency(val uuid: UUID, val name: String, val reexport: Boolean)
    data class UsedLanguage(val uuid: UUID, val name: String)

    val dependencies = mutableListOf<Dependency>()
    val usedLanguages = mutableListOf<UsedLanguage>()
}

class PhysicalLanguageModule(uuid: UUID, name: String) : PhysicalModule(uuid, name)

class PhysicalSolutionModule(uuid: UUID, name: String) : PhysicalModule(uuid, name)
