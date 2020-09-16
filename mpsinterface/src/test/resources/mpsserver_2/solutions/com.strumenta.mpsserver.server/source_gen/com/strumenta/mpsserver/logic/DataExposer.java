package com.strumenta.mpsserver.logic;

/*Generated by MPS */

import org.jetbrains.mps.openapi.module.SRepository;
import java.util.Objects;
import java.util.Arrays;
import org.jetbrains.mps.openapi.language.SAbstractConcept;
import org.jetbrains.mps.openapi.language.SLanguage;
import jetbrains.mps.internal.collections.runtime.CollectionSequence;
import jetbrains.mps.smodel.language.LanguageRegistry;
import jetbrains.mps.internal.collections.runtime.Sequence;
import java.util.List;
import com.strumenta.mpsprotocol.data.ModuleInfo;
import java.util.LinkedList;
import org.jetbrains.mps.openapi.module.SModule;
import com.strumenta.mpsprotocol.data.SolutionInfo;
import jetbrains.mps.project.Solution;
import com.strumenta.mpsprotocol.data.LanguageInfo;
import java.util.function.Predicate;
import com.strumenta.mpsprotocol.data.LanguageInfoDetailed;
import com.strumenta.mpsprotocol.data.ModuleInfoDetailed;
import org.jetbrains.mps.openapi.model.SModel;
import com.strumenta.mpsprotocol.data.ModelInfo;
import javax.swing.SwingUtilities;
import jetbrains.mps.smodel.TrivialModelDescriptor;
import jetbrains.mps.extapi.model.EditableSModelBase;
import jetbrains.mps.internal.collections.runtime.ListSequence;
import org.jetbrains.mps.openapi.model.EditableSModel;
import com.strumenta.mpsprotocol.data.ModelInfoDetailed;
import org.jetbrains.mps.openapi.model.SNode;
import com.strumenta.mpsprotocol.data.NodeInfo;
import com.strumenta.mpsprotocol.data.NodeInfoDetailed;
import org.jetbrains.mps.openapi.model.SNodeId;
import com.strumenta.mpsprotocol.data.NodeIDInModel;
import com.strumenta.mpsprotocol.data.RegularNodeIDInfo;
import org.jetbrains.mps.openapi.language.SProperty;
import org.jetbrains.mps.openapi.model.SNodeAccessUtil;
import org.jetbrains.mps.openapi.model.SReference;
import com.strumenta.mpsprotocol.data.NodeIDInfo;
import com.strumenta.mpsprotocol.data.ForeignNodeIDInfo;
import jetbrains.mps.smodel.StringBasedIdForJavaStubMethods;
import jetbrains.mps.internal.collections.runtime.SetSequence;
import com.strumenta.mpsprotocol.data.DeclarationInfo;
import org.jetbrains.mps.openapi.language.SInterfaceConcept;
import com.strumenta.mpsprotocol.data.MMPropertyInfo;
import com.strumenta.mpsprotocol.data.ConceptInfo;
import jetbrains.mps.internal.collections.runtime.LinkedListSequence;
import org.jetbrains.mps.openapi.language.SConcept;
import com.strumenta.mpsprotocol.data.MMReferenceInfo;
import org.jetbrains.mps.openapi.language.SReferenceLink;
import com.strumenta.mpsprotocol.data.MMContainmentInfo;
import org.jetbrains.mps.openapi.language.SContainmentLink;
import com.strumenta.mpsprotocol.data.EnumInfo;
import org.jetbrains.mps.openapi.language.SEnumeration;
import com.strumenta.mpsprotocol.data.EnumLiteralInfo;
import org.jetbrains.mps.openapi.language.SEnumerationLiteral;
import org.jetbrains.mps.openapi.language.SDataType;
import jetbrains.mps.smodel.adapter.structure.types.SPrimitiveTypes;
import jetbrains.mps.project.ModuleId;
import com.strumenta.mpsprotocol.KUUID;
import jetbrains.mps.smodel.SModelId;
import jetbrains.mps.java.stub.JavaPackageModelId;
import com.strumenta.mpsprotocol.data.ReferenceInfo;

/**
 * Permits to navigate the data of a repo and expose it as data objects.
 */
public class DataExposer {

  private final SRepository repo;

  public DataExposer(final SRepository repo) {
    this.repo = repo;
  }

