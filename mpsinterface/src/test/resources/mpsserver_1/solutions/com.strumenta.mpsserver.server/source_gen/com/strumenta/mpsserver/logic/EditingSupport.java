package com.strumenta.mpsserver.logic;

/*Generated by MPS */

import java.util.List;
import org.jetbrains.mps.openapi.model.SNode;
import org.jetbrains.mps.openapi.language.SContainmentLink;
import org.jetbrains.mps.openapi.language.SReferenceLink;
import org.jetbrains.mps.openapi.language.SScope;
import jetbrains.mps.internal.collections.runtime.ListSequence;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Should provide information useful to implement an external editor, like a web editor.
 * 
 * Probably I cannot get substitutions by using ModelActions.createChildNodeSubstituteActions as that requires an editor context
 * 
 * I could find a way to get the <node expression>.search scope (link, operation context)
 * It is described here: https://www.jetbrains.com/help/mps/smodel-language-queries.html
 */
public class EditingSupport {

  public List<SNode> visibleNodesForWrappingReference(SNode node, SContainmentLink clink, SReferenceLink referenceLink) {
    SScope scope = referenceLink.getScope(node, clink, 0);
    List<SNode> nodes = ListSequence.fromList(new ArrayList<SNode>());
    for (Iterator<SNode> it = scope.getAvailableElements("").iterator(); it.hasNext();) {
      ListSequence.fromList(nodes).addElement(it.next());
    }
    return nodes;
  }

  public List<SNode> visibleNodesForDirectReferences(SNode node, SReferenceLink referenceLink) {
    SScope scope = referenceLink.getScope(node);
    List<SNode> nodes = ListSequence.fromList(new ArrayList<SNode>());
    for (Iterator<SNode> it = scope.getAvailableElements("").iterator(); it.hasNext();) {
      ListSequence.fromList(nodes).addElement(it.next());
    }
    return nodes;
  }

}