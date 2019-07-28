package com.strumenta.mpsinterop.binary

import com.strumenta.mpsinterop.logicalmodel.*
import com.strumenta.mpsinterop.physicalmodel.PhysicalModel
import java.io.IOException;
import java.util.*

internal class LanguageLoaderHelper {
    //private val languageNamesByID = HashMap<LanguageUUID, String>()
    private val languageByID = HashMap<LanguageUUID, Language>()
    fun registerLanguage(id: UUID, name: String) {
        if (id in languageByID) {
            require(languageByID[id]!!.name == name)
        } else {
            languageByID[id] = Language(id, name)
        }
    }
    fun loadedLanguages() : Set<Language> {
        return languageByID.values.toSet()
    }
}

/**
 * @author evgeny
 * @author Artem Tikhomirov
 */
internal class BinaryPersistence {


//    @Throws(ModelReadException::class)
//    fun readHeader(@NotNull source: StreamDataSource): SModelHeader {
//        var mis: ModelInputStream? = null
//        try {
//            mis = ModelInputStream(source.openInputStream())
//            return loadHeader(mis)
//        } catch (e: IOException) {
//            throw ModelReadException("Couldn't read model: " + e.message, e)
//        } finally {
//            FileUtil.closeFileSafe(mis)
//        }
//    }
//
//    @Throws(ModelReadException::class)
//    fun readModel(@NotNull header: SModelHeader, @NotNull source: StreamDataSource, interfaceOnly: Boolean): ModelLoadResult {
//        val desiredModelRef = header.getModelReference()
//        try {
//            val rv = loadModel(source.openInputStream(), interfaceOnly, header.getMetaInfoProvider())
//            val actualModelRef = rv.getModel().getReference()
//            if (actualModelRef != desiredModelRef) {
//                throw ModelReadException(String.format("Intended to read model %s, actually read %s", desiredModelRef, actualModelRef), null, actualModelRef)
//            }
//            return rv
//        } catch (e: IOException) {
//            throw ModelReadException("Couldn't read model: $e", e, desiredModelRef)
//        }
//
//    }
//
//    @Throws(IOException::class)
//    fun writeModel(@NotNull model: SModel, @NotNull dataSource: StreamDataSource) {
//        if (dataSource.isReadOnly()) {
//            throw IOException(String.format("`%s' is read-only", dataSource.getLocation()))
//        }
//        writeModel(model, dataSource.openOutputStream())
//    }
//
//    @Throws(IOException::class)
//    fun writeModel(@NotNull model: SModel, @NotNull stream: OutputStream) {
//        var os: ModelOutputStream? = null
//        try {
//            os = ModelOutputStream(stream)
//            saveModel(model, os)
//        } finally {
//            FileUtil.closeFileSafe(os)
//        }
//    }
//
//    fun getDigestMap(model: jetbrains.mps.smodel.SModel, @Nullable mmiProvider: MetaModelInfoProvider?): Map<String, String> {
//        val result = LinkedHashMap<String, String>()
//        var meta: IdInfoRegistry? = null
//        var os = ModelDigestUtil.createDigestBuilderOutputStream()
//        try {
//            val bp = BinaryPersistence(if (mmiProvider == null) RegularMetaModelInfo() else mmiProvider, model)
//            val mos = ModelOutputStream(os)
//            meta = bp.saveModelProperties(mos)
//            mos.flush()
//        } catch (ignored: IOException) {
//            assert(false)
//            /* should never happen */
//        }
//
//        result[GeneratableSModel.HEADER] = os.getResult()
//
//        assert(meta != null)
//        // In fact, would be better to translate index attribute of any XXXInfo element into
//        // a value not related to meta-element position in the registry. Otherwise, almost any change
//        // in a model (e.g. addition of a new root or new property value) might affect all other root hashes
//        // as the index of meta-model elements might change. However, as long as our binary models are not exposed
//        // for user editing, we don't care.
//
//        for (node in model.getRootNodes()) {
//            os = ModelDigestUtil.createDigestBuilderOutputStream()
//            try {
//                val mos = ModelOutputStream(os)
//                NodesWriter(model.getReference(), mos, meta).writeNode(node)
//                mos.flush()
//            } catch (ignored: IOException) {
//                assert(false)
//                /* should never happen */
//            }
//
//            val nodeId = node.getNodeId()
//            if (nodeId != null) {
//                result[nodeId!!.toString()] = os.getResult()
//            }
//        }
//
//        return result
//    }


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


//    @NotNull
//    @Throws(IOException::class)
//    private fun loadHeader(`is`: ModelInputStream): SModelHeader {
//        if (`is`.readInt() != HEADER_START) {
//            throw IOException("bad stream, no header")
//        }
//
//        val streamId = `is`.readInt()
//        if (streamId == STREAM_ID_V1) {
//            throw IOException(String.format("Can't read old binary persistence version (%x), please re-save models", streamId))
//        }
//        if (streamId != STREAM_ID) {
//            throw IOException(String.format("bad stream, unknown version: %x", streamId))
//        }
//
//        val modelRef = `is`.readModelReference()
//        val result = SModelHeader()
//        result.setModelReference(modelRef)
//        `is`.readInt() //left for compatibility: old version was here
//        `is`.mark(4)
//        if (`is`.readByte() == HEADER_ATTRIBUTES) {
//            result.setDoNotGenerate(`is`.readBoolean())
//            var propsCount = `is`.readShort().toInt()
//            while (propsCount > 0) {
//                val key = `is`.readString()
//                val value = `is`.readString()
//                result.setOptionalProperty(key, value)
//                propsCount--
//            }
//        } else {
//            `is`.reset()
//        }
//        assertSyncToken(`is`, HEADER_END)
//        return result
//    }
//
//    @NotNull
//    @Throws(IOException::class)
//    private fun loadModel(`is`: InputStream, interfaceOnly: Boolean, @Nullable mmiProvider: MetaModelInfoProvider?): ModelLoadResult {
//        var mis: ModelInputStream? = null
//        try {
//            mis = ModelInputStream(`is`)
//            val modelHeader = loadHeader(mis)
//
//            val model = DefaultSModel(modelHeader.getModelReference(), modelHeader)
//            val bp = BinaryPersistence(if (mmiProvider == null) RegularMetaModelInfo() else mmiProvider, model)
//            val rh = bp.loadModelProperties(mis)
//            rh.requestInterfaceOnly(interfaceOnly)
//
//            val reader = NodesReader(modelHeader.getModelReference(), mis, rh)
//            reader.readNodesInto(model)
//            return ModelLoadResult(model, if (reader.hasSkippedNodes()) ModelLoadingState.INTERFACE_LOADED else ModelLoadingState.FULLY_LOADED)
//        } finally {
//            FileUtil.closeFileSafe(mis)
//        }
//    }
//
//    @Throws(IOException::class)
//    private fun saveModel(model: SModel, os: ModelOutputStream) {
//        val mmiProvider: MetaModelInfoProvider
//        if (model is DefaultSModel && (model as DefaultSModel).getSModelHeader().getMetaInfoProvider() != null) {
//            mmiProvider = (model as DefaultSModel).getSModelHeader().getMetaInfoProvider()
//        } else {
//            mmiProvider = RegularMetaModelInfo()
//        }
//        val bp = BinaryPersistence(mmiProvider, model)
//        val meta = bp.saveModelProperties(os)
//
//        val roots = IterableUtil.asCollection(model.getRootNodes())
//        NodesWriter(model.getReference(), os, meta).writeNodes(roots)
//    }

//    constructor(mmiProvider: MetaModelInfoProvider, modelData: SModel) {
//        metaInfoProvider = mmiProvider
//        loadingModel = modelData
//    }

