package com.strumenta.mpsinterop.binary

/*
 * Copyright 2003-2019 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



import java.io.IOException;
import java.util.*
import java.util.HashMap
import java.util.logging.LogManager




public interface MetaModelInfoProvider {

}


/**
 * @author evgeny, 11/21/12
 * @author Artem Tikhomirov
 */
public final class BinaryPersistence(model: SModel) {

    private var myMetaInfoProvider: MetaModelInfoProvider? = null
    private var myModelData: SModel? = null

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

    private fun BinaryPersistence(mmiProvider: MetaModelInfoProvider, modelData: SModel) {
        myMetaInfoProvider = mmiProvider
        myModelData = modelData
    }
    
    class ReadHelper(myMetaInfoProvider: MetaModelInfoProvider?) {
        //private val LOG = LogManager.getLogger(ReadHelper::class.java)
        val MODEL_SEPARATOR_CHAR = '.'
        val DYNAMIC_REFERENCE_ID = "^"

//        private var myModelRef: SModelReference
//        private var myModelByIx: Map<String, SModelReference>
//        private var myMaxImportIndex = 0
//        fun ReadHelper(modelRef: SModelReference): ??? {
//            myModelByIx = MapSequence.fromMap(HashMap<String, SModelReference>())
//            myModelRef = modelRef
//        }
//
//        fun addModelRef(index: String, modelRef: SModelReference) {
//            MapSequence.fromMap(myModelByIx).put(index, modelRef)
//        }
//
//        fun addImportToModel(model: SModel, index: String, modelUID: String?, version: Int, implicit: Boolean) {
//            if (modelUID == null) {
//                if (LOG.isEnabledFor(Level.ERROR)) {
//                    LOG.error("Error loading import element for index $index in $myModelRef")
//                }
//                return
//            }
//            val modelRef = VCSPersistenceUtil.createModelReference(modelUID)
//            val elem = SModel.ImportElement(modelRef, ++myMaxImportIndex, version)
//            if (implicit) {
//                model.getImplicitImportsSupport().addAdditionalModelVersion(elem)
//            } else {
//                model.addModelImport(elem)
//            }
//            addModelRef(index, modelRef)
//        }
//
//        fun getSModelReference(@NotNull ix: String?): SModelReference? {
//            return if (ix == null || ix.length == 0) myModelRef else MapSequence.fromMap(myModelByIx).get(ix)
//        }
//
//        @NotNull
//        fun readLink_internal(src: String?): Pair<Boolean, SNodeReference> {
//            // returns <true, xxx> - if src is Dynamic Reference
//            // [modelID.]nodeID | [modelID.]^
//            val result = Pair<Boolean, SNodeReference>(false, null)
//            if (src == null) {
//                return result
//            }
//            val dotIndex = src.indexOf(MODEL_SEPARATOR_CHAR.toInt())
//            val text = decode(src.substring(dotIndex + 1, src.length))
//            result.o1 = DYNAMIC_REFERENCE_ID == text
//            val modelRef = getSModelReference(if (dotIndex < 0) "" else src.substring(0, dotIndex))
//            val nodeId = if (result.o1) null else jetbrains.mps.smodel.SNodeId.fromString(text)
//            result.o2 = SNodePointer(modelRef, nodeId)
//            return result
//        }
//
//        fun readLinkId(src: String): SNodeReference {
//            // [modelID.]nodeID[:version] | [modelID.]^[:version]
//            return readLink_internal(src).o2
//        }
//
//        fun getStubConceptQualifiedName(type: String): String? {
//            val originalConcept = readType(type)
//            val lastDot = originalConcept.lastIndexOf('.')
//            return if (lastDot == -1) {
//                null
//            } else originalConcept.substring(0, lastDot + 1) + "Stub" + originalConcept.substring(lastDot + 1)
//        }
//
//        fun readType(s: String): String {
//            val ix = s.indexOf(MODEL_SEPARATOR_CHAR.toInt())
//            if (ix <= 0) {
//                // no model ID - fqName is here
////                if (LOG.isEnabledFor(Level.ERROR)) {
////                    LOG.error("Broken reference to type=$s in model $myModelRef")
////                }
//                return s.substring(ix + 1)
//            }
//            val modelRef = getSModelReference(s.substring(0, ix))
//            if (modelRef == null) {
////                if (LOG.isEnabledFor(Level.ERROR)) {
////                    LOG.error("couldn't create node '" + s.substring(ix + 1) + "' : import for index [" + s.substring(0, ix) + "] not found")
////                }
//                return s.substring(ix + 1)
//            } else {
//                return modelRef.name.getLongName() + '.'.toString() + s.substring(ix + 1)
//            }
//        }

        fun readRole(s: String): String {
            return s
        }

        fun readName(s: String): String {
            return s
        }

        fun decode(s: String): String {
            return s.replace("%d", ".").replace("%c", ":").replace("%p", "%")
        }
    }


