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
import org.jetbrains.mps.openapi.module.SRepository;
import com.strumenta.mpsserver.logic.DataExposer;
import com.strumenta.mpsserver.logic.ServerController;
import org.jetbrains.mps.openapi.model.SNode;
import jetbrains.mps.lang.smodel.generator.smodelAdapter.SPointerOperations;
import jetbrains.mps.smodel.SNodePointer;
import jetbrains.mps.lang.smodel.generator.smodelAdapter.SLinkOperations;
import java.util.List;
import com.strumenta.mpsprotocol.data.SmartReferenceInfo;
import jetbrains.mps.smodel.SNodeId;
import junit.framework.Assert;
import jetbrains.mps.internal.collections.runtime.ListSequence;
import org.jetbrains.mps.openapi.language.SContainmentLink;
import jetbrains.mps.smodel.adapter.structure.MetaAdapterFactory;
import org.jetbrains.mps.openapi.language.SConcept;

@MPSLaunch
public class ServerControllerTest_Test extends BaseTransformationTest {
  @ClassRule
  public static final TestParametersCache ourParamCache = new TestParametersCache(ServerControllerTest_Test.class, "${mpsserver.home}", "r:086a4983-b245-40f9-a1ab-f4ca8b2b0f03(com.strumenta.mpsserver.server.tests.tests@tests)", false);
  @Rule
  public final RunWithCommand myWithCommandRule = new RunWithCommand(this);

  public ServerControllerTest_Test() {
    super(ourParamCache);
  }

  @Test
  public void test_getModulesListWithUUID() throws Throwable {
    new TestBody(this).test_getModulesListWithUUID();
  }

  /*package*/ static class TestBody extends BaseTestBody {

    /*package*/ TestBody(TransformationTest owner) {
      super(owner);
    }

    public void test_getModulesListWithUUID() throws Exception {
      SRepository repository = myProject.getRepository();
      DataExposer data = new DataExposer(repository);
      ServerController controller = new ServerController(repository, data);
      SNode acme = SPointerOperations.resolveNode(new SNodePointer("r:480d701d-f50a-403e-b280-ef64b84517ed(com.strumenta.businessorg.sandbox.acmeinc)", "5270253970127314084"), repository);
      SNode john = SLinkOperations.getChildren(acme, LINKS.persons$ccj7).get(0);
      List<SmartReferenceInfo> alts = controller.getSmartReferenceAlternatives("com.strumenta.businessorg.sandbox.acmeinc", ((SNodeId.Regular) john.getNodeId()).getId(), "roles", CONCEPTS.RolePlayed$g1);
      Assert.assertEquals(ListSequence.fromList(SLinkOperations.getChildren(acme, LINKS.roles$cck5)).count(), ListSequence.fromList(alts).count());
    }


  }

  private static final class LINKS {
    /*package*/ static final SContainmentLink persons$ccj7 = MetaAdapterFactory.getContainmentLink(0x96ad5b8f04fe4e16L, 0xa7d60e014b8726e4L, 0x4923b41015868b94L, 0x4923b4101587dca5L, "persons");
    /*package*/ static final SContainmentLink roles$cck5 = MetaAdapterFactory.getContainmentLink(0x96ad5b8f04fe4e16L, 0xa7d60e014b8726e4L, 0x4923b41015868b94L, 0x4923b4101587dca7L, "roles");
  }

  private static final class CONCEPTS {
    /*package*/ static final SConcept RolePlayed$g1 = MetaAdapterFactory.getConcept(0x96ad5b8f04fe4e16L, 0xa7d60e014b8726e4L, 0x4923b41015880f85L, "com.strumenta.businessorg.structure.RolePlayed");
  }
}
