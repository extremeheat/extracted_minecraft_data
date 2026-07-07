package com.mojang.renderpearl.api.device;

import java.util.List;

public class BackendCreationException extends Exception {
   private final Reason reason;
   private final List<String> missingCapabilities;

   public BackendCreationException(final String message, final Reason reason, final List<String> missingCapabilities) {
      super(message);
      this.reason = reason;
      this.missingCapabilities = List.copyOf(missingCapabilities);
   }

   public BackendCreationException(final String message, final Reason reason) {
      this(message, reason, List.of());
   }

   public Reason getReason() {
      return this.reason;
   }

   public List<String> getMissingCapabilities() {
      return this.missingCapabilities;
   }

   public static enum Reason {
      GLFW_ERROR("glfw_error"),
      VULKAN_LOADER_MISSING("vulkan_loader_missing"),
      VULKAN_INSTANCE_CREATION_FAILED("vulkan_instance_creation_failed"),
      VULKAN_NO_DEVICE("vulkan_no_device"),
      VULKAN_KNOWN_PROBLEMATIC("vulkan_known_problematic"),
      VULKAN_DEVICE_VERSION_TOO_LOW("vulkan_device_version_too_low"),
      VULKAN_NO_GRAPHICS_QUEUE("vulkan_no_graphics_queue"),
      VULKAN_MISSING_EXTENSION("vulkan_missing_extension"),
      VULKAN_MISSING_FEATURE("vulkan_missing_feature"),
      OPENGL_MISSING("opengl_missing"),
      OTHER("other");

      private final String displayName;

      private Reason(final String key) {
         this.displayName = key;
      }

      public String displayName() {
         return this.displayName;
      }

      // $FF: synthetic method
      private static Reason[] $values() {
         return new Reason[]{GLFW_ERROR, VULKAN_LOADER_MISSING, VULKAN_INSTANCE_CREATION_FAILED, VULKAN_NO_DEVICE, VULKAN_KNOWN_PROBLEMATIC, VULKAN_DEVICE_VERSION_TOO_LOW, VULKAN_NO_GRAPHICS_QUEUE, VULKAN_MISSING_EXTENSION, VULKAN_MISSING_FEATURE, OPENGL_MISSING, OTHER};
      }
   }
}
