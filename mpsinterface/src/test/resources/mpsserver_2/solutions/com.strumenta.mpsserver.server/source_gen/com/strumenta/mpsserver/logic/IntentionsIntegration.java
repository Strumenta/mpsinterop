package com.strumenta.mpsserver.logic;

/*Generated by MPS */

import java.util.Map;
import org.jetbrains.mps.openapi.model.SNode;
import jetbrains.mps.nodeEditor.EditorContext;
import jetbrains.mps.internal.collections.runtime.MapSequence;
import java.util.HashMap;
import org.jetbrains.mps.openapi.module.SRepository;
import javax.swing.SwingUtilities;
import jetbrains.mps.nodeEditor.EditorComponent;
import jetbrains.mps.nodeEditor.NodeEditorComponent;
import jetbrains.mps.lang.smodel.generator.smodelAdapter.SNodeOperations;
import jetbrains.mps.openapi.intentions.IntentionExecutable;
import java.util.function.Consumer;
import java.util.List;
import jetbrains.mps.intentions.IntentionsManager;
import java.util.Collection;
import jetbrains.mps.util.Pair;
import java.util.function.Function;
import java.util.stream.Collectors;

public class IntentionsIntegration {

  private Map<SNode, EditorContext> editorContextsForNode = MapSequence.fromMap(new HashMap<SNode, EditorContext>());
  private SRepository repo;

  public IntentionsIntegration(SRepository repo) {
    this.repo = repo;
  }

  private EditorContext editorContextForNode(SNode node) {
    if (!(MapSequence.fromMap(editorContextsForNode).containsKey(node))) {
      if (!(SwingUtilities.isEventDispatchThread())) {
        throw new RuntimeException("This should be called from EDT");
      }
      EditorComponent ec = new NodeEditorComponent(repo);
      ec.editNode(node);
      EditorContext ctx = new EditorContext(ec, SNodeOperations.getModel(node), repo);
      MapSequence.fromMap(editorContextsForNode).put(node, ctx);
    }
    return MapSequence.fromMap(editorContextsForNode).get(node);
  }

  public class Intention {
    private SNode node;
    private IntentionExecutable intentionExecutable;

    public Intention(SNode node, IntentionExecutable intentionExecutable) {
      this.node = node;
      this.intentionExecutable = intentionExecutable;
    }

    public String getDescription() {
      return intentionExecutable.getDescription(node, editorContextForNode(node));
    }

    public void execute() {
      intentionExecutable.execute(node, editorContextForNode(node));
    }
  }

  public void listIntentions(final SNode node, final Consumer<List<Intention>> intentionsConsumer) {
    if (SwingUtilities.isEventDispatchThread()) {
      System.out.println("listIntentions from EDT");
      intentionsConsumer.accept(listIntentions(node));
      System.out.println("listIntentions from EDT, done");
    } else {
      System.out.println("listIntentions from outside EDT");
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          System.out.println("listIntentions from outside EDT, now in EDT");
          repo.getModelAccess().runReadAction(new Runnable() {
            public void run() {
              intentionsConsumer.accept(listIntentions(node));
            }
          });
          System.out.println("listIntentions from outside EDT, done");

        }
      });

    }
  }

  public List<Intention> listIntentions(SNode node) {
    if (!(SwingUtilities.isEventDispatchThread())) {
      throw new RuntimeException("This method is available only from the EDT");
    }
    IntentionsManager.QueryDescriptor queryDescriptor = new IntentionsManager.QueryDescriptor();
    queryDescriptor.setCurrentNodeOnly(false);

    Collection<Pair<IntentionExecutable, SNode>> result = IntentionsManager.getInstance().getAvailableIntentions(queryDescriptor, node, editorContextForNode(node));
    return (List<Intention>) result.stream().map(new Function<Pair<IntentionExecutable, SNode>, Intention>() {
      @Override
      public Intention apply(Pair<IntentionExecutable, SNode> pair) {
        return new Intention(pair.o2, pair.o1);
      }
    }).collect(Collectors.toList());
  }
}
