package com.mojang.blaze3d.vulkan;

import com.mojang.blaze3d.GpuFormat;
import java.nio.LongBuffer;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK12;
import org.lwjgl.vulkan.VkAllocationCallbacks;
import org.lwjgl.vulkan.VkDescriptorSetLayoutBinding;
import org.lwjgl.vulkan.VkDescriptorSetLayoutCreateInfo;

public record VulkanBindGroupLayout(long handle, List<Entry> entries) {
   public static final VulkanBindGroupLayout INVALID_LAYOUT = new VulkanBindGroupLayout(0L, List.of());

   public VulkanBindGroupLayout {
      super();
   }

   public static VulkanBindGroupLayout create(final VulkanDevice device, final List<Entry> entries, final String name) {
      MemoryStack stack = MemoryStack.stackPush();

      long layoutHandle;
      try {
         VkDescriptorSetLayoutBinding.Buffer bindings = VkDescriptorSetLayoutBinding.calloc(entries.size(), stack);

         for(int i = 0; i < entries.size(); ++i) {
            VkDescriptorSetLayoutBinding var10000 = VkDescriptorSetLayoutBinding.calloc(stack);
            byte var10001;
            switch (((Entry)entries.get(i)).type().ordinal()) {
               case 0 -> var10001 = 6;
               case 1 -> var10001 = 1;
               case 2 -> var10001 = 4;
               default -> throw new MatchException((String)null, (Throwable)null);
            }

            VkDescriptorSetLayoutBinding binding = var10000.descriptorType(var10001).descriptorCount(1).binding(i).stageFlags(17);
            bindings.put(binding);
         }

         bindings.flip();
         VkDescriptorSetLayoutCreateInfo setCreateInfo = VkDescriptorSetLayoutCreateInfo.calloc(stack).sType$Default().flags(1).pBindings(bindings);
         LongBuffer pointer = stack.mallocLong(1);
         VulkanUtils.crashIfFailure(VK12.vkCreateDescriptorSetLayout(device.vkDevice(), setCreateInfo, (VkAllocationCallbacks)null, pointer), "Can't set layout for " + name);
         layoutHandle = pointer.get(0);
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

      return new VulkanBindGroupLayout(layoutHandle, entries);
   }

   public static enum VulkanBindGroupEntryType {
      UNIFORM_BUFFER,
      SAMPLED_IMAGE,
      TEXEL_BUFFER;

      private VulkanBindGroupEntryType() {
      }

      // $FF: synthetic method
      private static VulkanBindGroupEntryType[] $values() {
         return new VulkanBindGroupEntryType[]{UNIFORM_BUFFER, SAMPLED_IMAGE, TEXEL_BUFFER};
      }
   }

   public static record Entry(VulkanBindGroupEntryType type, String name, @Nullable GpuFormat texelBufferFormat) {
      public Entry {
         super();
      }
   }
}
