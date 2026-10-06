package com.mojang.renderpearl.backend.vulkan;

import com.mojang.logging.LogUtils;
import com.mojang.renderpearl.api.device.GpuSurface;
import com.mojang.renderpearl.api.device.SurfaceException;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import com.mojang.renderpearl.backend.api.CommandEncoderBackend;
import com.mojang.renderpearl.backend.api.GpuSurfaceBackend;
import com.mojang.renderpearl.backend.vulkan.init.FeatureSet;
import com.mojang.renderpearl.util.PlatformUtil;
import com.mojang.renderpearl.util.UncheckedAutoCloseable;
import com.mojang.renderpearl.util.dx.COMUnknown;
import com.mojang.renderpearl.util.dx.DXUtils;
import com.mojang.renderpearl.util.dx.Win32Handle;
import com.mojang.renderpearl.util.dx.d3d12.D3D12CommandAllocator;
import com.mojang.renderpearl.util.dx.d3d12.D3D12CommandQueue;
import com.mojang.renderpearl.util.dx.d3d12.D3D12CommandQueueDesc;
import com.mojang.renderpearl.util.dx.d3d12.D3D12Device;
import com.mojang.renderpearl.util.dx.d3d12.D3D12DeviceChild;
import com.mojang.renderpearl.util.dx.d3d12.D3D12Fence;
import com.mojang.renderpearl.util.dx.d3d12.D3D12GraphicsCommandList;
import com.mojang.renderpearl.util.dx.d3d12.D3D12HeapProperties;
import com.mojang.renderpearl.util.dx.d3d12.D3D12Resource;
import com.mojang.renderpearl.util.dx.d3d12.D3D12ResourceDesc;
import com.mojang.renderpearl.util.dx.dxgi.DXGIAdapter;
import com.mojang.renderpearl.util.dx.dxgi.DXGIFactory5;
import com.mojang.renderpearl.util.dx.dxgi.DXGISampleDesc;
import com.mojang.renderpearl.util.dx.dxgi.DXGISwapchain3;
import com.mojang.renderpearl.util.dx.dxgi.DXGISwapchainDesc1;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.nio.LongBuffer;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.jspecify.annotations.Nullable;
import org.lwjgl.sdl.SDLProperties;
import org.lwjgl.sdl.SDLVideo;
import org.lwjgl.system.JNI;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.KHRExternalMemoryWin32;
import org.lwjgl.vulkan.KHRSynchronization2;
import org.lwjgl.vulkan.VK12;
import org.lwjgl.vulkan.VkAllocationCallbacks;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkDependencyInfo;
import org.lwjgl.vulkan.VkExternalImageFormatProperties;
import org.lwjgl.vulkan.VkExternalMemoryImageCreateInfo;
import org.lwjgl.vulkan.VkExternalSemaphoreProperties;
import org.lwjgl.vulkan.VkImageBlit;
import org.lwjgl.vulkan.VkImageCreateInfo;
import org.lwjgl.vulkan.VkImageFormatProperties2;
import org.lwjgl.vulkan.VkImageMemoryBarrier2;
import org.lwjgl.vulkan.VkImageSubresourceLayers;
import org.lwjgl.vulkan.VkImageSubresourceRange;
import org.lwjgl.vulkan.VkImportMemoryWin32HandleInfoKHR;
import org.lwjgl.vulkan.VkImportSemaphoreWin32HandleInfoKHR;
import org.lwjgl.vulkan.VkMemoryAllocateInfo;
import org.lwjgl.vulkan.VkMemoryBarrier2;
import org.lwjgl.vulkan.VkMemoryDedicatedAllocateInfo;
import org.lwjgl.vulkan.VkMemoryRequirements;
import org.lwjgl.vulkan.VkMemoryWin32HandlePropertiesKHR;
import org.lwjgl.vulkan.VkOffset3D;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceExternalImageFormatInfo;
import org.lwjgl.vulkan.VkPhysicalDeviceExternalSemaphoreInfo;
import org.lwjgl.vulkan.VkPhysicalDeviceIDProperties;
import org.lwjgl.vulkan.VkPhysicalDeviceImageFormatInfo2;
import org.lwjgl.vulkan.VkPhysicalDeviceProperties2;
import org.lwjgl.vulkan.VkSemaphoreCreateInfo;
import org.lwjgl.vulkan.VkSemaphoreTypeCreateInfo;
import org.slf4j.Logger;

