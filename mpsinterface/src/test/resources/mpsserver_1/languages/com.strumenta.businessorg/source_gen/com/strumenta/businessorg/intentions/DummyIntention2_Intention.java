package com.strumenta.businessorg.intentions;

/*Generated by MPS */

import jetbrains.mps.intentions.AbstractIntentionDescriptor;
import jetbrains.mps.openapi.intentions.IntentionFactory;
import java.util.Collection;
import jetbrains.mps.openapi.intentions.IntentionExecutable;
import jetbrains.mps.openapi.intentions.Kind;
import jetbrains.mps.smodel.SNodePointer;
import org.jetbrains.mps.openapi.model.SNode;
import jetbrains.mps.openapi.editor.EditorContext;
import java.util.Collections;
import jetbrains.mps.intentions.AbstractIntentionExecutable;
import jetbrains.mps.openapi.intentions.IntentionDescriptor;

public final class DummyIntention2_Intention extends AbstractIntentionDescriptor implements IntentionFactory {
  private Collection<IntentionExecutable> myCachedExecutable;
  public DummyIntention2_Intention() {
    super(Kind.NORMAL, true, new SNodePointer("r:7d44744b-e2b4-4f4d-95e4-ccc1b33c8297(com.strumenta.businessorg.intentions)", "7672163306534520564"));
  }
  @Override
  public String getPresentation() {
    return "DummyIntention2";
  }
  @Override
  public boolean isApplicable(final SNode node, final EditorContext editorContext) {
    return true;
  }
  @Override
  public boolean isSurroundWith() {
    return false;
  }
  public Collection<IntentionExecutable> instances(final SNode node, final EditorContext context) {
    if (myCachedExecutable == null) {
      myCachedExecutable = Collections.<IntentionExecutable>singletonList(new IntentionImplementation());
    }
    return myCachedExecutable;
  }
  /*package*/ final class IntentionImplementation extends AbstractIntentionExecutable {
    public IntentionImplementation() {
    }
    @Override
    public String getDescription(final SNode node, final EditorContext editorContext) {
      return "Dummy Intention 2";
    }
    @Override
    public void execute(final SNode node, final EditorContext editorContext) {
      MyInvocationRegister.register("Dummy Intention 2", node, editorContext);
    }
    @Override
    public IntentionDescriptor getDescriptor() {
      return DummyIntention2_Intention.this;
    }
  }
}