    internal fun loadModelProperties(mis: ModelInputStream,
                                     languageLoaderHelper: LanguageLoaderHelper,
                                     model: PhysicalModel): ReadHelper {
        val readHelper = loadRegistry(mis, languageLoaderHelper, model)
//
        loadUsedLanguages(mis, languageLoaderHelper)
//
        for (ref in loadModuleRefList(mis)) {
            // FIXME add temporary code to read both module ref and SLanguage in 3.4 (write SLangugae, read both)
            //SModelLegacy(loadingModel).addEngagedOnGenerationLanguage(ref)
        }
        for (ref in loadModuleRefList(mis)) {
            //loadingModel.addDevKit(ref)
        }

        loadImports(mis)
//        for (imp in loadImports(mis)) {
//            //loadingModel.addModelImport(imp)
//        }

        assertSyncToken(mis, MODEL_START)
//
        return readHelper
//        TODO()
    }
//
//    @Throws(IOException::class)
//    private fun saveModelProperties(os: ModelOutputStream): IdInfoRegistry {
//        // header
//        os.writeInt(HEADER_START)
//        os.writeInt(STREAM_ID)
//        os.writeModelReference(loadingModel.getReference())
//        os.writeInt(-1)  //old model version
//        if (loadingModel is DefaultSModel) {
//            os.writeByte(HEADER_ATTRIBUTES)
//            val mh = (loadingModel as DefaultSModel).getSModelHeader()
//            os.writeBoolean(mh.isDoNotGenerate())
//            val props = HashMap(mh.getOptionalProperties())
//            os.writeShort(props.size)
//            for (e in props.entries) {
//                os.writeString(e.key)
//                os.writeString(e.value)
//            }
//        }
//        os.writeInt(HEADER_END)
//
//        val rv = saveRegistry(os)
//
//        //languages
//        saveUsedLanguages(os)
//        saveModuleRefList(loadingModel.engagedOnGenerationLanguages(), os)
//        saveModuleRefList(loadingModel.importedDevkits(), os)
//
//        // imports
//        saveImports(loadingModel.importedModels(), os)
//        // no need to save implicit imports as we serialize them ad-hoc, the moment we find external reference from a node
//
//        os.writeInt(MODEL_START)
//        return rv
//    }
//
//    @Throws(IOException::class)
//    private fun saveRegistry(os: ModelOutputStream): IdInfoRegistry {
//        os.writeInt(REGISTRY_START)
//        val metaInfo = IdInfoRegistry()
//        IdInfoCollector(metaInfo, metaInfoProvider).fill(loadingModel.getRootNodes())
//        val languagesInUse = metaInfo.getLanguagesInUse()
//        os.writeShort(languagesInUse.size)
//        // We use position of an element during persistence as its index, thus don't need to
//        // keep the index value - can restore it during read
//        var langIndex: Int
//        var conceptIndex: Int
//        var propertyIndex: Int
//        var associationIndex: Int
//        var aggregationIndex: Int
//        aggregationIndex = 0
//        associationIndex = aggregationIndex
//        propertyIndex = associationIndex
//        conceptIndex = propertyIndex
//        langIndex = conceptIndex
//        for (ul in languagesInUse) {
//            os.writeUUID(ul.getLanguageId().getIdValue())
//            os.writeString(ul.getName())
//            ul.setIntIndex(langIndex++)
//            //
//            val conceptsInUse = ul.getConceptsInUse()
//            os.writeShort(conceptsInUse.size)
//            for (ci in conceptsInUse) {
//                os.writeLong(ci.getConceptId().getIdValue())
//                assert(ul.getName().equals(NameUtil.namespaceFromConceptFQName(ci.getName()))) { "We save concept short name. This check ensures we can re-construct fqn based on language name" }
//                os.writeString(ci.getBriefName())
//                os.writeByte(ci.getKind().ordinal() shl 4 or ci.getScope().ordinal())
//                if (ci.isImplementationWithStub()) {
//                    os.writeByte(STUB_ID)
//                    os.writeLong(ci.getStubCounterpart().getIdValue())
//                } else {
//                    os.writeByte(STUB_NONE)
//                }
//                ci.setIntIndex(conceptIndex++)
//                //
//                val propertiesInUse = ci.getPropertiesInUse()
//                os.writeShort(propertiesInUse.size)
//                for (pi in propertiesInUse) {
//                    os.writeLong(pi.getPropertyId().getIdValue())
//                    os.writeString(pi.getName())
//                    pi.setIntIndex(propertyIndex++)
//                }
//                //
//                val associationsInUse = ci.getAssociationsInUse()
//                os.writeShort(associationsInUse.size)
//                for (li in associationsInUse) {
//                    os.writeLong(li.getLinkId().getIdValue())
//                    os.writeString(li.getName())
//                    li.setIntIndex(associationIndex++)
//                }
//                //
//                val aggregationsInUse = ci.getAggregationsInUse()
//                os.writeShort(aggregationsInUse.size)
//                for (li in aggregationsInUse) {
//                    os.writeLong(li.getLinkId().getIdValue())
//                    os.writeString(li.getName())
//                    os.writeBoolean(li.isUnordered())
//                    li.setIntIndex(aggregationIndex++)
//                }
//            }
//        }
//        os.writeInt(REGISTRY_END)
//        return metaInfo
//    }
//

