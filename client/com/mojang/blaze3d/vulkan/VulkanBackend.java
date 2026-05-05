package com.mojang.blaze3d.vulkan;

import com.mojang.blaze3d.GLFWErrorCapture;
import com.mojang.blaze3d.platform.NativeLibrariesBootstrap;
import com.mojang.blaze3d.shaders.GpuDebugOptions;
import com.mojang.blaze3d.shaders.ShaderSource;
import com.mojang.blaze3d.systems.BackendCreationException;
import com.mojang.blaze3d.systems.GpuBackend;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.vulkan.init.VulkanFeature;
import com.mojang.blaze3d.vulkan.init.VulkanPNextStruct;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.nio.IntBuffer;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.jspecify.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.vma.Vma;
import org.lwjgl.util.vma.VmaAllocatorCreateInfo;
import org.lwjgl.util.vma.VmaVulkanFunctions;
import org.lwjgl.vulkan.VK12;
import org.lwjgl.vulkan.VkAllocationCallbacks;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkDeviceCreateInfo;
import org.lwjgl.vulkan.VkDeviceQueueCreateInfo;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceDynamicRenderingFeatures;
import org.lwjgl.vulkan.VkPhysicalDeviceFeatures;
import org.lwjgl.vulkan.VkPhysicalDeviceFeatures2;
import org.lwjgl.vulkan.VkPhysicalDeviceProperties;
import org.lwjgl.vulkan.VkPhysicalDeviceProperties2;
import org.lwjgl.vulkan.VkPhysicalDeviceSynchronization2Features;
import org.lwjgl.vulkan.VkPhysicalDeviceVertexAttributeDivisorFeaturesEXT;
import org.lwjgl.vulkan.VkPhysicalDeviceVulkan12Features;
import org.slf4j.Logger;

public class VulkanBackend implements GpuBackend {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Set<String> REQUIRED_DEVICE_EXTENSIONS = Set.of("VK_KHR_dynamic_rendering", "VK_KHR_push_descriptor", "VK_KHR_synchronization2", "VK_EXT_vertex_attribute_divisor", "VK_KHR_swapchain");
   public static final VulkanPNextStruct VK10_FEATURES_STRUCT;
   public static final VulkanPNextStruct VK12_FEATURES_STRUCT;
   public static final VulkanPNextStruct SYNC2_FEATURES_STRUCT;
   public static final VulkanPNextStruct DYNAMIC_RENDERING_FEATURES_STURCT;
   public static final VulkanPNextStruct VERTEX_ATTRIB_DIVISOR_FEATURES_STURCT;
   public static final Set<VulkanFeature> REQUIRED_DEVICE_FEATURES;

   public VulkanBackend() {
      super();
   }

   public String getName() {
      return "Vulkan";
   }

   public void setWindowHints() {
      GLFW.glfwWindowHint(139265, 0);
   }

   public void handleWindowCreationErrors(final GLFWErrorCapture.@Nullable Error error) throws BackendCreationException {
      if (error != null) {
         throw new BackendCreationException(String.format(Locale.ROOT, "GLFW_ERROR: 0x%X", error.error()), BackendCreationException.Reason.GLFW_ERROR);
      } else {
         throw new BackendCreationException("Failed to create window for Vulkan", BackendCreationException.Reason.GLFW_ERROR);
      }
   }

