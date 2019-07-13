package com.strumenta.mpsinterop

import java.io.IOException
import java.io.InputStream

private val HEADER_START = -0x6e545457
private val STREAM_ID_V1 = 0x00000300
private val STREAM_ID_V2 = 0x00000400
private val STREAM_ID = STREAM_ID_V2
private val HEADER_ATTRIBUTES: Byte = 0x7e
private val HEADER_END = -0x54545455
private val MODEL_START = -0x45454546
private val REGISTRY_START = 0x5a5a5a5a
private val REGISTRY_END = -0x5a5a5a5b
private val STUB_NONE: Byte = 0x12
private val STUB_ID: Byte = 0x13

// see https://github.com/JetBrains/MPS/blob/4e050f99500d7781d3b0cc5756ec07f129dc3a54/core/persistence/source/jetbrains/mps/persistence/binary/BinaryPersistence.java

class ModelInputStream(val inputStream: InputStream) {
    fun readInt() : Int {
        TODO()
    }

    fun readModelReference(): Any {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun readByte(): Any {

    }

}

class SModelHeader {
    
}


@Throws(IOException::class)
private fun loadHeader(`is`: ModelInputStream): SModelHeader {
    if (`is`.readInt() !== HEADER_START) {
        throw IOException("bad stream, no header")
    }

    val streamId = `is`.readInt()
    if (streamId == STREAM_ID_V1) {
        throw IOException(String.format("Can't read old binary persistence version (%x), please re-save models", streamId))
    }
    if (streamId != STREAM_ID) {
        throw IOException(String.format("bad stream, unknown version: %x", streamId))
    }

    val modelRef = `is`.readModelReference()
    val result = SModelHeader()
    result.setModelReference(modelRef)
    `is`.readInt() //left for compatibility: old version was here
    `is`.mark(4)
    if (`is`.readByte() === HEADER_ATTRIBUTES) {
        result.setDoNotGenerate(`is`.readBoolean())
        var propsCount = `is`.readShort()
        while (propsCount > 0) {
            val key = `is`.readString()
            val value = `is`.readString()
            result.setOptionalProperty(key, value)
            propsCount--
        }
    } else {
        `is`.reset()
    }
    assertSyncToken(`is`, HEADER_END)
    return result
}