public class D3D12GpuSurface implements GpuSurfaceBackend {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final VulkanDevice vulkanDevice;
   private final long win32Window;
   private final DXGIFactory5 dxgiFactory = DXGIFactory5.create();
   private final boolean allowTearing;
   private final D3D12Device d3d12Device;
   private final int nodeMask;
   private final D3D12CommandQueue commandQueue;
   private final D3D12Fence fence;
   private long fenceValue = 0L;
   private final Win32Handle fenceSharedHandle;
   private final long vkImportedSemaphore;
   private boolean blitModeActive;
   private @Nullable DXGISwapchain3 swapchain;
   private @Nullable Win32Handle waitHandle;
   private final List<D3D12Resource> dxImagesToClose = new ReferenceArrayList();
   private final List<D3D12Resource> dxgiSwapchainImages = new ReferenceArrayList();
   private final List<D3D12Resource> d3dBlitTargets = new ReferenceArrayList();
   private final List<Win32Handle> blitTargetHandles = new ReferenceArrayList();
   private final LongList vkImportedImages = new LongArrayList();
   private final LongList vkImportedImageMemories = new LongArrayList();
   private int swapchainWidth;
   private int swapchainHeight;
   private GpuSurface.PresentMode mode;
   private final List<D3D12CommandAllocator> allocators;
   private @Nullable D3D12GraphicsCommandList commandList;

   public static boolean available(final VkPhysicalDevice device, final FeatureSet enabledFeatureSet) {
      return !environmentMeetsBasicRequirements() ? false : checkPhysicalDeviceImportCapabilities(device, enabledFeatureSet);
   }

   private static boolean environmentMeetsBasicRequirements() {
      if (!PlatformUtil.IS_WINDOWS) {
         return false;
      } else if (PlatformUtil.IS_AARCH64) {
         return false;
      } else if ("1".equals(System.getenv("ENABLE_VULKAN_RENDERDOC_CAPTURE"))) {
         LOGGER.info("DXGI disabled with renderdoc attached");
         return false;
      } else if ("1".equals(System.getenv("RENDERPEARL_DISABLE_DXGI_PRESENT"))) {
         LOGGER.info("DXGI manually disabled");
         return false;
      } else {
         boolean dllLoadFailed = false;
         Path sys32 = Path.of(System.getenv("SystemRoot"), "System32");

         try {
            System.load(sys32.resolve("dxgi.dll").toAbsolutePath().toString());
         } catch (Throwable e) {
            LOGGER.warn("Failed to load DXGI", e);
            dllLoadFailed = true;
         }

         try {
            System.load(sys32.resolve("d3d12.dll").toAbsolutePath().toString());
         } catch (Throwable e) {
            LOGGER.warn("Failed to load D3D12", e);
            dllLoadFailed = true;
         }

         return !dllLoadFailed;
      }
   }

