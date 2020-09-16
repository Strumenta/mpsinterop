package com.strumenta.mpsserver.server.tests.tests;

/*Generated by MPS */

import jetbrains.mps.MPSLaunch;
import jetbrains.mps.lang.test.runtime.BaseTransformationTest;
import org.junit.ClassRule;
import jetbrains.mps.lang.test.runtime.TestParametersCache;
import org.junit.Rule;
import jetbrains.mps.lang.test.runtime.RunWithCommand;
import org.junit.Test;
import jetbrains.mps.lang.test.runtime.BaseTestBody;
import jetbrains.mps.lang.test.runtime.TransformationTest;
import com.strumenta.mpsserver.modelhierarchy.MyRepo;
import com.strumenta.mpsserver.modelhierarchy.MyModule;
import java.util.UUID;
import com.strumenta.mpsserver.modelhierarchy.MyModelRoot;
import org.jetbrains.mps.openapi.model.SModelId;
import org.jetbrains.mps.openapi.model.SModel;
import org.jetbrains.mps.openapi.model.SNode;
import jetbrains.mps.lang.smodel.generator.smodelAdapter.SModelOperations;
import jetbrains.mps.lang.smodel.generator.smodelAdapter.SNodeOperations;
import jetbrains.mps.lang.smodel.generator.smodelAdapter.SLinkOperations;
import jetbrains.mps.internal.collections.runtime.ListSequence;
import junit.framework.Assert;
import jetbrains.mps.typechecking.TypecheckingFacade;
import com.strumenta.mpsserver.modelhierarchy.MyModel;
import com.strumenta.mpsserver.modelhierarchy.MyModelReference;
import com.strumenta.mpsserver.modelhierarchy.MyDataSource;
import jetbrains.mps.smodel.adapter.structure.MetaAdapterFactory;
import com.strumenta.mpsserver.logic.ModelChecker;
import org.jetbrains.mps.openapi.util.Consumer;
import java.util.List;
import jetbrains.mps.errors.item.ReportItem;
import org.jetbrains.annotations.NotNull;
import jetbrains.mps.internal.collections.runtime.IWhereFilter;
import java.util.Objects;
import jetbrains.mps.errors.MessageStatus;
import jetbrains.mps.internal.collections.runtime.SetSequence;
import java.util.HashSet;
import jetbrains.mps.internal.collections.runtime.ISelector;
import jetbrains.mps.typesystemEngine.checker.NonTypesystemChecker;
import java.util.Set;
import jetbrains.mps.errors.item.NodeReportItem;
import jetbrains.mps.typechecking.TypecheckingSession;
import jetbrains.mps.typesystem.LegacyTypecheckingQueries;
import jetbrains.mps.typechecking.backend.TypecheckingBackend;
import jetbrains.mps.smodel.builder.SNodeBuilder;
import org.jetbrains.mps.openapi.language.SContainmentLink;
import org.jetbrains.mps.openapi.language.SConcept;
import org.jetbrains.mps.openapi.language.SProperty;

@MPSLaunch
public class CheckingErrors_Test extends BaseTransformationTest {
  @ClassRule
  public static final TestParametersCache ourParamCache = new TestParametersCache(CheckingErrors_Test.class, "${mpsserver.home}", "r:086a4983-b245-40f9-a1ab-f4ca8b2b0f03(com.strumenta.mpsserver.server.tests.tests@tests)", false);
  @Rule
  public final RunWithCommand myWithCommandRule = new RunWithCommand(this);

  public CheckingErrors_Test() {
    super(ourParamCache);
  }

  @Test
  public void test_calculatingTypeOnBuiltModel() throws Throwable {
    new TestBody(this).test_calculatingTypeOnBuiltModel();
  }
  @Test
  public void test_calculatingErrorsOnBuiltModel() throws Throwable {
    new TestBody(this).test_calculatingErrorsOnBuiltModel();
  }
  @Test
  public void test_calculatingErrorsOnBuiltModelForTypeChecking() throws Throwable {
    new TestBody(this).test_calculatingErrorsOnBuiltModelForTypeChecking();
  }
  @Test
  public void test_calculatingErrorsOnBuiltModelOnBusinessOrg() throws Throwable {
    new TestBody(this).test_calculatingErrorsOnBuiltModelOnBusinessOrg();
  }

