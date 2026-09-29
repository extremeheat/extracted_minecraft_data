package com.mojang.renderpearl.util;

import org.lwjgl.system.Platform;
import org.lwjgl.system.Platform.Architecture;

public class PlatformUtil {
   public static final boolean IS_WINDOWS;
   public static final boolean IS_MACOS;
   public static final boolean IS_AARCH64;

   public PlatformUtil() {
      super();
   }

   public static boolean isAppleSiliconMac(final String renderer) {
      return renderer.startsWith("Apple");
   }

   static {
      IS_WINDOWS = Platform.get() == Platform.WINDOWS;
      IS_MACOS = Platform.get() == Platform.MACOSX;
      IS_AARCH64 = Platform.getArchitecture() == Architecture.ARM64;
   }
}
