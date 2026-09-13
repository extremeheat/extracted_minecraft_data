package com.mojang.blaze3d.platform;

import ca.weblite.objc.Client;
import ca.weblite.objc.Proxy;
import java.util.Set;
import net.minecraft.util.Util;
import org.lwjgl.sdl.SDLHints;
import org.lwjgl.system.macosx.ObjCRuntime;

public final class MacosUtil {
   public static final boolean IS_MACOS;

   private MacosUtil() {
      super();
   }

   public static void disableCloseWindowMenuItem() {
      if (IS_MACOS) {
         Proxy windowsMenu = Client.getInstance().sendProxy("NSApplication", "sharedApplication", new Object[0]).sendProxy("windowsMenu", new Object[0]);
         int itemCount = windowsMenu.sendInt("numberOfItems", new Object[0]);

         for(int i = 0; i < itemCount; ++i) {
            Proxy item = windowsMenu.sendProxy("itemAtIndex:", new Object[]{i});
            if (MacosUtil.DisabledActions.SELECTORS.contains((Long)item.sendRaw("action", new Object[0]))) {
               item.send("setEnabled:", new Object[]{false});
               item.send("setHidden:", new Object[]{true});
               item.send("setKeyEquivalent:", new Object[]{""});
            }
         }

      }
   }

   public static void setFullscreenMenuVisibility(final boolean value) {
      if (IS_MACOS) {
         SDLHints.SDL_SetHint("SDL_VIDEO_MAC_FULLSCREEN_MENU_VISIBILITY", value ? "1" : "0");
      }
   }

   public static void setCtrlClickEmulatesRightClick(final boolean value) {
      if (IS_MACOS) {
         SDLHints.SDL_SetHint("SDL_MAC_CTRL_CLICK_EMULATE_RIGHT_CLICK", value ? "1" : "0");
      }
   }

   static {
      IS_MACOS = Util.getPlatform() == Util.OS.OSX;
   }

   private static final class DisabledActions {
      private static final Set<Long> SELECTORS = Set.of(ObjCRuntime.sel_getUid("performClose:"), ObjCRuntime.sel_getUid("closeAll:"));

      private DisabledActions() {
         super();
      }
   }
}