  /*package*/ static class TestBody extends BaseTestBody {

    /*package*/ TestBody(TransformationTest owner) {
      super(owner);
    }

    public void test_calculatingTypeOnBuiltModel() throws Exception {
      MyRepo repo = new MyRepo();
      MyModule moduleA = new MyModule(UUID.fromString("1983eea1-e90c-40f3-a117-fa3efb0f711a"), "my.model.a");
      repo.addModule(moduleA);

      MyModelRoot modelRootA = new MyModelRoot();
      moduleA.addModelRoot(modelRootA);

      SModelId myModelId = jetbrains.mps.smodel.SModelId.regular(UUID.fromString("19821939-e90c-40f3-a117-fa3efb0f711a"));

      SModel modelA = modelRootA.getModel(myModelId);
      SNode classFoo = createClassConcept_u0h5cm_a0k0d01();
      SModelOperations.addRootNode(modelA, classFoo);
      SNode ic = SNodeOperations.cast(SLinkOperations.getTarget(SNodeOperations.cast(ListSequence.fromList(SLinkOperations.getChildren(classFoo, LINKS.member$oYX5)).getElement(0), CONCEPTS.FieldDeclaration$Ps), LINKS.initializer$KgD), CONCEPTS.IntegerConstant$mo);
      Assert.assertEquals(CONCEPTS.IntegerType$Eo, SNodeOperations.getConcept(TypecheckingFacade.getFromContext().getTypeOf(ic)));
    }
    public void test_calculatingErrorsOnBuiltModel() throws Exception {
      MyRepo repo = new MyRepo();
      MyModule moduleA = new MyModule(UUID.fromString("1983eea1-e90c-40f3-a117-fa3efb0f711a"), "my.model.a");
      repo.addModule(moduleA);

      MyModelRoot modelRootA = new MyModelRoot();
      moduleA.addModelRoot(modelRootA);

      Assert.assertEquals(moduleA, modelRootA.getModule());
      SModelId myModelId = jetbrains.mps.smodel.SModelId.regular(UUID.fromString("19821939-e90c-40f3-a117-fa3efb0f711a"));
      MyModel myModel = new MyModel(new MyModelReference(myModelId, "com.foo.ModelA"), new MyDataSource());
      myModel.addLanguage(MetaAdapterFactory.getLanguage(0xf3061a5392264cc5L, 0xa443f952ceaf5816L, "jetbrains.mps.baseLanguage"));
      modelRootA.addModel(myModel);

      Assert.assertEquals(repo, moduleA.getRepository());


      SModel modelA = modelRootA.getModel(myModelId);
      Assert.assertEquals(myModel, modelA);
      Assert.assertEquals(repo, modelA.getRepository());

      SNode classFoo = createClassConcept_u0h5cm_a0u0e01();
      SModelOperations.addRootNode(modelA, classFoo);
      ModelChecker mc = new ModelChecker();
      mc.check(modelA, new Consumer<List<ReportItem>>() {
        @Override
        public void consume(@NotNull List<ReportItem> items) {
          Assert.assertEquals(4, ListSequence.fromList(items).where(new IWhereFilter<ReportItem>() {
            public boolean accept(ReportItem it) {
              return Objects.equals(it.getSeverity(), MessageStatus.ERROR);
            }
          }).count());
          Assert.assertEquals(SetSequence.fromSetAndArray(new HashSet<String>(), "Error: Field a is already declared", "Error: type int is not a subtype of string", "Error: Field a is already declared", "Property constraint violation for the property \"name\""), new HashSet<String>(ListSequence.fromList(items).where(new IWhereFilter<ReportItem>() {
            public boolean accept(ReportItem it) {
              return Objects.equals(it.getSeverity(), MessageStatus.ERROR);
            }
          }).select(new ISelector<ReportItem, String>() {
            public String select(ReportItem it) {
              return it.getMessage();
            }
          }).toListSequence()));
        }
      });
    }
    public void test_calculatingErrorsOnBuiltModelForTypeChecking() throws Exception {
      MyRepo repo = new MyRepo();
      MyModule moduleA = new MyModule(UUID.fromString("1983eea1-e90c-40f3-a117-fa3efb0f711a"), "my.model.a");
      repo.addModule(moduleA);

      MyModelRoot modelRootA = new MyModelRoot();
      moduleA.addModelRoot(modelRootA);

      Assert.assertEquals(moduleA, modelRootA.getModule());
      SModelId myModelId = jetbrains.mps.smodel.SModelId.regular(UUID.fromString("19821939-e90c-40f3-a117-fa3efb0f711a"));
      SModel myModel = new MyModel(new MyModelReference(myModelId, "com.foo.ModelA"), new MyDataSource());
      modelRootA.addModel(myModel);

      Assert.assertEquals(repo, moduleA.getRepository());


      SModel modelA = modelRootA.getModel(myModelId);
      Assert.assertEquals(myModel, modelA);
      Assert.assertEquals(repo, modelA.getRepository());

      final SNode classFoo = createClassConcept_u0h5cm_a0t0f01();
      SModelOperations.addRootNode(modelA, classFoo);

      TypecheckingFacade tcf = TypecheckingFacade.getFromContext();


      NonTypesystemChecker nonTypesystemChecker = new NonTypesystemChecker();

      Set<NodeReportItem> is = nonTypesystemChecker.getErrors(classFoo, repo);
      System.out.println("IS " + is);

      tcf.runIsolated(new java.util.function.Consumer<TypecheckingSession>() {

        @Override
        public void accept(TypecheckingSession session) {
          LegacyTypecheckingQueries lq = session.getQueries(LegacyTypecheckingQueries.class);
          lq.checkRecursively(classFoo, new java.util.function.Consumer<NodeReportItem>() {

            @Override
            public void accept(NodeReportItem item) {
              System.out.println("ITEM " + item);
            }
          });
        }
      });
      TypecheckingSession.Flags incremental = TypecheckingSession.Flags.forRoot(classFoo).incremental();
      TypecheckingFacade.getFromContext().checkRecursively(classFoo, new java.util.function.Consumer<NodeReportItem>() {
        public void accept(NodeReportItem it) {
          System.out.println("ITEM");
        }
      });


      TypecheckingBackend tcb = new TypecheckingBackend();

    }
    public void test_calculatingErrorsOnBuiltModelOnBusinessOrg() throws Exception {
      MyRepo repo = new MyRepo();
      MyModule moduleA = new MyModule(UUID.fromString("1983eea1-e90c-40f3-a117-fa3efb0f711a"), "my.model.a");
      repo.addModule(moduleA);

      MyModelRoot modelRootA = new MyModelRoot();
      moduleA.addModelRoot(modelRootA);


      Assert.assertEquals(moduleA, modelRootA.getModule());
      SModelId myModelId = jetbrains.mps.smodel.SModelId.regular(UUID.fromString("19821939-e90c-40f3-a117-fa3efb0f711a"));
      MyModelReference myModelReference = new MyModelReference(myModelId, "com.foo.ModelA");
      MyModel myModel = new MyModel(myModelReference, new MyDataSource());
      modelRootA.addModel(myModel);

      Assert.assertEquals(repo, moduleA.getRepository());

      SModel modelA = modelRootA.getModel(myModelId);
      myModel.addLanguage(MetaAdapterFactory.getLanguage(0x96ad5b8f04fe4e16L, 0xa7d60e014b8726e4L, "com.strumenta.businessorg"));
      Assert.assertEquals(myModel, modelA);
      Assert.assertEquals(repo, modelA.getRepository());

      SNode acme = createOrganization_u0h5cm_a0v0g01();
      Assert.assertNotNull(myModel.getSModelInternal().getModelDescriptor());
      SModelOperations.addRootNode(modelA, acme);
      Assert.assertEquals(modelA, acme.getModel());

      Assert.assertEquals(modelA, SNodeOperations.getModel(acme));
      Assert.assertEquals(modelA, SNodeOperations.getModel(acme));
      Assert.assertEquals(modelA, SNodeOperations.getModel(ListSequence.fromList(SLinkOperations.getChildren(acme, LINKS.roles$cck5)).getElement(0)));
      Assert.assertEquals(modelA, SNodeOperations.getModel(ListSequence.fromList(SLinkOperations.getChildren(acme, LINKS.roles$cck5)).getElement(1)));
      ModelChecker mc = new ModelChecker();
      mc.check(modelA, new Consumer<List<ReportItem>>() {
        @Override
        public void consume(@NotNull List<ReportItem> items) {
          Assert.assertEquals(2, ListSequence.fromList(items).count());
          Assert.assertEquals("Error: Duplicate name", ListSequence.fromList(items).getElement(0).getMessage());
          Assert.assertEquals("Error: Duplicate name", ListSequence.fromList(items).getElement(1).getMessage());
        }
      });
    }


    private static SNode createClassConcept_u0h5cm_a0k0d01() {
      SNodeBuilder rootBuilder1 = new SNodeBuilder().init(CONCEPTS.ClassConcept$IY);
      rootBuilder1.setProperty(PROPS.name$tAp1, "Foo");
      {
        SNodeBuilder n2 = rootBuilder1.forChild(LINKS.member$oYX5).init(CONCEPTS.FieldDeclaration$Ps);
        n2.setProperty(PROPS.name$tAp1, "a");
        n2.forChild(LINKS.type$pLrO).init(CONCEPTS.IntegerType$Eo);
        {
          SNodeBuilder n3 = n2.forChild(LINKS.initializer$KgD).init(CONCEPTS.IntegerConstant$mo);
          n3.setProperty(PROPS.value$ZeO0, PROPS.value$ZeO0.getType().toString(1));
        }
      }
      return rootBuilder1.getResult();
    }
    private static SNode createClassConcept_u0h5cm_a0u0e01() {
      SNodeBuilder rootBuilder1 = new SNodeBuilder().init(CONCEPTS.ClassConcept$IY);
      rootBuilder1.setProperty(PROPS.name$tAp1, "Foo");
      {
        SNodeBuilder n2 = rootBuilder1.forChild(LINKS.member$oYX5).init(CONCEPTS.FieldDeclaration$Ps);
        n2.setProperty(PROPS.name$tAp1, "a");
        n2.forChild(LINKS.type$pLrO).init(CONCEPTS.StringType$2b);
        {
          SNodeBuilder n3 = n2.forChild(LINKS.initializer$KgD).init(CONCEPTS.IntegerConstant$mo);
          n3.setProperty(PROPS.value$ZeO0, PROPS.value$ZeO0.getType().toString(1));
        }
        SNodeBuilder n4 = n2.forSibling().init(CONCEPTS.FieldDeclaration$Ps);
        n4.setProperty(PROPS.name$tAp1, "a");
        n4.forChild(LINKS.type$pLrO).init(CONCEPTS.IntegerType$Eo);
        {
          SNodeBuilder n5 = n4.forChild(LINKS.initializer$KgD).init(CONCEPTS.IntegerConstant$mo);
          n5.setProperty(PROPS.value$ZeO0, PROPS.value$ZeO0.getType().toString(1));
        }
        SNodeBuilder n6 = n4.forSibling().init(CONCEPTS.FieldDeclaration$Ps);
        n6.forChild(LINKS.type$pLrO).init(CONCEPTS.IntegerType$Eo);
        {
          SNodeBuilder n7 = n6.forChild(LINKS.initializer$KgD).init(CONCEPTS.IntegerConstant$mo);
          n7.setProperty(PROPS.value$ZeO0, PROPS.value$ZeO0.getType().toString(1));
        }
      }
      return rootBuilder1.getResult();
    }
    private static SNode createClassConcept_u0h5cm_a0t0f01() {
      SNodeBuilder rootBuilder1 = new SNodeBuilder().init(CONCEPTS.ClassConcept$IY);
      rootBuilder1.setProperty(PROPS.name$tAp1, "Foo");
      {
        SNodeBuilder n2 = rootBuilder1.forChild(LINKS.member$oYX5).init(CONCEPTS.FieldDeclaration$Ps);
        n2.setProperty(PROPS.name$tAp1, "a");
        n2.forChild(LINKS.type$pLrO).init(CONCEPTS.StringType$2b);
        {
          SNodeBuilder n3 = n2.forChild(LINKS.initializer$KgD).init(CONCEPTS.IntegerConstant$mo);
          n3.setProperty(PROPS.value$ZeO0, PROPS.value$ZeO0.getType().toString(1));
        }
        SNodeBuilder n4 = n2.forSibling().init(CONCEPTS.FieldDeclaration$Ps);
        n4.setProperty(PROPS.name$tAp1, "a");
        n4.forChild(LINKS.type$pLrO).init(CONCEPTS.StringType$2b);
        {
          SNodeBuilder n5 = n4.forChild(LINKS.initializer$KgD).init(CONCEPTS.IntegerConstant$mo);
          n5.setProperty(PROPS.value$ZeO0, PROPS.value$ZeO0.getType().toString(1));
        }
      }
      return rootBuilder1.getResult();
    }
    private static SNode createOrganization_u0h5cm_a0v0g01() {
      SNodeBuilder rootBuilder1 = new SNodeBuilder().init(CONCEPTS.Organization$hA);
      rootBuilder1.setProperty(PROPS.name$tAp1, "Acme");
      {
        SNodeBuilder n2 = rootBuilder1.forChild(LINKS.roles$cck5).init(CONCEPTS.Role$j3);
        n2.setProperty(PROPS.name$tAp1, "CEO");
        SNodeBuilder n3 = n2.forSibling().init(CONCEPTS.Role$j3);
        n3.setProperty(PROPS.name$tAp1, "CEO");
      }
      return rootBuilder1.getResult();
    }
  }

