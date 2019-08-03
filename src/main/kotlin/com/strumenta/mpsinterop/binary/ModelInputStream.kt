package com.strumenta.mpsinterop.binary

import com.strumenta.mpsinterop.logicalmodel.*
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.IOException
import java.io.InputStream
import java.util.*

/**
 * Original code written by Evgeny Gryaznov
 */
internal class ModelInputStream(val inputStream: InputStream) : DataInputStream(BufferedInputStream(inputStream, 65536)) {

    private val myStrings = ArrayList<String>(2048)
    private val myModelRefs = ArrayList<SModelReference>(1024)
    private val myModuleRefs = ArrayList<SModuleReference>(128)
//    private val myLanguages = ArrayList<SLanguage>(128)
    private val myConcepts = ArrayList<Concept>(128)
    private val myProperties = ArrayList<Property>(128)
//    private val myAssociations = ArrayList<ReferenceLink>(128)
//    private val myAggregations = ArrayList<ContainmentLink>(128)

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

    fun readModuleReference(): SModuleReference? {
        val c = readByte()
        if (c == NULL) {
            return null
        } else if (c == MODULEREF_INDEX) {
            val index = readInt()
            return myModuleRefs[index]
        }

        var id: ModuleId? = null
        if (c == MODULEREF_MODULEID) {
            id = readModuleID()
        }
        val ref = SModuleReference(readString()!!, id!!)
        myModuleRefs.add(ref)
        return ref
    }

    fun readModuleID(): ModuleId? {
        val c = readByte()
        return if (c == NULL) {
            null
        } else if (c == MODULEID_REGULAR) {
            val uuid = UUID(readLong(), readLong())
            ModuleId.regular(uuid)
        } else if (c == MODULEID_FOREIGN) {
            ModuleId.foreign(readString()!!)
        } else {
            throw IOException("unknown id")
        }
    }

    fun readModelReference(): SModelReference? {
        val c = readByte()
        if (c == NULL) {
            return null
        } else if (c == MODELREF_INDEX) {
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
        return if (c == NULL) {
            null
        } else if (c == MODELID_REGULAR) {
            SModelId.regular(readUUID())
        } else if (c == MODELID_FOREIGN) {
            SModelId.foreign(readString()!!)
        } else if (c == MODELID_STRING) {
            createModelId(readString()!!)
        } else if (c == MODELID_INTEGER) {
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
            NULL -> null
            NODEID_LONG -> NodeId.regular(readLong())
            NODEID_STRING -> NodeId.fromString(readString()!!)

            else -> throw IOException("no id")
        }
    }
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
    fun readConcept(): Concept? {
        val b = readByte()
        if (b == NULL) {
            return null
        }
        if (b == CONCEPT_INDEX) {
            return myConcepts[readShort().toInt()]
        }
        if (b != CONCEPT) {
            throw IOException(Integer.toHexString(b.toInt()))
        }
        readLong()
        readLong()
        readLong()
        readString()
        TODO()
        // val c = MetaAdapterFactory.getConcept(readLong(), readLong(), readLong(), readString())
        // myConcepts.add(c)
        // return c
    }
//
//    @Throws(IOException::class)
    fun readProperty(): Property? {
        val b = readByte()
        println("readProperty $b")
        if (b == NULL) {
            return null
        }
        if (b == PROPERTY_INDEX) {
            return myProperties[readShort().toInt()]
        }
        if (b != PROPERTY) {
            throw IOException(Integer.toHexString(b.toInt()))
        }
        val c = readConcept()
        val propertyId = readLong()
        val propertyName = readString()
        val propertyType = TODO()
        // val p = MetaAdapterFactory.getProperty(AbsolutePropertyId(MetaIdHelper.getConcept(c), readLong()), readString())
        val p = Property(AbsolutePropertyId(c!!.absoluteID!!, propertyId), propertyName!!, propertyType)
        myProperties.add(p)
        return p
    }

    @Throws(IOException::class)
    fun readReferenceLink(): ReferenceLink? {
        val b = readByte()
        if (b == NULL) {
            return null
        }
        if (b == ASSOCIATION_INDEX) {
            TODO()
            // return myAssociations[readShort()]
        }
        if (b != ASSOCIATION) {
            throw IOException(Integer.toHexString(b.toInt()))
        }
        val c = readConcept()
//        val l = MetaAdapterFactory.getReferenceLink(AbsoluteReferenceLinkId(MetaIdHelper.getConcept(c), readLong()), readString())
//        myAssociations.add(l)
//        return l
        TODO()
    }

    @Throws(IOException::class)
    fun readContainmentLink(): ContainmentLink? {
        val b = readByte()
        if (b == NULL) {
            return null
        }
        if (b == AGGREGATION_INDEX) {
            TODO()
            // return myAggregations[readShort()]
        }
        if (b != AGGREGATION) {
            throw IOException(Integer.toHexString(b.toInt()))
        }
        val c = readConcept()
//        val l = MetaAdapterFactory.getContainmentLink(AbsoluteContainmentLinkId(MetaIdHelper.getConcept(c), readLong()), readString())
//        myAggregations.add(l)
//        return l
        TODO()
    }
}