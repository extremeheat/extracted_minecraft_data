package com.mojang.blaze3d.platform;

import ca.weblite.objc.Client;
import ca.weblite.objc.Proxy;
import java.util.Set;
import org.lwjgl.system.macosx.ObjCRuntime;

public final class MacosUtil {
   private static final Set<Long> DISABLED_ACTIONS = Set.of(ObjCRuntime.sel_getUid("performClose:"), ObjCRuntime.sel_getUid("closeAll:"));

   private MacosUtil() {
      super();
   }

   public static void disableCloseWindowMenuItem() {
      Proxy windowsMenu = Client.getInstance().sendProxy("NSApplication", "sharedApplication", new Object[0]).sendProxy("windowsMenu", new Object[0]);
      int itemCount = windowsMenu.sendInt("numberOfItems", new Object[0]);

      for(int i = 0; i < itemCount; ++i) {
         Proxy item = windowsMenu.sendProxy("itemAtIndex:", new Object[]{i});
         if (DISABLED_ACTIONS.contains((Long)item.sendRaw("action", new Object[0]))) {
            item.send("setEnabled:", new Object[]{false});
            item.send("setHidden:", new Object[]{true});
            item.send("setKeyEquivalent:", new Object[]{""});
         }
      }

   }
}
