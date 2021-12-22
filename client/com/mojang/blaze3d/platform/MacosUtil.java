package com.mojang.blaze3d.platform;

import ca.weblite.objc.NSObject;
import com.sun.jna.Pointer;
import java.util.Optional;
import org.lwjgl.glfw.GLFWNativeCocoa;

public class MacosUtil {
   private static final int NS_FULL_SCREEN_WINDOW_MASK = 16384;

   public MacosUtil() {
      super();
   }

   public static void toggleFullscreen(long var0) {
      getNsWindow(var0).filter(MacosUtil::isInKioskMode).ifPresent(MacosUtil::toggleFullscreen);
   }

   private static Optional<NSObject> getNsWindow(long var0) {
      long var2 = GLFWNativeCocoa.glfwGetCocoaWindow(var0);
      return var2 != 0L ? Optional.of(new NSObject(new Pointer(var2))) : Optional.empty();
   }

   private static boolean isInKioskMode(NSObject var0) {
      return ((Long)var0.sendRaw("styleMask", new Object[0]) & 16384L) == 16384L;
   }

   private static void toggleFullscreen(NSObject var0) {
      var0.send("toggleFullScreen:", new Object[0]);
   }
}
