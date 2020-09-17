package com.strumenta.mpsserver.modelhierarchy;

/*Generated by MPS */

import jetbrains.mps.extapi.model.SModelBase;
import java.util.List;
import org.jetbrains.mps.openapi.model.SNode;
import jetbrains.mps.internal.collections.runtime.ListSequence;
import java.util.ArrayList;
import org.jetbrains.mps.openapi.module.SModule;
import jetbrains.mps.smodel.SModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.mps.openapi.model.SModelReference;
import org.jetbrains.mps.openapi.persistence.DataSource;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.mps.openapi.module.SRepository;

public class MyModel extends SModelBase {
  private List<SNode> roots = ListSequence.fromList(new ArrayList<SNode>());
  private SModule module;
  private SModel internalSModel;
  @NotNull
  private SModelReference modelReference;
  public MyModel(@NotNull SModelReference modelReference, @NotNull DataSource source) {
    super(modelReference, source);
    this.modelReference = modelReference;
    this.internalSModel = new SModel(modelReference);
    this.internalSModel.setModelDescriptor(this, null);
  }
  @Nullable
  @Override
  protected SModel getCurrentModelInternal() {
    throw new UnsupportedOperationException();
  }
  @Deprecated
  @Override
  public SModel getSModelInternal() {
    return internalSModel;
  }
  @Override
  public void addRootNode(@NotNull SNode node) {
    ListSequence.fromList(this.roots).addElement(node);
    internalSModel.addRootNode(node);

    // TODO call AttachedNodeOwner.registerNode 

  }
  @Override
  public SRepository getRepository() {
    if (module == null) {
      return null;
    }
    return module.getRepository();
  }
  @Override
  public void setModule(SModule module) {
    this.module = module;
  }
  @Nullable
  @Override
  public SModule getModule() {
    return this.module;
  }
}