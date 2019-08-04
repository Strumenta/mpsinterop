package com.strumenta.mpsinterop.logicalmodel

import com.strumenta.mpsinterop.utils.Base64

abstract class NodeId {
    abstract fun toStringRepresentation(): String
    abstract fun isCompatibleWith(value: Long): Boolean

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
    override fun toStringRepresentation(): String {
        return value
    }

    override fun isCompatibleWith(longValue: Long): Boolean {
        return value == Base64.toString(longValue)
    }
}

internal data class RegularSNodeId(val value: Long) : NodeId() {
    override fun toString(): String {
        return value.toString()
    }

    override fun toStringRepresentation() = Base64.toString(value)
    override fun isCompatibleWith(longValue: Long): Boolean {
        return longValue == value
    }
}
