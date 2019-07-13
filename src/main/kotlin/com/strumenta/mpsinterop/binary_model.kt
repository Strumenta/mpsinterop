package com.strumenta.mpsinterop

import com.strumenta.mpsinterop.physicalmodel.PhysicalModel
import java.util.UUID
import java.io.*
import java.io.DataInputStream.readUTF
import java.util.ArrayList


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

internal const val NULL : Byte = 0x70
internal const val NODEID_STRING : Byte = 0x17
internal const val NODEID_LONG : Byte = 0x18
internal const val MODELID_STRING : Byte = 0x26
internal const val MODELID_REGULAR : Byte = 0x28
internal const val MODELID_FOREIGN : Byte = 0x27
internal const val MODELID_INTEGER : Byte = 0x29
internal const val NODEPTR : Byte = 0x44
internal const val MODULEID_FOREIGN : Byte = 0x47
internal const val MODULEID_REGULAR : Byte = 0x48
internal const val MODELREF : Byte = 7
internal const val MODELREF_INDEX : Byte = 9
internal const val MODULEREF_MODULEID : Byte = 0x17
internal const val MODULEREF_NAMEONLY : Byte = 0x18
internal const val MODULEREF_INDEX : Byte = 0x19
internal const val LANGUAGE : Byte = 0x30
internal const val LANGUAGE_INDEX : Byte = 0x31
internal const val CONCEPT : Byte = 0x32
internal const val CONCEPT_INDEX : Byte = 0x33
internal const val PROPERTY : Byte = 0x34
internal const val PROPERTY_INDEX : Byte = 0x35
internal const val ASSOCIATION : Byte = 0x36
internal const val ASSOCIATION_INDEX : Byte = 0x37
internal const val AGGREGATION : Byte = 0x38
internal const val AGGREGATION_INDEX : Byte = 0x39

///**
// * Evgeny Gryaznov, Sep 27, 2010
// */
//class ModelOutputStream : DataOutputStream {
//
//}

/**
 * Original code written by Evgeny Gryaznov
 */
class ModelInputStream(val inputStream: InputStream) : DataInputStream(BufferedInputStream(inputStream, 65536)) {

    private val myStrings = ArrayList<String>(2048)
//    private val myModelRefs = ArrayList<SModelReference>(1024)
//    private val myModuleRefs = ArrayList<SModuleReference>(128)
//    private val myLanguages = ArrayList<SLanguage>(128)
//    private val myConcepts = ArrayList<SConcept>(128)
//    private val myProperties = ArrayList<SProperty>(128)
//    private val myAssociations = ArrayList<SReferenceLink>(128)
//    private val myAggregations = ArrayList<SContainmentLink>(128)

    @Throws(IOException::class)
    fun readStrings(): Collection<String>? {
        var size = readInt()
        if (size == -1) {
            return null
        }
        val result = ArrayList<String>(size)
        while (size > 0) {
            result.add(readString()!!)
            size--
        }
        return result
    }

    @Throws(IOException::class)
    fun readString(): String? {
        var c = readByte()
        if (c == NULL) {
            return null
        } else if (c == 1.toByte()) {
            val index = readInt()
            return myStrings[index]
        }
        var sb: StringBuilder? = null
        while (c == 42.toByte()) {
            val prefix = readUTF()
            if (sb == null) {
                sb = StringBuilder(prefix)
            } else {
                sb.append(prefix)
            }
            c = readByte()
        }
        val res: String
        res = if (sb == null) {
            readUTF()
        } else {
            sb.append(readUTF()).toString()
        }
        myStrings.add(res)
        return res
    }

//    @Throws(IOException::class)
//    fun readModuleReference(): SModuleReference? {
//        val c = readByte()
//        if (c == NULL) {
//            return null
//        } else if (c == MODULEREF_INDEX) {
//            val index = readInt()
//            return myModuleRefs[index]
//        }
//
//        var id: ModuleId? = null
//        if (c == MODULEREF_MODULEID) {
//            id = readModuleID()
//        }
//        val ref = jetbrains.mps.project.structure.modules.ModuleReference(readString(), id)
//        myModuleRefs.add(ref)
//        return ref
//    }

//    @Throws(IOException::class)
//    fun readModuleID(): ModuleId? {
//        val c = readByte()
//        if (c == NULL) {
//            return null
//        } else if (c == MODULEID_REGULAR) {
//            val uuid = UUID(readLong(), readLong())
//            return ModuleId.regular(uuid)
//        } else return if (c == MODULEID_FOREIGN) {
//            ModuleId.foreign(readString())
//        } else {
//            throw IOException("unknown id")
//        }
//    }
//
//    @Throws(IOException::class)
//    fun readModelReference(): SModelReference? {
//        val c = readByte()
//        if (c == NULL) {
//            return null
//        } else if (c == MODELREF_INDEX) {
//            val index = readInt()
//            return myModelRefs[index]
//        }
//
//        val id = readModelID()
//        val modelName = readString()
//        val moduleRef = readModuleReference()
//        val ref = jetbrains.mps.smodel.SModelReference(moduleRef, id, modelName)
//        myModelRefs.add(ref)
//        return ref
//    }
//
//    @Throws(IOException::class)
//    fun readModelID(): SModelId? {
//        val c = readByte()
//        return if (c == NULL) {
//            null
//        } else if (c == MODELID_REGULAR) {
//            jetbrains.mps.smodel.SModelId.regular(readUUID())
//        } else if (c == MODELID_FOREIGN) {
//            jetbrains.mps.smodel.SModelId.foreign(readString())
//        } else if (c == MODELID_STRING) {
//            PersistenceFacade.getInstance().createModelId(readString())
//        } else if (c == MODELID_INTEGER) {
//            IntegerSModelId(readInt())
//        } else {
//            throw IOException("unknown id")
//        }
//    }

//    @Throws(IOException::class)
//    fun readNodeId(): SNodeId? {
//        val c = readByte()
//        if (c == NULL) {
//            return null
//        } else if (c == NODEID_LONG) {
//            return Regular(readLong())
//        } else if (c == NODEID_STRING) {
//            return PersistenceFacade.getInstance().createNodeId(readString())
//        }
//        throw IOException("no id")
//    }
//
//    @Throws(IOException::class)
//    fun readNodePointer(): SNodeReference? {
//        val b = readByte()
//        return if (b == NULL) {
//            null
//        } else {
//            jetbrains.mps.smodel.SNodePointer(readModelReference(), readNodeId())
//        }
//    }

