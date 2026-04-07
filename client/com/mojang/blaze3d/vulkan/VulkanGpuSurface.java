package com.mojang.blaze3d.vulkan;

import com.mojang.blaze3d.systems.CommandEncoderBackend;
import com.mojang.blaze3d.systems.GpuSurface;
import com.mojang.blaze3d.systems.GpuSurfaceBackend;
import com.mojang.blaze3d.systems.SurfaceException;
import com.mojang.blaze3d.textures.GpuTextureView;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.longs.LongList;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.Locale;
import org.jspecify.annotations.Nullable;
import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.KHRSurface;
import org.lwjgl.vulkan.KHRSwapchain;
import org.lwjgl.vulkan.KHRSynchronization2;
import org.lwjgl.vulkan.VK12;
import org.lwjgl.vulkan.VkAllocationCallbacks;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkCommandBufferBeginInfo;
import org.lwjgl.vulkan.VkDependencyInfo;
import org.lwjgl.vulkan.VkExtent2D;
import org.lwjgl.vulkan.VkImageBlit;
import org.lwjgl.vulkan.VkImageMemoryBarrier2;
import org.lwjgl.vulkan.VkImageSubresourceLayers;
import org.lwjgl.vulkan.VkImageSubresourceRange;
import org.lwjgl.vulkan.VkMemoryBarrier2;
import org.lwjgl.vulkan.VkOffset3D;
import org.lwjgl.vulkan.VkPresentInfoKHR;
import org.lwjgl.vulkan.VkQueue;
import org.lwjgl.vulkan.VkSemaphoreCreateInfo;
import org.lwjgl.vulkan.VkSurfaceCapabilitiesKHR;
import org.lwjgl.vulkan.VkSurfaceFormatKHR;
import org.lwjgl.vulkan.VkSwapchainCreateInfoKHR;

public class VulkanGpuSurface implements GpuSurfaceBackend {
   private static final int[] VSYNC_PRESENT_MODE_ORDER = new int[]{3, 2};
   private static final int[] NOSYNC_PRESENT_MODE = new int[]{0, 1, 2};
   private static final int NO_CURRENT_IMAGE = -1;
   private final VulkanDevice device;
   private final VkQueue presentQueue;
   private final long surface;
   private final int swapchainImageFormat;
   private final int vsyncPresentMode;
   private final int nosyncPresentMode;
   private long swapchain;
   private int swapchainWidth;
   private int swapchainHeight;
   private final LongList swapchainImages = new LongArrayList();
   private final long[] acquireSemaphores = new long[2];
   private int currentAcquireSemaphore = 0;
   private long[] presentSemaphores;
   private int currentImageIndex;
   private @Nullable SurfaceException eatenException;
   private boolean swapchainSuboptimal;
   private boolean swapchainOutOfDate;