  public static String injectStructureInConceptName(String conceptName) {
    String[] parts = conceptName.split("\\.");
    String nameToSearch;
    if (parts.length < 2 || !(Objects.equals(parts[parts.length - 2], "structure"))) {
      String[] prefix = Arrays.copyOfRange(parts, 0, parts.length - 1);
      nameToSearch = "";
      for (int i = 0; i < prefix.length; i++) {
        nameToSearch += prefix[i];
        nameToSearch += ".";
      }
      nameToSearch += "structure.";
      nameToSearch += parts[parts.length - 1];
    } else {
      nameToSearch = conceptName;
    }
    return nameToSearch;
  }

  public SAbstractConcept getConceptByName(String conceptName) {
    String nameToSearch = injectStructureInConceptName(conceptName);
    for (SLanguage language : CollectionSequence.fromCollection(LanguageRegistry.getInstance(repo).getAllLanguages())) {
      for (SAbstractConcept concept : Sequence.fromIterable(language.getConcepts())) {
        if (Objects.equals(concept.getQualifiedName(), nameToSearch)) {
          return concept;
        }
      }
    }
    return null;
  }


  public List<ModuleInfo> modulesList() {
    List<ModuleInfo> modules = new LinkedList<ModuleInfo>();
    for (SModule module : repo.getModules()) {
      ModuleInfo moduleInfo = new ModuleInfo();
      fillModuleInfo(module, moduleInfo);
      modules.add(moduleInfo);
    }
    return modules;
  }

  public List<SolutionInfo> solutionsList() {
    List<SolutionInfo> solutions = new LinkedList<SolutionInfo>();
    for (SModule module : repo.getModules()) {
      if (module instanceof Solution) {
        SolutionInfo solutionInfo = new SolutionInfo();
        fillModuleInfo(module, solutionInfo);
        fillAdditionalSolutionInfo(((Solution) module), solutionInfo);
        solutions.add(solutionInfo);
      }
    }
    return solutions;
  }

  public List<LanguageInfo> languagesListFromRegistry() {
    List<LanguageInfo> languages = new LinkedList<LanguageInfo>();
    for (SLanguage language : LanguageRegistry.getInstance(repo).getAllLanguages()) {
      LanguageInfo info = new LanguageInfo();
      fillLanguageInfo(language, info);
      languages.add(info);
    }
    return languages;
  }

  public LanguageInfo language(final String languageName) {
    return languagesListFromRegistry().stream().filter(new Predicate<LanguageInfo>() {
      public boolean test(LanguageInfo li) {
        return Objects.equals(li.getQualifiedName(), languageName);
      }
    }).findFirst().orElse(null);
  }

  public LanguageInfoDetailed languageDetailed(String languageName) {
    for (SLanguage language : LanguageRegistry.getInstance(repo).getAllLanguages()) {
      if (Objects.equals(language.getQualifiedName(), languageName)) {
        LanguageInfoDetailed info = new LanguageInfoDetailed();
        fillLanguageInfo(language, info);
        fillLanguageInfoDetailed(language, info);
        return info;
      }
    }
    return null;
  }

  public ModuleInfoDetailed moduleDetailsByName(String name) {
    for (SModule module : repo.getModules()) {
      if (Objects.equals(module.getModuleName(), name)) {
        ModuleInfoDetailed moduleInfoDetailed = new ModuleInfoDetailed();
        fillModuleInfo(module, moduleInfoDetailed);
        for (SModel model : module.getModels()) {
          ModelInfo modelInfo = new ModelInfo();
          fillModelInfo(model, modelInfo);
          moduleInfoDetailed.getModels().add(modelInfo);
        }
        return moduleInfoDetailed;
      }
    }
    throw new NotFoundException("module with name " + name);
  }

  public SModel findModelByName(String modelName) {
    for (SModule module : repo.getModules()) {
      for (SModel model : Sequence.fromIterable(module.getModels())) {
        if (Objects.equals(model.getName().getLongName(), modelName)) {
          return model;
        }
      }
    }
    throw new NotFoundException("Model not found: " + modelName);
  }

