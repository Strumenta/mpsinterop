package com.strumenta.mps.binary

import com.strumenta.deprecated_mpsinterop.logicalmodel.* // ktlint-disable
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.IOException
import java.io.InputStream
import java.util.* // ktlint-disable

/**
 * Original code written by Evgeny Gryaznov
 */
class ModelInputStream(val inputStream: InputStream) : DataInputStream(BufferedInputStream(inputStream, 65536)) {

    private val myStrings = ArrayList<String>(2048)
    private val myModelRefs = ArrayList<SModelReference>(1024)
    private val myModuleRefs = ArrayList<SModuleReference>(128)
    private val myConcepts = ArrayList<Concept>(128)
    private val myProperties = ArrayList<Property>(128)

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
        if (c == com.strumenta.deprecated_mpsinterop.binary.NULL) {
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

    fun readModuleReference(): SModuleReference? {
        val c = readByte()
        if (c == com.strumenta.deprecated_mpsinterop.binary.NULL) {
            return null
        } else if (c == com.strumenta.deprecated_mpsinterop.binary.MODULEREF_INDEX) {
            val index = readInt()
            return myModuleRefs[index]
        }

        var id: ModuleId? = null
        if (c == com.strumenta.deprecated_mpsinterop.binary.MODULEREF_MODULEID) {
            id = readModuleID()
        }
        val ref = SModuleReference(readString()!!, id!!)
        myModuleRefs.add(ref)
        return ref
    }

    fun readModuleID(): ModuleId? {
        val c = readByte()
        return if (c == com.strumenta.deprecated_mpsinterop.binary.NULL) {
            null
        } else if (c == com.strumenta.deprecated_mpsinterop.binary.MODULEID_REGULAR) {
            val uuid = UUID(readLong(), readLong())
            ModuleId.regular(uuid)
        } else if (c == com.strumenta.deprecated_mpsinterop.binary.MODULEID_FOREIGN) {
            ModuleId.foreign(readString()!!)
        } else {
            throw IOException("unknown id")
        }
    }

    fun readModelReference(): SModelReference? {
        val c = readByte()
        if (c == com.strumenta.deprecated_mpsinterop.binary.NULL) {
            return null
        } else if (c == com.strumenta.deprecated_mpsinterop.binary.MODELREF_INDEX) {
            val index = readInt()
            return myModelRefs[index]
        }

        val id = readModelID()
        val modelName = readString()
        val moduleRef = readModuleReference()
        val ref = SModelReference(moduleRef, id!!, modelName!!)
        myModelRefs.add(ref)
        return ref
    }

    fun readModelID(): SModelId? {
        val c = readByte()
        return if (c == com.strumenta.deprecated_mpsinterop.binary.NULL) {
            null
        } else if (c == com.strumenta.deprecated_mpsinterop.binary.MODELID_REGULAR) {
            SModelId.regular(readUUID())
        } else if (c == com.strumenta.deprecated_mpsinterop.binary.MODELID_FOREIGN) {
            SModelId.foreign(readString()!!)
        } else if (c == com.strumenta.deprecated_mpsinterop.binary.MODELID_STRING) {
            createModelId(readString()!!)
        } else if (c == com.strumenta.deprecated_mpsinterop.binary.MODELID_INTEGER) {
            IntegerSModelId(readInt())
        } else {
            throw IOException("unknown id")
        }
    }

    private fun createModelId(s: String): SModelId {
        return when {
            s.startsWith("java:") -> SModelId.foreign(s)
            else -> TODO(s)
        }
    }

    fun readNodeId(): NodeId? {
        val c = readByte()
        return when (c) {
            com.strumenta.deprecated_mpsinterop.binary.NULL -> null
            com.strumenta.deprecated_mpsinterop.binary.NODEID_LONG -> NodeId.regular(readLong())
            com.strumenta.deprecated_mpsinterop.binary.NODEID_STRING -> NodeId.fromString(readString()!!)

            else -> throw IOException("no id")
        }
    }

    @Throws(IOException::class)
    fun readUUID(): UUID {
        val headBits = readLong()
        val tailBits = readLong()
        return UUID(headBits, tailBits)
    }

    fun readConcept(): Concept? {
        val b = readByte()
        if (b == com.strumenta.deprecated_mpsinterop.binary.NULL) {
            return null
        }
        if (b == com.strumenta.deprecated_mpsinterop.binary.CONCEPT_INDEX) {
            return myConcepts[readShort().toInt()]
        }
        if (b != com.strumenta.deprecated_mpsinterop.binary.CONCEPT) {
            throw IOException(Integer.toHexString(b.toInt()))
        }
        readLong()
        readLong()
        readLong()
        readString()
        TODO()
    }

    fun readProperty(): Property? {
        val b = readByte()
        println("readProperty $b")
        if (b == com.strumenta.deprecated_mpsinterop.binary.NULL) {
            return null
        }
        if (b == com.strumenta.deprecated_mpsinterop.binary.PROPERTY_INDEX) {
            return myProperties[readShort().toInt()]
        }
        if (b != com.strumenta.deprecated_mpsinterop.binary.PROPERTY) {
            throw IOException(Integer.toHexString(b.toInt()))
        }
        val c = readConcept()
        val propertyId = readLong()
        val propertyName = readString()
        val propertyType = TODO()
        val p = Property(AbsolutePropertyId(c!!.absoluteID!!, propertyId), propertyName!!, propertyType)
        myProperties.add(p)
        return p
    }

    @Throws(IOException::class)
    fun readReferenceLink(): ReferenceLink? {
        val b = readByte()
        if (b == com.strumenta.deprecated_mpsinterop.binary.NULL) {
            return null
        }
        if (b == com.strumenta.deprecated_mpsinterop.binary.ASSOCIATION_INDEX) {
            TODO()
            // return myAssociations[readShort()]
        }
        if (b != com.strumenta.deprecated_mpsinterop.binary.ASSOCIATION) {
            throw IOException(Integer.toHexString(b.toInt()))
        }
        val c = readConcept()
        TODO()
    }

    @Throws(IOException::class)
    fun readContainmentLink(): ContainmentLink? {
        val b = readByte()
        if (b == com.strumenta.deprecated_mpsinterop.binary.NULL) {
            return null
        }
        if (b == com.strumenta.deprecated_mpsinterop.binary.AGGREGATION_INDEX) {
            TODO()
            // return myAggregations[readShort()]
        }
        if (b != com.strumenta.deprecated_mpsinterop.binary.AGGREGATION) {
            throw IOException(Integer.toHexString(b.toInt()))
        }
        val c = readConcept()
        TODO()
    }
}
