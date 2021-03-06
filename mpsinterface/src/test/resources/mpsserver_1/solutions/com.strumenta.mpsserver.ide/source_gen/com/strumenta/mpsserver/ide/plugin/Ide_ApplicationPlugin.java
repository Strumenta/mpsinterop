package com.strumenta.mpsserver.ide.plugin;

/*Generated by MPS */

import jetbrains.mps.plugins.applicationplugins.BaseApplicationPlugin;
import com.intellij.openapi.extensions.PluginId;
import org.jetbrains.annotations.NotNull;
import jetbrains.mps.ide.actions.Tools_ActionGroup;

public class Ide_ApplicationPlugin extends BaseApplicationPlugin {
  private final PluginId myId = PluginId.getId("com.strumenta.mpsserver.ide");

  public Ide_ApplicationPlugin() {
  }

  @NotNull
  public PluginId getId() {
    return myId;
  }

  public void createGroups() {
    // actions w/o parameters 
    addAction(new ShowServerLauncherTool_Action());
    // groups 
    addGroup(new MPSServer_ActionGroup(this));
  }
  public void adjustRegularGroups() {
    insertGroupIntoAnother(MPSServer_ActionGroup.ID, Tools_ActionGroup.ID, Tools_ActionGroup.LABEL_ID_customTools);
  }
}
