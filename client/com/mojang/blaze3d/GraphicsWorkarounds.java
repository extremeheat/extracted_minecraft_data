package com.mojang.blaze3d;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.systems.GpuDevice;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

public class GraphicsWorkarounds {
   private static final List<String> INTEL_GEN11_CORE = List.of("i3-1000g1", "i3-1000g4", "i3-1000ng4", "i3-1005g1", "i3-l13g4", "i5-1030g4", "i5-1030g7", "i5-1030ng7", "i5-1034g1", "i5-1035g1", "i5-1035g4", "i5-1035g7", "i5-1038ng7", "i5-l16g7", "i7-1060g7", "i7-1060ng7", "i7-1065g7", "i7-1068g7", "i7-1068ng7");
   private static final List<String> INTEL_GEN11_ATOM = List.of("x6211e", "x6212re", "x6214re", "x6413e", "x6414re", "x6416re", "x6425e", "x6425re", "x6427fe");
   private static final List<String> INTEL_GEN11_CELERON = List.of("j6412", "j6413", "n4500", "n4505", "n5095", "n5095a", "n5100", "n5105", "n6210", "n6211");
   private static final List<String> INTEL_GEN11_PENTIUM = List.of("6805", "j6426", "n6415", "n6000", "n6005");
   private static @Nullable GraphicsWorkarounds instance;
   private final WeakReference<GpuDevice> gpuDevice;
   private final boolean alwaysCreateFreshImmediateBuffer;
   private final boolean isGlOnDx12;
   private final boolean isAmd;

   private GraphicsWorkarounds(final GpuDevice gpuDevice) {
      super();
      this.gpuDevice = new WeakReference(gpuDevice);
      this.alwaysCreateFreshImmediateBuffer = isIntelGen11(gpuDevice);
      this.isGlOnDx12 = isGlOnDx12(gpuDevice);
      this.isAmd = isAmd(gpuDevice);
   }

   public static GraphicsWorkarounds get(final GpuDevice gpuDevice) {
      GraphicsWorkarounds instance = GraphicsWorkarounds.instance;
      if (instance == null || instance.gpuDevice.get() != gpuDevice) {
         GraphicsWorkarounds.instance = instance = new GraphicsWorkarounds(gpuDevice);
      }

      return instance;
   }

   public boolean alwaysCreateFreshImmediateBuffer() {
      return this.alwaysCreateFreshImmediateBuffer;
   }

   public boolean isGlOnDx12() {
      return this.isGlOnDx12;
   }

   public boolean isAmd() {
      return this.isAmd;
   }

   private static boolean isIntelGen11(final GpuDevice gpuDevice) {
      String cpuInfo = GLX._getCpuInfo().toLowerCase(Locale.ROOT);
      String renderer = gpuDevice.getDeviceInfo().name().toLowerCase(Locale.ROOT);
      if (cpuInfo.contains("intel") && renderer.contains("intel") && !renderer.contains("mesa")) {
         if (renderer.endsWith("gen11")) {
            return true;
         } else if (!renderer.contains("uhd graphics") && !renderer.contains("iris")) {
            return false;
         } else {
            boolean var6;
            label49: {
               if (cpuInfo.contains("atom")) {
                  Stream var10000 = INTEL_GEN11_ATOM.stream();
                  Objects.requireNonNull(cpuInfo);
                  if (var10000.anyMatch(cpuInfo::contains)) {
                     break label49;
                  }
               }

               if (cpuInfo.contains("celeron")) {
                  Stream var3 = INTEL_GEN11_CELERON.stream();
                  Objects.requireNonNull(cpuInfo);
                  if (var3.anyMatch(cpuInfo::contains)) {
                     break label49;
                  }
               }

               if (cpuInfo.contains("pentium")) {
                  Stream var4 = INTEL_GEN11_PENTIUM.stream();
                  Objects.requireNonNull(cpuInfo);
                  if (var4.anyMatch(cpuInfo::contains)) {
                     break label49;
                  }
               }

               Stream var5 = INTEL_GEN11_CORE.stream();
               Objects.requireNonNull(cpuInfo);
               if (!var5.anyMatch(cpuInfo::contains)) {
                  var6 = false;
                  return var6;
               }
            }

            var6 = true;
            return var6;
         }
      } else {
         return false;
      }
   }

   private static boolean isGlOnDx12(final GpuDevice gpuDevice) {
      boolean isWindowsArm64 = Util.getPlatform() == Util.OS.WINDOWS && Util.isAarch64();
      return isWindowsArm64 || gpuDevice.getDeviceInfo().name().startsWith("D3D12");
   }

   private static boolean isAmd(final GpuDevice gpuDevice) {
      return gpuDevice.getDeviceInfo().name().contains("AMD");
   }
}
