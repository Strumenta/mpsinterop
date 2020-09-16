<?xml version="1.0" encoding="UTF-8"?>
<model ref="00000000-0000-4000-5f02-5beb5f025beb/i:f4fd49b(checkpoints/com.strumenta.businessorg.typesystem@descriptorclasses)">
  <persistence version="9" />
  <attribute name="checkpoint" value="DescriptorClasses" />
  <attribute name="generation-plan" value="AspectCPS" />
  <languages />
  <imports>
    <import index="g364" ref="r:3588f820-f38c-4943-a5b1-ddc6c762e9a9(com.strumenta.businessorg.typesystem)" />
    <import index="c17a" ref="8865b7a8-5271-43d3-884c-6fd1d9cfdd34/java:org.jetbrains.mps.openapi.language(MPS.OpenAPI/)" />
    <import index="lziw" ref="r:7b9b5e1d-3054-41f7-a62a-e0116b0952e3(com.strumenta.businessorg.structure)" />
    <import index="2gg1" ref="6ed54515-acc8-4d1e-a16c-9fd6cfe951ea/java:jetbrains.mps.errors(MPS.Core/)" />
    <import index="qurh" ref="6ed54515-acc8-4d1e-a16c-9fd6cfe951ea/java:jetbrains.mps.lang.typesystem.runtime(MPS.Core/)" />
    <import index="33ny" ref="6354ebe7-c22a-4a0f-ac54-50b52ab9b065/java:java.util(JDK/)" />
    <import index="u78q" ref="6ed54515-acc8-4d1e-a16c-9fd6cfe951ea/java:jetbrains.mps.typesystem.inference(MPS.Core/)" />
    <import index="zavc" ref="6ed54515-acc8-4d1e-a16c-9fd6cfe951ea/java:jetbrains.mps.errors.messageTargets(MPS.Core/)" />
    <import index="mhbf" ref="8865b7a8-5271-43d3-884c-6fd1d9cfdd34/java:org.jetbrains.mps.openapi.model(MPS.OpenAPI/)" />
    <import index="tpck" ref="r:00000000-0000-4000-0000-011c89590288(jetbrains.mps.lang.core.structure)" />
  </imports>
  <registry>
    <language id="f3061a53-9226-4cc5-a443-f952ceaf5816" name="jetbrains.mps.baseLanguage">
      <concept id="1082485599095" name="jetbrains.mps.baseLanguage.structure.BlockStatement" flags="nn" index="9aQIb">
        <child id="1082485599096" name="statements" index="9aQI4" />
      </concept>
      <concept id="1202948039474" name="jetbrains.mps.baseLanguage.structure.InstanceMethodCallOperation" flags="nn" index="liA8E" />
      <concept id="1154032098014" name="jetbrains.mps.baseLanguage.structure.AbstractLoopStatement" flags="nn" index="2LF5Ji">
        <child id="1154032183016" name="body" index="2LFqv$" />
      </concept>
      <concept id="1197027756228" name="jetbrains.mps.baseLanguage.structure.DotExpression" flags="nn" index="2OqwBi">
        <child id="1197027771414" name="operand" index="2Oq$k0" />
        <child id="1197027833540" name="operation" index="2OqNvi" />
      </concept>
      <concept id="1197029447546" name="jetbrains.mps.baseLanguage.structure.FieldReferenceOperation" flags="nn" index="2OwXpG">
        <reference id="1197029500499" name="fieldDeclaration" index="2Oxat5" />
      </concept>
      <concept id="1145552977093" name="jetbrains.mps.baseLanguage.structure.GenericNewExpression" flags="nn" index="2ShNRf">
        <child id="1145553007750" name="creator" index="2ShVmc" />
      </concept>
      <concept id="1070475354124" name="jetbrains.mps.baseLanguage.structure.ThisExpression" flags="nn" index="Xjq3P" />
      <concept id="1070475926800" name="jetbrains.mps.baseLanguage.structure.StringLiteral" flags="nn" index="Xl_RD">
        <property id="1070475926801" name="value" index="Xl_RC" />
      </concept>
      <concept id="1070534058343" name="jetbrains.mps.baseLanguage.structure.NullLiteral" flags="nn" index="10Nm6u" />
      <concept id="1070534644030" name="jetbrains.mps.baseLanguage.structure.BooleanType" flags="in" index="10P_77" />
      <concept id="1068390468198" name="jetbrains.mps.baseLanguage.structure.ClassConcept" flags="ig" index="312cEu">
        <child id="1095933932569" name="implementedInterface" index="EKbjA" />
        <child id="1165602531693" name="superclass" index="1zkMxy" />
      </concept>
      <concept id="1068431474542" name="jetbrains.mps.baseLanguage.structure.VariableDeclaration" flags="ng" index="33uBYm">
        <property id="1176718929932" name="isFinal" index="3TUv4t" />
        <child id="1068431790190" name="initializer" index="33vP2m" />
      </concept>
      <concept id="1068498886296" name="jetbrains.mps.baseLanguage.structure.VariableReference" flags="nn" index="37vLTw">
        <reference id="1068581517664" name="variableDeclaration" index="3cqZAo" />
      </concept>
      <concept id="1068498886292" name="jetbrains.mps.baseLanguage.structure.ParameterDeclaration" flags="ir" index="37vLTG" />
      <concept id="1225271283259" name="jetbrains.mps.baseLanguage.structure.NPEEqualsExpression" flags="nn" index="17R0WA" />
      <concept id="1225271408483" name="jetbrains.mps.baseLanguage.structure.IsNotEmptyOperation" flags="nn" index="17RvpY" />
      <concept id="4972933694980447171" name="jetbrains.mps.baseLanguage.structure.BaseVariableDeclaration" flags="ng" index="19Szcq">
        <child id="5680397130376446158" name="type" index="1tU5fm" />
      </concept>
      <concept id="1068580123132" name="jetbrains.mps.baseLanguage.structure.BaseMethodDeclaration" flags="ng" index="3clF44">
        <child id="1068580123133" name="returnType" index="3clF45" />
        <child id="1068580123134" name="parameter" index="3clF46" />
        <child id="1068580123135" name="body" index="3clF47" />
      </concept>
      <concept id="1068580123165" name="jetbrains.mps.baseLanguage.structure.InstanceMethodDeclaration" flags="ig" index="3clFb_" />
      <concept id="1068580123155" name="jetbrains.mps.baseLanguage.structure.ExpressionStatement" flags="nn" index="3clFbF">
        <child id="1068580123156" name="expression" index="3clFbG" />
      </concept>
      <concept id="1068580123159" name="jetbrains.mps.baseLanguage.structure.IfStatement" flags="nn" index="3clFbJ">
        <child id="1068580123160" name="condition" index="3clFbw" />
        <child id="1068580123161" name="ifTrue" index="3clFbx" />
      </concept>
      <concept id="1068580123136" name="jetbrains.mps.baseLanguage.structure.StatementList" flags="sn" stub="5293379017992965193" index="3clFbS">
        <child id="1068581517665" name="statement" index="3cqZAp" />
      </concept>
      <concept id="1068580123137" name="jetbrains.mps.baseLanguage.structure.BooleanConstant" flags="nn" index="3clFbT" />
      <concept id="1068580123140" name="jetbrains.mps.baseLanguage.structure.ConstructorDeclaration" flags="ig" index="3clFbW" />
      <concept id="1068581242878" name="jetbrains.mps.baseLanguage.structure.ReturnStatement" flags="nn" index="3cpWs6">
        <child id="1068581517676" name="expression" index="3cqZAk" />
      </concept>
      <concept id="1068581242864" name="jetbrains.mps.baseLanguage.structure.LocalVariableDeclarationStatement" flags="nn" index="3cpWs8">
        <child id="1068581242865" name="localVariableDeclaration" index="3cpWs9" />
      </concept>
      <concept id="1068581242863" name="jetbrains.mps.baseLanguage.structure.LocalVariableDeclaration" flags="nr" index="3cpWsn" />
      <concept id="1068581517677" name="jetbrains.mps.baseLanguage.structure.VoidType" flags="in" index="3cqZAl" />
      <concept id="1204053956946" name="jetbrains.mps.baseLanguage.structure.IMethodCall" flags="ng" index="1ndlxa">
        <reference id="1068499141037" name="baseMethodDeclaration" index="37wK5l" />
        <child id="1068499141038" name="actualArgument" index="37wK5m" />
      </concept>
      <concept id="1212685548494" name="jetbrains.mps.baseLanguage.structure.ClassCreator" flags="nn" index="1pGfFk" />
      <concept id="1107461130800" name="jetbrains.mps.baseLanguage.structure.Classifier" flags="ng" index="3pOWGL">
        <child id="5375687026011219971" name="member" index="jymVt" unordered="true" />
      </concept>
      <concept id="7812454656619025412" name="jetbrains.mps.baseLanguage.structure.LocalMethodCall" flags="nn" index="1rXfSq" />
      <concept id="1107535904670" name="jetbrains.mps.baseLanguage.structure.ClassifierType" flags="in" index="3uibUv">
        <reference id="1107535924139" name="classifier" index="3uigEE" />
      </concept>
      <concept id="1081773326031" name="jetbrains.mps.baseLanguage.structure.BinaryOperation" flags="nn" index="3uHJSO">
        <child id="1081773367579" name="rightExpression" index="3uHU7w" />
        <child id="1081773367580" name="leftExpression" index="3uHU7B" />
      </concept>
      <concept id="1073239437375" name="jetbrains.mps.baseLanguage.structure.NotEqualsExpression" flags="nn" index="3y3z36" />
      <concept id="1178549954367" name="jetbrains.mps.baseLanguage.structure.IVisible" flags="ng" index="1B3ioH">
        <child id="1178549979242" name="visibility" index="1B3o_S" />
      </concept>
      <concept id="1146644602865" name="jetbrains.mps.baseLanguage.structure.PublicVisibility" flags="nn" index="3Tm1VV" />
      <concept id="1080120340718" name="jetbrains.mps.baseLanguage.structure.AndExpression" flags="nn" index="1Wc70l" />
    </language>
    <language id="b401a680-8325-4110-8fd3-84331ff25bef" name="jetbrains.mps.lang.generator">
      <concept id="7980339663309897032" name="jetbrains.mps.lang.generator.structure.OriginTrace" flags="ng" index="cd27G">
        <child id="7980339663309897037" name="origin" index="cd27D" />
      </concept>
      <concept id="9032177546941580387" name="jetbrains.mps.lang.generator.structure.TrivialNodeId" flags="nn" index="2$VJBW">
        <property id="9032177546941580392" name="nodeId" index="2$VJBR" />
        <child id="8557539026538618631" name="cncpt" index="3iCydw" />
      </concept>
      <concept id="5808518347809715508" name="jetbrains.mps.lang.generator.structure.GeneratorDebug_InputNode" flags="nn" index="385nmt">
        <property id="5808518347809748738" name="presentation" index="385vuF" />
        <child id="5808518347809747118" name="node" index="385v07" />
      </concept>
      <concept id="3864140621129707969" name="jetbrains.mps.lang.generator.structure.GeneratorDebug_Mappings" flags="nn" index="39dXUE">
        <child id="3864140621129713349" name="labels" index="39e2AI" />
      </concept>
      <concept id="3864140621129713351" name="jetbrains.mps.lang.generator.structure.GeneratorDebug_NodeMapEntry" flags="nn" index="39e2AG">
        <property id="5843998055530255671" name="isNewRoot" index="2mV_xN" />
        <reference id="3864140621129713371" name="inputOrigin" index="39e2AK" />
        <child id="5808518347809748862" name="inputNode" index="385vvn" />
        <child id="3864140621129713365" name="outputNode" index="39e2AY" />
      </concept>
      <concept id="3864140621129713348" name="jetbrains.mps.lang.generator.structure.GeneratorDebug_LabelEntry" flags="nn" index="39e2AJ">
        <property id="3864140621129715945" name="label" index="39e3Y2" />
        <child id="3864140621129715947" name="entries" index="39e3Y0" />
      </concept>
      <concept id="3864140621129713362" name="jetbrains.mps.lang.generator.structure.GeneratorDebug_NodeRef" flags="nn" index="39e2AT">
        <reference id="3864140621129713363" name="node" index="39e2AS" />
      </concept>
      <concept id="3637169702552512264" name="jetbrains.mps.lang.generator.structure.ElementaryNodeId" flags="ng" index="3u3nmq">
        <property id="3637169702552512269" name="nodeId" index="3u3nmv" />
      </concept>
    </language>
    <language id="7a5dda62-9140-4668-ab76-d5ed1746f2b2" name="jetbrains.mps.lang.typesystem">
      <concept id="2990591960991114251" name="jetbrains.mps.lang.typesystem.structure.OriginalNodeId" flags="ng" index="6wLe0">
        <property id="2990591960991114264" name="nodeId" index="6wLej" />
        <property id="2990591960991114295" name="modelId" index="6wLeW" />
      </concept>
    </language>
    <language id="df345b11-b8c7-4213-ac66-48d2a9b75d88" name="jetbrains.mps.baseLanguageInternal">
      <concept id="1176743162354" name="jetbrains.mps.baseLanguageInternal.structure.InternalVariableReference" flags="nn" index="3VmV3z">
        <property id="1176743296073" name="name" index="3VnrPo" />
        <child id="1176743202636" name="type" index="3Vn4Tt" />
      </concept>
    </language>
    <language id="7866978e-a0f0-4cc7-81bc-4d213d9375e1" name="jetbrains.mps.lang.smodel">
      <concept id="1177026924588" name="jetbrains.mps.lang.smodel.structure.RefConcept_Reference" flags="nn" index="chp4Y">
        <reference id="1177026940964" name="conceptDeclaration" index="cht4Q" />
      </concept>
      <concept id="6911370362349121511" name="jetbrains.mps.lang.smodel.structure.ConceptId" flags="nn" index="2x4n5u">
        <property id="6911370362349122519" name="conceptName" index="2x4mPI" />
        <property id="6911370362349121516" name="conceptId" index="2x4n5l" />
        <child id="6911370362349121514" name="languageIdentity" index="2x4n5j" />
      </concept>
      <concept id="2396822768958367367" name="jetbrains.mps.lang.smodel.structure.AbstractTypeCastExpression" flags="nn" index="$5XWr">
        <child id="6733348108486823193" name="leftExpression" index="1m5AlR" />
        <child id="3906496115198199033" name="conceptArgument" index="3oSUPX" />
      </concept>
      <concept id="1145404486709" name="jetbrains.mps.lang.smodel.structure.SemanticDowncastExpression" flags="nn" index="2JrnkZ">
        <child id="1145404616321" name="leftExpression" index="2JrQYb" />
      </concept>
      <concept id="1145573345940" name="jetbrains.mps.lang.smodel.structure.Node_GetAllSiblingsOperation" flags="nn" index="2TvwIu" />
      <concept id="3542851458883438784" name="jetbrains.mps.lang.smodel.structure.LanguageId" flags="nn" index="2V$Bhx">
        <property id="3542851458883439831" name="namespace" index="2V$B1Q" />
        <property id="3542851458883439832" name="languageId" index="2V$B1T" />
      </concept>
      <concept id="2644386474302386080" name="jetbrains.mps.lang.smodel.structure.PropertyIdRefExpression" flags="nn" index="355D3s">
        <reference id="2644386474302386081" name="conceptDeclaration" index="355D3t" />
        <reference id="2644386474302386082" name="propertyDeclaration" index="355D3u" />
      </concept>
      <concept id="2644386474301421077" name="jetbrains.mps.lang.smodel.structure.LinkIdRefExpression" flags="nn" index="359W_D">
        <reference id="2644386474301421078" name="conceptDeclaration" index="359W_E" />
        <reference id="2644386474301421079" name="linkDeclaration" index="359W_F" />
      </concept>
      <concept id="2644386474300074836" name="jetbrains.mps.lang.smodel.structure.ConceptIdRefExpression" flags="nn" index="35c_gC">
        <reference id="2644386474300074837" name="conceptDeclaration" index="35c_gD" />
      </concept>
      <concept id="6677504323281689838" name="jetbrains.mps.lang.smodel.structure.SConceptType" flags="in" index="3bZ5Sz" />
      <concept id="1139621453865" name="jetbrains.mps.lang.smodel.structure.Node_IsInstanceOfOperation" flags="nn" index="1mIQ4w">
        <child id="1177027386292" name="conceptArgument" index="cj9EA" />
      </concept>
      <concept id="1140137987495" name="jetbrains.mps.lang.smodel.structure.SNodeTypeCastExpression" flags="nn" index="1PxgMI" />
      <concept id="1138055754698" name="jetbrains.mps.lang.smodel.structure.SNodeType" flags="in" index="3Tqbb2" />
      <concept id="1138056022639" name="jetbrains.mps.lang.smodel.structure.SPropertyAccess" flags="nn" index="3TrcHB">
        <reference id="1138056395725" name="property" index="3TsBF5" />
      </concept>
      <concept id="1138056143562" name="jetbrains.mps.lang.smodel.structure.SLinkAccess" flags="nn" index="3TrEf2">
        <reference id="1138056516764" name="link" index="3Tt5mk" />
      </concept>
    </language>
    <language id="ceab5195-25ea-4f22-9b92-103b95ca8c0c" name="jetbrains.mps.lang.core">
      <concept id="1133920641626" name="jetbrains.mps.lang.core.structure.BaseConcept" flags="ng" index="2VYdi">
        <child id="5169995583184591170" name="smodelAttribute" index="lGtFl" />
      </concept>
      <concept id="1169194658468" name="jetbrains.mps.lang.core.structure.INamedConcept" flags="ng" index="TrEIO">
        <property id="1169194664001" name="name" index="TrG5h" />
      </concept>
    </language>
    <language id="83888646-71ce-4f1c-9c53-c54016f6ad4f" name="jetbrains.mps.baseLanguage.collections">
      <concept id="1153943597977" name="jetbrains.mps.baseLanguage.collections.structure.ForEachStatement" flags="nn" index="2Gpval">
        <child id="1153944400369" name="variable" index="2Gsz3X" />
        <child id="1153944424730" name="inputSequence" index="2GsD0m" />
      </concept>
      <concept id="1153944193378" name="jetbrains.mps.baseLanguage.collections.structure.ForEachVariable" flags="nr" index="2GrKxI" />
      <concept id="1153944233411" name="jetbrains.mps.baseLanguage.collections.structure.ForEachVariableReference" flags="nn" index="2GrUjf">
        <reference id="1153944258490" name="variable" index="2Gs0qQ" />
      </concept>
    </language>
  </registry>
  <node concept="39dXUE" id="0">
    <node concept="39e2AJ" id="1" role="39e2AI">
      <property role="39e3Y2" value="classForRule" />
      <node concept="39e2AG" id="5" role="39e3Y0">
        <ref role="39e2AK" to="g364:2KOWfKOng0D" resolve="check_Role" />
        <node concept="385nmt" id="7" role="385vvn">
          <property role="385vuF" value="check_Role" />
          <node concept="2$VJBW" id="9" role="385v07">
            <property role="2$VJBR" value="3185435802458718249" />
            <node concept="2x4n5u" id="a" role="3iCydw">
              <property role="2x4mPI" value="NonTypesystemRule" />
              <property role="2x4n5l" value="f92nru9m" />
              <node concept="2V$Bhx" id="b" role="2x4n5j">
                <property role="2V$B1T" value="7a5dda62-9140-4668-ab76-d5ed1746f2b2" />
                <property role="2V$B1Q" value="jetbrains.mps.lang.typesystem" />
              </node>
            </node>
          </node>
        </node>
        <node concept="39e2AT" id="8" role="39e2AY">
          <ref role="39e2AS" node="5t" resolve="check_Role_NonTypesystemRule" />
        </node>
      </node>
      <node concept="39e2AG" id="6" role="39e3Y0">
        <ref role="39e2AK" to="g364:2j5iOBR4Wa$" resolve="check_RolePlayed" />
        <node concept="385nmt" id="c" role="385vvn">
          <property role="385vuF" value="check_RolePlayed" />
          <node concept="2$VJBW" id="e" role="385v07">
            <property role="2$VJBR" value="2649606736827368100" />
            <node concept="2x4n5u" id="f" role="3iCydw">
              <property role="2x4mPI" value="NonTypesystemRule" />
              <property role="2x4n5l" value="f92nru9m" />
              <node concept="2V$Bhx" id="g" role="2x4n5j">
                <property role="2V$B1T" value="7a5dda62-9140-4668-ab76-d5ed1746f2b2" />
                <property role="2V$B1Q" value="jetbrains.mps.lang.typesystem" />
              </node>
            </node>
          </node>
        </node>
        <node concept="39e2AT" id="d" role="39e2AY">
          <ref role="39e2AS" node="1e" resolve="check_RolePlayed_NonTypesystemRule" />
        </node>
      </node>
    </node>
    <node concept="39e2AJ" id="2" role="39e2AI">
      <property role="39e3Y2" value="isApplicableMethod" />
      <node concept="39e2AG" id="h" role="39e3Y0">
        <ref role="39e2AK" to="g364:2KOWfKOng0D" resolve="check_Role" />
        <node concept="385nmt" id="j" role="385vvn">
          <property role="385vuF" value="check_Role" />
          <node concept="2$VJBW" id="l" role="385v07">
            <property role="2$VJBR" value="3185435802458718249" />
            <node concept="2x4n5u" id="m" role="3iCydw">
              <property role="2x4mPI" value="NonTypesystemRule" />
              <property role="2x4n5l" value="f92nru9m" />
              <node concept="2V$Bhx" id="n" role="2x4n5j">
                <property role="2V$B1T" value="7a5dda62-9140-4668-ab76-d5ed1746f2b2" />
                <property role="2V$B1Q" value="jetbrains.mps.lang.typesystem" />
              </node>
            </node>
          </node>
        </node>
        <node concept="39e2AT" id="k" role="39e2AY">
          <ref role="39e2AS" node="5x" resolve="isApplicableAndPattern" />
        </node>
      </node>
      <node concept="39e2AG" id="i" role="39e3Y0">
        <ref role="39e2AK" to="g364:2j5iOBR4Wa$" resolve="check_RolePlayed" />
        <node concept="385nmt" id="o" role="385vvn">
          <property role="385vuF" value="check_RolePlayed" />
          <node concept="2$VJBW" id="q" role="385v07">
            <property role="2$VJBR" value="2649606736827368100" />
            <node concept="2x4n5u" id="r" role="3iCydw">
              <property role="2x4mPI" value="NonTypesystemRule" />
              <property role="2x4n5l" value="f92nru9m" />
              <node concept="2V$Bhx" id="s" role="2x4n5j">
                <property role="2V$B1T" value="7a5dda62-9140-4668-ab76-d5ed1746f2b2" />
                <property role="2V$B1Q" value="jetbrains.mps.lang.typesystem" />
              </node>
            </node>
          </node>
        </node>
        <node concept="39e2AT" id="p" role="39e2AY">
          <ref role="39e2AS" node="1i" resolve="isApplicableAndPattern" />
        </node>
      </node>
    </node>
    <node concept="39e2AJ" id="3" role="39e2AI">
      <property role="39e3Y2" value="mainMethodForRule" />
      <node concept="39e2AG" id="t" role="39e3Y0">
        <ref role="39e2AK" to="g364:2KOWfKOng0D" resolve="check_Role" />
        <node concept="385nmt" id="v" role="385vvn">
          <property role="385vuF" value="check_Role" />
          <node concept="2$VJBW" id="x" role="385v07">
            <property role="2$VJBR" value="3185435802458718249" />
            <node concept="2x4n5u" id="y" role="3iCydw">
              <property role="2x4mPI" value="NonTypesystemRule" />
              <property role="2x4n5l" value="f92nru9m" />
              <node concept="2V$Bhx" id="z" role="2x4n5j">
                <property role="2V$B1T" value="7a5dda62-9140-4668-ab76-d5ed1746f2b2" />
                <property role="2V$B1Q" value="jetbrains.mps.lang.typesystem" />
              </node>
            </node>
          </node>
        </node>
        <node concept="39e2AT" id="w" role="39e2AY">
          <ref role="39e2AS" node="5v" resolve="applyRule" />
        </node>
      </node>
      <node concept="39e2AG" id="u" role="39e3Y0">
        <ref role="39e2AK" to="g364:2j5iOBR4Wa$" resolve="check_RolePlayed" />
        <node concept="385nmt" id="$" role="385vvn">
          <property role="385vuF" value="check_RolePlayed" />
          <node concept="2$VJBW" id="A" role="385v07">
            <property role="2$VJBR" value="2649606736827368100" />
            <node concept="2x4n5u" id="B" role="3iCydw">
              <property role="2x4mPI" value="NonTypesystemRule" />
              <property role="2x4n5l" value="f92nru9m" />
              <node concept="2V$Bhx" id="C" role="2x4n5j">
                <property role="2V$B1T" value="7a5dda62-9140-4668-ab76-d5ed1746f2b2" />
                <property role="2V$B1Q" value="jetbrains.mps.lang.typesystem" />
              </node>
            </node>
          </node>
        </node>
        <node concept="39e2AT" id="_" role="39e2AY">
          <ref role="39e2AS" node="1g" resolve="applyRule" />
        </node>
      </node>
    </node>
    <node concept="39e2AJ" id="4" role="39e2AI">
      <property role="39e3Y2" value="descriptorClass" />
      <node concept="39e2AG" id="D" role="39e3Y0">
        <property role="2mV_xN" value="true" />
        <node concept="39e2AT" id="E" role="39e2AY">
          <ref role="39e2AS" node="F" resolve="TypesystemDescriptor" />
        </node>
      </node>
    </node>
  </node>
  <node concept="312cEu" id="F">
    <property role="TrG5h" value="TypesystemDescriptor" />
    <node concept="3clFbW" id="G" role="jymVt">
      <node concept="3clFbS" id="J" role="3clF47">
        <node concept="9aQIb" id="M" role="3cqZAp">
          <node concept="3clFbS" id="O" role="9aQI4">
            <node concept="3cpWs8" id="P" role="3cqZAp">
              <node concept="3cpWsn" id="R" role="3cpWs9">
                <property role="TrG5h" value="nonTypesystemRule" />
                <node concept="3uibUv" id="S" role="1tU5fm">
                  <ref role="3uigEE" to="qurh:~NonTypesystemRule_Runtime" resolve="NonTypesystemRule_Runtime" />
                </node>
                <node concept="2ShNRf" id="T" role="33vP2m">
                  <node concept="1pGfFk" id="U" role="2ShVmc">
                    <ref role="37wK5l" node="5u" resolve="check_Role_NonTypesystemRule" />
                  </node>
                </node>
              </node>
            </node>
            <node concept="3clFbF" id="Q" role="3cqZAp">
              <node concept="2OqwBi" id="V" role="3clFbG">
                <node concept="2OqwBi" id="W" role="2Oq$k0">
                  <node concept="Xjq3P" id="Y" role="2Oq$k0" />
                  <node concept="2OwXpG" id="Z" role="2OqNvi">
                    <ref role="2Oxat5" to="qurh:~BaseHelginsDescriptor.myNonTypesystemRules" resolve="myNonTypesystemRules" />
                  </node>
                </node>
                <node concept="liA8E" id="X" role="2OqNvi">
                  <ref role="37wK5l" to="33ny:~Set.add(java.lang.Object)" resolve="add" />
                  <node concept="37vLTw" id="10" role="37wK5m">
                    <ref role="3cqZAo" node="R" resolve="nonTypesystemRule" />
                  </node>
                </node>
              </node>
            </node>
          </node>
        </node>
        <node concept="9aQIb" id="N" role="3cqZAp">
          <node concept="3clFbS" id="11" role="9aQI4">
            <node concept="3cpWs8" id="12" role="3cqZAp">
              <node concept="3cpWsn" id="14" role="3cpWs9">
                <property role="TrG5h" value="nonTypesystemRule" />
                <node concept="3uibUv" id="15" role="1tU5fm">
                  <ref role="3uigEE" to="qurh:~NonTypesystemRule_Runtime" resolve="NonTypesystemRule_Runtime" />
                </node>
                <node concept="2ShNRf" id="16" role="33vP2m">
                  <node concept="1pGfFk" id="17" role="2ShVmc">
                    <ref role="37wK5l" node="1f" resolve="check_RolePlayed_NonTypesystemRule" />
                  </node>
                </node>
              </node>
            </node>
            <node concept="3clFbF" id="13" role="3cqZAp">
              <node concept="2OqwBi" id="18" role="3clFbG">
                <node concept="2OqwBi" id="19" role="2Oq$k0">
                  <node concept="Xjq3P" id="1b" role="2Oq$k0" />
                  <node concept="2OwXpG" id="1c" role="2OqNvi">
                    <ref role="2Oxat5" to="qurh:~BaseHelginsDescriptor.myNonTypesystemRules" resolve="myNonTypesystemRules" />
                  </node>
                </node>
                <node concept="liA8E" id="1a" role="2OqNvi">
                  <ref role="37wK5l" to="33ny:~Set.add(java.lang.Object)" resolve="add" />
                  <node concept="37vLTw" id="1d" role="37wK5m">
                    <ref role="3cqZAo" node="14" resolve="nonTypesystemRule" />
                  </node>
                </node>
              </node>
            </node>
          </node>
        </node>
      </node>
      <node concept="3Tm1VV" id="K" role="1B3o_S" />
      <node concept="3cqZAl" id="L" role="3clF45" />
    </node>
    <node concept="3Tm1VV" id="H" role="1B3o_S" />
    <node concept="3uibUv" id="I" role="1zkMxy">
      <ref role="3uigEE" to="qurh:~BaseHelginsDescriptor" resolve="BaseHelginsDescriptor" />
    </node>
  </node>
  <node concept="312cEu" id="1e">
    <property role="TrG5h" value="check_RolePlayed_NonTypesystemRule" />
    <node concept="3clFbW" id="1f" role="jymVt">
      <node concept="3clFbS" id="1o" role="3clF47">
        <node concept="cd27G" id="1s" role="lGtFl">
          <node concept="3u3nmq" id="1t" role="cd27D">
            <property role="3u3nmv" value="2649606736827368100" />
          </node>
        </node>
      </node>
      <node concept="3Tm1VV" id="1p" role="1B3o_S">
        <node concept="cd27G" id="1u" role="lGtFl">
          <node concept="3u3nmq" id="1v" role="cd27D">
            <property role="3u3nmv" value="2649606736827368100" />
          </node>
        </node>
      </node>
      <node concept="3cqZAl" id="1q" role="3clF45">
        <node concept="cd27G" id="1w" role="lGtFl">
          <node concept="3u3nmq" id="1x" role="cd27D">
            <property role="3u3nmv" value="2649606736827368100" />
          </node>
        </node>
      </node>
      <node concept="cd27G" id="1r" role="lGtFl">
        <node concept="3u3nmq" id="1y" role="cd27D">
          <property role="3u3nmv" value="2649606736827368100" />
        </node>
      </node>
    </node>
    <node concept="3clFb_" id="1g" role="jymVt">
      <property role="TrG5h" value="applyRule" />
      <node concept="3cqZAl" id="1z" role="3clF45">
        <node concept="cd27G" id="1E" role="lGtFl">
          <node concept="3u3nmq" id="1F" role="cd27D">
            <property role="3u3nmv" value="2649606736827368100" />
          </node>
        </node>
      </node>
      <node concept="37vLTG" id="1$" role="3clF46">
        <property role="3TUv4t" value="true" />
        <property role="TrG5h" value="rolePlayed" />
        <node concept="3Tqbb2" id="1G" role="1tU5fm">
          <node concept="cd27G" id="1I" role="lGtFl">
            <node concept="3u3nmq" id="1J" role="cd27D">
              <property role="3u3nmv" value="2649606736827368100" />
            </node>
          </node>
        </node>
        <node concept="cd27G" id="1H" role="lGtFl">
          <node concept="3u3nmq" id="1K" role="cd27D">
            <property role="3u3nmv" value="2649606736827368100" />
          </node>
        </node>
      </node>
      <node concept="37vLTG" id="1_" role="3clF46">
        <property role="TrG5h" value="typeCheckingContext" />
        <property role="3TUv4t" value="true" />
        <node concept="3uibUv" id="1L" role="1tU5fm">
          <ref role="3uigEE" to="u78q:~TypeCheckingContext" resolve="TypeCheckingContext" />
          <node concept="cd27G" id="1N" role="lGtFl">
            <node concept="3u3nmq" id="1O" role="cd27D">
              <property role="3u3nmv" value="2649606736827368100" />
            </node>
          </node>
        </node>
        <node concept="cd27G" id="1M" role="lGtFl">
          <node concept="3u3nmq" id="1P" role="cd27D">
            <property role="3u3nmv" value="2649606736827368100" />
          </node>
        </node>
      </node>
      <node concept="37vLTG" id="1A" role="3clF46">
        <property role="TrG5h" value="status" />
        <node concept="3uibUv" id="1Q" role="1tU5fm">
          <ref role="3uigEE" to="qurh:~IsApplicableStatus" resolve="IsApplicableStatus" />
          <node concept="cd27G" id="1S" role="lGtFl">
            <node concept="3u3nmq" id="1T" role="cd27D">
              <property role="3u3nmv" value="2649606736827368100" />
            </node>
          </node>
        </node>
        <node concept="cd27G" id="1R" role="lGtFl">
          <node concept="3u3nmq" id="1U" role="cd27D">
            <property role="3u3nmv" value="2649606736827368100" />
          </node>
        </node>
      </node>
      <node concept="3clFbS" id="1B" role="3clF47">
        <node concept="2Gpval" id="1V" role="3cqZAp">
          <node concept="2GrKxI" id="1X" role="2Gsz3X">
            <property role="TrG5h" value="s" />
            <node concept="cd27G" id="21" role="lGtFl">
              <node concept="3u3nmq" id="22" role="cd27D">
                <property role="3u3nmv" value="2649606736827371220" />
              </node>
            </node>
          </node>
          <node concept="2OqwBi" id="1Y" role="2GsD0m">
            <node concept="37vLTw" id="23" role="2Oq$k0">
              <ref role="3cqZAo" node="1$" resolve="rolePlayed" />
              <node concept="cd27G" id="26" role="lGtFl">
                <node concept="3u3nmq" id="27" role="cd27D">
                  <property role="3u3nmv" value="2649606736827371239" />
                </node>
              </node>
            </node>
            <node concept="2TvwIu" id="24" role="2OqNvi">
              <node concept="cd27G" id="28" role="lGtFl">
                <node concept="3u3nmq" id="29" role="cd27D">
                  <property role="3u3nmv" value="2649606736827372258" />
                </node>
              </node>
            </node>
            <node concept="cd27G" id="25" role="lGtFl">
              <node concept="3u3nmq" id="2a" role="cd27D">
                <property role="3u3nmv" value="2649606736827371820" />
              </node>
            </node>
          </node>
          <node concept="3clFbS" id="1Z" role="2LFqv$">
            <node concept="3clFbJ" id="2b" role="3cqZAp">
              <node concept="1Wc70l" id="2d" role="3clFbw">
                <node concept="1Wc70l" id="2g" role="3uHU7B">
                  <node concept="2OqwBi" id="2j" role="3uHU7B">
                    <node concept="2GrUjf" id="2m" role="2Oq$k0">
                      <ref role="2Gs0qQ" node="1X" resolve="s" />
                      <node concept="cd27G" id="2p" role="lGtFl">
                        <node concept="3u3nmq" id="2q" role="cd27D">
                          <property role="3u3nmv" value="2649606736827372306" />
                        </node>
                      </node>
                    </node>
                    <node concept="1mIQ4w" id="2n" role="2OqNvi">
                      <node concept="chp4Y" id="2r" role="cj9EA">
                        <ref role="cht4Q" to="lziw:4$zH10ly0Y5" resolve="RolePlayed" />
                        <node concept="cd27G" id="2t" role="lGtFl">
                          <node concept="3u3nmq" id="2u" role="cd27D">
                            <property role="3u3nmv" value="2649606736827374135" />
                          </node>
                        </node>
                      </node>
                      <node concept="cd27G" id="2s" role="lGtFl">
                        <node concept="3u3nmq" id="2v" role="cd27D">
                          <property role="3u3nmv" value="2649606736827374016" />
                        </node>
                      </node>
                    </node>
                    <node concept="cd27G" id="2o" role="lGtFl">
                      <node concept="3u3nmq" id="2w" role="cd27D">
                        <property role="3u3nmv" value="2649606736827373458" />
                      </node>
                    </node>
                  </node>
                  <node concept="3y3z36" id="2k" role="3uHU7w">
                    <node concept="2OqwBi" id="2x" role="3uHU7B">
                      <node concept="1PxgMI" id="2$" role="2Oq$k0">
                        <node concept="chp4Y" id="2B" role="3oSUPX">
                          <ref role="cht4Q" to="lziw:4$zH10ly0Y5" resolve="RolePlayed" />
                          <node concept="cd27G" id="2E" role="lGtFl">
                            <node concept="3u3nmq" id="2F" role="cd27D">
                              <property role="3u3nmv" value="2649606736827376914" />
                            </node>
                          </node>
                        </node>
                        <node concept="2GrUjf" id="2C" role="1m5AlR">
                          <ref role="2Gs0qQ" node="1X" resolve="s" />
                          <node concept="cd27G" id="2G" role="lGtFl">
                            <node concept="3u3nmq" id="2H" role="cd27D">
                              <property role="3u3nmv" value="2649606736827375590" />
                            </node>
                          </node>
                        </node>
                        <node concept="cd27G" id="2D" role="lGtFl">
                          <node concept="3u3nmq" id="2I" role="cd27D">
                            <property role="3u3nmv" value="2649606736827376607" />
                          </node>
                        </node>
                      </node>
                      <node concept="3TrEf2" id="2_" role="2OqNvi">
                        <ref role="3Tt5mk" to="lziw:4$zH10ly0Y6" resolve="role" />
                        <node concept="cd27G" id="2J" role="lGtFl">
                          <node concept="3u3nmq" id="2K" role="cd27D">
                            <property role="3u3nmv" value="2649606736827378038" />
                          </node>
                        </node>
                      </node>
                      <node concept="cd27G" id="2A" role="lGtFl">
                        <node concept="3u3nmq" id="2L" role="cd27D">
                          <property role="3u3nmv" value="2649606736827377516" />
                        </node>
                      </node>
                    </node>
                    <node concept="10Nm6u" id="2y" role="3uHU7w">
                      <node concept="cd27G" id="2M" role="lGtFl">
                        <node concept="3u3nmq" id="2N" role="cd27D">
                          <property role="3u3nmv" value="2649606736827379399" />
                        </node>
                      </node>
                    </node>
                    <node concept="cd27G" id="2z" role="lGtFl">
                      <node concept="3u3nmq" id="2O" role="cd27D">
                        <property role="3u3nmv" value="2649606736827378926" />
                      </node>
                    </node>
                  </node>
                  <node concept="cd27G" id="2l" role="lGtFl">
                    <node concept="3u3nmq" id="2P" role="cd27D">
                      <property role="3u3nmv" value="2649606736827375505" />
                    </node>
                  </node>
                </node>
                <node concept="17R0WA" id="2h" role="3uHU7w">
                  <node concept="2OqwBi" id="2Q" role="3uHU7B">
                    <node concept="1PxgMI" id="2T" role="2Oq$k0">
                      <node concept="chp4Y" id="2W" role="3oSUPX">
                        <ref role="cht4Q" to="lziw:4$zH10ly0Y5" resolve="RolePlayed" />
                        <node concept="cd27G" id="2Z" role="lGtFl">
                          <node concept="3u3nmq" id="30" role="cd27D">
                            <property role="3u3nmv" value="2649606736827379634" />
                          </node>
                        </node>
                      </node>
                      <node concept="2GrUjf" id="2X" role="1m5AlR">
                        <ref role="2Gs0qQ" node="1X" resolve="s" />
                        <node concept="cd27G" id="31" role="lGtFl">
                          <node concept="3u3nmq" id="32" role="cd27D">
                            <property role="3u3nmv" value="2649606736827379635" />
                          </node>
                        </node>
                      </node>
                      <node concept="cd27G" id="2Y" role="lGtFl">
                        <node concept="3u3nmq" id="33" role="cd27D">
                          <property role="3u3nmv" value="2649606736827379633" />
                        </node>
                      </node>
                    </node>
                    <node concept="3TrEf2" id="2U" role="2OqNvi">
                      <ref role="3Tt5mk" to="lziw:4$zH10ly0Y6" resolve="role" />
                      <node concept="cd27G" id="34" role="lGtFl">
                        <node concept="3u3nmq" id="35" role="cd27D">
                          <property role="3u3nmv" value="2649606736827379636" />
                        </node>
                      </node>
                    </node>
                    <node concept="cd27G" id="2V" role="lGtFl">
                      <node concept="3u3nmq" id="36" role="cd27D">
                        <property role="3u3nmv" value="2649606736827379632" />
                      </node>
                    </node>
                  </node>
                  <node concept="2OqwBi" id="2R" role="3uHU7w">
                    <node concept="37vLTw" id="37" role="2Oq$k0">
                      <ref role="3cqZAo" node="1$" resolve="rolePlayed" />
                      <node concept="cd27G" id="3a" role="lGtFl">
                        <node concept="3u3nmq" id="3b" role="cd27D">
                          <property role="3u3nmv" value="2649606736827380203" />
                        </node>
                      </node>
                    </node>
                    <node concept="3TrEf2" id="38" role="2OqNvi">
                      <ref role="3Tt5mk" to="lziw:4$zH10ly0Y6" resolve="role" />
                      <node concept="cd27G" id="3c" role="lGtFl">
                        <node concept="3u3nmq" id="3d" role="cd27D">
                          <property role="3u3nmv" value="2649606736827382651" />
                        </node>
                      </node>
                    </node>
                    <node concept="cd27G" id="39" role="lGtFl">
                      <node concept="3u3nmq" id="3e" role="cd27D">
                        <property role="3u3nmv" value="2649606736827381321" />
                      </node>
                    </node>
                  </node>
                  <node concept="cd27G" id="2S" role="lGtFl">
                    <node concept="3u3nmq" id="3f" role="cd27D">
                      <property role="3u3nmv" value="2649606736827382926" />
                    </node>
                  </node>
                </node>
                <node concept="cd27G" id="2i" role="lGtFl">
                  <node concept="3u3nmq" id="3g" role="cd27D">
                    <property role="3u3nmv" value="2649606736827379494" />
                  </node>
                </node>
              </node>
              <node concept="3clFbS" id="2e" role="3clFbx">
                <node concept="9aQIb" id="3h" role="3cqZAp">
                  <node concept="3clFbS" id="3j" role="9aQI4">
                    <node concept="3cpWs8" id="3m" role="3cqZAp">
                      <node concept="3cpWsn" id="3o" role="3cpWs9">
                        <property role="TrG5h" value="errorTarget" />
                        <property role="3TUv4t" value="true" />
                        <node concept="3uibUv" id="3p" role="1tU5fm">
                          <ref role="3uigEE" to="zavc:~MessageTarget" resolve="MessageTarget" />
                        </node>
                        <node concept="2ShNRf" id="3q" role="33vP2m">
                          <node concept="1pGfFk" id="3r" role="2ShVmc">
                            <ref role="37wK5l" to="zavc:~ReferenceMessageTarget.&lt;init&gt;(org.jetbrains.mps.openapi.language.SAbstractLink)" resolve="ReferenceMessageTarget" />
                            <node concept="359W_D" id="3t" role="37wK5m">
                              <ref role="359W_E" to="lziw:4$zH10ly0Y5" resolve="RolePlayed" />
                              <ref role="359W_F" to="lziw:4$zH10ly0Y6" resolve="role" />
                              <node concept="cd27G" id="3v" role="lGtFl">
                                <node concept="3u3nmq" id="3w" role="cd27D">
                                  <property role="3u3nmv" value="2649606736827383116" />
                                </node>
                              </node>
                            </node>
                            <node concept="cd27G" id="3u" role="lGtFl">
                              <node concept="3u3nmq" id="3x" role="cd27D">
                                <property role="3u3nmv" value="2649606736827383116" />
                              </node>
                            </node>
                          </node>
                          <node concept="cd27G" id="3s" role="lGtFl">
                            <node concept="3u3nmq" id="3y" role="cd27D">
                              <property role="3u3nmv" value="2649606736827383116" />
                            </node>
                          </node>
                        </node>
                      </node>
                    </node>
                    <node concept="3cpWs8" id="3n" role="3cqZAp">
                      <node concept="3cpWsn" id="3z" role="3cpWs9">
                        <property role="TrG5h" value="_reporter_2309309498" />
                        <node concept="3uibUv" id="3$" role="1tU5fm">
                          <ref role="3uigEE" to="2gg1:~IErrorReporter" resolve="IErrorReporter" />
                        </node>
                        <node concept="2OqwBi" id="3_" role="33vP2m">
                          <node concept="3VmV3z" id="3A" role="2Oq$k0">
                            <property role="3VnrPo" value="typeCheckingContext" />
                            <node concept="3uibUv" id="3C" role="3Vn4Tt">
                              <ref role="3uigEE" to="u78q:~TypeCheckingContext" resolve="TypeCheckingContext" />
                            </node>
                          </node>
                          <node concept="liA8E" id="3B" role="2OqNvi">
                            <ref role="37wK5l" to="u78q:~TypeCheckingContext.reportTypeError(org.jetbrains.mps.openapi.model.SNode,java.lang.String,java.lang.String,java.lang.String,jetbrains.mps.errors.QuickFixProvider,jetbrains.mps.errors.messageTargets.MessageTarget)" resolve="reportTypeError" />
                            <node concept="37vLTw" id="3D" role="37wK5m">
                              <ref role="3cqZAo" node="1$" resolve="rolePlayed" />
                              <node concept="cd27G" id="3J" role="lGtFl">
                                <node concept="3u3nmq" id="3K" role="cd27D">
                                  <property role="3u3nmv" value="2649606736827383102" />
                                </node>
                              </node>
                            </node>
                            <node concept="Xl_RD" id="3E" role="37wK5m">
                              <property role="Xl_RC" value="Duplicate role" />
                              <node concept="cd27G" id="3L" role="lGtFl">
                                <node concept="3u3nmq" id="3M" role="cd27D">
                                  <property role="3u3nmv" value="2649606736827383084" />
                                </node>
                              </node>
                            </node>
                            <node concept="Xl_RD" id="3F" role="37wK5m">
                              <property role="Xl_RC" value="r:3588f820-f38c-4943-a5b1-ddc6c762e9a9(com.strumenta.businessorg.typesystem)" />
                            </node>
                            <node concept="Xl_RD" id="3G" role="37wK5m">
                              <property role="Xl_RC" value="2649606736827383072" />
                            </node>
                            <node concept="10Nm6u" id="3H" role="37wK5m" />
                            <node concept="37vLTw" id="3I" role="37wK5m">
                              <ref role="3cqZAo" node="3o" resolve="errorTarget" />
                            </node>
                          </node>
                        </node>
                      </node>
                    </node>
                  </node>
                  <node concept="6wLe0" id="3k" role="lGtFl">
                    <property role="6wLej" value="2649606736827383072" />
                    <property role="6wLeW" value="r:3588f820-f38c-4943-a5b1-ddc6c762e9a9(com.strumenta.businessorg.typesystem)" />
                  </node>
                  <node concept="cd27G" id="3l" role="lGtFl">
                    <node concept="3u3nmq" id="3N" role="cd27D">
                      <property role="3u3nmv" value="2649606736827383072" />
                    </node>
                  </node>
                </node>
                <node concept="cd27G" id="3i" role="lGtFl">
                  <node concept="3u3nmq" id="3O" role="cd27D">
                    <property role="3u3nmv" value="2649606736827372296" />
                  </node>
                </node>
              </node>
              <node concept="cd27G" id="2f" role="lGtFl">
                <node concept="3u3nmq" id="3P" role="cd27D">
                  <property role="3u3nmv" value="2649606736827372294" />
                </node>
              </node>
            </node>
            <node concept="cd27G" id="2c" role="lGtFl">
              <node concept="3u3nmq" id="3Q" role="cd27D">
                <property role="3u3nmv" value="2649606736827371222" />
              </node>
            </node>
          </node>
          <node concept="cd27G" id="20" role="lGtFl">
            <node concept="3u3nmq" id="3R" role="cd27D">
              <property role="3u3nmv" value="2649606736827371219" />
            </node>
          </node>
        </node>
        <node concept="cd27G" id="1W" role="lGtFl">
          <node concept="3u3nmq" id="3S" role="cd27D">
            <property role="3u3nmv" value="2649606736827368101" />
          </node>
        </node>
      </node>
      <node concept="3Tm1VV" id="1C" role="1B3o_S">
        <node concept="cd27G" id="3T" role="lGtFl">
          <node concept="3u3nmq" id="3U" role="cd27D">
            <property role="3u3nmv" value="2649606736827368100" />
          </node>
        </node>
      </node>
      <node concept="cd27G" id="1D" role="lGtFl">
        <node concept="3u3nmq" id="3V" role="cd27D">
          <property role="3u3nmv" value="2649606736827368100" />
        </node>
      </node>
    </node>
    <node concept="3clFb_" id="1h" role="jymVt">
      <property role="TrG5h" value="getApplicableConcept" />
      <node concept="3bZ5Sz" id="3W" role="3clF45">
        <node concept="cd27G" id="40" role="lGtFl">
          <node concept="3u3nmq" id="41" role="cd27D">
            <property role="3u3nmv" value="2649606736827368100" />
          </node>
        </node>
      </node>
      <node concept="3clFbS" id="3X" role="3clF47">
        <node concept="3cpWs6" id="42" role="3cqZAp">
          <node concept="35c_gC" id="44" role="3cqZAk">
            <ref role="35c_gD" to="lziw:4$zH10ly0Y5" resolve="RolePlayed" />
            <node concept="cd27G" id="46" role="lGtFl">
              <node concept="3u3nmq" id="47" role="cd27D">
                <property role="3u3nmv" value="2649606736827368100" />
              </node>
            </node>
          </node>
          <node concept="cd27G" id="45" role="lGtFl">
            <node concept="3u3nmq" id="48" role="cd27D">
              <property role="3u3nmv" value="2649606736827368100" />
            </node>
          </node>
        </node>
        <node concept="cd27G" id="43" role="lGtFl">
          <node concept="3u3nmq" id="49" role="cd27D">
            <property role="3u3nmv" value="2649606736827368100" />
          </node>
        </node>
      </node>
      <node concept="3Tm1VV" id="3Y" role="1B3o_S">
        <node concept="cd27G" id="4a" role="lGtFl">
          <node concept="3u3nmq" id="4b" role="cd27D">
            <property role="3u3nmv" value="2649606736827368100" />
          </node>
        </node>
      </node>
      <node concept="cd27G" id="3Z" role="lGtFl">
        <node concept="3u3nmq" id="4c" role="cd27D">
          <property role="3u3nmv" value="2649606736827368100" />
        </node>
      </node>
    </node>
    <node concept="3clFb_" id="1i" role="jymVt">
      <property role="TrG5h" value="isApplicableAndPattern" />
      <node concept="37vLTG" id="4d" role="3clF46">
        <property role="TrG5h" value="argument" />
        <node concept="3Tqbb2" id="4i" role="1tU5fm">
          <node concept="cd27G" id="4k" role="lGtFl">
            <node concept="3u3nmq" id="4l" role="cd27D">
              <property role="3u3nmv" value="2649606736827368100" />
            </node>
          </node>
        </node>
        <node concept="cd27G" id="4j" role="lGtFl">
          <node concept="3u3nmq" id="4m" role="cd27D">
            <property role="3u3nmv" value="2649606736827368100" />
          </node>
        </node>
      </node>
      <node concept="3clFbS" id="4e" role="3clF47">
        <node concept="9aQIb" id="4n" role="3cqZAp">
          <node concept="3clFbS" id="4p" role="9aQI4">
            <node concept="3cpWs6" id="4r" role="3cqZAp">
              <node concept="2ShNRf" id="4t" role="3cqZAk">
                <node concept="1pGfFk" id="4v" role="2ShVmc">
                  <ref role="37wK5l" to="qurh:~IsApplicableStatus.&lt;init&gt;(boolean,jetbrains.mps.lang.pattern.GeneratedMatchingPattern)" resolve="IsApplicableStatus" />
                  <node concept="2OqwBi" id="4x" role="37wK5m">
                    <node concept="2OqwBi" id="4$" role="2Oq$k0">
                      <node concept="liA8E" id="4B" role="2OqNvi">
                        <ref role="37wK5l" to="mhbf:~SNode.getConcept()" resolve="getConcept" />
                        <node concept="cd27G" id="4E" role="lGtFl">
                          <node concept="3u3nmq" id="4F" role="cd27D">
                            <property role="3u3nmv" value="2649606736827368100" />
                          </node>
                        </node>
                      </node>
                      <node concept="2JrnkZ" id="4C" role="2Oq$k0">
                        <node concept="37vLTw" id="4G" role="2JrQYb">
                          <ref role="3cqZAo" node="4d" resolve="argument" />
                          <node concept="cd27G" id="4I" role="lGtFl">
                            <node concept="3u3nmq" id="4J" role="cd27D">
                              <property role="3u3nmv" value="2649606736827368100" />
                            </node>
                          </node>
                        </node>
                        <node concept="cd27G" id="4H" role="lGtFl">
                          <node concept="3u3nmq" id="4K" role="cd27D">
                            <property role="3u3nmv" value="2649606736827368100" />
                          </node>
                        </node>
                      </node>
                      <node concept="cd27G" id="4D" role="lGtFl">
                        <node concept="3u3nmq" id="4L" role="cd27D">
                          <property role="3u3nmv" value="2649606736827368100" />
                        </node>
                      </node>
                    </node>
                    <node concept="liA8E" id="4_" role="2OqNvi">
                      <ref role="37wK5l" to="c17a:~SAbstractConcept.isSubConceptOf(org.jetbrains.mps.openapi.language.SAbstractConcept)" resolve="isSubConceptOf" />
                      <node concept="1rXfSq" id="4M" role="37wK5m">
                        <ref role="37wK5l" node="1h" resolve="getApplicableConcept" />
                        <node concept="cd27G" id="4O" role="lGtFl">
                          <node concept="3u3nmq" id="4P" role="cd27D">
                            <property role="3u3nmv" value="2649606736827368100" />
                          </node>
                        </node>
                      </node>
                      <node concept="cd27G" id="4N" role="lGtFl">
                        <node concept="3u3nmq" id="4Q" role="cd27D">
                          <property role="3u3nmv" value="2649606736827368100" />
                        </node>
                      </node>
                    </node>
                    <node concept="cd27G" id="4A" role="lGtFl">
                      <node concept="3u3nmq" id="4R" role="cd27D">
                        <property role="3u3nmv" value="2649606736827368100" />
                      </node>
                    </node>
                  </node>
                  <node concept="10Nm6u" id="4y" role="37wK5m">
                    <node concept="cd27G" id="4S" role="lGtFl">
                      <node concept="3u3nmq" id="4T" role="cd27D">
                        <property role="3u3nmv" value="2649606736827368100" />
                      </node>
                    </node>
                  </node>
                  <node concept="cd27G" id="4z" role="lGtFl">
                    <node concept="3u3nmq" id="4U" role="cd27D">
                      <property role="3u3nmv" value="2649606736827368100" />
                    </node>
                  </node>
                </node>
                <node concept="cd27G" id="4w" role="lGtFl">
                  <node concept="3u3nmq" id="4V" role="cd27D">
                    <property role="3u3nmv" value="2649606736827368100" />
                  </node>
                </node>
              </node>
              <node concept="cd27G" id="4u" role="lGtFl">
                <node concept="3u3nmq" id="4W" role="cd27D">
                  <property role="3u3nmv" value="2649606736827368100" />
                </node>
              </node>
            </node>
            <node concept="cd27G" id="4s" role="lGtFl">
              <node concept="3u3nmq" id="4X" role="cd27D">
                <property role="3u3nmv" value="2649606736827368100" />
              </node>
            </node>
          </node>
          <node concept="cd27G" id="4q" role="lGtFl">
            <node concept="3u3nmq" id="4Y" role="cd27D">
              <property role="3u3nmv" value="2649606736827368100" />
            </node>
          </node>
        </node>
        <node concept="cd27G" id="4o" role="lGtFl">
          <node concept="3u3nmq" id="4Z" role="cd27D">
            <property role="3u3nmv" value="2649606736827368100" />
          </node>
        </node>
      </node>
      <node concept="3uibUv" id="4f" role="3clF45">
        <ref role="3uigEE" to="qurh:~IsApplicableStatus" resolve="IsApplicableStatus" />
        <node concept="cd27G" id="50" role="lGtFl">
          <node concept="3u3nmq" id="51" role="cd27D">
            <property role="3u3nmv" value="2649606736827368100" />
          </node>
        </node>
      </node>
      <node concept="3Tm1VV" id="4g" role="1B3o_S">
        <node concept="cd27G" id="52" role="lGtFl">
          <node concept="3u3nmq" id="53" role="cd27D">
            <property role="3u3nmv" value="2649606736827368100" />
          </node>
        </node>
      </node>
      <node concept="cd27G" id="4h" role="lGtFl">
        <node concept="3u3nmq" id="54" role="cd27D">
          <property role="3u3nmv" value="2649606736827368100" />
        </node>
      </node>
    </node>
    <node concept="3clFb_" id="1j" role="jymVt">
      <property role="TrG5h" value="overrides" />
      <node concept="3clFbS" id="55" role="3clF47">
        <node concept="3cpWs6" id="59" role="3cqZAp">
          <node concept="3clFbT" id="5b" role="3cqZAk">
            <node concept="cd27G" id="5d" role="lGtFl">
              <node concept="3u3nmq" id="5e" role="cd27D">
                <property role="3u3nmv" value="2649606736827368100" />
              </node>
            </node>
          </node>
          <node concept="cd27G" id="5c" role="lGtFl">
            <node concept="3u3nmq" id="5f" role="cd27D">
              <property role="3u3nmv" value="2649606736827368100" />
            </node>
          </node>
        </node>
        <node concept="cd27G" id="5a" role="lGtFl">
          <node concept="3u3nmq" id="5g" role="cd27D">
            <property role="3u3nmv" value="2649606736827368100" />
          </node>
        </node>
      </node>
      <node concept="10P_77" id="56" role="3clF45">
        <node concept="cd27G" id="5h" role="lGtFl">
          <node concept="3u3nmq" id="5i" role="cd27D">
            <property role="3u3nmv" value="2649606736827368100" />
          </node>
        </node>
      </node>
      <node concept="3Tm1VV" id="57" role="1B3o_S">
        <node concept="cd27G" id="5j" role="lGtFl">
          <node concept="3u3nmq" id="5k" role="cd27D">
            <property role="3u3nmv" value="2649606736827368100" />
          </node>
        </node>
      </node>
      <node concept="cd27G" id="58" role="lGtFl">
        <node concept="3u3nmq" id="5l" role="cd27D">
          <property role="3u3nmv" value="2649606736827368100" />
        </node>
      </node>
    </node>
    <node concept="3uibUv" id="1k" role="EKbjA">
      <ref role="3uigEE" to="qurh:~NonTypesystemRule_Runtime" resolve="NonTypesystemRule_Runtime" />
      <node concept="cd27G" id="5m" role="lGtFl">
        <node concept="3u3nmq" id="5n" role="cd27D">
          <property role="3u3nmv" value="2649606736827368100" />
        </node>
      </node>
    </node>
    <node concept="3uibUv" id="1l" role="1zkMxy">
      <ref role="3uigEE" to="qurh:~AbstractNonTypesystemRule_Runtime" resolve="AbstractNonTypesystemRule_Runtime" />
      <node concept="cd27G" id="5o" role="lGtFl">
        <node concept="3u3nmq" id="5p" role="cd27D">
          <property role="3u3nmv" value="2649606736827368100" />
        </node>
      </node>
    </node>
    <node concept="3Tm1VV" id="1m" role="1B3o_S">
      <node concept="cd27G" id="5q" role="lGtFl">
        <node concept="3u3nmq" id="5r" role="cd27D">
          <property role="3u3nmv" value="2649606736827368100" />
        </node>
      </node>
    </node>
    <node concept="cd27G" id="1n" role="lGtFl">
      <node concept="3u3nmq" id="5s" role="cd27D">
        <property role="3u3nmv" value="2649606736827368100" />
      </node>
    </node>
  </node>
  <node concept="312cEu" id="5t">
    <property role="TrG5h" value="check_Role_NonTypesystemRule" />
    <node concept="3clFbW" id="5u" role="jymVt">
      <node concept="3clFbS" id="5B" role="3clF47">
        <node concept="cd27G" id="5F" role="lGtFl">
          <node concept="3u3nmq" id="5G" role="cd27D">
            <property role="3u3nmv" value="3185435802458718249" />
          </node>
        </node>
      </node>
      <node concept="3Tm1VV" id="5C" role="1B3o_S">
        <node concept="cd27G" id="5H" role="lGtFl">
          <node concept="3u3nmq" id="5I" role="cd27D">
            <property role="3u3nmv" value="3185435802458718249" />
          </node>
        </node>
      </node>
      <node concept="3cqZAl" id="5D" role="3clF45">
        <node concept="cd27G" id="5J" role="lGtFl">
          <node concept="3u3nmq" id="5K" role="cd27D">
            <property role="3u3nmv" value="3185435802458718249" />
          </node>
        </node>
      </node>
      <node concept="cd27G" id="5E" role="lGtFl">
        <node concept="3u3nmq" id="5L" role="cd27D">
          <property role="3u3nmv" value="3185435802458718249" />
        </node>
      </node>
    </node>
    <node concept="3clFb_" id="5v" role="jymVt">
      <property role="TrG5h" value="applyRule" />
      <node concept="3cqZAl" id="5M" role="3clF45">
        <node concept="cd27G" id="5T" role="lGtFl">
          <node concept="3u3nmq" id="5U" role="cd27D">
            <property role="3u3nmv" value="3185435802458718249" />
          </node>
        </node>
      </node>
      <node concept="37vLTG" id="5N" role="3clF46">
        <property role="3TUv4t" value="true" />
        <property role="TrG5h" value="role" />
        <node concept="3Tqbb2" id="5V" role="1tU5fm">
          <node concept="cd27G" id="5X" role="lGtFl">
            <node concept="3u3nmq" id="5Y" role="cd27D">
              <property role="3u3nmv" value="3185435802458718249" />
            </node>
          </node>
        </node>
        <node concept="cd27G" id="5W" role="lGtFl">
          <node concept="3u3nmq" id="5Z" role="cd27D">
            <property role="3u3nmv" value="3185435802458718249" />
          </node>
        </node>
      </node>
      <node concept="37vLTG" id="5O" role="3clF46">
        <property role="TrG5h" value="typeCheckingContext" />
        <property role="3TUv4t" value="true" />
        <node concept="3uibUv" id="60" role="1tU5fm">
          <ref role="3uigEE" to="u78q:~TypeCheckingContext" resolve="TypeCheckingContext" />
          <node concept="cd27G" id="62" role="lGtFl">
            <node concept="3u3nmq" id="63" role="cd27D">
              <property role="3u3nmv" value="3185435802458718249" />
            </node>
          </node>
        </node>
        <node concept="cd27G" id="61" role="lGtFl">
          <node concept="3u3nmq" id="64" role="cd27D">
            <property role="3u3nmv" value="3185435802458718249" />
          </node>
        </node>
      </node>
      <node concept="37vLTG" id="5P" role="3clF46">
        <property role="TrG5h" value="status" />
        <node concept="3uibUv" id="65" role="1tU5fm">
          <ref role="3uigEE" to="qurh:~IsApplicableStatus" resolve="IsApplicableStatus" />
          <node concept="cd27G" id="67" role="lGtFl">
            <node concept="3u3nmq" id="68" role="cd27D">
              <property role="3u3nmv" value="3185435802458718249" />
            </node>
          </node>
        </node>
        <node concept="cd27G" id="66" role="lGtFl">
          <node concept="3u3nmq" id="69" role="cd27D">
            <property role="3u3nmv" value="3185435802458718249" />
          </node>
        </node>
      </node>
      <node concept="3clFbS" id="5Q" role="3clF47">
        <node concept="3clFbJ" id="6a" role="3cqZAp">
          <node concept="3clFbS" id="6c" role="3clFbx">
            <node concept="2Gpval" id="6f" role="3cqZAp">
              <node concept="2GrKxI" id="6h" role="2Gsz3X">
                <property role="TrG5h" value="s" />
                <node concept="cd27G" id="6l" role="lGtFl">
                  <node concept="3u3nmq" id="6m" role="cd27D">
                    <property role="3u3nmv" value="3185435802458721679" />
                  </node>
                </node>
              </node>
              <node concept="2OqwBi" id="6i" role="2GsD0m">
                <node concept="37vLTw" id="6n" role="2Oq$k0">
                  <ref role="3cqZAo" node="5N" resolve="role" />
                  <node concept="cd27G" id="6q" role="lGtFl">
                    <node concept="3u3nmq" id="6r" role="cd27D">
                      <property role="3u3nmv" value="3185435802458721698" />
                    </node>
                  </node>
                </node>
                <node concept="2TvwIu" id="6o" role="2OqNvi">
                  <node concept="cd27G" id="6s" role="lGtFl">
                    <node concept="3u3nmq" id="6t" role="cd27D">
                      <property role="3u3nmv" value="3185435802458722999" />
                    </node>
                  </node>
                </node>
                <node concept="cd27G" id="6p" role="lGtFl">
                  <node concept="3u3nmq" id="6u" role="cd27D">
                    <property role="3u3nmv" value="3185435802458722379" />
                  </node>
                </node>
              </node>
              <node concept="3clFbS" id="6j" role="2LFqv$">
                <node concept="3clFbJ" id="6v" role="3cqZAp">
                  <node concept="1Wc70l" id="6x" role="3clFbw">
                    <node concept="1Wc70l" id="6$" role="3uHU7B">
                      <node concept="2OqwBi" id="6B" role="3uHU7B">
                        <node concept="2GrUjf" id="6E" role="2Oq$k0">
                          <ref role="2Gs0qQ" node="6h" resolve="s" />
                          <node concept="cd27G" id="6H" role="lGtFl">
                            <node concept="3u3nmq" id="6I" role="cd27D">
                              <property role="3u3nmv" value="3185435802458723044" />
                            </node>
                          </node>
                        </node>
                        <node concept="1mIQ4w" id="6F" role="2OqNvi">
                          <node concept="chp4Y" id="6J" role="cj9EA">
                            <ref role="cht4Q" to="lziw:4$zH10lxCIn" resolve="Role" />
                            <node concept="cd27G" id="6L" role="lGtFl">
                              <node concept="3u3nmq" id="6M" role="cd27D">
                                <property role="3u3nmv" value="3185435802458724752" />
                              </node>
                            </node>
                          </node>
                          <node concept="cd27G" id="6K" role="lGtFl">
                            <node concept="3u3nmq" id="6N" role="cd27D">
                              <property role="3u3nmv" value="3185435802458724633" />
                            </node>
                          </node>
                        </node>
                        <node concept="cd27G" id="6G" role="lGtFl">
                          <node concept="3u3nmq" id="6O" role="cd27D">
                            <property role="3u3nmv" value="3185435802458724196" />
                          </node>
                        </node>
                      </node>
                      <node concept="3y3z36" id="6C" role="3uHU7w">
                        <node concept="2OqwBi" id="6P" role="3uHU7B">
                          <node concept="1PxgMI" id="6S" role="2Oq$k0">
                            <node concept="chp4Y" id="6V" role="3oSUPX">
                              <ref role="cht4Q" to="lziw:4$zH10lxCIn" resolve="Role" />
                              <node concept="cd27G" id="6Y" role="lGtFl">
                                <node concept="3u3nmq" id="6Z" role="cd27D">
                                  <property role="3u3nmv" value="3185435802458726328" />
                                </node>
                              </node>
                            </node>
                            <node concept="2GrUjf" id="6W" role="1m5AlR">
                              <ref role="2Gs0qQ" node="6h" resolve="s" />
                              <node concept="cd27G" id="70" role="lGtFl">
                                <node concept="3u3nmq" id="71" role="cd27D">
                                  <property role="3u3nmv" value="3185435802458724965" />
                                </node>
                              </node>
                            </node>
                            <node concept="cd27G" id="6X" role="lGtFl">
                              <node concept="3u3nmq" id="72" role="cd27D">
                                <property role="3u3nmv" value="3185435802458726021" />
                              </node>
                            </node>
                          </node>
                          <node concept="3TrcHB" id="6T" role="2OqNvi">
                            <ref role="3TsBF5" to="tpck:h0TrG11" resolve="name" />
                            <node concept="cd27G" id="73" role="lGtFl">
                              <node concept="3u3nmq" id="74" role="cd27D">
                                <property role="3u3nmv" value="3185435802458727746" />
                              </node>
                            </node>
                          </node>
                          <node concept="cd27G" id="6U" role="lGtFl">
                            <node concept="3u3nmq" id="75" role="cd27D">
                              <property role="3u3nmv" value="3185435802458727042" />
                            </node>
                          </node>
                        </node>
                        <node concept="10Nm6u" id="6Q" role="3uHU7w">
                          <node concept="cd27G" id="76" role="lGtFl">
                            <node concept="3u3nmq" id="77" role="cd27D">
                              <property role="3u3nmv" value="3185435802458730336" />
                            </node>
                          </node>
                        </node>
                        <node concept="cd27G" id="6R" role="lGtFl">
                          <node concept="3u3nmq" id="78" role="cd27D">
                            <property role="3u3nmv" value="3185435802458729422" />
                          </node>
                        </node>
                      </node>
                      <node concept="cd27G" id="6D" role="lGtFl">
                        <node concept="3u3nmq" id="79" role="cd27D">
                          <property role="3u3nmv" value="3185435802458724889" />
                        </node>
                      </node>
                    </node>
                    <node concept="17R0WA" id="6_" role="3uHU7w">
                      <node concept="2OqwBi" id="7a" role="3uHU7B">
                        <node concept="1PxgMI" id="7d" role="2Oq$k0">
                          <node concept="chp4Y" id="7g" role="3oSUPX">
                            <ref role="cht4Q" to="lziw:4$zH10lxCIn" resolve="Role" />
                            <node concept="cd27G" id="7j" role="lGtFl">
                              <node concept="3u3nmq" id="7k" role="cd27D">
                                <property role="3u3nmv" value="3185435802458737564" />
                              </node>
                            </node>
                          </node>
                          <node concept="2GrUjf" id="7h" role="1m5AlR">
                            <ref role="2Gs0qQ" node="6h" resolve="s" />
                            <node concept="cd27G" id="7l" role="lGtFl">
                              <node concept="3u3nmq" id="7m" role="cd27D">
                                <property role="3u3nmv" value="3185435802458737565" />
                              </node>
                            </node>
                          </node>
                          <node concept="cd27G" id="7i" role="lGtFl">
                            <node concept="3u3nmq" id="7n" role="cd27D">
                              <property role="3u3nmv" value="3185435802458737563" />
                            </node>
                          </node>
                        </node>
                        <node concept="3TrcHB" id="7e" role="2OqNvi">
                          <ref role="3TsBF5" to="tpck:h0TrG11" resolve="name" />
                          <node concept="cd27G" id="7o" role="lGtFl">
                            <node concept="3u3nmq" id="7p" role="cd27D">
                              <property role="3u3nmv" value="3185435802458737566" />
                            </node>
                          </node>
                        </node>
                        <node concept="cd27G" id="7f" role="lGtFl">
                          <node concept="3u3nmq" id="7q" role="cd27D">
                            <property role="3u3nmv" value="3185435802458737562" />
                          </node>
                        </node>
                      </node>
                      <node concept="2OqwBi" id="7b" role="3uHU7w">
                        <node concept="37vLTw" id="7r" role="2Oq$k0">
                          <ref role="3cqZAo" node="5N" resolve="role" />
                          <node concept="cd27G" id="7u" role="lGtFl">
                            <node concept="3u3nmq" id="7v" role="cd27D">
                              <property role="3u3nmv" value="3185435802458741263" />
                            </node>
                          </node>
                        </node>
                        <node concept="3TrcHB" id="7s" role="2OqNvi">
                          <ref role="3TsBF5" to="tpck:h0TrG11" resolve="name" />
                          <node concept="cd27G" id="7w" role="lGtFl">
                            <node concept="3u3nmq" id="7x" role="cd27D">
                              <property role="3u3nmv" value="3185435802458742390" />
                            </node>
                          </node>
                        </node>
                        <node concept="cd27G" id="7t" role="lGtFl">
                          <node concept="3u3nmq" id="7y" role="cd27D">
                            <property role="3u3nmv" value="3185435802458742195" />
                          </node>
                        </node>
                      </node>
                      <node concept="cd27G" id="7c" role="lGtFl">
                        <node concept="3u3nmq" id="7z" role="cd27D">
                          <property role="3u3nmv" value="3185435802458849070" />
                        </node>
                      </node>
                    </node>
                    <node concept="cd27G" id="6A" role="lGtFl">
                      <node concept="3u3nmq" id="7$" role="cd27D">
                        <property role="3u3nmv" value="3185435802458737033" />
                      </node>
                    </node>
                  </node>
                  <node concept="3clFbS" id="6y" role="3clFbx">
                    <node concept="9aQIb" id="7_" role="3cqZAp">
                      <node concept="3clFbS" id="7B" role="9aQI4">
                        <node concept="3cpWs8" id="7E" role="3cqZAp">
                          <node concept="3cpWsn" id="7G" role="3cpWs9">
                            <property role="TrG5h" value="errorTarget" />
                            <property role="3TUv4t" value="true" />
                            <node concept="3uibUv" id="7H" role="1tU5fm">
                              <ref role="3uigEE" to="zavc:~MessageTarget" resolve="MessageTarget" />
                            </node>
                            <node concept="2ShNRf" id="7I" role="33vP2m">
                              <node concept="1pGfFk" id="7J" role="2ShVmc">
                                <ref role="37wK5l" to="zavc:~PropertyMessageTarget.&lt;init&gt;(org.jetbrains.mps.openapi.language.SProperty)" resolve="PropertyMessageTarget" />
                                <node concept="355D3s" id="7L" role="37wK5m">
                                  <ref role="355D3t" to="tpck:h0TrEE$" resolve="INamedConcept" />
                                  <ref role="355D3u" to="tpck:h0TrG11" resolve="name" />
                                  <node concept="cd27G" id="7N" role="lGtFl">
                                    <node concept="3u3nmq" id="7O" role="cd27D">
                                      <property role="3u3nmv" value="3185435802458873596" />
                                    </node>
                                  </node>
                                </node>
                                <node concept="cd27G" id="7M" role="lGtFl">
                                  <node concept="3u3nmq" id="7P" role="cd27D">
                                    <property role="3u3nmv" value="3185435802458873596" />
                                  </node>
                                </node>
                              </node>
                              <node concept="cd27G" id="7K" role="lGtFl">
                                <node concept="3u3nmq" id="7Q" role="cd27D">
                                  <property role="3u3nmv" value="3185435802458873596" />
                                </node>
                              </node>
                            </node>
                          </node>
                        </node>
                        <node concept="3cpWs8" id="7F" role="3cqZAp">
                          <node concept="3cpWsn" id="7R" role="3cpWs9">
                            <property role="TrG5h" value="_reporter_2309309498" />
                            <node concept="3uibUv" id="7S" role="1tU5fm">
                              <ref role="3uigEE" to="2gg1:~IErrorReporter" resolve="IErrorReporter" />
                            </node>
                            <node concept="2OqwBi" id="7T" role="33vP2m">
                              <node concept="3VmV3z" id="7U" role="2Oq$k0">
                                <property role="3VnrPo" value="typeCheckingContext" />
                                <node concept="3uibUv" id="7W" role="3Vn4Tt">
                                  <ref role="3uigEE" to="u78q:~TypeCheckingContext" resolve="TypeCheckingContext" />
                                </node>
                              </node>
                              <node concept="liA8E" id="7V" role="2OqNvi">
                                <ref role="37wK5l" to="u78q:~TypeCheckingContext.reportTypeError(org.jetbrains.mps.openapi.model.SNode,java.lang.String,java.lang.String,java.lang.String,jetbrains.mps.errors.QuickFixProvider,jetbrains.mps.errors.messageTargets.MessageTarget)" resolve="reportTypeError" />
                                <node concept="37vLTw" id="7X" role="37wK5m">
                                  <ref role="3cqZAo" node="5N" resolve="role" />
                                  <node concept="cd27G" id="83" role="lGtFl">
                                    <node concept="3u3nmq" id="84" role="cd27D">
                                      <property role="3u3nmv" value="3185435802458742611" />
                                    </node>
                                  </node>
                                </node>
                                <node concept="Xl_RD" id="7Y" role="37wK5m">
                                  <property role="Xl_RC" value="Duplicate name" />
                                  <node concept="cd27G" id="85" role="lGtFl">
                                    <node concept="3u3nmq" id="86" role="cd27D">
                                      <property role="3u3nmv" value="3185435802458742572" />
                                    </node>
                                  </node>
                                </node>
                                <node concept="Xl_RD" id="7Z" role="37wK5m">
                                  <property role="Xl_RC" value="r:3588f820-f38c-4943-a5b1-ddc6c762e9a9(com.strumenta.businessorg.typesystem)" />
                                </node>
                                <node concept="Xl_RD" id="80" role="37wK5m">
                                  <property role="Xl_RC" value="3185435802458742560" />
                                </node>
                                <node concept="10Nm6u" id="81" role="37wK5m" />
                                <node concept="37vLTw" id="82" role="37wK5m">
                                  <ref role="3cqZAo" node="7G" resolve="errorTarget" />
                                </node>
                              </node>
                            </node>
                          </node>
                        </node>
                      </node>
                      <node concept="6wLe0" id="7C" role="lGtFl">
                        <property role="6wLej" value="3185435802458742560" />
                        <property role="6wLeW" value="r:3588f820-f38c-4943-a5b1-ddc6c762e9a9(com.strumenta.businessorg.typesystem)" />
                      </node>
                      <node concept="cd27G" id="7D" role="lGtFl">
                        <node concept="3u3nmq" id="87" role="cd27D">
                          <property role="3u3nmv" value="3185435802458742560" />
                        </node>
                      </node>
                    </node>
                    <node concept="cd27G" id="7A" role="lGtFl">
                      <node concept="3u3nmq" id="88" role="cd27D">
                        <property role="3u3nmv" value="3185435802458723034" />
                      </node>
                    </node>
                  </node>
                  <node concept="cd27G" id="6z" role="lGtFl">
                    <node concept="3u3nmq" id="89" role="cd27D">
                      <property role="3u3nmv" value="3185435802458723032" />
                    </node>
                  </node>
                </node>
                <node concept="cd27G" id="6w" role="lGtFl">
                  <node concept="3u3nmq" id="8a" role="cd27D">
                    <property role="3u3nmv" value="3185435802458721681" />
                  </node>
                </node>
              </node>
              <node concept="cd27G" id="6k" role="lGtFl">
                <node concept="3u3nmq" id="8b" role="cd27D">
                  <property role="3u3nmv" value="3185435802458721678" />
                </node>
              </node>
            </node>
            <node concept="cd27G" id="6g" role="lGtFl">
              <node concept="3u3nmq" id="8c" role="cd27D">
                <property role="3u3nmv" value="3185435802458731031" />
              </node>
            </node>
          </node>
          <node concept="1Wc70l" id="6d" role="3clFbw">
            <node concept="2OqwBi" id="8d" role="3uHU7w">
              <node concept="2OqwBi" id="8g" role="2Oq$k0">
                <node concept="37vLTw" id="8j" role="2Oq$k0">
                  <ref role="3cqZAo" node="5N" resolve="role" />
                  <node concept="cd27G" id="8m" role="lGtFl">
                    <node concept="3u3nmq" id="8n" role="cd27D">
                      <property role="3u3nmv" value="3185435802458735124" />
                    </node>
                  </node>
                </node>
                <node concept="3TrcHB" id="8k" role="2OqNvi">
                  <ref role="3TsBF5" to="tpck:h0TrG11" resolve="name" />
                  <node concept="cd27G" id="8o" role="lGtFl">
                    <node concept="3u3nmq" id="8p" role="cd27D">
                      <property role="3u3nmv" value="3185435802458735307" />
                    </node>
                  </node>
                </node>
                <node concept="cd27G" id="8l" role="lGtFl">
                  <node concept="3u3nmq" id="8q" role="cd27D">
                    <property role="3u3nmv" value="3185435802458735158" />
                  </node>
                </node>
              </node>
              <node concept="17RvpY" id="8h" role="2OqNvi">
                <node concept="cd27G" id="8r" role="lGtFl">
                  <node concept="3u3nmq" id="8s" role="cd27D">
                    <property role="3u3nmv" value="3185435802458736899" />
                  </node>
                </node>
              </node>
              <node concept="cd27G" id="8i" role="lGtFl">
                <node concept="3u3nmq" id="8t" role="cd27D">
                  <property role="3u3nmv" value="3185435802458735456" />
                </node>
              </node>
            </node>
            <node concept="3y3z36" id="8e" role="3uHU7B">
              <node concept="2OqwBi" id="8u" role="3uHU7B">
                <node concept="37vLTw" id="8x" role="2Oq$k0">
                  <ref role="3cqZAo" node="5N" resolve="role" />
                  <node concept="cd27G" id="8$" role="lGtFl">
                    <node concept="3u3nmq" id="8_" role="cd27D">
                      <property role="3u3nmv" value="3185435802458731064" />
                    </node>
                  </node>
                </node>
                <node concept="3TrcHB" id="8y" role="2OqNvi">
                  <ref role="3TsBF5" to="tpck:h0TrG11" resolve="name" />
                  <node concept="cd27G" id="8A" role="lGtFl">
                    <node concept="3u3nmq" id="8B" role="cd27D">
                      <property role="3u3nmv" value="3185435802458732433" />
                    </node>
                  </node>
                </node>
                <node concept="cd27G" id="8z" role="lGtFl">
                  <node concept="3u3nmq" id="8C" role="cd27D">
                    <property role="3u3nmv" value="3185435802458731733" />
                  </node>
                </node>
              </node>
              <node concept="10Nm6u" id="8v" role="3uHU7w">
                <node concept="cd27G" id="8D" role="lGtFl">
                  <node concept="3u3nmq" id="8E" role="cd27D">
                    <property role="3u3nmv" value="3185435802458734789" />
                  </node>
                </node>
              </node>
              <node concept="cd27G" id="8w" role="lGtFl">
                <node concept="3u3nmq" id="8F" role="cd27D">
                  <property role="3u3nmv" value="3185435802458733865" />
                </node>
              </node>
            </node>
            <node concept="cd27G" id="8f" role="lGtFl">
              <node concept="3u3nmq" id="8G" role="cd27D">
                <property role="3u3nmv" value="3185435802458734993" />
              </node>
            </node>
          </node>
          <node concept="cd27G" id="6e" role="lGtFl">
            <node concept="3u3nmq" id="8H" role="cd27D">
              <property role="3u3nmv" value="3185435802458731029" />
            </node>
          </node>
        </node>
        <node concept="cd27G" id="6b" role="lGtFl">
          <node concept="3u3nmq" id="8I" role="cd27D">
            <property role="3u3nmv" value="3185435802458718250" />
          </node>
        </node>
      </node>
      <node concept="3Tm1VV" id="5R" role="1B3o_S">
        <node concept="cd27G" id="8J" role="lGtFl">
          <node concept="3u3nmq" id="8K" role="cd27D">
            <property role="3u3nmv" value="3185435802458718249" />
          </node>
        </node>
      </node>
      <node concept="cd27G" id="5S" role="lGtFl">
        <node concept="3u3nmq" id="8L" role="cd27D">
          <property role="3u3nmv" value="3185435802458718249" />
        </node>
      </node>
    </node>
    <node concept="3clFb_" id="5w" role="jymVt">
      <property role="TrG5h" value="getApplicableConcept" />
      <node concept="3bZ5Sz" id="8M" role="3clF45">
        <node concept="cd27G" id="8Q" role="lGtFl">
          <node concept="3u3nmq" id="8R" role="cd27D">
            <property role="3u3nmv" value="3185435802458718249" />
          </node>
        </node>
      </node>
      <node concept="3clFbS" id="8N" role="3clF47">
        <node concept="3cpWs6" id="8S" role="3cqZAp">
          <node concept="35c_gC" id="8U" role="3cqZAk">
            <ref role="35c_gD" to="lziw:4$zH10lxCIn" resolve="Role" />
            <node concept="cd27G" id="8W" role="lGtFl">
              <node concept="3u3nmq" id="8X" role="cd27D">
                <property role="3u3nmv" value="3185435802458718249" />
              </node>
            </node>
          </node>
          <node concept="cd27G" id="8V" role="lGtFl">
            <node concept="3u3nmq" id="8Y" role="cd27D">
              <property role="3u3nmv" value="3185435802458718249" />
            </node>
          </node>
        </node>
        <node concept="cd27G" id="8T" role="lGtFl">
          <node concept="3u3nmq" id="8Z" role="cd27D">
            <property role="3u3nmv" value="3185435802458718249" />
          </node>
        </node>
      </node>
      <node concept="3Tm1VV" id="8O" role="1B3o_S">
        <node concept="cd27G" id="90" role="lGtFl">
          <node concept="3u3nmq" id="91" role="cd27D">
            <property role="3u3nmv" value="3185435802458718249" />
          </node>
        </node>
      </node>
      <node concept="cd27G" id="8P" role="lGtFl">
        <node concept="3u3nmq" id="92" role="cd27D">
          <property role="3u3nmv" value="3185435802458718249" />
        </node>
      </node>
    </node>
    <node concept="3clFb_" id="5x" role="jymVt">
      <property role="TrG5h" value="isApplicableAndPattern" />
      <node concept="37vLTG" id="93" role="3clF46">
        <property role="TrG5h" value="argument" />
        <node concept="3Tqbb2" id="98" role="1tU5fm">
          <node concept="cd27G" id="9a" role="lGtFl">
            <node concept="3u3nmq" id="9b" role="cd27D">
              <property role="3u3nmv" value="3185435802458718249" />
            </node>
          </node>
        </node>
        <node concept="cd27G" id="99" role="lGtFl">
          <node concept="3u3nmq" id="9c" role="cd27D">
            <property role="3u3nmv" value="3185435802458718249" />
          </node>
        </node>
      </node>
      <node concept="3clFbS" id="94" role="3clF47">
        <node concept="9aQIb" id="9d" role="3cqZAp">
          <node concept="3clFbS" id="9f" role="9aQI4">
            <node concept="3cpWs6" id="9h" role="3cqZAp">
              <node concept="2ShNRf" id="9j" role="3cqZAk">
                <node concept="1pGfFk" id="9l" role="2ShVmc">
                  <ref role="37wK5l" to="qurh:~IsApplicableStatus.&lt;init&gt;(boolean,jetbrains.mps.lang.pattern.GeneratedMatchingPattern)" resolve="IsApplicableStatus" />
                  <node concept="2OqwBi" id="9n" role="37wK5m">
                    <node concept="2OqwBi" id="9q" role="2Oq$k0">
                      <node concept="liA8E" id="9t" role="2OqNvi">
                        <ref role="37wK5l" to="mhbf:~SNode.getConcept()" resolve="getConcept" />
                        <node concept="cd27G" id="9w" role="lGtFl">
                          <node concept="3u3nmq" id="9x" role="cd27D">
                            <property role="3u3nmv" value="3185435802458718249" />
                          </node>
                        </node>
                      </node>
                      <node concept="2JrnkZ" id="9u" role="2Oq$k0">
                        <node concept="37vLTw" id="9y" role="2JrQYb">
                          <ref role="3cqZAo" node="93" resolve="argument" />
                          <node concept="cd27G" id="9$" role="lGtFl">
                            <node concept="3u3nmq" id="9_" role="cd27D">
                              <property role="3u3nmv" value="3185435802458718249" />
                            </node>
                          </node>
                        </node>
                        <node concept="cd27G" id="9z" role="lGtFl">
                          <node concept="3u3nmq" id="9A" role="cd27D">
                            <property role="3u3nmv" value="3185435802458718249" />
                          </node>
                        </node>
                      </node>
                      <node concept="cd27G" id="9v" role="lGtFl">
                        <node concept="3u3nmq" id="9B" role="cd27D">
                          <property role="3u3nmv" value="3185435802458718249" />
                        </node>
                      </node>
                    </node>
                    <node concept="liA8E" id="9r" role="2OqNvi">
                      <ref role="37wK5l" to="c17a:~SAbstractConcept.isSubConceptOf(org.jetbrains.mps.openapi.language.SAbstractConcept)" resolve="isSubConceptOf" />
                      <node concept="1rXfSq" id="9C" role="37wK5m">
                        <ref role="37wK5l" node="5w" resolve="getApplicableConcept" />
                        <node concept="cd27G" id="9E" role="lGtFl">
                          <node concept="3u3nmq" id="9F" role="cd27D">
                            <property role="3u3nmv" value="3185435802458718249" />
                          </node>
                        </node>
                      </node>
                      <node concept="cd27G" id="9D" role="lGtFl">
                        <node concept="3u3nmq" id="9G" role="cd27D">
                          <property role="3u3nmv" value="3185435802458718249" />
                        </node>
                      </node>
                    </node>
                    <node concept="cd27G" id="9s" role="lGtFl">
                      <node concept="3u3nmq" id="9H" role="cd27D">
                        <property role="3u3nmv" value="3185435802458718249" />
                      </node>
                    </node>
                  </node>
                  <node concept="10Nm6u" id="9o" role="37wK5m">
                    <node concept="cd27G" id="9I" role="lGtFl">
                      <node concept="3u3nmq" id="9J" role="cd27D">
                        <property role="3u3nmv" value="3185435802458718249" />
                      </node>
                    </node>
                  </node>
                  <node concept="cd27G" id="9p" role="lGtFl">
                    <node concept="3u3nmq" id="9K" role="cd27D">
                      <property role="3u3nmv" value="3185435802458718249" />
                    </node>
                  </node>
                </node>
                <node concept="cd27G" id="9m" role="lGtFl">
                  <node concept="3u3nmq" id="9L" role="cd27D">
                    <property role="3u3nmv" value="3185435802458718249" />
                  </node>
                </node>
              </node>
              <node concept="cd27G" id="9k" role="lGtFl">
                <node concept="3u3nmq" id="9M" role="cd27D">
                  <property role="3u3nmv" value="3185435802458718249" />
                </node>
              </node>
            </node>
            <node concept="cd27G" id="9i" role="lGtFl">
              <node concept="3u3nmq" id="9N" role="cd27D">
                <property role="3u3nmv" value="3185435802458718249" />
              </node>
            </node>
          </node>
          <node concept="cd27G" id="9g" role="lGtFl">
            <node concept="3u3nmq" id="9O" role="cd27D">
              <property role="3u3nmv" value="3185435802458718249" />
            </node>
          </node>
        </node>
        <node concept="cd27G" id="9e" role="lGtFl">
          <node concept="3u3nmq" id="9P" role="cd27D">
            <property role="3u3nmv" value="3185435802458718249" />
          </node>
        </node>
      </node>
      <node concept="3uibUv" id="95" role="3clF45">
        <ref role="3uigEE" to="qurh:~IsApplicableStatus" resolve="IsApplicableStatus" />
        <node concept="cd27G" id="9Q" role="lGtFl">
          <node concept="3u3nmq" id="9R" role="cd27D">
            <property role="3u3nmv" value="3185435802458718249" />
          </node>
        </node>
      </node>
      <node concept="3Tm1VV" id="96" role="1B3o_S">
        <node concept="cd27G" id="9S" role="lGtFl">
          <node concept="3u3nmq" id="9T" role="cd27D">
            <property role="3u3nmv" value="3185435802458718249" />
          </node>
        </node>
      </node>
      <node concept="cd27G" id="97" role="lGtFl">
        <node concept="3u3nmq" id="9U" role="cd27D">
          <property role="3u3nmv" value="3185435802458718249" />
        </node>
      </node>
    </node>
    <node concept="3clFb_" id="5y" role="jymVt">
      <property role="TrG5h" value="overrides" />
      <node concept="3clFbS" id="9V" role="3clF47">
        <node concept="3cpWs6" id="9Z" role="3cqZAp">
          <node concept="3clFbT" id="a1" role="3cqZAk">
            <node concept="cd27G" id="a3" role="lGtFl">
              <node concept="3u3nmq" id="a4" role="cd27D">
                <property role="3u3nmv" value="3185435802458718249" />
              </node>
            </node>
          </node>
          <node concept="cd27G" id="a2" role="lGtFl">
            <node concept="3u3nmq" id="a5" role="cd27D">
              <property role="3u3nmv" value="3185435802458718249" />
            </node>
          </node>
        </node>
        <node concept="cd27G" id="a0" role="lGtFl">
          <node concept="3u3nmq" id="a6" role="cd27D">
            <property role="3u3nmv" value="3185435802458718249" />
          </node>
        </node>
      </node>
      <node concept="10P_77" id="9W" role="3clF45">
        <node concept="cd27G" id="a7" role="lGtFl">
          <node concept="3u3nmq" id="a8" role="cd27D">
            <property role="3u3nmv" value="3185435802458718249" />
          </node>
        </node>
      </node>
      <node concept="3Tm1VV" id="9X" role="1B3o_S">
        <node concept="cd27G" id="a9" role="lGtFl">
          <node concept="3u3nmq" id="aa" role="cd27D">
            <property role="3u3nmv" value="3185435802458718249" />
          </node>
        </node>
      </node>
      <node concept="cd27G" id="9Y" role="lGtFl">
        <node concept="3u3nmq" id="ab" role="cd27D">
          <property role="3u3nmv" value="3185435802458718249" />
        </node>
      </node>
    </node>
    <node concept="3uibUv" id="5z" role="EKbjA">
      <ref role="3uigEE" to="qurh:~NonTypesystemRule_Runtime" resolve="NonTypesystemRule_Runtime" />
      <node concept="cd27G" id="ac" role="lGtFl">
        <node concept="3u3nmq" id="ad" role="cd27D">
          <property role="3u3nmv" value="3185435802458718249" />
        </node>
      </node>
    </node>
    <node concept="3uibUv" id="5$" role="1zkMxy">
      <ref role="3uigEE" to="qurh:~AbstractNonTypesystemRule_Runtime" resolve="AbstractNonTypesystemRule_Runtime" />
      <node concept="cd27G" id="ae" role="lGtFl">
        <node concept="3u3nmq" id="af" role="cd27D">
          <property role="3u3nmv" value="3185435802458718249" />
        </node>
      </node>
    </node>
    <node concept="3Tm1VV" id="5_" role="1B3o_S">
      <node concept="cd27G" id="ag" role="lGtFl">
        <node concept="3u3nmq" id="ah" role="cd27D">
          <property role="3u3nmv" value="3185435802458718249" />
        </node>
      </node>
    </node>
    <node concept="cd27G" id="5A" role="lGtFl">
      <node concept="3u3nmq" id="ai" role="cd27D">
        <property role="3u3nmv" value="3185435802458718249" />
      </node>
    </node>
  </node>
</model>