   public GpuDevice createDevice(final long window, final ShaderSource defaultShaderSource, final GpuDebugOptions debugOptions) throws BackendCreationException {
      if (!NativeLibrariesBootstrap.isVulkanLoaderAvailable()) {
         throw new BackendCreationException("Vulkan loader library is missing", BackendCreationException.Reason.VULKAN_LOADER_MISSING);
      } else if (!GLFWVulkan.glfwVulkanSupported()) {
         throw new BackendCreationException("Vulkan is not supported", BackendCreationException.Reason.GLFW_ERROR);
      } else {
         Set<String> deviceExtensions = new HashSet(REQUIRED_DEVICE_EXTENSIONS);
         VulkanInstance instance = null;
         VulkanPhysicalDevice physicalDevice = null;
         VkDevice device = null;
         long vma = 0L;

         try {
            boolean renderdocAttached = "1".equals(System.getenv("ENABLE_VULKAN_RENDERDOC_CAPTURE"));
            instance = new VulkanInstance(debugOptions.logLevel(), debugOptions.useLabels() || renderdocAttached, debugOptions.useValidationLayers());
            physicalDevice = findPhysicalDevice(instance);
            if (physicalDevice.hasDeviceExtension("VK_KHR_portability_subset")) {
               deviceExtensions.add("VK_KHR_portability_subset");
            }

            device = createDevice(deviceExtensions, physicalDevice);
            vma = createVma(device);
         } catch (BackendCreationException e) {
            if (vma != 0L) {
               Vma.vmaDestroyAllocator(vma);
            }

            if (device != null) {
               VK12.vkDestroyDevice(device, (VkAllocationCallbacks)null);
            }

            if (physicalDevice != null) {
               physicalDevice.close();
            }

            if (instance != null) {
               instance.close();
            }

            throw e;
         }

         return new GpuDevice(new VulkanDevice(defaultShaderSource, instance, physicalDevice, deviceExtensions, device, vma));
      }
   }

   private static long createVma(final VkDevice vkDevice) throws BackendCreationException {
      MemoryStack stack = MemoryStack.stackPush();

      long var5;
      try {
         VmaVulkanFunctions vmaVulkanFunctions = VmaVulkanFunctions.calloc(stack).set(vkDevice.getPhysicalDevice().getInstance(), vkDevice);
         VmaAllocatorCreateInfo createInfo = VmaAllocatorCreateInfo.calloc(stack).instance(vkDevice.getPhysicalDevice().getInstance()).vulkanApiVersion(VK12.VK_API_VERSION_1_2).device(vkDevice).physicalDevice(vkDevice.getPhysicalDevice()).pVulkanFunctions(vmaVulkanFunctions);
         PointerBuffer pointer = stack.callocPointer(1);
         VulkanUtils.throwIfFailure(Vma.vmaCreateAllocator(createInfo, pointer), "Failed to create VMA allocator", BackendCreationException.Reason.OTHER);
         var5 = pointer.get(0);
      } catch (Throwable var8) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (stack != null) {
         stack.close();
      }

      return var5;
   }