  private static final class LINKS {
    /*package*/ static final SContainmentLink member$oYX5 = MetaAdapterFactory.getContainmentLink(0xf3061a5392264cc5L, 0xa443f952ceaf5816L, 0x101d9d3ca30L, 0x4a9a46de59132803L, "member");
    /*package*/ static final SContainmentLink initializer$KgD = MetaAdapterFactory.getContainmentLink(0xf3061a5392264cc5L, 0xa443f952ceaf5816L, 0xf8c37a7f6eL, 0xf8c37f506eL, "initializer");
    /*package*/ static final SContainmentLink roles$cck5 = MetaAdapterFactory.getContainmentLink(0x96ad5b8f04fe4e16L, 0xa7d60e014b8726e4L, 0x4923b41015868b94L, 0x4923b4101587dca7L, "roles");
    /*package*/ static final SContainmentLink type$pLrO = MetaAdapterFactory.getContainmentLink(0xf3061a5392264cc5L, 0xa443f952ceaf5816L, 0x450368d90ce15bc3L, 0x4ed4d318133c80ceL, "type");
  }

  private static final class CONCEPTS {
    /*package*/ static final SConcept FieldDeclaration$Ps = MetaAdapterFactory.getConcept(0xf3061a5392264cc5L, 0xa443f952ceaf5816L, 0xf8c108ca68L, "jetbrains.mps.baseLanguage.structure.FieldDeclaration");
    /*package*/ static final SConcept IntegerConstant$mo = MetaAdapterFactory.getConcept(0xf3061a5392264cc5L, 0xa443f952ceaf5816L, 0xf8cc59b314L, "jetbrains.mps.baseLanguage.structure.IntegerConstant");
    /*package*/ static final SConcept IntegerType$Eo = MetaAdapterFactory.getConcept(0xf3061a5392264cc5L, 0xa443f952ceaf5816L, 0xf940d22479L, "jetbrains.mps.baseLanguage.structure.IntegerType");
    /*package*/ static final SConcept ClassConcept$IY = MetaAdapterFactory.getConcept(0xf3061a5392264cc5L, 0xa443f952ceaf5816L, 0xf8c108ca66L, "jetbrains.mps.baseLanguage.structure.ClassConcept");
    /*package*/ static final SConcept StringType$2b = MetaAdapterFactory.getConcept(0xf3061a5392264cc5L, 0xa443f952ceaf5816L, 0x11d47da71ecL, "jetbrains.mps.baseLanguage.structure.StringType");
    /*package*/ static final SConcept Organization$hA = MetaAdapterFactory.getConcept(0x96ad5b8f04fe4e16L, 0xa7d60e014b8726e4L, 0x4923b41015868b94L, "com.strumenta.businessorg.structure.Organization");
    /*package*/ static final SConcept Role$j3 = MetaAdapterFactory.getConcept(0x96ad5b8f04fe4e16L, 0xa7d60e014b8726e4L, 0x4923b41015868b97L, "com.strumenta.businessorg.structure.Role");
  }

  private static final class PROPS {
    /*package*/ static final SProperty name$tAp1 = MetaAdapterFactory.getProperty(0xceab519525ea4f22L, 0x9b92103b95ca8c0cL, 0x110396eaaa4L, 0x110396ec041L, "name");
    /*package*/ static final SProperty value$ZeO0 = MetaAdapterFactory.getProperty(0xf3061a5392264cc5L, 0xa443f952ceaf5816L, 0xf8cc59b314L, 0xf8cc59b315L, "value");
  }
}
