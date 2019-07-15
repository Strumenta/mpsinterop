package com.strumenta.mpsinterop.binary

import com.strumenta.mpsinterop.physicalmodel.PhysicalModel
import java.util.UUID
import java.io.*
import java.util.HashMap
import java.io.IOException
import java.sql.Types.REF

// From ModelPersistence

val MODEL = "model"
val REF = "ref"
val MODEL_UID = "modelUID"
val NAME = "name"
val VALUE = "value"

val PERSISTENCE = "persistence"
val PERSISTENCE_VERSION = "version"

val FIRST_SUPPORTED_VERSION = 9
val LAST_VERSION = 9

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

open class SModelId {
    companion object {
        fun regular(uuid: UUID) : SModelId = RegularSModelId(uuid)
        fun foreign(id: String) : SModelId {
            TODO()
        }
    }
}

internal class RegularSModelId(val uuid: UUID) : SModelId()

class IntegerSModelId(val id: Int) : SModelId() {

}

open class ModuleId {
    companion object {
        fun regular(uuid: UUID) : ModuleId = RegularModuleId(uuid)
        fun foreign(id: String) : ModuleId {
            TODO()
        }
    }
}

internal class RegularModuleId(val uuid: UUID) : ModuleId()

data class SModelReference(val moduleRef : SModuleReference?, val id: SModelId, val name: String) {

}

data class SModuleReference(val name: String, val id: ModuleId)

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
    //private val myMetaInfoProvider: MetaModelInfoProvider? = null

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

//    fun setModelReference(@Nullable modelRef: SModelReference?) {
//        myModelRef = modelRef
//    }
//
//    fun getOptionalProperties(): Map<String, String> {
//        return Collections.unmodifiableMap(myOptionalProperties)
//    }
//
//    fun getOptionalProperty(key: String): String {
//        return myOptionalProperties[key]
//    }
//
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

    /**
     * PROVISIONAL, DO NOT USE (unless your name starts with 'A' and you know what you're doing)
     *
     * This is per-model mechanism to alter meta-model (aka structure model) information used in persistence.
     * Generally, this mechanism shall not be in use, and `null` value is legitimate default, which means
     * native MPS mechanism of SConcept (and ConceptDescriptors) would be in use.
     * However, certain scenarios (command-line merge and ant task to convert models to binary) can't yet afford starting whole
     * MPS and thus shall rely on meta-information read from model files (which is generally sufficient to write the files back).
     *
     * For these scenarios, we used to have global [jetbrains.mps.persistence.ModelEnvironmentInfo], which is global and a bit
     * outdated for modern persistence, hence it has been replaced with MetaModelInfoProvider, although this solution is provisional
     * and likely to get changed in future (perhaps, class known now as IdInfoCollector would replace it).
     */
//    fun setMetaInfoProvider(@Nullable mmiProvider: MetaModelInfoProvider) {
//        myMetaInfoProvider = mmiProvider
//    }
//
//    fun getMetaInfoProvider(): MetaModelInfoProvider? {
//        return myMetaInfoProvider
//    }
//
//    fun create(persistenceVersion: Int): SModelHeader {
//        val header = SModelHeader()
//        header.setPersistenceVersion(persistenceVersion)
//        return header
//    }
//
//    // FIXME move save and load into respective class (binary persistence)
//    @Throws(IOException::class)
//    fun save(stream: ModelOutputStream) {
//        stream.writeByte(77)
//        stream.writeString(if (myModelRef == null) null else PersistenceFacade.getInstance().asString(myModelRef))
//        stream.writeInt(myPersistenceVersion)
//        stream.writeInt(0) //version was here
//        stream.writeBoolean(doNotGenerate)
//        stream.writeInt(myOptionalProperties.size())
//        for (ss in myOptionalProperties.entrySet()) {
//            stream.writeString(ss.key)
//            stream.writeString(ss.value)
//        }
//    }
//
//    @Throws(IOException::class)
//    fun load(stream: ModelInputStream): SModelHeader {
//        if (stream.readByte().toInt() != 77) throw IOException("bad stream: no model header start marker")
//        val result = SModelHeader()
//        val s = stream.readString()
//        result.setModelReference(if (s == null) null else PersistenceFacade.getInstance().createModelReference(s))
//        result.setPersistenceVersion(stream.readInt())
//        stream.readInt() //old model version was here
//        result.setDoNotGenerate(stream.readBoolean())
//        for (size in stream.readInt() downTo 1) {
//            result.setOptionalProperty(stream.readString(), stream.readString())
//        }
//        return result
//    }
//
//    fun createCopy(): SModelHeader {
//        val copy = SModelHeader()
//        copy.myModelRef = myModelRef
//        copy.myPersistenceVersion = myPersistenceVersion
//        copy.doNotGenerate = doNotGenerate
//        copy.myOptionalProperties.putAll(myOptionalProperties)
//        return copy
//    }
}

fun loadMpsModelFromBinaryFile(inputStream: InputStream) : SModel {
    val mis = ModelInputStream(inputStream)
    val modelHeader = loadHeader(mis)
    val model = SModel(modelHeader.getModelReference(), modelHeader)
    val bp = BinaryPersistence(/*if (mmiProvider == null) RegularMetaModelInfo() else mmiProvider,*/ model)
    val rh = bp.loadModelProperties(mis)
//    rh.requestInterfaceOnly(interfaceOnly)
//
    val reader = NodesReader(modelHeader.getModelReference()!!, mis, rh)
    println()
    println("readNodesInto")
    println()
    reader.readNodesInto(model)
//    return ModelLoadResult(model, if (reader.hasSkippedNodes()) ModelLoadingState.INTERFACE_LOADED else ModelLoadingState.FULLY_LOADED)
            //TODO()
    return model
}

class SModel(modelReference: SModelReference?, val modelHeader: SModelHeader) {

    val name: String
        get() = modelHeader.modelRef!!.name
}

@Throws(IOException::class)
private fun loadHeader(mis: ModelInputStream): SModelHeader {
    if (mis.readInt() !== HEADER_START) {
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
    mis.readInt() //left for compatibility: old version was here
    mis.mark(4)
    if (mis.readByte() === HEADER_ATTRIBUTES) {
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