package com.strumenta.mpsserver.logic;

/*Generated by MPS */

import org.jetbrains.mps.openapi.module.SRepository;
import com.strumenta.mpsprotocol.data.NodeIDInModel;
import org.jetbrains.mps.openapi.model.SNode;
import org.jetbrains.mps.openapi.language.SAbstractConcept;
import jetbrains.mps.smodel.action.SNodeFactoryOperations;
import jetbrains.mps.lang.smodel.generator.smodelAdapter.SNodeOperations;
import org.jetbrains.mps.openapi.model.SModel;
import org.jetbrains.mps.openapi.language.SLanguage;
import jetbrains.mps.internal.collections.runtime.SetSequence;
import jetbrains.mps.internal.collections.runtime.Sequence;
import com.strumenta.mpsprotocol.data.RegularNodeIDInfo;
import jetbrains.mps.lang.structure.util.SmartRefAttributeUtil;
import org.jetbrains.mps.openapi.language.SReferenceLink;
import jetbrains.mps.lang.smodel.generator.smodelAdapter.SPropertyOperations;
import org.jetbrains.mps.openapi.language.SContainmentLink;
import jetbrains.mps.internal.collections.runtime.CollectionSequence;
import java.util.Objects;
import jetbrains.mps.baseLanguage.closures.runtime.Wrappers;
import jetbrains.mps.internal.collections.runtime.LinkedListSequence;
import java.util.LinkedList;
import jetbrains.mps.internal.collections.runtime.ListSequence;
import jetbrains.mps.internal.collections.runtime.ISelector;
import java.util.List;
import com.strumenta.mpsprotocol.data.SmartReferenceInfo;
import jetbrains.mps.scope.Scope;
import jetbrains.mps.lang.core.behavior.BaseConcept__BehaviorDescriptor;
import jetbrains.mps.smodel.constraints.ReferenceDescriptor;
import jetbrains.mps.smodel.constraints.ModelConstraints;
import jetbrains.mps.scope.ErrorScope;
import java.util.ArrayList;
import org.jetbrains.mps.openapi.model.SReference;
import org.jetbrains.mps.openapi.language.SProperty;
import java.util.Iterator;
import org.jetbrains.mps.openapi.util.Consumer;
import jetbrains.mps.smodel.adapter.structure.types.SPrimitiveTypes;
import jetbrains.mps.lang.smodel.generator.smodelAdapter.SConceptOperations;
import jetbrains.mps.smodel.adapter.structure.MetaAdapterFactory;

public class ServerController {
  private DataExposer data;
  private final SRepository repo;

  public ServerController(final SRepository repo, final DataExposer data) {
    this.repo = repo;
    this.data = data;
  }

  public DataExposer getData() {
    return this.data;
  }
  public SRepository getRepo() {
    return repo;
  }

  public void instantiateNode(final NodeIDInModel node, final String conceptToInstantiate) {
    repo.getModelAccess().executeCommandInEDT(new Runnable() {
      public void run() {
        SNode originalSNode = data.getSNode(node);
        SAbstractConcept concept = findConcept(node.getModel(), conceptToInstantiate);
        SNode newNode = SNodeFactoryOperations.createNewNode(concept, null);
        SNodeOperations.replaceWithAnother(originalSNode, newNode);
      }
    });
  }
  /**
   * TODO move to DataExposer
   * 
   * @param modelName 
   * @param conceptName 
   * @return 
   */
  public SAbstractConcept findConcept(String modelName, String conceptName) {
    SModel model = data.findModelByName(modelName);
    for (SLanguage language : SetSequence.fromSet(model.getModule().getUsedLanguages())) {
      for (SAbstractConcept concept : Sequence.fromIterable(language.getConcepts())) {
        if (match(language, concept, conceptName)) {
          return concept;
        }
      }
    }
    throw new IllegalArgumentException("Concept not found " + conceptName);
  }

