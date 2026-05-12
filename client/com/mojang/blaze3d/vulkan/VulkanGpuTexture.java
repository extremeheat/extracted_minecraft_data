package com.mojang.blaze3d.vulkan;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.textures.GpuTexture;
import java.nio.LongBuffer;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.vma.Vma;
import org.lwjgl.util.vma.VmaAllocationCreateInfo;
import org.lwjgl.util.vma.VmaAllocationInfo;
import org.lwjgl.vulkan.VK12;
import org.lwjgl.vulkan.VkBufferMemoryBarrier;
import org.lwjgl.vulkan.VkImageCreateInfo;
import org.lwjgl.vulkan.VkImageMemoryBarrier;
import org.lwjgl.vulkan.VkImageSubresourceRange;
import org.lwjgl.vulkan.VkMemoryBarrier;

public class VulkanGpuTexture extends GpuTexture implements Destroyable {
   private final VulkanDevice device;
   private final long vkImage;
   private final long vmaAllocation;
   private boolean closed = false;
   private int views = 0;

   public VulkanGpuTexture(final VulkanDevice device, final @GpuTexture.Usage int usage, final String label, final GpuFormat format, final int width, final int height, final int depthOrLayers, final int mipLevels) {
      super(usage, label, format, width, height, depthOrLayers, mipLevels);
      this.device = device;
      MemoryStack stack = MemoryStack.stackPush();

      try {
         VkImageCreateInfo imageCreateInfo = VkImageCreateInfo.calloc(stack).sType$Default();
         imageCreateInfo.imageType(1);
         imageCreateInfo.extent().set(width, height, 1);
         imageCreateInfo.mipLevels(mipLevels);
         imageCreateInfo.arrayLayers(depthOrLayers);
         imageCreateInfo.format(VulkanConst.toVk(format));
         imageCreateInfo.tiling(0);
         imageCreateInfo.initialLayout(0);
         imageCreateInfo.usage(VulkanConst.textureUsageToVk(usage, format));
         imageCreateInfo.sharingMode(0);
         imageCreateInfo.samples(1);
         imageCreateInfo.flags(VulkanUtils.hasAnyBit(usage, 16) ? 16 : 0);
         VmaAllocationCreateInfo allocationCreateInfo = VmaAllocationCreateInfo.calloc(stack);
         allocationCreateInfo.usage(8);
         LongBuffer imageHandlePtr = stack.callocLong(1);
         PointerBuffer allocationHandlePtr = stack.callocPointer(1);
         VulkanUtils.crashIfFailure(device, Vma.vmaCreateImage(device.vma(), imageCreateInfo, allocationCreateInfo, imageHandlePtr, allocationHandlePtr, (VmaAllocationInfo)null), "Failed to create image");
         this.vkImage = imageHandlePtr.get(0);
         this.vmaAllocation = allocationHandlePtr.get(0);
         VkImageMemoryBarrier.Buffer barrier = VkImageMemoryBarrier.calloc(1, stack).sType$Default();
         barrier.oldLayout(0);
         barrier.newLayout(1);
         barrier.srcAccessMask(0);
         barrier.dstAccessMask(98304);
         barrier.srcQueueFamilyIndex(-1);
         barrier.dstQueueFamilyIndex(-1);
         barrier.image(this.vkImage);
         VkImageSubresourceRange subresourceRange = barrier.subresourceRange();
         subresourceRange.aspectMask(this.getFormat().hasColorAspect() ? 1 : 2);
         subresourceRange.baseMipLevel(0);
         subresourceRange.levelCount(this.getMipLevels());
         subresourceRange.baseArrayLayer(0);
         subresourceRange.layerCount(depthOrLayers);
         VK12.vkCmdPipelineBarrier(device.createCommandEncoder().textureInitCommandBuffer(), 1, 65536, 0, (VkMemoryBarrier.Buffer)null, (VkBufferMemoryBarrier.Buffer)null, barrier);
         device.instance().debug().setObjectName(device.vkDevice(), 10, this.vkImage, label);
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

      this.addViews();
   }

   public void destroy() {
      Vma.vmaDestroyImage(this.device.vma(), this.vkImage, this.vmaAllocation);
   }

   public void close() {
      if (!this.closed) {
         this.closed = true;
         this.removeViews();
      }
   }

   public boolean isClosed() {
      return this.closed;
   }

   public void addViews() {
      ++this.views;
   }

   public void removeViews() {
      --this.views;
      if (this.views < 0) {
         throw new IllegalStateException("Too many views removed from texture");
      } else {
         if (this.closed && this.views == 0) {
            this.device.createCommandEncoder().queueForDestroy(this);
         }

      }
   }

   public long vkImage() {
      return this.vkImage;
   }
}