    fun loadModelProperties(mis: ModelInputStream): ReadHelper {
        val readHelper = loadRegistry(mis)
//
//        loadUsedLanguages(mis)
//
//        for (ref in loadModuleRefList(mis)) {
//            // FIXME add temporary code to read both module ref and SLanguage in 3.4 (write SLangugae, read both)
//            SModelLegacy(myModelData).addEngagedOnGenerationLanguage(ref)
//        }
//        for (ref in loadModuleRefList(mis)) {
//            myModelData.addDevKit(ref)
//        }
//
//        for (imp in loadImports(mis)) myModelData.addModelImport(imp)
//
//        assertSyncToken(mis, MODEL_START)
//
//        return readHelper
        TODO()
    }
//
//    @Throws(IOException::class)
//    private fun saveModelProperties(os: ModelOutputStream): IdInfoRegistry {
//        // header
//        os.writeInt(HEADER_START)
//        os.writeInt(STREAM_ID)
//        os.writeModelReference(myModelData.getReference())
//        os.writeInt(-1)  //old model version
//        if (myModelData is DefaultSModel) {
//            os.writeByte(HEADER_ATTRIBUTES)
//            val mh = (myModelData as DefaultSModel).getSModelHeader()
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
//        saveModuleRefList(myModelData.engagedOnGenerationLanguages(), os)
//        saveModuleRefList(myModelData.importedDevkits(), os)
//
//        // imports
//        saveImports(myModelData.importedModels(), os)
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
//        IdInfoCollector(metaInfo, myMetaInfoProvider).fill(myModelData.getRootNodes())
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

    private fun loadRegistry(mis: ModelInputStream): ReadHelper {
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

        val rh = ReadHelper(myMetaInfoProvider)
        var langCount = mis.readShort().toInt()
        println("langCount $langCount")
        while (langCount-- > 0) {
            val languageId = SLanguageId(mis.readUUID())
            val langName = mis.readString()
            println("langName $langName")

            //rh.withLanguage(languageId, langName, langIndex++)
//            //
            var conceptCount = mis.readShort().toInt()
            println("conceptCount $conceptCount")
            while (conceptCount-- > 0) {
                val conceptId = SConceptId(languageId, mis.readLong())
                println("conceptId $conceptId")
                val conceptName = mis.readString()
                println("  conceptName $conceptName")
                val flags = mis.readByte().toInt()
                println("  flags $flags")
                val staticScopeValue = flags and 0x0f
                val conceptKindValue = flags shr 4 and 0x0f
                println("  staticScopeValue $staticScopeValue")
                println("  conceptKindValue $conceptKindValue")
                val stubToken = mis.readByte().toInt()
                val stubId: SConceptId?
                if (stubToken == STUB_NONE.toInt()) {
                    stubId = null
                } else {
                    assert(stubToken == STUB_ID.toInt()) { "StubToken expected to be NONE ($STUB_NONE) or ID ($STUB_ID) instead it is $stubToken" }
                    stubId = SConceptId(languageId, mis.readLong())
                }

//                rh.withConcept(conceptId, conceptName, StaticScope.values()[flags and 0x0f], ConceptKind.values()[flags shr 4 and 0x0f], stubId, conceptIndex++)
//                //
                conceptIndex++
                var propertyCount = mis.readShort().toInt()
                println("  propertyCount $propertyCount")
                while (propertyCount-- > 0) {
                    val propertyId = mis.readLong()
                    val propertyName = mis.readString()
                    //rh.property(SPropertyId(conceptId, mis.readLong()), mis.readString(), propertyIndex++)
                    propertyIndex++
                }
//                //
                var associationCount = mis.readShort().toInt()
                println("  associationCount $associationCount")
                while (associationCount-- > 0) {
                    val id = mis.readLong()
                    val name = mis.readString()
                    println("     $id $name")
                    associationIndex++
//                    rh.association(SReferenceLinkId(conceptId, mis.readLong()), mis.readString(), associationIndex++)
                }
//                //
                var aggregationCount = mis.readShort().toInt()
                println("  aggregationCount $aggregationCount")
                while (aggregationCount-- > 0) {
                    val id = mis.readLong()
                    val name = mis.readString()
                    println("     $id $name")
                    aggregationIndex++
//                    rh.aggregation(SContainmentLinkId(conceptId, mis.readLong()), mis.readString(), mis.readBoolean(), aggregationIndex++)
                }
            }
        }
        assertSyncToken(mis, REGISTRY_END)
        return rh
    }

    data class SConceptId(val languageId: SLanguageId, val readLong: Long) {

    }
//
//    @Throws(IOException::class)
//    private fun saveUsedLanguages(os: ModelOutputStream) {
//        val refs = myModelData.usedLanguages()
//        os.writeShort(refs.size)
//        for (l in refs) {
//            // id, name, version
//            os.writeUUID(MetaIdHelper.getLanguage(l).getIdValue())
//            os.writeString(l.getQualifiedName())
//        }
//    }
//
//    @Throws(IOException::class)
//    private fun loadUsedLanguages(`is`: ModelInputStream) {
//        val size = `is`.readShort().toInt()
//        for (i in 0 until size) {
//            val id = SLanguageId(`is`.readUUID())
//            val name = `is`.readString()
//            val l = MetaAdapterFactory.getLanguage(id, name)
//            myModelData.addLanguage(l)
//        }
//    }
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
//    private fun loadModuleRefList(`is`: ModelInputStream): Collection<SModuleReference> {
//        val size = `is`.readShort().toInt()
//        val result = ArrayList<SModuleReference>(size)
//        for (i in 0 until size) {
//            result.add(`is`.readModuleReference())
//        }
//        return result
//    }
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
//    private fun loadImports(`is`: ModelInputStream): List<ImportElement> {
//        val size = `is`.readInt()
//        val result = ArrayList<ImportElement>()
//        for (i in 0 until size) {
//            val ref = `is`.readModelReference()
//            result.add(ImportElement(ref, -1, `is`.readInt()))
//        }
//        return result
//    }
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

data class SLanguageId(val uuid: UUID) {

}


class SNodeReference {

}

class SConceptId(languageId: SLanguageId, idValue: Long) {

}