    private fun loadRegistry(mis: ModelInputStream,
                             languageLoaderHelper: LanguageLoaderHelper,
                             model: PhysicalModel): ReadHelper {
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
        //println("langCount $langCount")
        while (langCount-- > 0) {
            val languageId = mis.readUUID()
            val langName = mis.readString()
            languageLoaderHelper.registerLanguage(languageId, langName!!)
            model.putLanguageInRegistry(languageId, langName)
            //println("langName $langName")

            //rh.withLanguage(languageId, langName, langIndex++)
//            //
            var conceptCount = mis.readShort().toInt()
            //println("conceptCount $conceptCount")
            while (conceptCount-- > 0) {
                val conceptId = SConceptId(languageId, mis.readLong())
                //println("conceptId $conceptId")
                val conceptName = mis.readString()
                //println("  conceptName $conceptName")
                val flags = mis.readByte().toInt()
                //println("  flags $flags")
                val staticScopeValue = flags and 0x0f
                val conceptKindValue = flags shr 4 and 0x0f
                //println("  staticScopeValue $staticScopeValue")
                //println("  conceptKindValue $conceptKindValue")
                val stubToken = mis.readByte().toInt()
                val stubId: SConceptId?
                if (stubToken == STUB_NONE.toInt()) {
                    stubId = null
                } else {
                    assert(stubToken == STUB_ID.toInt()) { "StubToken expected to be NONE ($STUB_NONE) or ID ($STUB_ID) instead it is $stubToken" }
                    stubId = SConceptId(languageId, mis.readLong())
                }

                val conceptKind = ConceptKind.values()[flags shr 4 and 0x0f]

                val concept = rh.withConcept(conceptIndex, conceptId,
                        langName,
                        conceptName!!, conceptKind)
                model.registerConcept(concept)
//                rh.withConcept(conceptId, conceptName, StaticScope.values()[flags and 0x0f], ConceptKind.values()[flags shr 4 and 0x0f], stubId, conceptIndex++)
//                //
                conceptIndex++
                var propertyCount = mis.readShort().toInt()
                //println("  propertyCount $propertyCount")
                while (propertyCount-- > 0) {
                    val propertyId = mis.readLong()
                    val propertyName = mis.readString()!!
                    //println("PROP $conceptId $propertyName")
                    rh.property(SPropertyId(conceptId, propertyId), propertyName, propertyIndex)
                    propertyIndex++
                }
//                //
                var associationCount = mis.readShort().toInt()
                //println("  associationCount $associationCount")
                while (associationCount-- > 0) {
                    val id = mis.readLong()
                    val name = mis.readString()
                    //println("     $id $name")
                    rh.association(SReferenceLinkId(conceptId, id), name!!, associationIndex++)
                }
//                //
                var aggregationCount = mis.readShort().toInt()
                //println("  aggregationCount $aggregationCount")
                while (aggregationCount-- > 0) {
                    rh.aggregation(SContainmentLinkId(conceptId, mis.readLong()), mis.readString()!!, mis.readBoolean(), aggregationIndex++)
                }
            }
        }
        assertSyncToken(mis, REGISTRY_END)
        return rh
    }

//    data class SConceptId(val languageId: LanguageUUID, val readLong: Long) {
//
//    }
//
//    @Throws(IOException::class)
//    private fun saveUsedLanguages(os: ModelOutputStream) {
//        val refs = loadingModel.usedLanguages()
//        os.writeShort(refs.size)
//        for (l in refs) {
//            // id, name, version
//            os.writeUUID(MetaIdHelper.getLanguage(l).getIdValue())
//            os.writeString(l.getQualifiedName())
//        }
//    }
//
//    @Throws(IOException::class)
    private fun loadUsedLanguages(`is`: ModelInputStream, languageLoaderHelper: LanguageLoaderHelper) {
        val size = `is`.readShort().toInt()
        for (i in 0 until size) {
            val id = `is`.readUUID()
            val name = `is`.readString()
            languageLoaderHelper.registerLanguage(id, name!!)
            //println("used language $id $name")
            //val l = MetaAdapterFactory.getLanguage(id, name)
            //loadingModel.addLanguage(l)
        }
    }
//
//    @Throws(IOException::class)
//    private fun saveModuleRefList(refs: Collection<SModuleReference>, os: ModelOutputStream) {
//        os.writeShort(refs.size)
//        for (ref in refs) {
//            os.writeModuleReference(ref)
//        }
//    }
//
//    @Throws(IOException::class)
    private fun loadModuleRefList(`is`: ModelInputStream): Collection<SModuleReference> {
        val size = `is`.readShort().toInt()
        val result = ArrayList<SModuleReference>(size)
        for (i in 0 until size) {
            result.add(`is`.readModuleReference()!!)
        }
        return result
    }
//
//    @Throws(IOException::class)
//    private fun saveImports(elements: Collection<ImportElement>, os: ModelOutputStream) {
//        os.writeInt(elements.size)
//        for (element in elements) {
//            os.writeModelReference(element.getModelReference())
//            os.writeInt(element.getUsedVersion())
//        }
//    }
//
//    @Throws(IOException::class)
    private fun loadImports(`is`: ModelInputStream)/*: List<ImportElement>*/ {
        val size = `is`.readInt()
        //val result = ArrayList<ImportElement>()
        for (i in 0 until size) {
            val ref = `is`.readModelReference()
            val i = `is`.readInt()
            //result.add(ImportElement(ref, -1, `is`.readInt()))
        }
        //return result
    }
//
//    @Throws(IOException::class)
//    fun index(content: InputStream, consumer: Callback) {
//        var mis: ModelInputStream? = null
//        try {
//            mis = ModelInputStream(content)
//            val modelHeader = loadHeader(mis)
//            val model = DefaultSModel(modelHeader.getModelReference(), modelHeader)
//            val bp = BinaryPersistence(StuffedMetaModelInfo(BaseMetaModelInfo()), model)
//            val readHelper = bp.loadModelProperties(mis)
//            for (element in model.importedModels()) {
//                consumer.imports(element.getModelReference())
//            }
//            for (cid in readHelper.getParticipatingConcepts()) {
//                consumer.instances(cid)
//            }
//            readHelper.requestInterfaceOnly(false)
//            val reader = NodesReader(modelHeader.getModelReference(), mis, readHelper)
//            val externalNodes = HashSet<SNodeId>()
//            val localNodes = HashSet<SNodeId>()
//            reader.collectExternalTargets(externalNodes)
//            reader.collectLocalTargets(localNodes)
//            reader.readChildren(null)
//            for (n in externalNodes) {
//                consumer.externalNodeRef(n)
//            }
//            for (n in localNodes) {
//                consumer.localNodeRef(n)
//            }
//        } finally {
//            FileUtil.closeFileSafe(mis)
//        }
//    }
//
//    @Throws(IOException::class)
//    fun getModelData(input: InputStream): SModelData {
//        val result = loadModel(input, false, StuffedMetaModelInfo(BaseMetaModelInfo()))
//        return result.getModel()
//    }

    private fun assertSyncToken(`is`: ModelInputStream, token: Int) {
        if (`is`.readInt() != token) {
            throw IOException("bad stream, no sync token")
        }
    }
}

