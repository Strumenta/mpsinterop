package com.strumenta.mps.logicalmodel

interface PropertyType {
    val defaultValue: Any

    val defaultValueAsString: String
        get() = defaultValue.toString()
}

data class AbsolutePropertyId(val conceptId: AbsoluteConceptId, val idValue: Long)
data class Property(val propertyId: AbsolutePropertyId, val name: String, val type: PropertyType)

enum class PrimitivePropertyType(override val defaultValue: Any) : PropertyType {
    BOOLEAN(false),
    INTEGER(0L),
    STRING("");
}

data class EnumerationAlternative(val name: String, val value: String?)
data class EnumerationPropertyType(
    val name: String,
    val baseType: PrimitivePropertyType,
    val alternatives: List<EnumerationAlternative>
) : PropertyType {
    override val defaultValue: Any
        get() = TODO()
}
data class ConstrainedPropertyType(val qname: String) : PropertyType {
    override val defaultValue: Any
        get() = TODO()
}
// TODO write me
class EnumerationDeclarationType() : PropertyType {
    override val defaultValue: Any
        get() = TODO()
}
