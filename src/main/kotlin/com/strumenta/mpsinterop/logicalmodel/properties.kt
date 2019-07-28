package com.strumenta.mpsinterop.logicalmodel

interface PropertyType
data class AbsolutePropertyId(val conceptId: AbsoluteConceptId, val idValue: Long)
data class Property(val propertyId: AbsolutePropertyId, val name: String, val type: PropertyType)

enum class PrimitivePropertyType : PropertyType {
    BOOLEAN,
    INTEGER,
    STRING
}

data class EnumerationAlternative(val name: String, val value: String?)
data class EnumerationPropertyType(val name: String,
                                   val baseType: PrimitivePropertyType,
                                   val alternatives: List<EnumerationAlternative>) : PropertyType
data class ConstrainedPropertyType(val qname: String) : PropertyType

