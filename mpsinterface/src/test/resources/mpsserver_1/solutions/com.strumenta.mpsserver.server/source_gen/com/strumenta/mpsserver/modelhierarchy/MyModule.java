package com.strumenta.mpsserver.modelhierarchy;

/*Generated by MPS */

import jetbrains.mps.extapi.module.SModuleBase;
import org.jetbrains.mps.openapi.module.SModuleReference;
import java.util.List;
import org.jetbrains.mps.openapi.persistence.ModelRoot;
import jetbrains.mps.internal.collections.runtime.ListSequence;
import java.util.ArrayList;
import org.jetbrains.mps.openapi.module.SRepository;
import org.jetbrains.annotations.NotNull;
import java.util.UUID;
import jetbrains.mps.project.ModuleId;
import org.jetbrains.mps.openapi.module.SModuleId;
import jetbrains.mps.extapi.persistence.ModelRootBase;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.mps.openapi.module.SDependency;
import java.util.Set;
import org.jetbrains.mps.openapi.language.SLanguage;
import org.jetbrains.mps.openapi.module.SModuleFacet;

/**
 * Cannot be used at this moment because it does not compile, because of a bug in MPS
 */
public class MyModule extends SModuleBase {

  private SModuleReference moduleReference;
  private List<ModelRoot> modelRoots = ListSequence.fromList(new ArrayList<ModelRoot>());

  private SRepository repository;


  @Override
  public void attach(@NotNull SRepository repo) {
    super.attach(repo);
    this.repository = repository;
  }

  public MyModule(UUID uuid, String name) {
    this(ModuleId.regular(uuid), name);
  }

  public MyModule(SModuleId id, String name) {
    this(new MyModuleReference(id, name));
  }

  public MyModule(SModuleReference moduleReference) {
    this.moduleReference = moduleReference;
  }

  public void addModelRoot(ModelRoot modelRoot) {
    ListSequence.fromList(this.modelRoots).addElement(modelRoot);
    if (modelRoot instanceof ModelRootBase) {
      ((ModelRootBase) modelRoot).setModule(this);
    }
  }

  @NotNull
  @Override
  public SModuleId getModuleId() {
    return moduleReference.getModuleId();
  }
  @Nullable
  @Override
  public String getModuleName() {
    return moduleReference.getModuleName();
  }
  @NotNull
  @Override
  public SModuleReference getModuleReference() {
    return moduleReference;
  }
  @Override
  public boolean isReadOnly() {
    return false;
  }
  @Override
  public boolean isPackaged() {
    return false;
  }
  @Override
  public Iterable<SDependency> getDeclaredDependencies() {
    throw new UnsupportedOperationException();
  }
  @Override
  public Set<SLanguage> getUsedLanguages() {
    throw new UnsupportedOperationException();
  }
  @Override
  public int getUsedLanguageVersion(@NotNull SLanguage language) {
    throw new UnsupportedOperationException();
  }
  @NotNull
  @Override
  public Iterable<SModuleFacet> getFacets() {
    throw new UnsupportedOperationException();
  }
  @Nullable
  @Override
  public <T extends SModuleFacet> T getFacet(@NotNull Class<T> aClass) {
    return null;
  }
  @Override
  public Iterable<ModelRoot> getModelRoots() {
    return modelRoots;
  }
}
