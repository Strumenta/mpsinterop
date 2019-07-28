package com.strumenta.mpsinterop.logicalmodel

import com.strumenta.mpsinterop.utils.JavaFriendlyBase64

abstract class NodeId {
    abstract fun toBase64(): String
    abstract fun toLong(): Long

    companion object {
        fun regular(idValue: Long): NodeId {
            return RegularSNodeId(idValue)
        }

        fun fromString(s: String): NodeId {
            return StringSNodeId(s)
        }
    }
}

internal data class StringSNodeId(val value: String) : NodeId() {
    override fun toBase64(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun toLong(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

internal data class RegularSNodeId(val value: Long) : NodeId() {
    override fun toBase64() = JavaFriendlyBase64.toString(value)
    override fun toLong() = value
}
