package com.mojang.blaze3d.platform;

import ca.weblite.objc.Client;
import ca.weblite.objc.NSObject;
import com.sun.jna.Pointer;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
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
      return (var0.sendRaw("styleMask", new Object[0]) & 16384L) == 16384L;
   }

   private static void toggleFullscreen(NSObject var0) {
      var0.send("toggleFullScreen:", new Object[]{Pointer.NULL});
   }

   public static void loadIcon(InputStream var0) throws IOException {
      String var1 = Base64.getEncoder().encodeToString(var0.readAllBytes());
      Client var2 = Client.getInstance();
      Object var3 = var2.sendProxy("NSData", "alloc", new Object[0]).send("initWithBase64Encoding:", new Object[]{var1});
      Object var4 = var2.sendProxy("NSImage", "alloc", new Object[0]).send("initWithData:", new Object[]{var3});
      var2.sendProxy("NSApplication", "sharedApplication", new Object[0]).send("setApplicationIconImage:", new Object[]{var4});
   }
}
