package com.mojang.blaze3d.vulkan;

import com.mojang.blaze3d.textures.GpuTextureView;
import java.nio.LongBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK12;
import org.lwjgl.vulkan.VkAllocationCallbacks;
import org.lwjgl.vulkan.VkImageSubresourceRange;
import org.lwjgl.vulkan.VkImageViewCreateInfo;

public class VulkanGpuTextureView extends GpuTextureView implements Destroyable {
   private final VulkanDevice device;
   private final long vkImageView;
   private boolean closed;

   protected VulkanGpuTextureView(final VulkanDevice device, final VulkanGpuTexture texture, final int baseMipLevel, final int mipLevels) {
      super(texture, baseMipLevel, mipLevels);
      this.device = device;
      MemoryStack stack = MemoryStack.stackPush();

      try {
         boolean isCubemap = (texture.usage() & 16) != 0;
         VkImageViewCreateInfo imageViewCreateInfo = VkImageViewCreateInfo.calloc(stack).sType$Default();
         imageViewCreateInfo.image(texture.vkImage());
         imageViewCreateInfo.viewType(isCubemap ? 3 : 1);
         imageViewCreateInfo.format(VulkanConst.toVk(texture.getFormat()));
         VkImageSubresourceRange subresourceRange = imageViewCreateInfo.subresourceRange();
         subresourceRange.aspectMask(texture.getFormat().hasColorAspect() ? 1 : 2);
         subresourceRange.baseMipLevel(baseMipLevel);
         subresourceRange.levelCount(mipLevels);
         subresourceRange.baseArrayLayer(0);
         subresourceRange.layerCount(isCubemap ? 6 : 1);
         LongBuffer handlePtr = stack.callocLong(1);
         VulkanUtils.crashIfFailure(VK12.vkCreateImageView(device.vkDevice(), imageViewCreateInfo, (VkAllocationCallbacks)null, handlePtr), "Failed to create VkImageView");
         this.vkImageView = handlePtr.get(0);
         device.instance().debug().setObjectName(device.vkDevice(), 14, this.vkImageView, texture.getLabel());
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

      texture.addViews();
   }

   public void destroy() {
      VK12.vkDestroyImageView(this.device.vkDevice(), this.vkImageView, (VkAllocationCallbacks)null);
   }

   public void close() {
      if (!this.closed) {
         this.closed = true;
         this.device.createCommandEncoder().queueForDestroy((Destroyable)this);
         this.texture().removeViews();
      }

   }

   public boolean isClosed() {
      return this.closed;
   }

   public VulkanGpuTexture texture() {
      return (VulkanGpuTexture)super.texture();
   }

   public long vkImageView() {
      return this.vkImageView;
   }
}
