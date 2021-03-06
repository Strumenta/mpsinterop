package com.strumenta.mpsserver.logic;

/*Generated by MPS */

import org.jetbrains.mps.openapi.model.SModel;
import org.jetbrains.mps.openapi.util.Consumer;
import java.util.List;
import jetbrains.mps.errors.item.ReportItem;
import jetbrains.mps.internal.collections.runtime.ListSequence;
import java.util.LinkedList;
import org.jetbrains.mps.openapi.util.ProgressMonitor;
import jetbrains.mps.progress.ProgressMonitorBase;
import jetbrains.mps.checkers.IAbstractChecker;
import jetbrains.mps.checkers.ModelCheckerBuilder;
import jetbrains.mps.errors.item.IssueKindReportItem;
import jetbrains.mps.checkers.IChecker;
import jetbrains.mps.checkers.ModelPropertiesChecker;
import jetbrains.mps.typesystemEngine.checker.TypesystemChecker;
import jetbrains.mps.checkers.ConstraintsChecker;
import jetbrains.mps.checkers.RefScopeChecker;
import jetbrains.mps.checkers.TargetConceptChecker;
import jetbrains.mps.checkers.UsedLanguagesChecker;
import jetbrains.mps.checkers.AbstractNodeCheckerInEditor;
import jetbrains.mps.project.validation.StructureChecker;
import jetbrains.mps.checkers.ModuleChecker;
import jetbrains.mps.typesystemEngine.checker.NonTypesystemChecker;
import org.jetbrains.mps.openapi.module.SRepository;

public class ModelChecker {

  public void check(final SModel model, final Consumer<List<ReportItem>> resultConsumer) {
    if (model == null) {
      throw new IllegalArgumentException("model should not be null");
    }
    final List<ReportItem> reportItems = ListSequence.fromList(new LinkedList<ReportItem>());
    Consumer<ReportItem> errorCollector = new Consumer<ReportItem>() {
      public void consume(ReportItem reportItem) {
        ListSequence.fromList(reportItems).addElement(reportItem);
      }
    };
    ProgressMonitor monitor = new ProgressMonitorBase() {
      protected void update(double p0) {
      }
      protected void startInternal(String p0) {
      }
      protected void doneInternal(String p0) {
      }
      protected void setTitleInternal(String p0) {
      }
      protected void setStepInternal(String p0) {
      }
      public void cancel() {
      }
      public boolean isCanceled() {
        return false;
      }
      @Override
      public void done() {
        super.done();
        resultConsumer.consume(reportItems);
      }
    };

    IAbstractChecker<ModelCheckerBuilder.ItemsToCheck, IssueKindReportItem> checker = new ModelCheckerBuilder(false).createChecker(ListSequence.fromListAndArray(new LinkedList<IChecker<?, ?>>(), new ModelPropertiesChecker(), new TypesystemChecker(), new ConstraintsChecker(null), new RefScopeChecker(null), new TargetConceptChecker(), new UsedLanguagesChecker(), (AbstractNodeCheckerInEditor) (AbstractNodeCheckerInEditor) new StructureChecker(), new ModuleChecker(), new NonTypesystemChecker()));
    ModelCheckerBuilder.ItemsToCheck itemsToCheck = new ModelCheckerBuilder.ItemsToCheck();
    ListSequence.fromList(itemsToCheck.models).addElement(model);
    SRepository repo = model.getRepository();
    checker.check(itemsToCheck, repo, errorCollector, monitor);
  }
}
