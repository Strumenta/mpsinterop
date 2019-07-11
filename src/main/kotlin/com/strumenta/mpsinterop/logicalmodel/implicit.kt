package com.strumenta.mpsinterop.logicalmodel

import java.util.*

fun implicitLanguages() : List<Language> {
    return listOf(createJetbrainsMpsLangStructure())
}

internal fun createJetbrainsMpsLangStructure() : Language {
    val language = Language("jetbrains.mps.lang.structure")
//    val iSmartReferent = InterfaceConcept("7094926192234036184", "ISmartReferent")
//    val baseConcept =
//    val abstractConceptDeclaration = Concept("1169125787135", "AbstractConceptDeclaration")
//    val conceptDeclaration = Concept("1071489090640", "ConceptDeclaration", abstractConceptDeclaration, listOf(iSmartReferent))
//    language.add(iSmartReferent)
//    language.add(abstractConceptDeclaration)
//    language.add(conceptDeclaration)
    return language
}