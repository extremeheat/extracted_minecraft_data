package com.mojang.blaze3d.vulkan;

import com.mojang.blaze3d.systems.BackendCreationException;
import org.joml.Vector4fc;
import org.lwjgl.vulkan.VkClearColorValue;

public class VulkanUtils {
   public VulkanUtils() {
      super();
   }

   public static void throwIfFailure(final int result, final String message, final BackendCreationException.Reason reason) throws BackendCreationException {
      if (result < 0) {
         throw new BackendCreationException(resultToString(result) + ": " + message, reason);
      }
   }

   public static void crashIfFailure(final int result, final String message) {
      if (result < 0) {
         String var10002 = resultToString(result);
         throw new IllegalStateException(var10002 + ": " + message);
      }
   }

   public static String resultToString(final int error) {
      String var10000;
      switch (error) {
         case -1000257000 -> var10000 = "VK_ERROR_INVALID_OPAQUE_CAPTURE_ADDRESS";
         case -1000161000 -> var10000 = "VK_ERROR_FRAGMENTATION";
         case -1000072003 -> var10000 = "VK_ERROR_INVALID_EXTERNAL_HANDLE";
         case -1000069000 -> var10000 = "VK_ERROR_OUT_OF_POOL_MEMORY";
         case -1000001004 -> var10000 = "VK_ERROR_OUT_OF_DATE_KHR";
         case -1000000001 -> var10000 = "VK_ERROR_NATIVE_WINDOW_IN_USE_KHR";
         case -1000000000 -> var10000 = "VK_ERROR_SURFACE_LOST_KHR";
         case -13 -> var10000 = "VK_ERROR_UNKNOWN";
         case -12 -> var10000 = "VK_ERROR_FRAGMENTED_POOL";
         case -11 -> var10000 = "VK_ERROR_FORMAT_NOT_SUPPORTED";
         case -10 -> var10000 = "VK_ERROR_TOO_MANY_OBJECTS";
         case -9 -> var10000 = "VK_ERROR_INCOMPATIBLE_DRIVER";
         case -8 -> var10000 = "VK_ERROR_FEATURE_NOT_PRESENT";
         case -7 -> var10000 = "VK_ERROR_EXTENSION_NOT_PRESENT";
         case -6 -> var10000 = "VK_ERROR_LAYER_NOT_PRESENT";
         case -5 -> var10000 = "VK_ERROR_MEMORY_MAP_FAILED";
         case -4 -> var10000 = "VK_ERROR_DEVICE_LOST";
         case -3 -> var10000 = "VK_ERROR_INITIALIZATION_FAILED";
         case -2 -> var10000 = "VK_ERROR_OUT_OF_DEVICE_MEMORY";
         case -1 -> var10000 = "VK_ERROR_OUT_OF_HOST_MEMORY";
         case 0 -> var10000 = "VK_SUCCESS";
         case 1 -> var10000 = "VK_NOT_READY";
         case 2 -> var10000 = "VK_TIMEOUT";
         case 3 -> var10000 = "VK_EVENT_SET";
         case 4 -> var10000 = "VK_EVENT_RESET";
         case 5 -> var10000 = "VK_INCOMPLETE";
         case 1000001003 -> var10000 = "VK_SUBOPTIMAL_KHR";
         default -> var10000 = "0x" + Integer.toHexString(error);
      }

      return var10000;
   }

   public static boolean hasAllBits(final int bitfield, final int bitmask) {
      return (bitfield & bitmask) == bitmask;
   }

   public static boolean hasAllBits(final long bitfield, final long bitmask) {
      return (bitfield & bitmask) == bitmask;
   }

   public static boolean hasAnyBit(final int bitfield, final int bitmask) {
      return (bitfield & bitmask) != 0;
   }

   public static boolean hasAnyBit(final long bitfield, final long bitmask) {
      return (bitfield & bitmask) != 0L;
   }

   public static boolean hasNoBit(final int bitfield, final int bitmask) {
      return (bitfield & bitmask) == 0;
   }

   public static boolean hasNoBit(final long bitfield, final long bitmask) {
      return (bitfield & bitmask) == 0L;
   }

   public static VkClearColorValue putArgb(final VkClearColorValue vkClearColor, final Vector4fc argb) {
      vkClearColor.float32(0, argb.x());
      vkClearColor.float32(1, argb.y());
      vkClearColor.float32(2, argb.z());
      vkClearColor.float32(3, argb.w());
      return vkClearColor;
   }
}
