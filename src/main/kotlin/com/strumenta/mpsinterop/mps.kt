package com.strumenta.mpsinterop

import java.util.*
//
///**
// * Each concept is identified by an ID globally and by an index within a single mps file.
// */
//data class Concept(val id: String, val name: String, val index: String) {
//    val roles = LinkedList<Role>()
//    val properties = LinkedList<Property>()
//
//    fun addRole(role: Role) {
//        roles.add(role)
//    }
//
//    fun roleByName(name: String): Role? {
//        return roles.find { it.name == name }
//    }
//
//    fun addProperty(property: Property) {
//        properties.add(property)
//    }
//
//    fun propertyByName(name: String): Property {
//        return properties.find { it.name == name }!!
//    }
//
//}
//
//data class Role(val container: Concept, val id: String, val name: String, val index: String)
//
//data class Property(val container: Concept, val id: String, val name: String, val index: String)
//
//class ModelNode(val model: Model, val parent: ModelNode?, val concept: Concept, val id: String) {
//    private val properties = HashMap<Property, MutableList<String>>()
//    private val children = HashMap<Role, MutableList<ModelNode>>()
//    private val references = HashMap<Role, ModelNode>()
//
//    fun addChild(role: Role, node: ModelNode) {
//        if (role !in children) {
//            children[role] = LinkedList()
//        }
//        children[role]!!.add(node)
//    }
//
//    fun addReference(role: Role, node: ModelNode) {
//        references[role] = node
//    }
//
//    fun addProperty(property: Property, propertyValue: String) {
//        if (property !in properties) {
//            properties[property] = LinkedList()
//        }
//        properties[property]!!.add(propertyValue)
//    }
//
//    fun singlePropertyValue(property: Property) : String {
//        return properties[property]!![0]
//    }
//
//    fun children(conceptName: String, roleName: String) : List<ModelNode> {
//        val concept = model.conceptByName(conceptName) ?: return emptyList()
//        val role = concept.roleByName(roleName)
//        return role?.let { children(it) } ?: emptyList()
//    }
//
//    fun children(role: Role) = children[role] ?: emptyList<ModelNode>()
//
//    fun reference(role: Role) = references[role]
//
//    fun ancestor(condition: (ModelNode) -> Boolean ): ModelNode? {
//        if (parent == null) {
//            return null
//        }
//        if (condition(parent)) {
//            return parent
//        }
//        return parent.ancestor(condition)
//    }
//
//
//}
//
//class Model {
//    private val conceptsById = HashMap<String, Concept>()
//    private val conceptsByIndex = HashMap<String, Concept>()
//    private val rolesByIndex = HashMap<String, Role>()
//    private val propertiesByIndex = HashMap<String, Property>()
//    private val conceptsByName = HashMap<String, Concept>()
//    private val roots = LinkedList<ModelNode>()
//
//    fun registerConcept(concept: Concept) {
//        conceptsById[concept.id] = concept
//        conceptsByIndex[concept.index] = concept
//        conceptsByName[concept.name] = concept
//        concept.properties.forEach { propertiesByIndex[it.index] = it }
//        concept.roles.forEach { rolesByIndex[it.index] = it }
//    }
//
//    fun conceptById(id: String) = conceptsById[id]!!
//    fun conceptByIndex(index: String) = conceptsByIndex[index] ?: throw IllegalArgumentException("Concept not found: index=$index")
//    fun conceptByName(name: String) = conceptsByName[name] //?: throw IllegalArgumentException("Concept not found: name=$name")
//
//    fun roleByIndex(index: String) = rolesByIndex[index]!!
//
//    fun propertyByIndex(index: String) = propertiesByIndex[index]!!
//
//    fun registerRoot(node: ModelNode) {
//        roots.add(node)
//    }
//
//    fun onRoots(conceptName: String, op: (ModelNode)->Unit) {
//        conceptByName(conceptName)?.let { onRoots(it, op) }
//    }
//
//    fun onRoots(concept: Concept, op: (ModelNode)->Unit) {
//        roots.filter { it.concept == concept }.forEach { op(it) }
//    }
//
//}