   private static VulkanPhysicalDevice findPhysicalDevice(final VulkanInstance instance) throws BackendCreationException {
      VkPhysicalDevice firstDevice = null;
      VkPhysicalDevice selectedDevice = null;
      MemoryStack stack = MemoryStack.stackPush();

      try {
         IntBuffer intBuffer = stack.callocInt(1);
         VulkanUtils.throwIfFailure(VK12.vkEnumeratePhysicalDevices(instance.vkInstance(), intBuffer, (PointerBuffer)null), "Failed to get number of physical devices", BackendCreationException.Reason.VULKAN_NO_DEVICE);
         if (intBuffer.get(0) == 0) {
            throw new BackendCreationException("No Vulkan capable devices", BackendCreationException.Reason.VULKAN_NO_DEVICE);
         }

         PointerBuffer pPhysicalDevices = stack.callocPointer(intBuffer.get(0));
         VulkanUtils.throwIfFailure(VK12.vkEnumeratePhysicalDevices(instance.vkInstance(), intBuffer, pPhysicalDevices), "Failed to get physical devices", BackendCreationException.Reason.VULKAN_NO_DEVICE);
         int numDevices = intBuffer.get(0);
         if (numDevices == 0) {
            throw new BackendCreationException("No Vulkan capable devices", BackendCreationException.Reason.VULKAN_NO_DEVICE);
         }

         for(int i = 0; i < numDevices; ++i) {
            if (pPhysicalDevices.get(i) != 0L) {
               VkPhysicalDevice currentDevice = new VkPhysicalDevice(pPhysicalDevices.get(i), instance.vkInstance());
               if (firstDevice == null) {
                  firstDevice = currentDevice;
               }

               if (deviceMeetsFeatureQueryRequirements(currentDevice) && isDeviceSuitable(currentDevice)) {
                  if (selectedDevice == null) {
                     selectedDevice = currentDevice;
                  } else if (isDeviceDiscrete(currentDevice) && !isDeviceDiscrete(selectedDevice)) {
                     LOGGER.info("Preferring discrete GPU: {}", getDeviceName(currentDevice));
                     selectedDevice = currentDevice;
                     break;
                  }
               }
            }
         }
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

      if (firstDevice == null) {
         throw new BackendCreationException("No Vulkan capable devices", BackendCreationException.Reason.VULKAN_NO_DEVICE);
      } else {
         if (selectedDevice == null) {
            throwForMissingRequrements(firstDevice);

            assert false;
         }

         return new VulkanPhysicalDevice(selectedDevice);
      }
   }

   private static boolean deviceMeetsFeatureQueryRequirements(final VkPhysicalDevice vkPhysicalDevice) {
      MemoryStack stack = MemoryStack.stackPush();

      boolean var3;
      try {
         VkPhysicalDeviceProperties properties = VkPhysicalDeviceProperties.calloc(stack);
         VK12.vkGetPhysicalDeviceProperties(vkPhysicalDevice, properties);
         var3 = properties.apiVersion() >= VK12.VK_API_VERSION_1_1;
      } catch (Throwable var5) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (stack != null) {
         stack.close();
      }

      return var3;
   }

   private static boolean isDeviceSuitable(final VkPhysicalDevice vkPhysicalDevice) {
      boolean var15;
      try (VulkanPhysicalDevice physicalDevice = new VulkanPhysicalDevice(vkPhysicalDevice)) {
         MemoryStack stack = MemoryStack.stackPush();

         try {
            String deviceName = physicalDevice.deviceName();
            Set<String> missingExtensions = physicalDevice.getMissingExtensions(REQUIRED_DEVICE_EXTENSIONS);
            VkPhysicalDeviceFeatures2 deviceFeatures = VkPhysicalDeviceFeatures2.calloc(stack).sType$Default();

            for(VulkanFeature requiredDeviceFeature : REQUIRED_DEVICE_FEATURES) {
               requiredDeviceFeature.struct().findOrCreateStructInPNextChain(deviceFeatures, stack);
            }

            VK12.vkGetPhysicalDeviceFeatures2(vkPhysicalDevice, deviceFeatures);
            boolean isSuitableDevice = true;
            if (physicalDevice.vkPhysicalDeviceProperties().apiVersion() < VK12.VK_API_VERSION_1_2) {
               LOGGER.warn("Device [{}] does not support Vulkan 1.2", deviceName);
               isSuitableDevice = false;
            }

            if (physicalDevice.graphicsQueueFamilyAndIndex() == null) {
               LOGGER.warn("Device [{}] does not have a graphics queue", deviceName);
               isSuitableDevice = false;
            }

            if (!missingExtensions.isEmpty()) {
               LOGGER.warn("Device [{}] does not support required extensions, missing: {}", deviceName, missingExtensions);
               isSuitableDevice = false;
            }

            for(VulkanFeature requiredDeviceFeature : REQUIRED_DEVICE_FEATURES) {
               if (!requiredDeviceFeature.get(deviceFeatures)) {
                  LOGGER.warn("Device [{}] does not have required feature [{}]", deviceName, requiredDeviceFeature.name());
                  isSuitableDevice = false;
               }
            }

            if (isSuitableDevice) {
               LOGGER.debug("Device [{}] is suitable", deviceName);
            }

            var15 = isSuitableDevice;
         } catch (Throwable var11) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var10) {
                  var11.addSuppressed(var10);
               }
            }

            throw var11;
         }

