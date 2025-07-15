package com.mojang.blaze3d;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.systems.GpuDevice;
import java.lang.ref.WeakReference;
import java.util.Locale;
import javax.annotation.Nullable;

public class GraphicsWorkarounds {
   @Nullable
   private static GraphicsWorkarounds instance;
   private final WeakReference<GpuDevice> gpuDevice;
   private final boolean alwaysCreateFreshImmediateBuffer;

   private GraphicsWorkarounds(GpuDevice var1) {
      super();
      this.gpuDevice = new WeakReference(var1);
      this.alwaysCreateFreshImmediateBuffer = isIntelGen11(var1);
   }

   public static GraphicsWorkarounds get(GpuDevice var0) {
      GraphicsWorkarounds var1 = instance;
      if (var1 == null || var1.gpuDevice.get() != var0) {
         instance = var1 = new GraphicsWorkarounds(var0);
      }

      return var1;
   }

   public boolean alwaysCreateFreshImmediateBuffer() {
      return this.alwaysCreateFreshImmediateBuffer;
   }

   private static boolean isIntelGen11(GpuDevice var0) {
      String var1 = GLX._getCpuInfo().toLowerCase(Locale.ROOT);
      if (!var1.contains("intel")) {
         return false;
      } else {
         boolean var10000;
         switch (var0.getRenderer()) {
            case "Intel(R) HD Graphics Gen11" -> var10000 = true;
            case "Intel(R) UHD Graphics" -> var10000 = var1.contains("i3-1005g1") || var1.contains("i5-1035g1") || var1.contains("i3-l13g4") || var1.contains("i5-l16g7") || var1.contains("atom") && var1.contains("x6413e") || var1.contains("celeron") && (var1.contains("n6210") || var1.contains("j6412")) || var1.contains("atom") && (var1.contains("x6425re") || var1.contains("x6425e")) || var1.contains("celeron") && (var1.contains("n4500") || var1.contains("n4505") || var1.contains("n5095")) || var1.contains("celeron") && (var1.contains("n5100") || var1.contains("n5105")) || var1.contains("pentium(r) silver") && (var1.contains("n6000") || var1.contains("n6005"));
            case "Intel(R) Iris(R) Plus Graphics" -> var10000 = var1.contains("i5-1035g4") || var1.contains("i5-1035g7") || var1.contains("i5-1038ng7") || var1.contains("i7-1065g7");
            default -> var10000 = false;
         }

         return var10000;
      }
   }
}
