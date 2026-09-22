package com.mojang.renderpearl.backend.vulkan;

import java.nio.LongBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK12;
import org.lwjgl.vulkan.VkAllocationCallbacks;
import org.lwjgl.vulkan.VkDescriptorPoolCreateInfo;
import org.lwjgl.vulkan.VkDescriptorPoolSize;
import org.lwjgl.vulkan.VkDescriptorSetAllocateInfo;

public class DescriptorPool implements Destroyable {
   private final VulkanDevice device;
   private final long poolHandle;

   public DescriptorPool(final VulkanDevice device) {
      super();
      this.device = device;
      MemoryStack stack = MemoryStack.stackPush();

      try {
         VkDescriptorPoolSize.Buffer sizes = VkDescriptorPoolSize.calloc(3, stack);
         ((VkDescriptorPoolSize)sizes.get(0)).type(6).descriptorCount(4096);
         ((VkDescriptorPoolSize)sizes.get(1)).type(1).descriptorCount(4096);
         ((VkDescriptorPoolSize)sizes.get(2)).type(4).descriptorCount(128);
         VkDescriptorPoolCreateInfo createInfo = VkDescriptorPoolCreateInfo.calloc(stack).sType$Default();
         createInfo.maxSets(1024);
         createInfo.pPoolSizes(sizes);
         LongBuffer poolHandleReturn = stack.callocLong(1);
         VulkanUtils.crashIfFailure(device, VK12.vkCreateDescriptorPool(device.vkDevice(), createInfo, (VkAllocationCallbacks)null, poolHandleReturn), "Failed to allocate descriptor pool");
         this.poolHandle = poolHandleReturn.get(0);
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

   public void destroy() {
      VK12.vkDestroyDescriptorPool(this.device.vkDevice(), this.poolHandle, (VkAllocationCallbacks)null);
   }

   public void reset() {
      VulkanUtils.crashIfFailure(this.device, VK12.vkResetDescriptorPool(this.device.vkDevice(), this.poolHandle, 0), "Failed to reset descriptor pool");
   }

   public long allocateSet(final long setLayout) {
      MemoryStack stack = MemoryStack.stackPush();

      long var11;
      label45: {
         try {
            VkDescriptorSetAllocateInfo allocateInfo = VkDescriptorSetAllocateInfo.calloc(stack).sType$Default();
            allocateInfo.descriptorPool(this.poolHandle);
            allocateInfo.pSetLayouts(stack.longs(setLayout));
            LongBuffer setHandleReturn = stack.callocLong(1);
            int returnCode = VK12.vkAllocateDescriptorSets(this.device.vkDevice(), allocateInfo, setHandleReturn);
            if (returnCode != -12 && returnCode != -1000069000) {
               VulkanUtils.crashIfFailure(this.device, returnCode, "Failed to allocate descriptor set");
               var11 = setHandleReturn.get(0);
               break label45;
            }

            var11 = 0L;
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

         return var11;
      }

      if (stack != null) {
         stack.close();
      }

      return var11;
   }
}