    @Throws(IOException::class)
    fun readUUID(): UUID {
        val headBits = readLong()
        val tailBits = readLong()
        return UUID(headBits, tailBits)
    }

//    @Throws(IOException::class)
//    fun readLanguage(): SLanguage? {
//        val b = readByte()
//        if (b == NULL) {
//            return null
//        }
//        if (b == LANGUAGE_INDEX) {
//            return myLanguages[readShort()]
//        }
//        if (b != LANGUAGE) {
//            throw IOException(Integer.toHexString(b.toInt()))
//        }
//        val l = MetaAdapterFactory.getLanguage(readLong(), readLong(), readString())
//        myLanguages.add(l)
//        return l
//    }
//
//    @Throws(IOException::class)
//    fun readConcept(): SConcept? {
//        val b = readByte()
//        if (b == NULL) {
//            return null
//        }
//        if (b == CONCEPT_INDEX) {
//            return myConcepts[readShort()]
//        }
//        if (b != CONCEPT) {
//            throw IOException(Integer.toHexString(b.toInt()))
//        }
//        val c = MetaAdapterFactory.getConcept(readLong(), readLong(), readLong(), readString())
//        myConcepts.add(c)
//        return c
//    }
//
//    @Throws(IOException::class)
//    fun readProperty(): SProperty? {
//        val b = readByte()
//        if (b == NULL) {
//            return null
//        }
//        if (b == PROPERTY_INDEX) {
//            return myProperties[readShort()]
//        }
//        if (b != PROPERTY) {
//            throw IOException(Integer.toHexString(b.toInt()))
//        }
//        val c = readConcept()
//        val p = MetaAdapterFactory.getProperty(SPropertyId(MetaIdHelper.getConcept(c), readLong()), readString())
//        myProperties.add(p)
//        return p
//    }
//
//    @Throws(IOException::class)
//    fun readReferenceLink(): SReferenceLink? {
//        val b = readByte()
//        if (b == NULL) {
//            return null
//        }
//        if (b == ASSOCIATION_INDEX) {
//            return myAssociations[readShort()]
//        }
//        if (b != ASSOCIATION) {
//            throw IOException(Integer.toHexString(b.toInt()))
//        }
//        val c = readConcept()
//        val l = MetaAdapterFactory.getReferenceLink(SReferenceLinkId(MetaIdHelper.getConcept(c), readLong()), readString())
//        myAssociations.add(l)
//        return l
//    }
//
//    @Throws(IOException::class)
//    fun readContainmentLink(): SContainmentLink? {
//        val b = readByte()
//        if (b == NULL) {
//            return null
//        }
//        if (b == AGGREGATION_INDEX) {
//            return myAggregations[readShort()]
//        }
//        if (b != AGGREGATION) {
//            throw IOException(Integer.toHexString(b.toInt()))
//        }
//        val c = readConcept()
//        val l = MetaAdapterFactory.getContainmentLink(SContainmentLinkId(MetaIdHelper.getConcept(c), readLong()), readString())
//        myAggregations.add(l)
//        return l
//    }

}

class SModelHeader {
    
}

fun loadMpsModelFromBinaryFile(inputStream: InputStream) : PhysicalModel {
    val mis = ModelInputStream(inputStream)
    loadHeader(mis)
    TODO()
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

    //val modelRef = `is`.readModelReference()
    val result = SModelHeader()
//    result.setModelReference(modelRef)
//    `is`.readInt() //left for compatibility: old version was here
//    `is`.mark(4)
//    if (`is`.readByte() === HEADER_ATTRIBUTES) {
//        result.setDoNotGenerate(`is`.readBoolean())
//        var propsCount = `is`.readShort()
//        while (propsCount > 0) {
//            val key = `is`.readString()
//            val value = `is`.readString()
//            result.setOptionalProperty(key, value)
//            propsCount--
//        }
//    } else {
//        `is`.reset()
//    }
//    assertSyncToken(`is`, HEADER_END)
    return result
}