package com.mojang.blaze3d.vulkan;

import com.mojang.logging.LogUtils;
import java.lang.StackWalker.Option;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.EXTDebugUtils;
import org.lwjgl.vulkan.VkAllocationCallbacks;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkDebugUtilsLabelEXT;
import org.lwjgl.vulkan.VkDebugUtilsMessengerCallbackDataEXT;
import org.lwjgl.vulkan.VkDebugUtilsMessengerCreateInfoEXT;
import org.lwjgl.vulkan.VkDebugUtilsObjectNameInfoEXT;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkInstanceCreateInfo;
import org.slf4j.Logger;

public interface VulkanDebug {
   static VulkanDebug create(final int verbosity, final boolean wantsDebugLabels, final Set<String> availableExtensions, final Set<String> enabledExtensions) {
      if ((verbosity > 0 || wantsDebugLabels) && availableExtensions.contains("VK_EXT_debug_utils")) {
         enabledExtensions.add("VK_EXT_debug_utils");
         return new Enabled(verbosity, wantsDebugLabels);
      } else {
         return new Disabled();
      }
   }

   void chainCreateInfo(VkInstanceCreateInfo instanceCreateInfo, MemoryStack stack);

   void setup(VkInstance vkInstance);

   void setObjectName(VkDevice device, int objectType, long objectHandle, String label);

   void setObjectName(VkDevice device, int objectType, long objectHandle, Supplier<String> label);

   void beginDebugGroup(VkCommandBuffer buffer, Supplier<String> label);

   void endDebugGroup(VkCommandBuffer buffer);

   void destroy(VkInstance instance);

   boolean enabled();

   public static class Enabled implements VulkanDebug {
      private static final StackWalker STACK_WALKER;
      private static final Logger LOGGER;
      public static final int MESSAGE_TYPE_BITMASK = 7;
      private static final int[] DEBUG_LEVELS;
      private final boolean wantsDebugLabels;
      private long messenger;
      private final int severityBitmask;

      public Enabled(final int verbosity, final boolean wantsDebugLabels) {
         super();
         this.wantsDebugLabels = wantsDebugLabels;
         int severityBitmask = 0;
         if (verbosity > 0) {
            for(int i = 0; i < Math.min(verbosity, DEBUG_LEVELS.length); ++i) {
               severityBitmask |= DEBUG_LEVELS[i];
            }
         }

         this.severityBitmask = severityBitmask;
      }

      public void chainCreateInfo(final VkInstanceCreateInfo instanceCreateInfo, final MemoryStack stack) {
         if (this.severityBitmask > 0) {
            instanceCreateInfo.pNext(VkDebugUtilsMessengerCreateInfoEXT.calloc(stack).sType$Default().messageSeverity(this.severityBitmask).messageType(7).pfnUserCallback(this::onDebugMessage));
         }

      }

      public void setup(final VkInstance vkInstance) {
         MemoryStack stack = MemoryStack.stackPush();

         label43: {
            try {
               LongBuffer pointer = stack.mallocLong(1);
               VkDebugUtilsMessengerCreateInfoEXT createInfo = VkDebugUtilsMessengerCreateInfoEXT.calloc(stack).sType$Default().messageSeverity(this.severityBitmask).messageType(7).pfnUserCallback(this::onDebugMessage);
               int result = EXTDebugUtils.vkCreateDebugUtilsMessengerEXT(vkInstance, createInfo, (VkAllocationCallbacks)null, pointer);
               if (result != 0) {
                  LOGGER.error("Error creating debug utils messenger: {}", VulkanUtils.resultToString(result));
                  break label43;
               }

               this.messenger = pointer.get(0);
            } catch (Throwable var7) {
               if (stack != null) {
                  try {
                     stack.close();
                  } catch (Throwable var6) {
                     var7.addSuppressed(var6);
                  }
               }

               throw var7;
            }

            if (stack != null) {
               stack.close();
            }

            return;
         }

         if (stack != null) {
            stack.close();
         }

      }