  public void reload(final String modelName) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        repo.getModelAccess().runWriteAction(new Runnable() {
          public void run() {
            SModel m = findModelByName(modelName);
            if (m instanceof TrivialModelDescriptor) {
              // nothing to do here 
            } else if (m instanceof EditableSModelBase) {
              EditableSModelBase esmb = ((EditableSModelBase) m);
              esmb.reloadFromSource();
            } else {
              throw new RuntimeException("Cannot reload model " + m.getClass());
            }
          }
        });
      }
    });
  }

  public List<SModel> changedModels() {
    final List<SModel> changedModels = ListSequence.fromList(new LinkedList<SModel>());
    repo.getModelAccess().runReadAction(new Runnable() {
      public void run() {
        for (SModule module : repo.getModules()) {
          // module isPackaged and isReadOnly are unreliable 
          for (SModel model : Sequence.fromIterable(module.getModels())) {
            if (model instanceof EditableSModel) {
              if (((EditableSModel) model).isChanged() || ((EditableSModel) model).needsReloading()) {
                ListSequence.fromList(changedModels).addElement(model);
              }
            }
          }
        }
      }
    });
    return changedModels;
  }

  public void saveSingleModel(final String modelName) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        repo.getModelAccess().runWriteAction(new Runnable() {
          public void run() {
            SModel m = findModelByName(modelName);
            EditableSModelBase esmb = ((EditableSModelBase) m);
            esmb.save();
          }
        });
      }
    });
  }
  public void saveAll() {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        repo.getModelAccess().runWriteAction(new Runnable() {
          public void run() {
            repo.saveAll();
          }
        });
      }
    });
  }
  public void reloadAll() {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        repo.getModelAccess().runWriteAction(new Runnable() {
          public void run() {
            for (SModule module : repo.getModules()) {
              for (SModel model : module.getModels()) {
                try {
                  if (model instanceof EditableSModel) {
                    if (((EditableSModel) model).isChanged() || ((EditableSModel) model).needsReloading()) {
                      ((EditableSModel) model).reloadFromSource();
                    }
                  }
                } catch (Throwable t) {
                  throw new RuntimeException("Issue on reloading " + model, t);
                }
              }
            }
          }
        });
      }
    });
  }

  public ModelInfoDetailed modelDetailsByName(String name) {
    SModel model = findModelByName(name);
    ModelInfoDetailed info = new ModelInfoDetailed();
    fillModelInfo(model, info);
    for (SNode root : Sequence.fromIterable(model.getRootNodes())) {
      NodeInfo nodeInfo = new NodeInfo();
      fillNodeInfo(root, nodeInfo);
      info.getRoots().add(nodeInfo);
    }
    return info;
  }

  public List<SNode> rootNodesByConceptName(String conceptName) {
    List<SNode> nodes = ListSequence.fromList(new LinkedList<SNode>());
    for (SModule module : repo.getModules()) {
      for (SModel model : Sequence.fromIterable(module.getModels())) {
        for (SNode root : Sequence.fromIterable(model.getRootNodes())) {
          if (Objects.equals(root.getConcept().getQualifiedName(), conceptName)) {
            ListSequence.fromList(nodes).addElement(root);
          }
        }
      }
    }
    return nodes;
  }

  public NodeInfoDetailed nodeInfoDetails(String modelName, long nodeIdValue) {
    SNode node = getSNode(modelName, nodeIdValue);
    return toInfoDetailed(node);
  }

  public SNode getSNode(String modelName, long nodeIdValue) {
    SModel model = findModelByName(modelName);
    SNodeId nodeId = jetbrains.mps.smodel.SNodeId.fromString(Long.toString(nodeIdValue));
    SNode node = model.getNode(nodeId);
    return node;
  }

  public SNode getSNode(NodeIDInModel nodeIDInModel) {
    SModel model = findModelByName(nodeIDInModel.getModel());
    RegularNodeIDInfo regularNodeIDInfo = ((RegularNodeIDInfo) nodeIDInModel.getId());
    SNodeId nodeId = jetbrains.mps.smodel.SNodeId.fromString(Long.toString(regularNodeIDInfo.getRegularId()));
    SNode node = model.getNode(nodeId);
    return node;
  }

  public NodeInfoDetailed toInfoDetailed(NodeIDInModel node) {
    return toInfoDetailed(toSNode(node));
  }

  public NodeInfoDetailed toInfoDetailed(SNode node) {
    NodeInfoDetailed info = new NodeInfoDetailed();
    return fillNodeInfoDetailed(node, info);
  }
  public NodeInfoDetailed fillNodeInfoDetailed(SNode node, NodeInfoDetailed info) {
    assert node != null;
    fillNodeInfo(node, info);
    if (node.getContainmentLink() != null) {
      info.setContainingLink(node.getContainmentLink().getName());
    }
    for (SProperty prop : CollectionSequence.fromCollection(node.getConcept().getProperties())) {
      Object propValue = SNodeAccessUtil.getPropertyValue(node, prop);
      info.getProperties().put(prop.getName(), propValue);
    }
    for (SReference ref : Sequence.fromIterable(node.getReferences())) {
      SNode targetNode = ref.getTargetNode();
      info.getRefs().put(ref.getLink().getName(), createReferenceInfo(targetNode));
    }
    for (SNode child : Sequence.fromIterable(node.getChildren())) {
      info.getChildren().add(toInfoDetailed(child));
    }
    return info;
  }

  public NodeInfo toBasicNodeInfo(SNode node) {
    NodeInfo nodeInfo = new NodeInfo();
    fillNodeInfo(node, nodeInfo);
    return nodeInfo;
  }

  public NodeIDInModel toNodeIDInModel(SNode node) {
    return new NodeIDInModel(node.getModel().getModelName(), toNodeIDInfo(node.getNodeId()));
  }
  public SNode toSNode(NodeIDInModel nodeIDInModel) {
    return getSNode(nodeIDInModel.getModel(), ((RegularNodeIDInfo) nodeIDInModel.getId()).getRegularId());
  }

  public NodeIDInfo toNodeIDInfo(SNodeId nodeId) {
    if (nodeId instanceof jetbrains.mps.smodel.SNodeId.Regular) {
      jetbrains.mps.smodel.SNodeId.Regular casted = ((jetbrains.mps.smodel.SNodeId.Regular) nodeId);
      RegularNodeIDInfo info = new RegularNodeIDInfo();
      info.setRegularId(casted.getId());
      return info;
    } else if (nodeId instanceof jetbrains.mps.smodel.SNodeId.Foreign) {
      jetbrains.mps.smodel.SNodeId.Foreign casted = ((jetbrains.mps.smodel.SNodeId.Foreign) nodeId);
      ForeignNodeIDInfo info = new ForeignNodeIDInfo(casted.getType() + ":" + casted.getId());
      return info;
    } else if (nodeId instanceof StringBasedIdForJavaStubMethods) {
      StringBasedIdForJavaStubMethods casted = ((StringBasedIdForJavaStubMethods) nodeId);
      ForeignNodeIDInfo info = new ForeignNodeIDInfo(casted.getType() + ":" + casted.getIdWithReturnTypeNoPrefix());
      return info;
    } else {
      throw new UnsupportedOperationException(nodeId.getClass().getCanonicalName());
    }
  }


  private void fillAdditionalSolutionInfo(Solution solution, SolutionInfo solutionInfo) {
    for (SLanguage ul : SetSequence.fromSet(solution.getUsedLanguages())) {
      solutionInfo.getUsedLanguages().add(ul.getQualifiedName());
    }
  }

  private DeclarationInfo toDeclarationInfo(SAbstractConcept c) {
    DeclarationInfo di = new DeclarationInfo();
    di.setConceptName(c.getQualifiedName());
    di.setInterface(c instanceof SInterfaceConcept);
    return di;
  }

  private MMPropertyInfo toPropertyInfo(SProperty property) {
    MMPropertyInfo pi = new MMPropertyInfo();
    pi.setName(property.getName());
    pi.setType(serialize(property.getType()));
    pi.setDeclaration(toDeclarationInfo(property.getOwner()));
    return pi;
  }

  private ConceptInfo toConceptInfo(SAbstractConcept concept) {
    ConceptInfo conceptInfo = new ConceptInfo();
    conceptInfo.setQualifiedName(concept.getQualifiedName());
    conceptInfo.setAlias(concept.getConceptAlias());
    if (isEmptyString(conceptInfo.getAlias())) {
      conceptInfo.setAlias(null);
    }
    conceptInfo.setRootable(concept.isAbstract());
    if (concept instanceof SInterfaceConcept) {
      SInterfaceConcept sInterfaceConcept = ((SInterfaceConcept) concept);
      conceptInfo.setInterface(true);
      conceptInfo.setInterfaceConcepts(LinkedListSequence.fromLinkedListNew(new LinkedList<String>()));
      for (SInterfaceConcept si : Sequence.fromIterable(sInterfaceConcept.getSuperInterfaces())) {
        conceptInfo.getInterfaceConcepts().add(si.getQualifiedName());
      }
    } else if (concept instanceof SConcept) {
      SConcept sConcept = ((SConcept) concept);
      conceptInfo.setInterface(false);
      conceptInfo.setSuperConcept(sConcept.getSuperConcept().getQualifiedName());
      conceptInfo.setRootable(sConcept.isRootable());
    } else {
      throw new UnsupportedOperationException(concept.getClass().getCanonicalName());
    }
    conceptInfo.setDeclaredProperties(LinkedListSequence.fromLinkedListNew(new LinkedList<MMPropertyInfo>()));
    conceptInfo.setInheritedProperties(LinkedListSequence.fromLinkedListNew(new LinkedList<MMPropertyInfo>()));
    for (SProperty property : CollectionSequence.fromCollection(concept.getProperties())) {
      MMPropertyInfo pi = toPropertyInfo(property);
      if (Objects.equals(property.getOwner(), concept)) {
        conceptInfo.getDeclaredProperties().add(pi);
      } else {
        conceptInfo.getInheritedProperties().add(pi);
      }
    }
    conceptInfo.setDeclaredReferences(LinkedListSequence.fromLinkedListNew(new LinkedList<MMReferenceInfo>()));
    conceptInfo.setInheritedReferences(LinkedListSequence.fromLinkedListNew(new LinkedList<MMReferenceInfo>()));
    for (SReferenceLink reference : CollectionSequence.fromCollection(concept.getReferenceLinks())) {
      MMReferenceInfo ri = new MMReferenceInfo();
      ri.setDeclaration(toDeclarationInfo(reference.getOwner()));
      ri.setName(reference.getName());
      ri.setOptional(reference.isOptional());
      ri.setType(reference.getTargetConcept().getQualifiedName());
      if (Objects.equals(reference.getOwner(), concept)) {
        conceptInfo.getDeclaredReferences().add(ri);
      } else {
        conceptInfo.getInheritedReferences().add(ri);
      }
    }
    conceptInfo.setDeclaredContainments(LinkedListSequence.fromLinkedListNew(new LinkedList<MMContainmentInfo>()));
    conceptInfo.setInheritedContainments(LinkedListSequence.fromLinkedListNew(new LinkedList<MMContainmentInfo>()));
    for (SContainmentLink containment : CollectionSequence.fromCollection(concept.getContainmentLinks())) {
      MMContainmentInfo ci = new MMContainmentInfo();
      ci.setDeclaration(toDeclarationInfo(containment.getOwner()));
      ci.setName(containment.getName());
      ci.setOptional(containment.isOptional());
      ci.setMultiple(containment.isMultiple());
      ci.setType(containment.getTargetConcept().getQualifiedName());
      if (Objects.equals(containment.getOwner(), concept)) {
        conceptInfo.getDeclaredContainments().add(ci);
      } else {
        conceptInfo.getInheritedContainments().add(ci);
      }
    }
    return conceptInfo;
  }
  private EnumInfo toEnumInfo(SEnumeration enumeration) {
    EnumInfo info = new EnumInfo();
    info.setName(enumeration.getName());
    if (enumeration.getDefault() != null) {
      info.setDefaultLiteral(enumeration.getDefault().getName());
    }
    info.setLiterals(LinkedListSequence.fromLinkedListNew(new LinkedList<EnumLiteralInfo>()));
    for (SEnumerationLiteral lit : CollectionSequence.fromCollection(enumeration.getLiterals())) {
      EnumLiteralInfo eli = new EnumLiteralInfo();
      eli.setName(lit.getName());
      eli.setLabel(lit.getPresentation());
      info.getLiterals().add(eli);
    }
    return info;
  }

  private void fillLanguageInfoDetailed(SLanguage language, LanguageInfoDetailed languageInfo) {
    languageInfo.setConcepts(LinkedListSequence.fromLinkedListNew(new LinkedList<ConceptInfo>()));
    languageInfo.setEnums(LinkedListSequence.fromLinkedListNew(new LinkedList<EnumInfo>()));
    for (SAbstractConcept concept : Sequence.fromIterable(language.getConcepts())) {
      languageInfo.getConcepts().add(toConceptInfo(concept));
    }
    for (SDataType dt : Sequence.fromIterable(language.getDatatypes())) {
      if (dt instanceof SEnumeration) {
        languageInfo.getEnums().add(toEnumInfo(((SEnumeration) dt)));
      } else {
        throw new UnsupportedOperationException(dt.toString());
      }
    }
  }

  private String serialize(SDataType dataType) {
    if (Objects.equals(dataType, SPrimitiveTypes.STRING)) {
      return "string";
    } else if (Objects.equals(dataType, SPrimitiveTypes.INTEGER)) {
      return "integer";
    } else if (Objects.equals(dataType, SPrimitiveTypes.BOOLEAN)) {
      return "boolean";
    } else if (dataType instanceof SEnumeration) {
      SEnumeration sEnumeration = ((SEnumeration) dataType);
      return sEnumeration.getName();
    } else {
      throw new UnsupportedOperationException(dataType.toString());
    }
  }


  private void fillLanguageInfo(SLanguage language, LanguageInfo languageInfo) {
    languageInfo.setQualifiedName(language.getQualifiedName());
    if (language.getSourceModule() == null) {
      languageInfo.setSourceModuleName(null);
    } else {
      languageInfo.setSourceModuleName(language.getSourceModule().getModuleName());
    }
  }


  private void fillModuleInfo(SModule module, ModuleInfo moduleInfo) {
    moduleInfo.setName(module.getModuleName());
    moduleInfo.setPackaged(module.isPackaged());
    moduleInfo.setReadOnly(module.isReadOnly());
    if (module.getModuleId() instanceof ModuleId.Regular) {
      ModuleId.Regular midr = ((ModuleId.Regular) module.getModuleId());
      moduleInfo.setUuid(new KUUID(midr.getUUID()));
    } else if (module.getModuleId() instanceof ModuleId.Foreign) {
      ModuleId.Foreign midf = ((ModuleId.Foreign) module.getModuleId());
      moduleInfo.setForeignName(midf.getName());
    } else {
      throw new UnsupportedOperationException(module.getModuleId().getClass().getCanonicalName());
    }
  }

  private ModelInfo createModelInfo(SModel model) {
    ModelInfo modelInfo = new ModelInfo();
    fillModelInfo(model, modelInfo);
    return modelInfo;
  }

  private void fillModelInfo(SModel model, ModelInfo modelInfo) {
    modelInfo.setIntValue(-1);
    modelInfo.setQualifiedName(model.getModelName());
    modelInfo.setReadOnly(model.isReadOnly());
    if (model.getModelId() instanceof SModelId.RegularSModelId) {
      SModelId.RegularSModelId casted = ((SModelId.RegularSModelId) model.getModelId());
      modelInfo.setUuid(new KUUID(casted.getId()));
    } else if (model.getModelId() instanceof SModelId.ForeignSModelId) {
      SModelId.ForeignSModelId casted = ((SModelId.ForeignSModelId) model.getModelId());
      modelInfo.setForeignName("foreign:" + casted.getModelName());
    } else if (model.getModelId() instanceof SModelId.IntegerSModelId) {
      SModelId.IntegerSModelId casted = ((SModelId.IntegerSModelId) model.getModelId());
      modelInfo.setIntValue(casted.getValue());
    } else if (model.getModelId() instanceof JavaPackageModelId) {
      JavaPackageModelId casted = ((JavaPackageModelId) model.getModelId());
      modelInfo.setForeignName(casted.toString());
    } else {
      throw new UnsupportedOperationException("Unknown ModelID: " + model.getModelId().getClass().getCanonicalName());
    }
  }

  private NodeIDInfo createNodeID(SNode node) {
    return toNodeIDInfo(node.getNodeId());
  }

  private ReferenceInfo createReferenceInfo(SNode targetNode) {
    ReferenceInfo info = new ReferenceInfo();
    info.setModel(createModelInfo(targetNode.getModel()));
    info.setId(createNodeID(targetNode));
    return info;
  }

  private void fillNodeInfo(SNode element, NodeInfo info) {
    if (element == null) {
      throw new IllegalArgumentException("element should not be null");
    }
    info.setName(element.getName());
    info.setConcept(element.getConcept().getLanguage().getQualifiedName() + "." + element.getConcept().getName());
    info.setAbstractConcept(element.getConcept().isAbstract());
    info.setInterfaceConcept(element.getConcept().isAbstract());
    info.setId(createNodeID(element));
    info.setConceptAlias(element.getConcept().getConceptAlias());
  }

  private static boolean isEmptyString(String str) {
    return str == null || str.length() == 0;
  }
}
