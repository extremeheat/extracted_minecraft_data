package com.mojang.renderpearl.backend.opengl;

import com.mojang.logging.LogUtils;
import com.mojang.renderpearl.api.device.DeviceFeatures;
import com.mojang.renderpearl.api.device.DeviceInfo;
import com.mojang.renderpearl.api.device.DeviceLimits;
import com.mojang.renderpearl.api.device.DeviceType;
import com.mojang.renderpearl.api.device.HintsAndWorkarounds;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.minecraft.util.Util;
import org.lwjgl.opengl.GL33C;
import org.lwjgl.opengl.GLCapabilities;
import org.slf4j.Logger;

public class GlHeuristics {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final List<String> DEVICE_NAMES_THAT_IMPLY_CPU = List.of("mesa offscreen", "llvmpipe");
   private static final List<String> DEVICE_NAMES_THAT_IMPLY_VIRTUAL = List.of("virtgl");
   private final boolean isGlOnDx12;
   private final boolean isAmd;
   private final boolean isNvidia;
   private final boolean couldBeIntelGen7;

   GlHeuristics(final String deviceName, final String vendor) {
      super();
      this.isGlOnDx12 = isGlOnDx12(deviceName);
      this.isAmd = isAmd(deviceName);
      this.isNvidia = isNvidia(deviceName);
      this.couldBeIntelGen7 = couldBeIntelGen7(deviceName.toLowerCase(Locale.ROOT), vendor.toLowerCase(Locale.ROOT));
   }

   public boolean isGlOnDx12() {
      return this.isGlOnDx12;
   }

   public boolean isAmd() {
      return this.isAmd;
   }

   public boolean isNvidia() {
      return this.isNvidia;
   }

   public boolean couldBeIntelGen7() {
      return this.couldBeIntelGen7;
   }

   private static boolean isGlOnDx12(final String deviceName) {
      boolean isWindowsArm64 = Util.getPlatform() == Util.OS.WINDOWS && Util.isAarch64();
      return isWindowsArm64 || deviceName.startsWith("D3D12");
   }

   private static boolean isAmd(final String deviceName) {
      return deviceName.contains("AMD");
   }

   private static boolean isNvidia(final String deviceName) {
      return deviceName.toLowerCase(Locale.ROOT).contains("nvidia");
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
      String renderer = GlStateManager._getString(7937);
      String vendor = GlStateManager._getString(7936);
      String rendererLowerCase = renderer.toLowerCase(Locale.ROOT);
      String vendorLowerCase = vendor.toLowerCase(Locale.ROOT);
      int drawIndirectCount = enabledExtensions.contains("GL_ARB_multi_draw_indirect") ? 2147483647 : (enabledExtensions.contains("GL_ARB_draw_indirect") ? 1 : 0);
      return new DeviceInfo(renderer, vendor, GlStateManager._getString(7938), capabilities.GL_ARB_clip_control, "OpenGL", 1.0F, new DeviceLimits(maxSupportedAnisotropy, GL33C.glGetInteger(35380), getMaxSupportedTextureSize(), 9223372036854775807L, 0, GL33C.glGetInteger(34852), drawIndirectCount), new DeviceFeatures(true, enabledExtensions.contains("GL_ARB_shader_draw_parameters"), false, true, enabledExtensions.contains("GL_ARB_multi_draw_indirect"), enabledExtensions.contains("GL_ARB_draw_indirect"), enabledExtensions.contains("GL_ARB_base_instance"), enabledExtensions.contains("GL_ARB_buffer_storage")), Collections.unmodifiableSet(enabledExtensions), new HintsAndWorkarounds(this.isGlOnDx12(), this.isAmd(), Util.isAppleSiliconMac(renderer), vendorLowerCase.contains("intel") && !rendererLowerCase.contains("arc")), this.guessDeviceType(rendererLowerCase, vendorLowerCase));
   }

   private static boolean couldBeIntelGen7(final String renderer, final String vendor) {
      if (!vendor.contains("intel")) {
         return false;
      } else if (renderer.contains("2500")) {
         return true;
      } else if (renderer.contains("4000")) {
         return true;
      } else if (renderer.contains("hd graphics (byt)")) {
         return true;
      } else {
         return renderer.endsWith("hd graphics");
      }
   }

   private DeviceType guessDeviceType(final String renderer, final String vendor) {
      if (vendor.contains("intel")) {
         return renderer.contains("arc") ? DeviceType.DISCRETE : DeviceType.INTEGRATED;
      } else {
         for(String string : DEVICE_NAMES_THAT_IMPLY_CPU) {
            if (renderer.contains(string)) {
               return DeviceType.CPU;
            }
         }

         for(String string : DEVICE_NAMES_THAT_IMPLY_VIRTUAL) {
            if (renderer.contains(string)) {
               return DeviceType.VIRTUAL;
            }
         }

         return DeviceType.OTHER;
      }
   }
}