  /**
   * Provide an answer to return to the called
   * 
   * index=-1 -> add at the end
   */
  public void addChild(final NodeIDInModel container, final String containmentLinkName, final String conceptToInstantiateName, final RegularNodeIDInfo wrappedNodeId, final int index, final NodeReceiver nodeReceiver) {
    repo.getModelAccess().runReadAction(new Runnable() {
      public void run() {
        final String modelName = container.getModel();
        final SNode containerSNode = data.getSNode(container);
        final SAbstractConcept conceptToInstantiate = findConcept(modelName, conceptToInstantiateName);
        repo.getModelAccess().executeCommandInEDT(new Runnable() {
          public void run() {
            SNode newNode = SNodeFactoryOperations.createNewNode(conceptToInstantiate, null);
            if (wrappedNodeId != null) {
              SNode ref = SmartRefAttributeUtil.getCharacteristicLinkDeclaration((SNode) SNodeOperations.asNode(conceptToInstantiate));
              if (ref != null) {
                SNode nodeToSet = data.getSNode(modelName, wrappedNodeId.getRegularId());
                SReferenceLink refLink = ConceptUtils.findReferenceOnNodeByName(newNode, SPropertyOperations.getString(ref, PROPS.name$tAp1));
                SNode wrapperNode = newNode;
                wrapperNode.setReferenceTarget(refLink, nodeToSet);
              }
            }
            if (index == -1) {
              containerSNode.addChild(containmentLinkName, newNode);
            } else {
              SNode child = getChildAtIndex(containerSNode, containmentLinkName, index);
              containerSNode.insertChildBefore(containmentLinkName, newNode, child);
            }
            if (nodeReceiver != null) {
              nodeReceiver.receive(newNode);
            }
          }
        });
      }
    });
  }

  /**
   * Provide an answer to return to the called
   */
  public void insertNextSibling(final String modelName, final long siblingNodeId) {
    repo.getModelAccess().executeCommandInEDT(new Runnable() {
      public void run() {
        SNode siblingSNode = data.getSNode(modelName, siblingNodeId);
        if (siblingSNode == null) {
          throw new RuntimeException("Sibling node not found");
        }
        SContainmentLink containmentLink = SNodeOperations.getContainingLink(siblingSNode);
        if (containmentLink == null) {
          throw new RuntimeException("No containment link for node " + siblingSNode);
        }
        SAbstractConcept abstractConcept = SNodeOperations.getContainingLink(siblingSNode).getTargetConcept();
        if (abstractConcept == null) {
          throw new RuntimeException("No target concept set for containing link");
        }
        SNodeFactoryOperations.insertNewNextSiblingChild(siblingSNode, abstractConcept);
      }
    });
  }

  /**
   * Provide an answer to return to the called
   */
  public void setChild(final NodeIDInModel container, final String containmentLinkName, final String conceptToInstantiateName, final RegularNodeIDInfo wrappedNodeId) {
    repo.getModelAccess().executeCommandInEDT(new Runnable() {
      public void run() {
        SNode containerSNode = data.getSNode(container);
        String modelName = container.getModel();
        Iterable<? extends SNode> children = containerSNode.getChildren(containmentLinkName);
        if (children.iterator().hasNext()) {
          containerSNode.removeChild(children.iterator().next());
        }
        addChild(container, containmentLinkName, conceptToInstantiateName, wrappedNodeId, -1, null);
      }
    });
  }

  /**
   * Provide an answer to return to the called
   */
  public String deleteNode(final NodeIDInModel node) {
    String response = "ok";
    repo.getModelAccess().executeCommandInEDT(new Runnable() {
      public void run() {
        SNode snode = data.getSNode(node);
        SNodeOperations.deleteNode(snode);
      }
    });
    return response;
  }


  /**
   * Provide an answer to return to the called
   */
  public void defaultInsertion(final String modelName, final long nodeId, final String containmentLinkName, final Callback<SNode> callback) {
    repo.getModelAccess().executeCommandInEDT(new Runnable() {
      public void run() {
        SNode container = data.getSNode(modelName, nodeId);
        SContainmentLink link = null;
        for (SContainmentLink cl : CollectionSequence.fromCollection(SNodeOperations.getConcept(container).getContainmentLinks())) {
          if (Objects.equals(cl.getName(), containmentLinkName)) {
            link = cl;
          }
        }
        if (link == null) {
          throw new RuntimeException("Unknown containment link " + containmentLinkName + ". Concept is " + SNodeOperations.getConcept(container).getQualifiedName());
        }

        callback.execute(SNodeFactoryOperations.addNewChild(container, link, link.getTargetConcept()));
      }
    });
  }


