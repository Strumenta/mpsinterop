package com.strumenta.mpsinterop.logicalmodel

interface PropertyType
enum class PrimitivePropertyType : PropertyType {
    BOOLEAN,
    INTEGER,
    STRING
}

data class EnumerationAlternative(val name: String, val value: String?)
data class EnumerationPropertyType(val name: String,
                                   val baseType: PrimitivePropertyType,
                                   val alternatives: List<EnumerationAlternative>) : PropertyType
data class ConstrainedDataTypeDeclaration(val qname: String) : PropertyType

data class Property(val sPropertyId: AbsolutePropertyId, val name: String, val type: PropertyType)
data class AbsolutePropertyId(val conceptId: AbsoluteConceptId, val idValue: Long)