   private static boolean checkPhysicalDeviceImportCapabilities(final VkPhysicalDevice device, final FeatureSet enabledFeatureSet) {
      if (!enabledFeatureSet.contains(VulkanFeatureSets.DXGI_FEATURESET)) {
         return false;
      } else {
         boolean capable = true;
         MemoryStack stack = MemoryStack.stackPush();

         try {
            VkPhysicalDeviceImageFormatInfo2 info = VkPhysicalDeviceImageFormatInfo2.calloc(stack).sType$Default();
            VkPhysicalDeviceExternalImageFormatInfo externalInfo = VkPhysicalDeviceExternalImageFormatInfo.calloc(stack).sType$Default();
            info.pNext(externalInfo);
            info.format(37);
            info.type(1);
            info.tiling(0);
            info.usage(2);
            externalInfo.handleType(64);
            VkImageFormatProperties2 properties2 = VkImageFormatProperties2.calloc(stack).sType$Default();
            VkExternalImageFormatProperties externalProperties = VkExternalImageFormatProperties.calloc(stack).sType$Default();
            properties2.pNext(externalProperties);
            VK12.vkGetPhysicalDeviceImageFormatProperties2(device, info, properties2);
            if ((externalProperties.externalMemoryProperties().externalMemoryFeatures() & 4) == 0) {
               LOGGER.info("Vulkan unable to import D3D12 images");
               capable = false;
            }
         } catch (Throwable var14) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var12) {
                  var14.addSuppressed(var12);
               }
            }

