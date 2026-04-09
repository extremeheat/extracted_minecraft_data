package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.systems.DeviceInfo;
import com.mojang.blaze3d.systems.DeviceLimits;
import com.mojang.blaze3d.systems.HintsAndWorkarounds;
import com.mojang.logging.LogUtils;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.util.Util;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLCapabilities;
import org.slf4j.Logger;

class GlHeuristics {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final List<String> INTEL_GEN11_CORE = List.of("i3-1000g1", "i3-1000g4", "i3-1000ng4", "i3-1005g1", "i3-l13g4", "i5-1030g4", "i5-1030g7", "i5-1030ng7", "i5-1034g1", "i5-1035g1", "i5-1035g4", "i5-1035g7", "i5-1038ng7", "i5-l16g7", "i7-1060g7", "i7-1060ng7", "i7-1065g7", "i7-1068g7", "i7-1068ng7");
   private static final List<String> INTEL_GEN11_ATOM = List.of("x6211e", "x6212re", "x6214re", "x6413e", "x6414re", "x6416re", "x6425e", "x6425re", "x6427fe");
   private static final List<String> INTEL_GEN11_CELERON = List.of("j6412", "j6413", "n4500", "n4505", "n5095", "n5095a", "n5100", "n5105", "n6210", "n6211");
   private static final List<String> INTEL_GEN11_PENTIUM = List.of("6805", "j6426", "n6415", "n6000", "n6005");
   private final boolean alwaysCreateFreshImmediateBuffer;
   private final boolean isGlOnDx12;
   private final boolean isAmd;

   GlHeuristics(final String deviceName) {
      super();
      this.alwaysCreateFreshImmediateBuffer = isIntelGen11(deviceName);
      this.isGlOnDx12 = isGlOnDx12(deviceName);
      this.isAmd = isAmd(deviceName);
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

   private static boolean isIntelGen11(final String deviceName) {
      String cpuInfo = GLX._getCpuInfo().toLowerCase(Locale.ROOT);
      String renderer = deviceName.toLowerCase(Locale.ROOT);
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

   private static boolean isGlOnDx12(final String deviceName) {
      boolean isWindowsArm64 = Util.getPlatform() == Util.OS.WINDOWS && Util.isAarch64();
      return isWindowsArm64 || deviceName.startsWith("D3D12");
   }

   private static boolean isAmd(final String deviceName) {
      return deviceName.contains("AMD");
   }

   private static int getMaxSupportedTextureSize() {
      int maxReported = GlStateManager._getInteger(3379);

      for(int texSize = Math.max(32768, maxReported); texSize >= 1024; texSize >>= 1) {
         GlStateManager._texImage2D(32868, 0, 6408, texSize, texSize, 0, 6408, 5121, (ByteBuffer)null);
         int width = GlStateManager._getTexLevelParameter(32868, 0, 4096);
         if (width != 0) {
            return texSize;
         }
      }

      int maxSupportedTextureSize = Math.max(maxReported, 1024);
      LOGGER.info("Failed to determine maximum texture size by probing, trying GL_MAX_TEXTURE_SIZE = {}", maxSupportedTextureSize);
      return maxSupportedTextureSize;
   }

   public DeviceInfo createDeviceInfo(final GLCapabilities capabilities, final int maxSupportedAnisotropy, final Set<String> enabledExtensions) {
      return new DeviceInfo(GlStateManager._getString(7937), GlStateManager._getString(7936), GlStateManager._getString(7938), capabilities.GL_ARB_clip_control, "OpenGL", 1.0F, new DeviceLimits(maxSupportedAnisotropy, GL11.glGetInteger(35380), getMaxSupportedTextureSize()), Collections.unmodifiableSet(enabledExtensions), new HintsAndWorkarounds(this.alwaysCreateFreshImmediateBuffer(), this.isGlOnDx12(), this.isAmd()));
   }
}
