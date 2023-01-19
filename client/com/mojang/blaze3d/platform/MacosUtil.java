package com.mojang.blaze3d.platform;

import ca.weblite.objc.Client;
import ca.weblite.objc.NSObject;
import com.sun.jna.Pointer;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Optional;
import net.minecraft.server.packs.resources.IoSupplier;
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

   public static void loadIcon(IoSupplier<InputStream> var0) throws IOException {
      try (InputStream var1 = (InputStream)var0.get()) {
         String var2 = Base64.getEncoder().encodeToString(var1.readAllBytes());
         Client var3 = Client.getInstance();
         Object var4 = var3.sendProxy("NSData", "alloc", new Object[0]).send("initWithBase64Encoding:", new Object[]{var2});
         Object var5 = var3.sendProxy("NSImage", "alloc", new Object[0]).send("initWithData:", new Object[]{var4});
         var3.sendProxy("NSApplication", "sharedApplication", new Object[0]).send("setApplicationIconImage:", new Object[]{var5});
      }
   }
}