      public void setObjectName(final VkDevice device, final int objectType, final long objectHandle, final String label) {
         if (this.wantsDebugLabels) {
            MemoryStack stack = MemoryStack.stackPush();

            try {
               ByteBuffer name = stack.UTF8(label);
               VkDebugUtilsObjectNameInfoEXT nameInfo = VkDebugUtilsObjectNameInfoEXT.calloc(stack).sType$Default().pObjectName(name).objectType(objectType).objectHandle(objectHandle);
               EXTDebugUtils.vkSetDebugUtilsObjectNameEXT(device, nameInfo);
            } catch (Throwable var10) {
               if (stack != null) {
                  try {
                     stack.close();
                  } catch (Throwable var9) {
                     var10.addSuppressed(var9);
                  }
               }

               throw var10;
            }

            if (stack != null) {
               stack.close();
            }

         }
      }

      public void setObjectName(final VkDevice device, final int objectType, final long objectHandle, final Supplier<String> label) {
         if (this.wantsDebugLabels) {
            this.setObjectName(device, objectType, objectHandle, (String)label.get());
         }
      }

      private int onDebugMessage(final int messageSeverity, final int messageTypes, final long pCallbackData, final long pUserData) {
         VkDebugUtilsMessengerCallbackDataEXT callbackData = VkDebugUtilsMessengerCallbackDataEXT.create(pCallbackData);
         String message = callbackData.pMessageString();
         if ((messageSeverity & 16) != 0) {
            LOGGER.info("{}", message);
         } else if ((messageSeverity & 256) != 0) {
            LOGGER.warn("{}", message);
         } else {
            if ((messageSeverity & 4096) != 0) {
               if (message == null || !message.contains("vkDestroyInstance") && !message.contains("vkDestroyDevice")) {
                  String callStack = (String)STACK_WALKER.walk((s) -> (String)s.filter((frame) -> frame.getDeclaringClass() != Enabled.class && !frame.getDeclaringClass().getPackageName().startsWith("org.lwjgl")).limit(5L).map((frame) -> "\t" + String.valueOf(frame)).collect(Collectors.joining("\n")));
                  LOGGER.error("{}\n{}", message, callStack);
               } else {
                  LOGGER.error("{}", message);
               }

               return 1;
            }

            LOGGER.debug("{}", message);
         }

         return 0;
      }

      public void beginDebugGroup(final VkCommandBuffer buffer, final Supplier<String> label) {
         if (this.wantsDebugLabels) {
            MemoryStack stack = MemoryStack.stackPush();

            try {
               ByteBuffer name = stack.UTF8((CharSequence)label.get());
               VkDebugUtilsLabelEXT nameInfo = VkDebugUtilsLabelEXT.calloc(stack).sType$Default().pLabelName(name);
               EXTDebugUtils.vkCmdBeginDebugUtilsLabelEXT(buffer, nameInfo);
            } catch (Throwable var7) {
               if (stack != null) {
                  try {
                     stack.close();
                  } catch (Throwable var6) {
                     var7.addSuppressed(var6);
                  }
               }

               throw var7;
            }

            if (stack != null) {
               stack.close();
            }

         }
      }

      public void endDebugGroup(final VkCommandBuffer buffer) {
         if (this.wantsDebugLabels) {
            EXTDebugUtils.vkCmdEndDebugUtilsLabelEXT(buffer);
         }
      }

      public void destroy(final VkInstance instance) {
         if (this.messenger != 0L) {
            EXTDebugUtils.vkDestroyDebugUtilsMessengerEXT(instance, this.messenger, (VkAllocationCallbacks)null);
         }

      }

      public boolean enabled() {
         return true;
      }

      static {
         STACK_WALKER = StackWalker.getInstance(Set.of(Option.RETAIN_CLASS_REFERENCE), 3);
         LOGGER = LogUtils.getLogger();
         DEBUG_LEVELS = new int[]{4096, 256, 16, 1};
      }
   }

   public static class Disabled implements VulkanDebug {
      public Disabled() {
         super();
      }

      public void chainCreateInfo(final VkInstanceCreateInfo instanceCreateInfo, final MemoryStack stack) {
      }

      public void setup(final VkInstance vkInstance) {
      }

      public void setObjectName(final VkDevice device, final int objectType, final long objectHandle, final String label) {
      }

      public void setObjectName(final VkDevice device, final int objectType, final long objectHandle, final Supplier<String> label) {
      }

      public void beginDebugGroup(final VkCommandBuffer buffer, final Supplier<String> label) {
      }

      public void endDebugGroup(final VkCommandBuffer buffer) {
      }

      public void destroy(final VkInstance instance) {
      }

      public boolean enabled() {
         return false;
      }
   }
}