            throw var14;
         }

         if (stack != null) {
            stack.close();
         }

         stack = MemoryStack.stackPush();

         try {
            VkPhysicalDeviceExternalSemaphoreInfo info = VkPhysicalDeviceExternalSemaphoreInfo.calloc(stack).sType$Default();
            info.handleType(8);
            VkExternalSemaphoreProperties properties = VkExternalSemaphoreProperties.calloc(stack).sType$Default();
            VK12.vkGetPhysicalDeviceExternalSemaphoreProperties(device, info, properties);
            if ((properties.externalSemaphoreFeatures() & 2) == 0) {
               LOGGER.info("Vulkan unable to import D3D12 fence");
               capable = false;
            }
         } catch (Throwable var13) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var11) {
                  var13.addSuppressed(var11);
               }
            }

            throw var13;
         }

         if (stack != null) {
            stack.close();
         }

         stack = MemoryStack.stackPush();

         try {
            VkPhysicalDeviceProperties2 deviceProperties = VkPhysicalDeviceProperties2.calloc(stack).sType$Default();
            VkPhysicalDeviceIDProperties deviceIDProperties = VkPhysicalDeviceIDProperties.calloc(stack).sType$Default();
            deviceProperties.pNext(deviceIDProperties);
            VK12.vkGetPhysicalDeviceProperties2(device, deviceProperties);
            if (deviceIDProperties.deviceLUIDValid()) {
               try (DXGIFactory5 dxgiFactory = DXGIFactory5.create()) {
                  if (!dxgiFactory.adapterWithLuidExists(MemorySegment.ofAddress(deviceIDProperties.deviceLUID().getLong(0)))) {
                     LOGGER.info("Unable to find D3D12 device with LUID matching Vulkan device");
                     capable = false;
                  }
               }
            } else {
               LOGGER.info("Vulkan device doesn't have D3D12 LUID");
               capable = false;
            }
         } catch (Throwable var16) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var9) {
                  var16.addSuppressed(var9);
               }
            }

            throw var16;
         }

         if (stack != null) {
            stack.close();
         }

         return capable;
      }
   }

   public D3D12GpuSurface(final VulkanDevice vulkanDevice, final long window) {
      super();
      this.mode = GpuSurface.PresentMode.IMMEDIATE;
      this.allocators = new ReferenceArrayList();
      this.vulkanDevice = vulkanDevice;
      this.win32Window = SDLProperties.SDL_GetPointerProperty(SDLVideo.SDL_GetWindowProperties(window), "SDL.window.win32.hwnd", 0L);
      this.allowTearing = this.dxgiFactory.checkTearingSupport();
      MemoryStack stack = MemoryStack.stackPush();

      try {
         VkPhysicalDeviceProperties2 deviceProperties = VkPhysicalDeviceProperties2.calloc(stack).sType$Default();
         VkPhysicalDeviceIDProperties deviceIDProperties = VkPhysicalDeviceIDProperties.calloc(stack).sType$Default();
         deviceProperties.pNext(deviceIDProperties);
         VK12.vkGetPhysicalDeviceProperties2(vulkanDevice.vkDevice().getPhysicalDevice(), deviceProperties);
         this.nodeMask = deviceIDProperties.deviceNodeMask();

         try (DXGIAdapter adapter = this.dxgiFactory.enumAdapterByLuid(MemorySegment.ofAddress(deviceIDProperties.deviceLUID().getLong(0)))) {
            this.d3d12Device = adapter.createD3D12Device(45056);
         }
      } catch (Throwable var17) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var12) {
               var17.addSuppressed(var12);
            }
         }

         throw var17;
      }

      if (stack != null) {
         stack.close();
      }

      Arena arena = Arena.ofConfined();

      try {
         D3D12CommandQueueDesc desc = new D3D12CommandQueueDesc(arena);
         desc.type(0);
         desc.priority(0);
         desc.flags(0);
         desc.nodeMask(this.nodeMask);
         this.commandQueue = this.d3d12Device.createCommandQueue(desc);
      } catch (Throwable var15) {
         if (arena != null) {
            try {
               arena.close();
            } catch (Throwable var11) {
               var15.addSuppressed(var11);
            }
         }

         throw var15;
      }

      if (arena != null) {
         arena.close();
      }

      this.fence = this.d3d12Device.createFence(0L, 1);
      this.fenceSharedHandle = this.d3d12Device.createSharedHandle(this.fence);
      MemoryStack stack = MemoryStack.stackPush();

      try {
         VkSemaphoreTypeCreateInfo semaphoreTypeCreateInfo = VkSemaphoreTypeCreateInfo.calloc(stack).sType$Default();
         semaphoreTypeCreateInfo.semaphoreType(1);
         semaphoreTypeCreateInfo.initialValue(0L);
         VkSemaphoreCreateInfo semaphoreCreateInfo = VkSemaphoreCreateInfo.calloc(stack).sType$Default();
         semaphoreCreateInfo.pNext(semaphoreTypeCreateInfo);
         LongBuffer semaphoreHandlePtr = stack.callocLong(1);
         VulkanUtils.crashIfFailure(vulkanDevice, VK12.vkCreateSemaphore(vulkanDevice.vkDevice(), semaphoreCreateInfo, (VkAllocationCallbacks)null, semaphoreHandlePtr), "Failed to create present VkSemaphore");
         this.vkImportedSemaphore = semaphoreHandlePtr.get(0);
         VkImportSemaphoreWin32HandleInfoKHR importInfo = VkImportSemaphoreWin32HandleInfoKHR.calloc(stack).sType$Default();
         importInfo.semaphore(this.vkImportedSemaphore);
         importInfo.handleType(8);
         importInfo.handle(this.fenceSharedHandle.handle());
         VulkanUtils.crashIfFailure(vulkanDevice, JNI.callPPI(vulkanDevice.vkDevice().address(), importInfo.address(), vulkanDevice.vkDevice().getCapabilities().vkImportSemaphoreWin32HandleKHR), "Failed to import D3D12 Fence");
      } catch (Throwable var14) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var10) {
               var14.addSuppressed(var10);
            }
         }

         throw var14;
      }

      if (stack != null) {
         stack.close();
      }

   }

   public void close() {
      this.destroySwapchain();
      VK12.vkDestroySemaphore(this.vulkanDevice.vkDevice(), this.vkImportedSemaphore, (VkAllocationCallbacks)null);
      this.fenceSharedHandle.close();
      this.fence.close();
      this.commandQueue.close();
      this.d3d12Device.close();
      this.dxgiFactory.close();
   }

   private void destroySwapchain() {
      if (this.swapchain != null) {
         assert this.waitHandle != null;

         VK12.vkDeviceWaitIdle(this.vulkanDevice.vkDevice());
         this.commandQueue.signal(this.fence, ++this.fenceValue);
         this.fence.waitValue(this.fenceValue);

         for(int i = 0; i < this.vkImportedImages.size(); ++i) {
            VK12.vkDestroyImage(this.vulkanDevice.vkDevice(), this.vkImportedImages.getLong(i), (VkAllocationCallbacks)null);
            VK12.vkFreeMemory(this.vulkanDevice.vkDevice(), this.vkImportedImageMemories.getLong(i), (VkAllocationCallbacks)null);
         }

         this.blitTargetHandles.forEach(UncheckedAutoCloseable::close);
         this.dxImagesToClose.forEach(UncheckedAutoCloseable::close);
         this.allocators.forEach(COMUnknown::close);
         if (this.commandList != null) {
            this.commandList.close();
         }

         this.waitHandle.close();
         this.swapchain.close();
         this.swapchain = null;
         this.vkImportedImages.clear();
         this.vkImportedImageMemories.clear();
         this.blitTargetHandles.clear();
         this.d3dBlitTargets.clear();
         this.dxgiSwapchainImages.clear();
         this.dxImagesToClose.clear();
         this.allocators.clear();
      }
   }

   public void configure(final GpuSurface.Configuration config) throws SurfaceException {
      this.mode = config.presentMode();
      if (this.swapchainWidth != config.width() || this.swapchainHeight != config.height()) {
         this.destroySwapchain();
         this.swapchainWidth = config.width();
         this.swapchainHeight = config.height();
         int swapchainImageCount = 3;
         boolean useBlitMode = true;
         this.blitModeActive = true;
         Arena arena = Arena.ofConfined();

         try {
            DXGISwapchainDesc1 swapchainDesc = new DXGISwapchainDesc1(arena);
            swapchainDesc.width(this.swapchainWidth);
            swapchainDesc.height(this.swapchainHeight);
            swapchainDesc.format(28);
            swapchainDesc.stereo(false);
            DXGISampleDesc sampleDesc = swapchainDesc.sampleDesc();
            sampleDesc.count(1);
            sampleDesc.quality(0);
            swapchainDesc.bufferUsage(0);
            swapchainDesc.bufferCount(3);
            swapchainDesc.scaling(1);
            swapchainDesc.swapEffect(4);
            swapchainDesc.alphaMode(3);
            swapchainDesc.flags((this.allowTearing ? 2048 : 0) | 64);
            this.swapchain = this.dxgiFactory.createSwapchain(this.commandQueue, this.win32Window, swapchainDesc);
            this.dxgiFactory.makeWindowAssociation(this.win32Window, 3);
            this.waitHandle = this.swapchain.waitHandle();
         } catch (Throwable var21) {
            if (arena != null) {
               try {
                  arena.close();
               } catch (Throwable var19) {
                  var21.addSuppressed(var19);
               }
            }

            throw var21;
         }

         if (arena != null) {
            arena.close();
         }

         this.swapchain.setMaximumLatency(2);

         for(int i = 0; i < 3; ++i) {
            D3D12Resource image = this.swapchain.getBuffer(i);
            this.dxgiSwapchainImages.add(image);
            this.dxImagesToClose.add(image);
            if (!this.blitModeActive) {
               this.d3dBlitTargets.add(image);
            }
         }

         if (this.blitModeActive) {
            arena = Arena.ofConfined();

            try {
               D3D12HeapProperties heapProperties = new D3D12HeapProperties(arena);
               heapProperties.type(1);
               heapProperties.cpuPageProperty(0);
               heapProperties.memoryPoolPreference(0);
               heapProperties.creationNodeMask(this.nodeMask);
               heapProperties.visibleNodeMask(this.nodeMask);
               D3D12ResourceDesc desc = new D3D12ResourceDesc(arena);
               desc.dimension(3);
               desc.width((long)this.swapchainWidth);
               desc.height(this.swapchainHeight);
               desc.depthOrArrayStride((short)1);
               desc.mipLevels((short)1);
               desc.format(28);
               desc.sampleDesc().count(1);
               desc.sampleDesc().quality(0);
               desc.layout(0);
               desc.flags(0);
               int heapFlags = 1;
               D3D12Resource blitTarget = this.d3d12Device.createCommittedResource(heapProperties, 1, desc, 0);
               this.d3dBlitTargets.add(blitTarget);
               this.dxImagesToClose.add(blitTarget);
            } catch (Throwable var20) {
               if (arena != null) {
                  try {
                     arena.close();
                  } catch (Throwable var18) {
                     var20.addSuppressed(var18);
                  }
               }

               throw var20;
            }

            if (arena != null) {
               arena.close();
            }

            for(int i = 0; i < 3; ++i) {
               this.allocators.add(this.d3d12Device.createCommandAllocator(0));
            }

            this.commandList = this.d3d12Device.createCommandList(this.nodeMask, 0, (D3D12CommandAllocator)this.allocators.getFirst());
         }

         int sharedImageCount = this.d3dBlitTargets.size();

         for(int i = 0; i < sharedImageCount; ++i) {
            this.blitTargetHandles.add(this.d3d12Device.createSharedHandle((D3D12DeviceChild)this.d3dBlitTargets.get(i)));
         }

         MemoryStack stack = MemoryStack.stackPush();

         try {
            VkExternalMemoryImageCreateInfo externalMemoryImageCreateInfo = VkExternalMemoryImageCreateInfo.calloc(stack).sType$Default();
            externalMemoryImageCreateInfo.handleTypes(64);
            VkImageCreateInfo imageCreateInfo = VkImageCreateInfo.calloc(stack).sType$Default();
            imageCreateInfo.pNext(externalMemoryImageCreateInfo);
            imageCreateInfo.imageType(1);
            imageCreateInfo.format(37);
            imageCreateInfo.extent().set(this.swapchainWidth, this.swapchainHeight, 1);
            imageCreateInfo.mipLevels(1);
            imageCreateInfo.arrayLayers(1);
            imageCreateInfo.samples(1);
            imageCreateInfo.tiling(0);
            imageCreateInfo.usage(2);
            imageCreateInfo.initialLayout(0);
            LongBuffer handleReturn = stack.callocLong(1);

            for(int i = 0; i < sharedImageCount; ++i) {
               VulkanUtils.crashIfFailure(this.vulkanDevice, VK12.vkCreateImage(this.vulkanDevice.vkDevice(), imageCreateInfo, (VkAllocationCallbacks)null, handleReturn), "Failed to create swapchain image for import");
               this.vkImportedImages.add(handleReturn.get(0));
            }

            VkMemoryWin32HandlePropertiesKHR handleMemoryRequirements = VkMemoryWin32HandlePropertiesKHR.calloc(stack).sType$Default();
            VkMemoryRequirements vulkanMemoryRequirements = VkMemoryRequirements.calloc(stack);
            VkMemoryDedicatedAllocateInfo dedicatedInfo = VkMemoryDedicatedAllocateInfo.calloc(stack).sType$Default();
            VkImportMemoryWin32HandleInfoKHR importInfo = VkImportMemoryWin32HandleInfoKHR.calloc(stack).sType$Default();
            VkMemoryAllocateInfo allocInfo = VkMemoryAllocateInfo.calloc(stack).sType$Default();
            allocInfo.pNext(importInfo).pNext(dedicatedInfo);

            for(int i = 0; i < sharedImageCount; ++i) {
               VulkanUtils.crashIfFailure(this.vulkanDevice, KHRExternalMemoryWin32.vkGetMemoryWin32HandlePropertiesKHR(this.vulkanDevice.vkDevice(), 64, ((Win32Handle)this.blitTargetHandles.get(i)).handle(), handleMemoryRequirements), "Failed to get D3D12 memory requirements");
               VK12.vkGetImageMemoryRequirements(this.vulkanDevice.vkDevice(), this.vkImportedImages.getLong(i), vulkanMemoryRequirements);
               int memoryTypeBits = handleMemoryRequirements.memoryTypeBits() & vulkanMemoryRequirements.memoryTypeBits();
               int memoryTypeIndex = Integer.numberOfTrailingZeros(memoryTypeBits);
               if (memoryTypeBits == 0) {
                  String var10002 = Integer.toBinaryString(handleMemoryRequirements.memoryTypeBits());
                  throw new IllegalStateException("No available memory type " + var10002 + " & " + Integer.toBinaryString(vulkanMemoryRequirements.memoryTypeBits()));
               }

               dedicatedInfo.image(this.vkImportedImages.getLong(i));
               importInfo.handleType(64);
               importInfo.handle(((Win32Handle)this.blitTargetHandles.get(i)).handle());
               allocInfo.allocationSize(vulkanMemoryRequirements.size());
               allocInfo.memoryTypeIndex(memoryTypeIndex);
               handleReturn.put(0, 0L);
               VulkanUtils.crashIfFailure(this.vulkanDevice, VK12.vkAllocateMemory(this.vulkanDevice.vkDevice(), allocInfo, (VkAllocationCallbacks)null, handleReturn), "Failed to import D3D12 image memory");
               this.vkImportedImageMemories.add(handleReturn.get(0));
            }

            for(int i = 0; i < sharedImageCount; ++i) {
               VulkanUtils.crashIfFailure(this.vulkanDevice, VK12.vkBindImageMemory(this.vulkanDevice.vkDevice(), this.vkImportedImages.getLong(i), this.vkImportedImageMemories.getLong(i), 0L), "Failed to bind swapchain image memory");
            }
         } catch (Throwable var22) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var17) {
                  var22.addSuppressed(var17);
               }
            }

            throw var22;
         }

         if (stack != null) {
            stack.close();
         }

      }
   }

   public boolean isSuboptimal() {
      return false;
   }

   public void acquireNextTexture() throws SurfaceException {
      if (this.waitHandle == null) {
         throw new IllegalStateException();
      } else {
         int waitResult = this.waitHandle.waitSingleObject(1000, true);
         if (waitResult != 0) {
            throw new IllegalStateException("Invalid result code waiting for swapchain image to be ready " + Integer.toHexString(waitResult));
         }
      }
   }

   public void blitFromTexture(final CommandEncoderBackend commandEncoder, final GpuTextureView textureView) {
      if (this.swapchain == null) {
         throw new IllegalStateException();
      } else {
         int currentImageIndex = this.swapchain.currentBackBufferIndex();
         VulkanCommandEncoder vulkanCommandEncoder = (VulkanCommandEncoder)commandEncoder;
         VkCommandBuffer blitCommandBuffer = vulkanCommandEncoder.allocateAndBeginTransientCommandBuffer();
         long vkBlitTarget = this.vkImportedImages.getLong(currentImageIndex % this.vkImportedImages.size());
         D3D12Resource d3dBlitTarget = (D3D12Resource)this.d3dBlitTargets.get(currentImageIndex % this.vkImportedImages.size());
         MemoryStack stack = MemoryStack.stackGet();
         MemoryStack var10 = stack.push();

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
            imageBarrier.image(vkBlitTarget);
            VkImageSubresourceRange subresourceRange = imageBarrier.subresourceRange();
            subresourceRange.aspectMask(1);
            subresourceRange.baseMipLevel(0);
            subresourceRange.levelCount(1);
            subresourceRange.baseArrayLayer(0);
            subresourceRange.layerCount(1);
            VkDependencyInfo depinfo = VkDependencyInfo.calloc(stack).sType$Default();
            depinfo.pImageMemoryBarriers(imageBarrier);
            KHRSynchronization2.vkCmdPipelineBarrier2KHR(blitCommandBuffer, depinfo);
         } catch (Throwable var23) {
            if (var10 != null) {
               try {
                  var10.close();
               } catch (Throwable var20) {
                  var23.addSuppressed(var20);
               }
            }

            throw var23;
         }

         if (var10 != null) {
            var10.close();
         }

         var10 = stack.push();

         try {
            int copyWidth = Math.min(this.swapchainWidth, textureView.getWidth(0));
            int copyHeight = Math.min(this.swapchainHeight, textureView.getHeight(0));
            VkOffset3D.Buffer srcOffsets = VkOffset3D.calloc(2, stack);
            srcOffsets.x(0).y(0).z(0);
            srcOffsets.position(1);
            srcOffsets.x(copyWidth).y(copyHeight).z(1);
            srcOffsets.position(0);
            VkOffset3D.Buffer dstOffsets = VkOffset3D.calloc(2, stack);
            dstOffsets.x(0).y(copyHeight).z(0);
            dstOffsets.position(1);
            dstOffsets.x(copyWidth).y(0).z(1);
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
            blitRegion.srcSubresource(srcSubresource);
            blitRegion.srcOffsets(srcOffsets);
            blitRegion.dstSubresource(dstSubresource);
            blitRegion.dstOffsets(dstOffsets);
            VK12.vkCmdBlitImage(blitCommandBuffer, ((VulkanGpuTexture)textureView.texture()).vkImage(), 1, vkBlitTarget, 7, blitRegion, 0);
         } catch (Throwable var22) {
            if (var10 != null) {
               try {
                  var10.close();
               } catch (Throwable var19) {
                  var22.addSuppressed(var19);
               }
            }

            throw var22;
         }

         if (var10 != null) {
            var10.close();
         }

         var10 = stack.push();

         try {
            VkImageMemoryBarrier2.Buffer imageBarrier = VkImageMemoryBarrier2.calloc(1, stack).sType$Default();
            imageBarrier.srcStageMask(4096L);
            imageBarrier.srcAccessMask(4096L);
            imageBarrier.dstStageMask(65536L);
            imageBarrier.dstAccessMask(0L);
            imageBarrier.oldLayout(7);
            imageBarrier.newLayout(1);
            imageBarrier.srcQueueFamilyIndex(this.vulkanDevice.graphicsQueue().queueFamilyIndex());
            imageBarrier.dstQueueFamilyIndex(-2);
            imageBarrier.image(vkBlitTarget);
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
         } catch (Throwable var21) {
            if (var10 != null) {
               try {
                  var10.close();
               } catch (Throwable var18) {
                  var21.addSuppressed(var18);
               }
            }

            throw var21;
         }

         if (var10 != null) {
            var10.close();
         }

         VulkanUtils.crashIfFailure(this.vulkanDevice, VK12.vkEndCommandBuffer(blitCommandBuffer), "Failed to end VkCommandBuffer");
         vulkanCommandEncoder.execute(blitCommandBuffer);
         vulkanCommandEncoder.signalSemaphore(this.vkImportedSemaphore, ++this.fenceValue, 4096L);
         DXUtils.crashIfFailure(this.commandQueue.wait(this.fence, this.fenceValue));
         if (this.blitModeActive) {
            D3D12CommandAllocator allocator = (D3D12CommandAllocator)this.allocators.get(currentImageIndex);
            allocator.reset();

            assert this.commandList != null;

            this.commandList.reset(allocator);
            this.commandList.copyResource((D3D12Resource)this.dxgiSwapchainImages.get(currentImageIndex), d3dBlitTarget);
            this.commandList.end();
            DXUtils.crashIfFailure(this.commandQueue.executeCommandList(this.commandList));
         }

      }
   }

   public void present() {
      if (this.swapchain == null) {
         throw new IllegalStateException();
      } else {
         int var10000;
         switch (this.mode) {
            case IMMEDIATE -> var10000 = this.swapchain.present(0, 512);
            case MAILBOX -> var10000 = this.swapchain.present(1, 4);
            case FIFO -> var10000 = this.swapchain.present(1, 0);
            case FIFO_RELAXED -> throw new IllegalStateException();
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         int presentCode = var10000;
         DXUtils.crashIfFailure(presentCode);
      }
   }

   public Collection<GpuSurface.PresentMode> supportedPresentModes() {
      return this.allowTearing ? Set.of(GpuSurface.PresentMode.IMMEDIATE, GpuSurface.PresentMode.MAILBOX, GpuSurface.PresentMode.FIFO) : Set.of(GpuSurface.PresentMode.MAILBOX, GpuSurface.PresentMode.FIFO);
   }
}
