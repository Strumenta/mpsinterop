package com.strumenta.financialcalc.editor;

/*Generated by MPS */

import jetbrains.mps.nodeEditor.EditorAspectDescriptorBase;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import jetbrains.mps.openapi.editor.descriptor.ConceptEditor;
import org.jetbrains.mps.openapi.language.SAbstractConcept;
import java.util.Collections;
import jetbrains.mps.lang.smodel.ConceptSwitchIndex;
import jetbrains.mps.lang.smodel.ConceptSwitchIndexBuilder;
import jetbrains.mps.smodel.adapter.ids.MetaIdFactory;

public class EditorAspectDescriptorImpl extends EditorAspectDescriptorBase {
  @NotNull
  public Collection<ConceptEditor> getDeclaredEditors(SAbstractConcept concept) {
    SAbstractConcept cncpt = ((SAbstractConcept) concept);
    switch (conceptIndex.index(cncpt)) {
      case 0:
        return Collections.<ConceptEditor>singletonList(new BooleanType_Editor());
      case 1:
        return Collections.<ConceptEditor>singletonList(new FinancialCalcSheet_Editor());
      case 2:
        return Collections.<ConceptEditor>singletonList(new Input_Editor());
      case 3:
        return Collections.<ConceptEditor>singletonList(new StringType_Editor());
      default:
    }
    return Collections.<ConceptEditor>emptyList();
  }



  private static final ConceptSwitchIndex conceptIndex = new ConceptSwitchIndexBuilder().put(MetaIdFactory.conceptId(0xa50bd8d842c14879L, 0x98505fb2cea64ad0L, 0x28a7a476f630fde9L), MetaIdFactory.conceptId(0xa50bd8d842c14879L, 0x98505fb2cea64ad0L, 0x4801ddb81016ebcL), MetaIdFactory.conceptId(0xa50bd8d842c14879L, 0x98505fb2cea64ad0L, 0x4801ddb81016ebfL), MetaIdFactory.conceptId(0xa50bd8d842c14879L, 0x98505fb2cea64ad0L, 0x4801ddb81016ec5L)).seal();
}