  /**
   * Provide an answer to return to the called
   */
  public Iterable<SAbstractConcept> askAlternatives(final String modelName, final long containerNodeId, final String containmentLinkName) {
    final Wrappers._T<Iterable<SAbstractConcept>> alternatives = new Wrappers._T<Iterable<SAbstractConcept>>(LinkedListSequence.fromLinkedListNew(new LinkedList<SAbstractConcept>()));
    repo.getModelAccess().runReadAction(new Runnable() {
      public void run() {
        SNode containerSNode = data.getSNode(modelName, containerNodeId);
        SContainmentLink link = null;
        for (SContainmentLink cl : CollectionSequence.fromCollection(SNodeOperations.getConcept(containerSNode).getContainmentLinks())) {
          if (Objects.equals(cl.getName(), containmentLinkName)) {
            link = cl;
          }
        }
        if (link == null) {
          throw new RuntimeException("Unknown containment link " + containmentLinkName + " for node of concept " + SNodeOperations.getConcept(containerSNode).getName());
        }
        alternatives.value = ListSequence.fromList(findDerivedConcepts(modelName, link.getTargetConcept())).sort(new ISelector<SAbstractConcept, String>() {
          public String select(SAbstractConcept it) {
            return it.getName();
          }
        }, true);
      }
    });
    return alternatives.value;
  }

  public List<SmartReferenceInfo> getSmartReferenceAlternatives(final String modelName, final long containerNodeId, final String containmentLinkName, final SAbstractConcept concept) {
    final Wrappers._T<List<SmartReferenceInfo>> result = new Wrappers._T<List<SmartReferenceInfo>>(null);
    assert !(concept.isAbstract());
    repo.getModelAccess().runReadAction(new Runnable() {
      public void run() {
        SNode containerSNode = data.getSNode(modelName, containerNodeId);
        SNode ref = SmartRefAttributeUtil.getCharacteristicLinkDeclaration((SNode) SNodeOperations.asNode(concept));
        if (ref != null) {
          Scope containerScope = null;
          for (SContainmentLink cl : CollectionSequence.fromCollection(SNodeOperations.getConcept(containerSNode).getContainmentLinks())) {
            if (Objects.equals(cl.getName(), containmentLinkName)) {
              containerScope = Scope.getScope(containerSNode, cl, 0, concept);
              if (containerScope != null) {
                System.out.println("found scope: " + containerScope + " for concept " + cl.getTargetConcept());
                break;
              }
            }
          }
          for (SReferenceLink link : CollectionSequence.fromCollection(concept.getReferenceLinks())) {
            // Is it OK to query by name? 
            if (Objects.equals(link.getName(), SPropertyOperations.getString(ref, PROPS.name$tAp1))) {
              System.out.println("found link: " + link.getSourceNode().getNodeId() + " name " + SPropertyOperations.getString(ref, PROPS.name$tAp1) + " container " + BaseConcept__BehaviorDescriptor.getDetailedPresentation_id22G2W3WJ92t.invoke(containerSNode));
              // See ReferenceMenuTransformationMenuPart.java (https://github.com/JetBrains/MPS/blob/9019fe399a9d66a16c6b21d03bcb0a9080aeaa7e/editor/editor-runtime/source/jetbrains/mps/lang/editor/menus/transformation/ReferenceMenuTransformationMenuPart.java) 
              ReferenceDescriptor referenceDescriptor = ModelConstraints.getReferenceDescriptor(containerSNode, link);
              Scope scope = referenceDescriptor.getScope();
              if (scope instanceof ErrorScope) {
                continue;
              }
              Iterable<SNode> availableElements = scope.getAvailableElements(null);
              SAbstractConcept targetConcept = link.getTargetConcept();
              result.value = ListSequence.fromList(new ArrayList<SmartReferenceInfo>());
              for (SNode node : Sequence.fromIterable(availableElements)) {
                if (node.getConcept().isSubConceptOf(targetConcept) && (containerScope == null || containerScope.contains(node))) {
                  SmartReferenceInfo nodeInfo = new SmartReferenceInfo();
                  data.fillNodeInfoDetailed(node, nodeInfo);
                  for (SReference candidateRef : Sequence.fromIterable(node.getReferences())) {
                    if (Objects.equals(candidateRef.getLink().getName(), SPropertyOperations.getString(ref, PROPS.name$tAp1))) {
                      SNode targetNode = candidateRef.getTargetNode();
                      nodeInfo.setSmartReference(data.toBasicNodeInfo(targetNode));
                    }
                  }
                  ListSequence.fromList(result.value).addElement(nodeInfo);
                }
              }
              break;
            }
          }
        }
      }
    });
    return result.value;
  }

