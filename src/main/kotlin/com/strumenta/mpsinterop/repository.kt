package com.strumenta.mpsinterop

import com.strumenta.mpsinterop.datamodel.Node

interface Repository {

}

fun Node.children(repository: Repository, conceptName: String, roleName: String) : List<Node> {
    TODO()
//    val concept = model.conceptByName(conceptName) ?: return emptyList()
//    val role = concept.roleByName(roleName)
//    return role?.let { children(it) } ?: emptyList()
}