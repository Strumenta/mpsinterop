package com.strumenta.mpsserver.ide.plugin;

/*Generated by MPS */

import jetbrains.mps.plugins.actions.GeneratedActionGroup;
import org.jetbrains.annotations.Nullable;
import jetbrains.mps.workbench.action.ApplicationPlugin;

public class MPSServer_ActionGroup extends GeneratedActionGroup {
  public static final String ID = "com.strumenta.mpsserver.ide.plugin.MPSServer_ActionGroup";

  public MPSServer_ActionGroup(@Nullable ApplicationPlugin plugin) {
    super("MPSServer", ID, plugin);
    setIsInternal(false);
    setPopup(false);
    MPSServer_ActionGroup.this.addSeparator();
    MPSServer_ActionGroup.this.addAction("com.strumenta.mpsserver.ide.plugin.ShowServerLauncherTool_Action");
    MPSServer_ActionGroup.this.addSeparator();
  }
}