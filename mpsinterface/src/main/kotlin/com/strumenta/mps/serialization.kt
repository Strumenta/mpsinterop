package com.strumenta.mps

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.google.gson.annotations.Expose
import org.w3c.dom.Element
import java.lang.reflect.Type

private val gson = GsonBuilder().setPrettyPrinting()
    .setFieldNamingStrategy { f -> f.name.removeSuffix("\$delegate") }
    .registerTypeHierarchyAdapter(
        Reference::class.java,
        object : JsonSerializer<Reference> {
            override fun serialize(src: Reference?, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
                return if (src == null) {
                    context.serialize(null)
                } else {
                    context.serialize(mapOf("linkName" to src.linkName, "to" to src.refString()))
                }
            }
        }
    )
    .registerTypeAdapter(
        Lazy::class.java,
        object : JsonSerializer<Lazy<*>> {
            override fun serialize(src: Lazy<*>, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
                val value = src.value
                return context.serialize(value)
            }
        }
    )
    .addSerializationExclusionStrategy(
        object : ExclusionStrategy {
            override fun shouldSkipClass(clazz: Class<*>?): Boolean {
                return false
            }

            override fun shouldSkipField(f: FieldAttributes): Boolean {
                if (f.name == "name" && f.declaringClass.canonicalName == "com.strumenta.mps.MpsProject.NodeImpl") {
                    return true
                }
                if (f.name == "value" && f.declaringClass.canonicalName == "com.strumenta.mps.MpsProject.ExternalReferenceImpl") {
                    return true
                }
                if (f.declaredClass == Element::class.java) {
                    return true
                }
                if (f.declaredClass?.canonicalName == "com.strumenta.mps.MpsProject.Registry") {
                    return true
                }
                if (f.annotations.any { it is Expose && !it.serialize }) {
                    return true
                }
                return false
            }
        }
    ).create()

fun Serializable.toJsonObject(): JsonObject {
    return gson.toJsonTree(this).asJsonObject
}

fun Serializable.toJsonString(): String {
    return gson.toJson(this)
}
