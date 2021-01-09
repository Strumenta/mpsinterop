package com.strumenta.mps.binary

import com.strumenta.mps.logicalmodel.Node
import com.strumenta.mps.logicalmodel.SModelReference
import java.io.IOException
import java.sql.Types.REF
import java.util.* // ktlint-disable

// From ModelPersistence

private val MODEL = "model"
private val REF = "ref"
private val MODEL_UID = "modelUID"
private val NAME = "name"
private val VALUE = "value"

private val PERSISTENCE = "persistence"
private val PERSISTENCE_VERSION = "version"

private val FIRST_SUPPORTED_VERSION = 9
private val LAST_VERSION = 9

private val HEADER_READ_LIMIT = 1 shl 16 // allow for huge headers

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

internal const val NULL: Byte = 0x70
internal const val NODEID_STRING: Byte = 0x17
internal const val NODEID_LONG: Byte = 0x18
internal const val MODELID_STRING: Byte = 0x26
internal const val MODELID_REGULAR: Byte = 0x28
internal const val MODELID_FOREIGN: Byte = 0x27
internal const val MODELID_INTEGER: Byte = 0x29
internal const val NODEPTR: Byte = 0x44
internal const val MODULEID_FOREIGN: Byte = 0x47
internal const val MODULEID_REGULAR: Byte = 0x48
internal const val MODELREF: Byte = 7
internal const val MODELREF_INDEX: Byte = 9
internal const val MODULEREF_MODULEID: Byte = 0x17
internal const val MODULEREF_NAMEONLY: Byte = 0x18
internal const val MODULEREF_INDEX: Byte = 0x19
internal const val LANGUAGE: Byte = 0x30
internal const val LANGUAGE_INDEX: Byte = 0x31
internal const val CONCEPT: Byte = 0x32
internal const val CONCEPT_INDEX: Byte = 0x33
internal const val PROPERTY: Byte = 0x34
internal const val PROPERTY_INDEX: Byte = 0x35
internal const val ASSOCIATION: Byte = 0x36
internal const val ASSOCIATION_INDEX: Byte = 0x37
internal const val AGGREGATION: Byte = 0x38
internal const val AGGREGATION_INDEX: Byte = 0x39

class SModelHeader {
    val DO_NOT_GENERATE = "doNotGenerate"

    /*
   * Model is identified with SModelId, optional module id and has a name, these are elements we'd like to keep in header
   * for quick consideration. Although SModelReference has all these, and it seems straightforward to use it here,
   * it's not quite 'right' due to semantics attached - resolution of a model within a repository. However,
   * exposing 3 getters/setters (modelId, modelName, moduleId) is cumbersome, and SModel#getReference is @NotNull regardless of model
   * loaded/attached state, hence for the time being we decided to live with SModelReference here.
   */
    var modelRef: SModelReference? = null
    private var myPersistenceVersion = -1
    private var doNotGenerate = false
    private val myOptionalProperties = HashMap<String, String>()

    fun getPersistenceVersion(): Int {
        return myPersistenceVersion
    }

    fun setPersistenceVersion(persistenceVersion: Int) {
        myPersistenceVersion = persistenceVersion
    }

    fun isDoNotGenerate(): Boolean {
        return doNotGenerate
    }

    fun setDoNotGenerate(doNotGenerate: Boolean) {
        this.doNotGenerate = doNotGenerate
    }

    /**
     * DESIGN NOTE: SModelReference is not a persisted attribute of a model. Conceptually, reference emerges once we have
     * an object to point to. The moment we got SModelHeader there's nothing to point to yet, although one could construct
     * SModelReference with
     * @return `null` if model header is not initialized with a model reference
     */
    fun getModelReference(): SModelReference? {
        return modelRef
    }

    fun setOptionalProperty(key: String?, value: String?) {
        assert(!DO_NOT_GENERATE.equals(key))
        assert(!REF.equals(key))
        // roughly following http://www.w3.org/TR/2008/PER-xml-20080205/#NT-Name
        assert(key!!.matches("^[:A-Z_a-z][-:A-Z_a-z.0-9]*".toRegex())) { "bad key [$key]" }

        myOptionalProperties[key] = value!!
    }

    fun removeOptionalProperty(key: String) {
        myOptionalProperties.remove(key)
    }
}

class SModel(modelReference: SModelReference?, val modelHeader: SModelHeader) {
    val numberOfRoots: Int
        get() = roots.size
    val roots = LinkedList<Node>()
    fun addRootNode(root: Node) {
        roots.add(root)
    }

    fun named(name: String) = roots.find { it.name == name }

    val name: String
        get() = modelHeader.modelRef!!.name
}

fun loadHeader(mis: ModelInputStream): SModelHeader {
    if (mis.readInt() != HEADER_START) {
        throw IOException("bad stream, no header")
    }

    val streamId = mis.readInt()
    if (streamId == STREAM_ID_V1) {
        throw IOException(String.format("Can't read old binary persistence version (%x), please re-save models", streamId))
    }
    if (streamId != STREAM_ID) {
        throw IOException(String.format("bad stream, unknown version: %x", streamId))
    }

    val modelRef = mis.readModelReference()
    val result = SModelHeader()
    result.modelRef = modelRef
    mis.readInt() // left for compatibility: old version was here
    mis.mark(4)
    if (mis.readByte() == HEADER_ATTRIBUTES) {
        result.setDoNotGenerate(mis.readBoolean())
        var propsCount = mis.readShort()
        while (propsCount > 0) {
            val key = mis.readString()
            val value = mis.readString()
            result.setOptionalProperty(key, value)
            propsCount--
        }
    } else {
        mis.reset()
    }
    assertSyncToken(mis, HEADER_END)
    return result
}

private fun assertSyncToken(`is`: ModelInputStream, token: Int) {
    if (`is`.readInt() != token) {
        throw IOException("bad stream, no sync token")
    }
}
