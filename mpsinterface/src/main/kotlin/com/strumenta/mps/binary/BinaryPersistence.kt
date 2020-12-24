package com.strumenta.mps.binary

import com.strumenta.mps.logicalmodel.* // ktlint-disable
import java.io.IOException
import java.util.* // ktlint-disable

internal class LanguageLoaderHelper {
    // private val languageNamesByID = HashMap<LanguageUUID, String>()
    private val languageByID = HashMap<LanguageUUID, Language>()
    fun registerLanguage(id: UUID, name: String) {
        if (id in languageByID) {
            require(languageByID[id]!!.name == name)
        } else {
            languageByID[id] = Language(id, name)
        }
    }
    fun loadedLanguages(): Set<Language> {
        return languageByID.values.toSet()
    }
}

/**
 * @author evgeny
 * @author Artem Tikhomirov
 */
internal class BinaryPersistence {

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

    internal fun loadModelProperties(
        mis: ModelInputStream,
        languageLoaderHelper: LanguageLoaderHelper
    ): ReadHelper {
        val readHelper = loadRegistry(mis, languageLoaderHelper) // , model)

        loadUsedLanguages(mis, languageLoaderHelper)
        for (ref in loadModuleRefList(mis)) {
            // FIXME add temporary code to read both module ref and SLanguage in 3.4 (write SLangugae, read both)
            // SModelLegacy(loadingModel).addEngagedOnGenerationLanguage(ref)
        }
        for (ref in loadModuleRefList(mis)) {
            // loadingModel.addDevKit(ref)
        }

        loadImports(mis)
        assertSyncToken(mis, MODEL_START)
        return readHelper
    }

    private fun loadRegistry(
        mis: ModelInputStream,
        languageLoaderHelper: LanguageLoaderHelper,
        // model: PhysicalModel
    ): ReadHelper {
        assertSyncToken(mis, REGISTRY_START)
        // see #saveRegistry, we use position of an element in persistence as its index
        var langIndex: Int
        var conceptIndex: Int
        var propertyIndex: Int
        var associationIndex: Int
        var aggregationIndex: Int
        aggregationIndex = 0
        associationIndex = aggregationIndex
        propertyIndex = associationIndex
        conceptIndex = propertyIndex
        langIndex = conceptIndex

        val rh = ReadHelper()
        var langCount = mis.readShort().toInt()
        while (langCount-- > 0) {
            val languageId = mis.readUUID()
            val langName = mis.readString()
            languageLoaderHelper.registerLanguage(languageId, langName!!)

            var conceptCount = mis.readShort().toInt()
            while (conceptCount-- > 0) {
                val conceptId = AbsoluteConceptId(languageId, mis.readLong())
                val conceptName = mis.readString()
                val flags = mis.readByte().toInt()
                val staticScopeValue = flags and 0x0f
                val conceptKindValue = flags shr 4 and 0x0f
                val stubToken = mis.readByte().toInt()
                val stubId: AbsoluteConceptId?
                if (stubToken == STUB_NONE.toInt()) {
                    stubId = null
                } else {
                    assert(stubToken == STUB_ID.toInt()) { "StubToken expected to be NONE ($STUB_NONE) or ID ($STUB_ID) instead it is $stubToken" }
                    stubId = AbsoluteConceptId(languageId, mis.readLong())
                }

                val conceptKind = ConceptKind.values()[flags shr 4 and 0x0f]

                val concept = rh.withConcept(
                    conceptIndex,
                    conceptId,
                    langName,
                    conceptName!!,
                    conceptKind
                )
                conceptIndex++
                var propertyCount = mis.readShort().toInt()
                while (propertyCount-- > 0) {
                    val propertyId = mis.readLong()
                    val propertyName = mis.readString()!!
                    rh.property(AbsolutePropertyId(conceptId, propertyId), propertyName, propertyIndex)
                    propertyIndex++
                }
                var associationCount = mis.readShort().toInt()
                while (associationCount-- > 0) {
                    val id = mis.readLong()
                    val name = mis.readString()
                    rh.association(AbsoluteReferenceLinkId(conceptId, id), name!!, associationIndex++)
                }
                var aggregationCount = mis.readShort().toInt()
                while (aggregationCount-- > 0) {
                    rh.aggregation(AbsoluteContainmentLinkId(conceptId, mis.readLong()), mis.readString()!!, mis.readBoolean(), aggregationIndex++)
                }
            }
        }
        assertSyncToken(mis, REGISTRY_END)
        return rh
    }

    private fun loadUsedLanguages(`is`: ModelInputStream, languageLoaderHelper: LanguageLoaderHelper) {
        val size = `is`.readShort().toInt()
        for (i in 0 until size) {
            val id = `is`.readUUID()
            val name = `is`.readString()
            languageLoaderHelper.registerLanguage(id, name!!)
        }
    }

    private fun loadModuleRefList(`is`: ModelInputStream): Collection<SModuleReference> {
        val size = `is`.readShort().toInt()
        val result = ArrayList<SModuleReference>(size)
        for (i in 0 until size) {
            result.add(`is`.readModuleReference()!!)
        }
        return result
    }

    private fun loadImports(`is`: ModelInputStream) {
        val size = `is`.readInt()
        for (i in 0 until size) {
            val ref = `is`.readModelReference()
            val i = `is`.readInt()
        }
    }

    private fun assertSyncToken(`is`: ModelInputStream, token: Int) {
        if (`is`.readInt() != token) {
            throw IOException("bad stream, no sync token")
        }
    }
}
