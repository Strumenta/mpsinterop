package com.strumenta.financialcalc.structure;

/*Generated by MPS */

import jetbrains.mps.smodel.runtime.ConceptPresentationAspectBase;
import jetbrains.mps.smodel.runtime.ConceptPresentation;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.mps.openapi.language.SAbstractConcept;
import jetbrains.mps.smodel.runtime.ConceptPresentationBuilder;

public class ConceptPresentationAspectImpl extends ConceptPresentationAspectBase {
  private ConceptPresentation props_BooleanType;
  private ConceptPresentation props_FinancialCalcSheet;
  private ConceptPresentation props_Input;
  private ConceptPresentation props_StringType;
  private ConceptPresentation props_Type;

  @Override
  @Nullable
  public ConceptPresentation getDescriptor(SAbstractConcept c) {
    StructureAspectDescriptor structureDescriptor = (StructureAspectDescriptor) myLanguageRuntime.getAspect(jetbrains.mps.smodel.runtime.StructureAspectDescriptor.class);
    switch (structureDescriptor.internalIndex(c)) {
      case LanguageConceptSwitch.BooleanType:
        if (props_BooleanType == null) {
          ConceptPresentationBuilder cpb = new ConceptPresentationBuilder();
          cpb.rawPresentation("boolean");
          props_BooleanType = cpb.create();
        }
        return props_BooleanType;
      case LanguageConceptSwitch.FinancialCalcSheet:
        if (props_FinancialCalcSheet == null) {
          ConceptPresentationBuilder cpb = new ConceptPresentationBuilder();
          cpb.presentationByName();
          props_FinancialCalcSheet = cpb.create();
        }
        return props_FinancialCalcSheet;
      case LanguageConceptSwitch.Input:
        if (props_Input == null) {
          ConceptPresentationBuilder cpb = new ConceptPresentationBuilder();
          cpb.presentationByName();
          props_Input = cpb.create();
        }
        return props_Input;
      case LanguageConceptSwitch.StringType:
        if (props_StringType == null) {
          ConceptPresentationBuilder cpb = new ConceptPresentationBuilder();
          cpb.rawPresentation("string");
          props_StringType = cpb.create();
        }
        return props_StringType;
      case LanguageConceptSwitch.Type:
        if (props_Type == null) {
          ConceptPresentationBuilder cpb = new ConceptPresentationBuilder();
          props_Type = cpb.create();
        }
        return props_Type;
    }
    return null;
  }
}