         if (stack != null) {
            stack.close();
         }
      }

      return var15;
   }

   private static boolean isDeviceDiscrete(final VkPhysicalDevice vkPhysicalDevice) {
      MemoryStack stack = MemoryStack.stackPush();

      boolean var3;
      try {
         VkPhysicalDeviceProperties2 deviceProperties = VkPhysicalDeviceProperties2.calloc(stack).sType$Default();
         VK12.vkGetPhysicalDeviceProperties2(vkPhysicalDevice, deviceProperties);
         var3 = deviceProperties.properties().deviceType() == 2;
      } catch (Throwable var5) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (stack != null) {
         stack.close();
      }

      return var3;
   }

   private static String getDeviceName(final VkPhysicalDevice vkPhysicalDevice) {
      MemoryStack stack = MemoryStack.stackPush();

      String var3;
      try {
         VkPhysicalDeviceProperties2 deviceProperties = VkPhysicalDeviceProperties2.calloc(stack).sType$Default();
         VK12.vkGetPhysicalDeviceProperties2(vkPhysicalDevice, deviceProperties);
         var3 = deviceProperties.properties().deviceNameString();
      } catch (Throwable var5) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (stack != null) {
         stack.close();
      }

      return var3;
   }

   private static void throwForMissingRequrements(final VkPhysicalDevice vkPhysicalDevice) throws BackendCreationException {
      List<String> missingCapabilities = new ReferenceArrayList();
      BackendCreationException.Reason mostProminentReason = BackendCreationException.Reason.OTHER;
      if (!deviceMeetsFeatureQueryRequirements(vkPhysicalDevice)) {
         throw new BackendCreationException("Device missing capabilities", BackendCreationException.Reason.VULKAN_DEVICE_VERSION_TOO_LOW, List.of("VULKAN_CORE_1_1"));
      } else {
         try (VulkanPhysicalDevice physicalDevice = new VulkanPhysicalDevice(vkPhysicalDevice)) {
            MemoryStack stack = MemoryStack.stackPush();

            try {
               VkPhysicalDeviceFeatures2 deviceFeatures = VkPhysicalDeviceFeatures2.calloc(stack).sType$Default();

               for(VulkanFeature requiredDeviceFeature : REQUIRED_DEVICE_FEATURES) {
                  requiredDeviceFeature.struct().findOrCreateStructInPNextChain(deviceFeatures, stack);
               }

               VK12.vkGetPhysicalDeviceFeatures2(vkPhysicalDevice, deviceFeatures);

               for(VulkanFeature requiredDeviceFeature : REQUIRED_DEVICE_FEATURES) {
                  if (!requiredDeviceFeature.get(deviceFeatures)) {
                     mostProminentReason = BackendCreationException.Reason.VULKAN_MISSING_FEATURE;
                     missingCapabilities.add(requiredDeviceFeature.name());
                  }
               }

               Set<String> missingExtensions = physicalDevice.getMissingExtensions(REQUIRED_DEVICE_EXTENSIONS);
               if (!missingExtensions.isEmpty()) {
                  mostProminentReason = BackendCreationException.Reason.VULKAN_MISSING_EXTENSION;
                  missingCapabilities.addAll(missingExtensions);
               }

               if (physicalDevice.graphicsQueueFamilyAndIndex() == null) {
                  mostProminentReason = BackendCreationException.Reason.VULKAN_NO_GRAPHICS_QUEUE;
                  missingCapabilities.add("COMBINED_GRAPHICS_COMPUTE_PRESENT_QUEUE");
               }

               if (physicalDevice.vkPhysicalDeviceProperties().apiVersion() < VK12.VK_API_VERSION_1_2) {
                  mostProminentReason = BackendCreationException.Reason.VULKAN_DEVICE_VERSION_TOO_LOW;
                  missingCapabilities.add("VULKAN_CORE_1_2");
               }
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

         throw new BackendCreationException("Device missing capabilities", mostProminentReason, missingCapabilities);
      }
   }

   private static VkDevice createDevice(final Collection<String> deviceExtensions, final VulkanPhysicalDevice physicalDevice) throws BackendCreationException {
      MemoryStack stack = MemoryStack.stackPush();

      VkDevice var9;
      try {
         VkPhysicalDeviceFeatures2 deviceFeatures = VkPhysicalDeviceFeatures2.calloc(stack).sType$Default();

         for(VulkanFeature requiredDeviceFeature : REQUIRED_DEVICE_FEATURES) {
            requiredDeviceFeature.set(deviceFeatures, true, stack);
         }

         Int2IntMap queuesToCreate = physicalDevice.queueFamilyCreateInfoMap();
         VkDeviceQueueCreateInfo.Buffer queueCreationInfo = VkDeviceQueueCreateInfo.calloc(queuesToCreate.size(), stack);
         ObjectIterator var6 = queuesToCreate.int2IntEntrySet().iterator();

         while(var6.hasNext()) {
            Int2IntMap.Entry familyCount = (Int2IntMap.Entry)var6.next();
            queueCreationInfo.sType$Default();
            queueCreationInfo.queueFamilyIndex(familyCount.getIntKey());
            queueCreationInfo.pQueuePriorities(stack.callocFloat(familyCount.getIntValue()));
            queueCreationInfo.position(queueCreationInfo.position() + 1);
         }

         queueCreationInfo.position(0);
         PointerBuffer enabledExtensionsBuffer = stack.callocPointer(deviceExtensions.size());

         for(String name : deviceExtensions) {
            enabledExtensionsBuffer.put(stack.UTF8(name));
         }

         enabledExtensionsBuffer.flip();
         VkDeviceCreateInfo deviceCreateInfo = VkDeviceCreateInfo.calloc(stack).sType$Default();
         deviceCreateInfo.pNext(deviceFeatures.pNext());
         deviceCreateInfo.pQueueCreateInfos(queueCreationInfo);
         deviceCreateInfo.ppEnabledExtensionNames(enabledExtensionsBuffer);
         deviceCreateInfo.pEnabledFeatures(deviceFeatures.features());
         PointerBuffer pointer = stack.callocPointer(1);
         VulkanUtils.throwIfFailure(VK12.vkCreateDevice(physicalDevice.vkPhysicalDevice(), deviceCreateInfo, (VkAllocationCallbacks)null, pointer), "Failed to create device", BackendCreationException.Reason.VULKAN_NO_DEVICE);
         var9 = new VkDevice(pointer.get(0), physicalDevice.vkPhysicalDevice(), deviceCreateInfo);
      } catch (Throwable var11) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var10) {
               var11.addSuppressed(var10);
            }
         }

         throw var11;
      }

      if (stack != null) {
         stack.close();
      }

      return var9;
   }

   static {
      VK10_FEATURES_STRUCT = new VulkanPNextStruct(1000059000, VkPhysicalDeviceProperties2.SIZEOF);
      VK12_FEATURES_STRUCT = new VulkanPNextStruct(51, VkPhysicalDeviceVulkan12Features.SIZEOF);
      SYNC2_FEATURES_STRUCT = new VulkanPNextStruct(1000314007, VkPhysicalDeviceSynchronization2Features.SIZEOF);
      DYNAMIC_RENDERING_FEATURES_STURCT = new VulkanPNextStruct(1000044003, VkPhysicalDeviceDynamicRenderingFeatures.SIZEOF);
      VERTEX_ATTRIB_DIVISOR_FEATURES_STURCT = new VulkanPNextStruct(1000190002, VkPhysicalDeviceVertexAttributeDivisorFeaturesEXT.SIZEOF);
      REQUIRED_DEVICE_FEATURES = Set.of(new VulkanFeature(VK10_FEATURES_STRUCT, "fillModeNonSolid", (long)VkPhysicalDeviceFeatures.FILLMODENONSOLID), new VulkanFeature(VK10_FEATURES_STRUCT, "samplerAnisotropy", (long)VkPhysicalDeviceFeatures.SAMPLERANISOTROPY), new VulkanFeature(VK12_FEATURES_STRUCT, "timelineSemaphore", (long)VkPhysicalDeviceVulkan12Features.TIMELINESEMAPHORE), new VulkanFeature(VK12_FEATURES_STRUCT, "hostQueryReset", (long)VkPhysicalDeviceVulkan12Features.HOSTQUERYRESET), new VulkanFeature(SYNC2_FEATURES_STRUCT, "synchronization2", (long)VkPhysicalDeviceSynchronization2Features.SYNCHRONIZATION2), new VulkanFeature(DYNAMIC_RENDERING_FEATURES_STURCT, "dynamicRendering", (long)VkPhysicalDeviceDynamicRenderingFeatures.DYNAMICRENDERING), new VulkanFeature(VERTEX_ATTRIB_DIVISOR_FEATURES_STURCT, "vertexAttributeInstanceRateDivisor", (long)VkPhysicalDeviceVertexAttributeDivisorFeaturesEXT.VERTEXATTRIBUTEINSTANCERATEDIVISOR));
   }
}
