package com.strumenta.mpsinterop.binary

abstract class SNodeId {
    companion object {
        fun regular(idValue: Long): SNodeId {
            return RegularSNodeId(idValue)
        }
    }
}

internal data class RegularSNodeId(val value: Long) : SNodeId()