   public VulkanGpuSurface(final VulkanDevice device, final long windowHandle) {
      super();
      this.presentSemaphores = LongArrays.EMPTY_ARRAY;
      this.currentImageIndex = -1;
      this.eatenException = null;
      this.device = device;
      this.presentQueue = device.graphicsQueue().vkQueue();
      MemoryStack stack = MemoryStack.stackPush();

      try {
         LongBuffer handlePtr = stack.longs(0L);
         VulkanUtils.crashIfFailure(GLFWVulkan.glfwCreateWindowSurface(device.vkDevice().getPhysicalDevice().getInstance(), windowHandle, (VkAllocationCallbacks)null, handlePtr), "Failed to create window surface");
         this.surface = handlePtr.get(0);
         IntBuffer countPtr = stack.callocInt(1);
         VulkanUtils.crashIfFailure(KHRSurface.vkGetPhysicalDeviceSurfacePresentModesKHR(device.vkDevice().getPhysicalDevice(), this.surface, countPtr, (IntBuffer)null), "Failed to enumerate surface present modes");
         int presentModeCount = countPtr.get(0);
         IntBuffer presentModes = stack.callocInt(presentModeCount);
         VulkanUtils.crashIfFailure(KHRSurface.vkGetPhysicalDeviceSurfacePresentModesKHR(device.vkDevice().getPhysicalDevice(), this.surface, countPtr, presentModes), "Failed to enumerate surface present modes");
         this.vsyncPresentMode = this.pickSwapchainPresentMode(VSYNC_PRESENT_MODE_ORDER, presentModes);
         this.nosyncPresentMode = this.pickSwapchainPresentMode(NOSYNC_PRESENT_MODE, presentModes);
         IntBuffer formatCount = stack.callocInt(1);
         KHRSurface.vkGetPhysicalDeviceSurfaceFormatsKHR(device.vkDevice().getPhysicalDevice(), this.surface, formatCount, (VkSurfaceFormatKHR.Buffer)null);
         VkSurfaceFormatKHR.Buffer formatsBuffer = VkSurfaceFormatKHR.calloc(formatCount.get(0));
         KHRSurface.vkGetPhysicalDeviceSurfaceFormatsKHR(device.vkDevice().getPhysicalDevice(), this.surface, formatCount, formatsBuffer);
         this.swapchainImageFormat = this.pickSwapchainSurfaceFormat(formatsBuffer).format();
         VkSemaphoreCreateInfo semaphoreCreateInfo = VkSemaphoreCreateInfo.calloc(stack).sType$Default();
         LongBuffer semaphoreHandlePtr = stack.callocLong(1);

         for(int i = 0; i < this.acquireSemaphores.length; ++i) {
            VulkanUtils.crashIfFailure(VK12.vkCreateSemaphore(device.vkDevice(), semaphoreCreateInfo, (VkAllocationCallbacks)null, semaphoreHandlePtr), "Failed to create VkSemaphore(binary)");
            this.acquireSemaphores[i] = semaphoreHandlePtr.get(0);
         }
      } catch (Throwable var15) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var14) {
               var15.addSuppressed(var14);
            }
         }

         throw var15;
      }

      if (stack != null) {
         stack.close();
      }

   }

   public int pickSwapchainPresentMode(final int[] preferenceOrder, final IntBuffer allowedModes) {
      for(int preferredPresentMode : preferenceOrder) {
         for(int j = 0; j < allowedModes.limit(); ++j) {
            int allowedPresentMode = allowedModes.get(j);
            if (allowedPresentMode == preferredPresentMode) {
               return preferredPresentMode;
            }
         }
      }

      throw new IllegalStateException("Failed to find present mode to use, FIFO guaranteed by spec");
   }

   public VkSurfaceFormatKHR pickSwapchainSurfaceFormat(final VkSurfaceFormatKHR.Buffer formats) {
      for(VkSurfaceFormatKHR format : formats) {
         if (format.colorSpace() == 0 && (format.format() == 37 || format.format() == 44)) {
            return format;
         }
      }

      throw new IllegalStateException("Could not find compatible swapchain format");
   }

   public static void throwIfFailure(final int result, final String message) throws SurfaceException {
      if (result < 0) {
         String var10002 = VulkanUtils.resultToString(result);
         throw new SurfaceException(var10002 + ": " + message);
      }
   }

   public void close() {
      this.destroySwapchain();

      for(int i = 0; i < this.acquireSemaphores.length; ++i) {
         VK12.vkDestroySemaphore(this.device.vkDevice(), this.acquireSemaphores[i], (VkAllocationCallbacks)null);
      }

      KHRSurface.vkDestroySurfaceKHR(this.device.instance().vkInstance(), this.surface, (VkAllocationCallbacks)null);
   }

   private void destroySwapchain() {
      if (this.swapchain != 0L) {
         this.device.graphicsQueue().waitIdle();
         KHRSwapchain.vkDestroySwapchainKHR(this.device.vkDevice(), this.swapchain, (VkAllocationCallbacks)null);

         for(int i = 0; i < this.presentSemaphores.length; ++i) {
            VK12.vkDestroySemaphore(this.device.vkDevice(), this.presentSemaphores[i], (VkAllocationCallbacks)null);
         }

         this.presentSemaphores = LongArrays.EMPTY_ARRAY;
         this.swapchain = 0L;
      }
   }

   public void configure(final GpuSurface.Configuration config) throws SurfaceException {
      this.destroySwapchain();

      try {
         MemoryStack stack = MemoryStack.stackPush();

         try {
            VkSurfaceCapabilitiesKHR surfaceCapabilities = VkSurfaceCapabilitiesKHR.calloc(stack);
            throwIfFailure(KHRSurface.vkGetPhysicalDeviceSurfaceCapabilitiesKHR(this.device.vkDevice().getPhysicalDevice(), this.surface, surfaceCapabilities), "Failed to get surface capabilities");
            VkExtent2D minExtent = surfaceCapabilities.minImageExtent();
            VkExtent2D maxExtent = surfaceCapabilities.maxImageExtent();
            if (config.width() < minExtent.width() || maxExtent.width() < config.width() || config.height() < minExtent.height() || maxExtent.height() < config.height()) {
               throw new SurfaceException(String.format(Locale.ROOT, "Requested swapchain extent (%d x %d) not within allowed extent: min(%d x %d) max(%d x %d)", config.width(), config.height(), minExtent.width(), minExtent.height(), maxExtent.width(), maxExtent.height()));
            }

            VkSwapchainCreateInfoKHR swapchainCreateInfo = VkSwapchainCreateInfoKHR.calloc(stack).sType$Default();
            swapchainCreateInfo.surface(this.surface);
            int currentPresentMode = config.vsync() ? this.vsyncPresentMode : this.nosyncPresentMode;
            int requestedImageCount = currentPresentMode == 1 ? 3 : 2;
            swapchainCreateInfo.minImageCount(Math.max(requestedImageCount, surfaceCapabilities.minImageCount()));
            swapchainCreateInfo.imageFormat(this.swapchainImageFormat);
            swapchainCreateInfo.imageColorSpace(0);
            swapchainCreateInfo.imageExtent(VkExtent2D.calloc(stack).set(config.width(), config.height()));
            swapchainCreateInfo.imageArrayLayers(1);
            swapchainCreateInfo.imageUsage(2);
            swapchainCreateInfo.imageSharingMode(0);
            swapchainCreateInfo.preTransform(surfaceCapabilities.currentTransform());
            swapchainCreateInfo.compositeAlpha(1);
            swapchainCreateInfo.presentMode(currentPresentMode);
            swapchainCreateInfo.clipped(true);
            LongBuffer swapchainPtr = stack.callocLong(1);
            throwIfFailure(KHRSwapchain.vkCreateSwapchainKHR(this.device.vkDevice(), swapchainCreateInfo, (VkAllocationCallbacks)null, swapchainPtr), "Failed to create Swapchain");
            this.swapchain = swapchainPtr.get(0);
            IntBuffer imageCountPtr = stack.callocInt(1);
            throwIfFailure(KHRSwapchain.vkGetSwapchainImagesKHR(this.device.vkDevice(), this.swapchain, imageCountPtr, (LongBuffer)null), "Failed to get swapchain image count");
            int swapchainImageCount = imageCountPtr.get(0);
            LongBuffer swapchainImagesPtr = stack.mallocLong(swapchainImageCount);
            throwIfFailure(KHRSwapchain.vkGetSwapchainImagesKHR(this.device.vkDevice(), this.swapchain, imageCountPtr, swapchainImagesPtr), "Failed to get swapchain images");
            this.swapchainImages.clear();

            for(int i = 0; i < swapchainImageCount; ++i) {
               this.swapchainImages.add(swapchainImagesPtr.get(i));
            }

            this.presentSemaphores = new long[swapchainImageCount];
            VkSemaphoreCreateInfo semaphoreCreateInfo = VkSemaphoreCreateInfo.calloc(stack).sType$Default();
            LongBuffer semaphoreHandlePtr = stack.callocLong(1);

            for(int i = 0; i < this.presentSemaphores.length; ++i) {
               throwIfFailure(VK12.vkCreateSemaphore(this.device.vkDevice(), semaphoreCreateInfo, (VkAllocationCallbacks)null, semaphoreHandlePtr), "Failed to create VkSemaphore(binary)");
               this.presentSemaphores[i] = semaphoreHandlePtr.get(0);
            }

            this.swapchainSuboptimal = false;
            this.swapchainOutOfDate = false;
            this.swapchainWidth = config.width();
            this.swapchainHeight = config.height();
         } catch (Throwable var17) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var16) {
                  var17.addSuppressed(var16);
               }
            }

            throw var17;
         }

         if (stack != null) {
            stack.close();
         }

      } catch (SurfaceException e) {
         this.swapchainSuboptimal = true;
         this.swapchainOutOfDate = true;
         throw e;
      }
   }

   public boolean isSuboptimal() {
      return this.swapchainSuboptimal;
   }

   public void acquireNextTexture() throws SurfaceException {
      if (this.eatenException != null) {
         SurfaceException toThrow = this.eatenException;
         this.eatenException = null;
         throw new SurfaceException(toThrow);
      } else if (this.swapchainOutOfDate) {
         throw new IllegalStateException("Attempt to use out of date swapchain");
      } else {
         MemoryStack stack = MemoryStack.stackPush();

         try {
            IntBuffer frameIndexPtr = stack.mallocInt(1);
            frameIndexPtr.put(0, -1);
            ++this.currentAcquireSemaphore;
            this.currentAcquireSemaphore %= this.acquireSemaphores.length;
            long acquireSemaphore = this.acquireSemaphores[this.currentAcquireSemaphore];
            int result = KHRSwapchain.vkAcquireNextImageKHR(this.device.vkDevice(), this.swapchain, 5000000000L, acquireSemaphore, 0L, frameIndexPtr);
            if (result == 2) {
               throw new IllegalStateException("GPU timeout attempting to acquire next frame");
            }

            this.currentImageIndex = frameIndexPtr.get(0);
            if (result == -1000001004) {
               this.swapchainSuboptimal = true;
               this.swapchainOutOfDate = true;
               throw new SurfaceException("Failed to acquire image, swapchain out of date");
            }

            if (result == 1000001003) {
               this.swapchainSuboptimal = true;
            } else {
               VulkanUtils.crashIfFailure(result, "Failed to acquire image");
            }
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

   public void blitFromTexture(final CommandEncoderBackend commandEncoder, final GpuTextureView textureView) {
      if (this.swapchainOutOfDate) {
         throw new IllegalStateException("Attempt to use out of date swapchain");
      } else {
         assert this.currentImageIndex != -1;

         VulkanCommandEncoder vulkanCommandEncoder = (VulkanCommandEncoder)commandEncoder;
         VkCommandBuffer blitCommandBuffer = vulkanCommandEncoder.allocateTransientCommandBuffer(true);
         long swapchainImage = this.swapchainImages.getLong(this.currentImageIndex);
         MemoryStack stack = MemoryStack.stackGet();
         MemoryStack var8 = stack.push();

         try {
            VkCommandBufferBeginInfo beginInfo = VkCommandBufferBeginInfo.calloc(stack).sType$Default();
            beginInfo.flags(1);
            VulkanUtils.crashIfFailure(VK12.vkBeginCommandBuffer(blitCommandBuffer, beginInfo), "Failed to begin VkCommandBuffer");
         } catch (Throwable var21) {
            if (var8 != null) {
               try {
                  var8.close();
               } catch (Throwable var17) {
                  var21.addSuppressed(var17);
               }
            }

            throw var21;
         }

         if (var8 != null) {
            var8.close();
         }

         var8 = stack.push();

         try {
            VkImageMemoryBarrier2.Buffer imageBarrier = VkImageMemoryBarrier2.calloc(1, stack).sType$Default();
            imageBarrier.srcStageMask(0L);
            imageBarrier.srcAccessMask(0L);
            imageBarrier.dstStageMask(4096L);
            imageBarrier.dstAccessMask(4096L);
            imageBarrier.oldLayout(0);
            imageBarrier.newLayout(7);
            imageBarrier.srcQueueFamilyIndex(-1);
            imageBarrier.dstQueueFamilyIndex(-1);
            imageBarrier.image(swapchainImage);
            VkImageSubresourceRange subresourceRange = imageBarrier.subresourceRange();
            subresourceRange.aspectMask(1);
            subresourceRange.baseMipLevel(0);
            subresourceRange.levelCount(1);
            subresourceRange.baseArrayLayer(0);
            subresourceRange.layerCount(1);
            VkDependencyInfo depinfo = VkDependencyInfo.calloc(stack).sType$Default();
            depinfo.pImageMemoryBarriers(imageBarrier);
            KHRSynchronization2.vkCmdPipelineBarrier2KHR(blitCommandBuffer, depinfo);
         } catch (Throwable var20) {
            if (var8 != null) {
               try {
                  var8.close();
               } catch (Throwable var16) {
                  var20.addSuppressed(var16);
               }
            }

            throw var20;
         }

         if (var8 != null) {
            var8.close();
         }

         var8 = stack.push();

         try {
            VkOffset3D.Buffer srcOffsets = VkOffset3D.calloc(2, stack);
            srcOffsets.x(0).y(0).z(0);
            srcOffsets.position(1);
            srcOffsets.x(textureView.getWidth(0)).y(textureView.getHeight(0)).z(1);
            srcOffsets.position(0);
            VkOffset3D.Buffer dstOffsets = VkOffset3D.calloc(2, stack);
            dstOffsets.x(0).y(this.swapchainHeight).z(0);
            dstOffsets.position(1);
            dstOffsets.x(this.swapchainWidth).y(0).z(1);
            dstOffsets.position(0);
            VkImageSubresourceLayers srcSubresource = VkImageSubresourceLayers.calloc(stack);
            srcSubresource.aspectMask(1);
            srcSubresource.mipLevel(textureView.baseMipLevel());
            srcSubresource.baseArrayLayer(0);
            srcSubresource.layerCount(1);
            VkImageSubresourceLayers dstSubresource = VkImageSubresourceLayers.calloc(stack);
            dstSubresource.aspectMask(1);
            dstSubresource.mipLevel(0);
            dstSubresource.baseArrayLayer(0);
            dstSubresource.layerCount(1);
            VkImageBlit.Buffer blitRegion = VkImageBlit.calloc(1, stack);
            blitRegion.srcSubresource(dstSubresource);
            blitRegion.srcOffsets(srcOffsets);
            blitRegion.dstSubresource(dstSubresource);
            blitRegion.dstOffsets(dstOffsets);
            VK12.vkCmdBlitImage(blitCommandBuffer, ((VulkanGpuTexture)textureView.texture()).vkImage(), 1, swapchainImage, 7, blitRegion, 0);
         } catch (Throwable var19) {
            if (var8 != null) {
               try {
                  var8.close();
               } catch (Throwable var15) {
                  var19.addSuppressed(var15);
               }
            }

            throw var19;
         }

         if (var8 != null) {
            var8.close();
         }

         var8 = stack.push();

         try {
            VkImageMemoryBarrier2.Buffer imageBarrier = VkImageMemoryBarrier2.calloc(1, stack).sType$Default();
            imageBarrier.srcStageMask(4096L);
            imageBarrier.srcAccessMask(4096L);
            imageBarrier.dstStageMask(65536L);
            imageBarrier.dstAccessMask(0L);
            imageBarrier.oldLayout(7);
            imageBarrier.newLayout(1000001002);
            imageBarrier.srcQueueFamilyIndex(-1);
            imageBarrier.dstQueueFamilyIndex(-1);
            imageBarrier.image(swapchainImage);
            VkImageSubresourceRange subresourceRange = imageBarrier.subresourceRange();
            subresourceRange.aspectMask(1);
            subresourceRange.baseMipLevel(0);
            subresourceRange.levelCount(1);
            subresourceRange.baseArrayLayer(0);
            subresourceRange.layerCount(1);
            VkMemoryBarrier2.Buffer memoryBarrier = VkMemoryBarrier2.calloc(1, stack).sType$Default();
            memoryBarrier.srcStageMask(4096L);
            memoryBarrier.srcAccessMask(2048L);
            memoryBarrier.dstStageMask(65536L);
            memoryBarrier.dstAccessMask(98304L);
            VkDependencyInfo depinfo = VkDependencyInfo.calloc(stack).sType$Default();
            depinfo.pMemoryBarriers(memoryBarrier);
            depinfo.pImageMemoryBarriers(imageBarrier);
            KHRSynchronization2.vkCmdPipelineBarrier2KHR(blitCommandBuffer, depinfo);
         } catch (Throwable var18) {
            if (var8 != null) {
               try {
                  var8.close();
               } catch (Throwable var14) {
                  var18.addSuppressed(var14);
               }
            }

            throw var18;
         }

         if (var8 != null) {
            var8.close();
         }

         VulkanUtils.crashIfFailure(VK12.vkEndCommandBuffer(blitCommandBuffer), "Failed to end VkCommandBuffer");
         vulkanCommandEncoder.waitSemaphore(this.acquireSemaphores[this.currentAcquireSemaphore], 0L, 65536L);
         vulkanCommandEncoder.execute(blitCommandBuffer);
         vulkanCommandEncoder.signalSemaphore(this.presentSemaphores[this.currentImageIndex], 0L, 4096L);
      }
   }

   public void present() {
      if (this.swapchainOutOfDate) {
         throw new IllegalStateException("Attempt to use out of date swapchain");
      } else {
         MemoryStack stack = MemoryStack.stackPush();

         try {
            VkPresentInfoKHR presentInfo = VkPresentInfoKHR.calloc(stack).sType$Default();
            presentInfo.pWaitSemaphores(stack.longs(this.presentSemaphores[this.currentImageIndex]));
            presentInfo.swapchainCount(1);
            presentInfo.pSwapchains(stack.longs(this.swapchain));
            presentInfo.pImageIndices(stack.ints(this.currentImageIndex));
            this.currentImageIndex = -1;
            int result = KHRSwapchain.vkQueuePresentKHR(this.presentQueue, presentInfo);
            if (result == -1000001004) {
               this.swapchainSuboptimal = true;
               this.swapchainOutOfDate = true;
               this.eatenException = new SurfaceException("Failed to present image, swapchain out of date");
            } else if (result == 1000001003) {
               this.swapchainSuboptimal = true;
            } else {
               VulkanUtils.crashIfFailure(result, "Failed to present image");
            }
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

      }
   }
}