  public SProperty findProperty(SNode snode, String propertyName) {
    SProperty propertyToChange = null;
    for (Iterator<SProperty> it = snode.getConcept().getProperties().iterator(); it.hasNext() && propertyToChange == null;) {
      SProperty prop = it.next();
      if (Objects.equals(prop.getName(), propertyName)) {
        propertyToChange = prop;
      }
    }
    return propertyToChange;
  }

  public String executePropertyChange(final String modelName, final long nodeId, final String propertyName, final Object rawValue, final Consumer<Object> newValueConsumer) {
    final Wrappers._T<String> response = new Wrappers._T<String>("ok");
    repo.getModelAccess().runReadAction(new Runnable() {
      public void run() {
        final SNode snode = data.getSNode(modelName, nodeId);
        if (snode == null) {
          throw new RuntimeException("Unable to find node " + nodeId + " in model " + modelName);
        }
        final SProperty propertyToChange = findProperty(snode, propertyName);
        if (propertyToChange == null) {
          response.value = "property not found";
        } else {
          repo.getModelAccess().executeCommandInEDT(new Runnable() {
            public void run() {
              String stringValue = String.valueOf(rawValue);
              if (Objects.equals(propertyToChange.getType(), SPrimitiveTypes.STRING)) {
                snode.setProperty(propertyToChange, stringValue);
                newValueConsumer.consume(snode.getProperty(propertyToChange));
              } else if (Objects.equals(propertyToChange.getType(), SPrimitiveTypes.BOOLEAN)) {
                boolean value = Boolean.valueOf(stringValue);
                snode.setProperty(propertyToChange, String.valueOf(value));
                newValueConsumer.consume(snode.getProperty(propertyToChange));
              } else {
                throw new UnsupportedOperationException("PropertyType=" + propertyToChange.getType());
              }
            }
          });
        }
      }
    });
    return response.value;
  }

  private boolean match(SLanguage language, SAbstractConcept concept, String conceptName) {
    // We typically consider the qualified concept name without the "structure" element 
    // for example: my.language.structure.MyConcept -> my.language.MyConcept 
    return Objects.equals(concept.getQualifiedName(), conceptName) || Objects.equals((language.getQualifiedName() + "." + concept.getName()), conceptName);
  }

  private List<SAbstractConcept> findDerivedConcepts(String modelName, SAbstractConcept abstractConcept) {
    SModel model = data.findModelByName(modelName);
    List<SAbstractConcept> result = ListSequence.fromList(new LinkedList<SAbstractConcept>());
    for (SAbstractConcept concept : ListSequence.fromList(SConceptOperations.getAllSubConcepts(abstractConcept, model))) {
      SAbstractConcept concreteConcept = concept;
      if (!(concreteConcept.isAbstract()) && !(ListSequence.fromList(result).contains(concreteConcept))) {
        ListSequence.fromList(result).addElement(concreteConcept);
      }
    }
    return result;
  }

  private SNode getChildAtIndex(SNode container, String containmentLinkName, int index) {
    int i = 0;
    for (Iterator<? extends SNode> it = container.getChildren(containmentLinkName).iterator(); it.hasNext();) {
      SNode child = it.next();
      if (i == index) {
        return child;
      }
      i++;
    }
    return null;
  }

  private static final class PROPS {
    /*package*/ static final SProperty name$tAp1 = MetaAdapterFactory.getProperty(0xceab519525ea4f22L, 0x9b92103b95ca8c0cL, 0x110396eaaa4L, 0x110396ec041L, "name");
  }
}
