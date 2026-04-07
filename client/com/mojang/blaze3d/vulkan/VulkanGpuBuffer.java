package com.mojang.blaze3d.vulkan;

import com.mojang.blaze3d.buffers.GpuBuffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.Objects;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.vma.Vma;
import org.lwjgl.util.vma.VmaAllocationCreateInfo;
import org.lwjgl.util.vma.VmaAllocationInfo;
import org.lwjgl.vulkan.VkBufferCreateInfo;

public class VulkanGpuBuffer extends GpuBuffer implements Destroyable {
   private final VulkanDevice device;
   private boolean closed = false;
   private final long vkBuffer;
   private final long vmaAllocation;

   public VulkanGpuBuffer(final VulkanDevice device, final @Nullable Supplier<String> label, final @GpuBuffer.Usage int usage, final long size, final boolean forceHostVisibleAllocation) {
      super(usage, size);
      this.device = device;
      MemoryStack stack = MemoryStack.stackPush();

      try {
         VkBufferCreateInfo bufferCreateInfo = VkBufferCreateInfo.calloc(stack).sType$Default();
         bufferCreateInfo.size(size);
         bufferCreateInfo.usage(VulkanConst.bufferUsageToVk(usage));
         bufferCreateInfo.sharingMode(0);
         bufferCreateInfo.pQueueFamilyIndices((IntBuffer)null);
         VmaAllocationCreateInfo allocCreateInfo = VmaAllocationCreateInfo.calloc(stack);
         allocCreateInfo.usage(8);
         if (forceHostVisibleAllocation) {
            allocCreateInfo.requiredFlags(allocCreateInfo.requiredFlags() | 2);
         }

         if (VulkanUtils.hasAnyBit(usage, 3)) {
            allocCreateInfo.requiredFlags(allocCreateInfo.requiredFlags() | 2 | 4);
            if (VulkanUtils.hasAnyBit(usage, 1)) {
               allocCreateInfo.preferredFlags(allocCreateInfo.preferredFlags() | 8);
               allocCreateInfo.flags(allocCreateInfo.flags() | 2048);
            } else {
               allocCreateInfo.flags(allocCreateInfo.flags() | 1024);
            }
         }

         if (VulkanUtils.hasAnyBit(usage, 5)) {
            allocCreateInfo.usage(9);
         }

         LongBuffer bufferPtr = stack.callocLong(1);
         PointerBuffer allocPtr = stack.callocPointer(1);
         int result = Vma.vmaCreateBuffer(device.vma(), bufferCreateInfo, allocCreateInfo, bufferPtr, allocPtr, (VmaAllocationInfo)null);
         VulkanUtils.crashIfFailure(result, "Failed to allocate VkBuffer");
         this.vkBuffer = bufferPtr.get(0);
         this.vmaAllocation = allocPtr.get(0);
         if (label != null) {
            device.instance().debug().setObjectName(device.vkDevice(), 9, this.vkBuffer, (Supplier)label);
         }
      } catch (Throwable var14) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var13) {
               var14.addSuppressed(var13);
            }
         }

         throw var14;
      }

      if (stack != null) {
         stack.close();
      }

   }

   public void destroy() {
      Vma.vmaDestroyBuffer(this.device.vma(), this.vkBuffer, this.vmaAllocation);
   }

   public boolean isClosed() {
      return this.closed;
   }

   public void close() {
      if (!this.closed) {
         this.closed = true;
         this.device.createCommandEncoder().queueForDestroy((Destroyable)this);
      }
   }

   public long vkBuffer() {
      return this.vkBuffer;
   }

   public GpuBuffer.MappedView map(final long offset, final long length) {
      MemoryStack stack = MemoryStack.stackPush();

      VkMappedView var8;
      try {
         PointerBuffer pointer = stack.callocPointer(1);
         VulkanUtils.crashIfFailure(Vma.vmaMapMemory(this.device.vma(), this.vmaAllocation, pointer), "Failed to map buffer");
         ByteBuffer byteBuffer = MemoryUtil.memByteBuffer(pointer.get(0) + offset, (int)length);
         var8 = new VkMappedView(byteBuffer);
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

      return var8;
   }

   private class VkMappedView implements GpuBuffer.MappedView {
      private final ByteBuffer data;
      private boolean closed;

      private VkMappedView(final ByteBuffer data) {
         Objects.requireNonNull(VulkanGpuBuffer.this);
         super();
         this.closed = false;
         this.data = data;
      }

      public ByteBuffer data() {
         return this.data;
      }

      public void close() {
         if (!this.closed) {
            this.closed = true;
            Vma.vmaUnmapMemory(VulkanGpuBuffer.this.device.vma(), VulkanGpuBuffer.this.vmaAllocation);
         }
      }
   }
}
