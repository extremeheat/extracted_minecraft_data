package com.mojang.renderpearl.backend.vulkan;

import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.buffers.TransientMemory;
import com.mojang.renderpearl.backend.util.TransientBlockAllocator;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.vma.Vma;
import org.lwjgl.util.vma.VmaAllocationCreateInfo;
import org.lwjgl.util.vma.VmaAllocationInfo;
import org.lwjgl.vulkan.VK12;
import org.lwjgl.vulkan.VkBufferCopy;
import org.lwjgl.vulkan.VkBufferCreateInfo;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkMemoryHeap;
import org.lwjgl.vulkan.VkMemoryType;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;

public class VulkanTransientMemory implements TransientMemory, Destroyable {
   private static final long BLOCK_SIZE = 524288L;
   private static final long MAX_CPU_ALIGNMENT = 16L;
   private static final long MAX_GPU_ALIGNMENT = Long.highestOneBit(9223372036854775807L);
   private static final int BUFFER_USAGE_BITS = 471;
   private final VulkanDevice device;
   private final VulkanCommandEncoder encoder;
   private final boolean useDeviceMemoryForMappedGpuStaging;
   private final TransientBlockAllocator<TransientBlockAllocator.Allocator.CpuBlock> cpuBlockAllocator = new TransientBlockAllocator<TransientBlockAllocator.Allocator.CpuBlock>(524288L, 16L, TransientBlockAllocator.Allocator.CpuBlock.memalloc());
   private final TransientBlockAllocator<VulkanAllocation> stagingBlockAllocator;
   private final TransientBlockAllocator<VulkanAllocation> gpuBlockAllocator;
   private final TransientBlockAllocator<TransferPair> gpuMappedBlockAllocator;
   private final int expectedCpuMemoryHeap;
   private final int expectedGpuMemoryHeap;
   private final int[] memoryTypeToHeapMap = new int[32];
   private long submitIndex = 0L;
   private boolean anyCommandRecorded = false;
   private @Nullable VkCommandBuffer commandBuffer;

   VulkanTransientMemory(final VulkanDevice device, final VulkanCommandEncoder encoder) {
      super();
      this.device = device;
      this.encoder = encoder;
      MemoryStack stack = MemoryStack.stackPush();

      try {
         VkDevice vkDevice = device.vkDevice();
         VkPhysicalDeviceMemoryProperties memoryProperties = VkPhysicalDeviceMemoryProperties.calloc(stack);
         VK12.vkGetPhysicalDeviceMemoryProperties(vkDevice.getPhysicalDevice(), memoryProperties);
         int heapCount = memoryProperties.memoryHeapCount();
         int typeCount = memoryProperties.memoryTypeCount();

         for(int i = 0; i < typeCount; ++i) {
            this.memoryTypeToHeapMap[i] = memoryProperties.memoryTypes(i).heapIndex();
         }

         int largestDeviceLocalHeapIndex = -1;
         long largestDeviceLocalHeapSize = -1L;

         for(int i = 0; i < heapCount; ++i) {
            VkMemoryHeap heapProperties = memoryProperties.memoryHeaps(i);
            if (VulkanUtils.hasAnyBit(heapProperties.flags(), 1) && heapProperties.size() >= largestDeviceLocalHeapSize) {
               largestDeviceLocalHeapIndex = i;
               largestDeviceLocalHeapSize = heapProperties.size();
            }
         }

         assert largestDeviceLocalHeapIndex != -1;

         boolean largestHeapIsHostVisibleAndCoherent = false;

         for(int i = 0; i < typeCount; ++i) {
            VkMemoryType typeProperties = memoryProperties.memoryTypes(i);
            if (typeProperties.heapIndex() == largestDeviceLocalHeapIndex && VulkanUtils.hasAllBits(typeProperties.propertyFlags(), 6)) {
               largestHeapIsHostVisibleAndCoherent = true;
               break;
            }
         }

         this.useDeviceMemoryForMappedGpuStaging = largestHeapIsHostVisibleAndCoherent;
         int selectedCpuHeap = -1;
         if (this.useDeviceMemoryForMappedGpuStaging) {
            selectedCpuHeap = largestDeviceLocalHeapIndex;
         } else {
            for(int i = 0; i < typeCount; ++i) {
               VkMemoryType typeProperties = memoryProperties.memoryTypes(i);
               if (VulkanUtils.hasAllBits(typeProperties.propertyFlags(), 6)) {
                  VkMemoryHeap heapProperties = memoryProperties.memoryHeaps(typeProperties.heapIndex());
                  if (!VulkanUtils.hasAnyBit(heapProperties.flags(), 1)) {
                     selectedCpuHeap = typeProperties.heapIndex();
                     break;
                  }
               }
            }
         }

         if (selectedCpuHeap == -1) {
            throw new IllegalStateException("Could not select heap for CPU allocations");
         }

         this.expectedCpuMemoryHeap = selectedCpuHeap;
         this.expectedGpuMemoryHeap = largestDeviceLocalHeapIndex;
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

      this.stagingBlockAllocator = new TransientBlockAllocator<VulkanAllocation>(524288L, MAX_GPU_ALIGNMENT, TransientBlockAllocator.Allocator.create((size) -> this.allocateVulkanBlock(size, true), this::freeVulkanBlock));
      this.gpuBlockAllocator = new TransientBlockAllocator<VulkanAllocation>(524288L, MAX_GPU_ALIGNMENT, TransientBlockAllocator.Allocator.create((size) -> this.allocateVulkanBlock(size, false), this::queueFreeVulkanBlock));
      this.gpuMappedBlockAllocator = new TransientBlockAllocator<TransferPair>(524288L, MAX_GPU_ALIGNMENT, TransientBlockAllocator.Allocator.create(this::allocateGpuMappedVulkanBlock, this::freeGpuMappedVulkanBlock), this::recordGpuMappedCopy);
   }

   public void destroy() {
      this.cpuBlockAllocator.close();
      this.stagingBlockAllocator.close();
      this.gpuBlockAllocator.close();
      this.gpuMappedBlockAllocator.close();
   }

   public void beginSubmit() {
      assert this.commandBuffer == null;

      this.commandBuffer = this.encoder.allocateAndBeginTransientCommandBuffer();
      this.encoder.execute(this.commandBuffer);
      this.anyCommandRecorded = false;
   }

   public void endSubmit() {
      this.cpuBlockAllocator.rotate().run();
      VulkanCommandEncoder var10000 = this.encoder;
      Runnable var10001 = this.stagingBlockAllocator.rotate();
      Objects.requireNonNull(var10001);
      var10000.queueForDestroy(var10001::run);
      if (this.useDeviceMemoryForMappedGpuStaging) {
         var10000 = this.encoder;
         var10001 = this.gpuBlockAllocator.rotate();
         Objects.requireNonNull(var10001);
         var10000.queueForDestroy(var10001::run);
      } else {
         this.gpuBlockAllocator.rotate().run();
      }

      this.gpuMappedBlockAllocator.rotate();

      assert this.commandBuffer != null;

      if (this.anyCommandRecorded) {
         MemoryStack stack = MemoryStack.stackPush();

         try {
            VulkanCommandEncoder.memoryBarrier(this.commandBuffer, stack);
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

      VK12.vkEndCommandBuffer(this.commandBuffer);
      this.commandBuffer = null;
      ++this.submitIndex;
   }

   private void recordGpuMappedCopy(final TransferPair block) {
      if (block.cpu() != block.gpu()) {
         assert block.cpu().size == block.gpu().size;

         MemoryStack stack = MemoryStack.stackPush();

         try {
            VkBufferCopy.Buffer region = VkBufferCopy.calloc(1, stack);
            region.srcOffset(0L);
            region.dstOffset(0L);
            region.size(block.cpu().size);

            assert this.commandBuffer != null;

            VK12.vkCmdCopyBuffer(this.commandBuffer, block.cpu().vkBuffer, block.gpu().vkBuffer, region);
            this.anyCommandRecorded = true;
         } catch (Throwable var6) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (stack != null) {
            stack.close();
         }

      }
   }

   private VulkanAllocation allocateVulkanBlock(final long size, final boolean staging) {
      MemoryStack stack = MemoryStack.stackPush();

      VulkanAllocation var14;
      try {
         VkBufferCreateInfo bufferCreateInfo = VkBufferCreateInfo.calloc(stack).sType$Default();
         bufferCreateInfo.size(size);
         bufferCreateInfo.usage(471);
         bufferCreateInfo.sharingMode(0);
         bufferCreateInfo.pQueueFamilyIndices((IntBuffer)null);
         VmaAllocationCreateInfo allocCreateInfo = VmaAllocationCreateInfo.calloc(stack);
         int expectedHeap;
         if (staging && !this.useDeviceMemoryForMappedGpuStaging) {
            allocCreateInfo.usage(9);
            expectedHeap = this.expectedCpuMemoryHeap;
         } else {
            allocCreateInfo.usage(8);
            expectedHeap = this.expectedGpuMemoryHeap;
         }

         if (this.useDeviceMemoryForMappedGpuStaging || staging) {
            allocCreateInfo.requiredFlags(6);
            allocCreateInfo.flags(1024);
         }

         LongBuffer bufferPtr = stack.callocLong(1);
         PointerBuffer allocPtr = stack.callocPointer(1);
         VmaAllocationInfo allocationInfo = VmaAllocationInfo.calloc(stack);
         int result = Vma.vmaCreateBuffer(this.device.vma(), bufferCreateInfo, allocCreateInfo, bufferPtr, allocPtr, allocationInfo);
         VulkanUtils.crashIfFailure(this.device, result, "Failed to allocate VkBuffer");
         PointerBuffer hostPtrPtr = stack.callocPointer(1);
         if (staging || this.useDeviceMemoryForMappedGpuStaging) {
            VulkanUtils.crashIfFailure(this.device, Vma.vmaMapMemory(this.device.vma(), allocPtr.get(0), hostPtrPtr), "Failed to map buffer");
         }

         this.device.instance().debug().setObjectName(this.device.vkDevice(), 9, bufferPtr.get(0), "Vulkan Transient Memory Buffer");
         int allocatedHeap = this.memoryTypeToHeapMap[allocationInfo.memoryType()];
         var14 = new VulkanAllocation(bufferPtr.get(0), allocPtr.get(0), hostPtrPtr.get(0), size, allocatedHeap != expectedHeap);
      } catch (Throwable var16) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var15) {
               var16.addSuppressed(var15);
            }
         }

         throw var16;
      }

      if (stack != null) {
         stack.close();
      }

      return var14;
   }

   private void queueFreeVulkanBlock(final VulkanAllocation allocation) {
      this.encoder.queueForDestroy(() -> this.freeVulkanBlock(allocation));
   }

   private void freeVulkanBlock(final VulkanAllocation allocation) {
      Vma.vmaDestroyBuffer(this.device.vma(), allocation.vkBuffer, allocation.vmaAllocation);
   }

   private TransferPair allocateGpuMappedVulkanBlock(final long size) {
      assert size >= 524288L;

      assert size >= this.gpuBlockAllocator.blockSize();

      if (this.useDeviceMemoryForMappedGpuStaging) {
         TransientBlockAllocator.Allocation<VulkanAllocation> block = this.gpuBlockAllocator.allocate(size, 16L, size, 1L);

         assert block.offset() == 0L;

         return new TransferPair(block.block(), block.block());
      } else {
         assert size >= this.stagingBlockAllocator.blockSize();

         TransientBlockAllocator.Allocation<VulkanAllocation> stagingBlock = this.stagingBlockAllocator.allocate(size, 16L, size, 1L);
         TransientBlockAllocator.Allocation<VulkanAllocation> gpuBlock = this.gpuBlockAllocator.allocate(size, 16L, size, 1L);
         return new TransferPair(stagingBlock.block(), gpuBlock.block());
      }
   }

   private void freeGpuMappedVulkanBlock(final TransferPair allocations) {
   }

   public ByteBuffer allocateCpu(final long size, final long alignment, final long minimumAllocation, final long elementSize) {
      assert size <= 2147483647L;

      TransientBlockAllocator.Allocation<TransientBlockAllocator.Allocator.CpuBlock> alloc = this.cpuBlockAllocator.allocate(size, alignment, minimumAllocation, elementSize);
      return MemoryUtil.memByteBuffer(((TransientBlockAllocator.Allocator.CpuBlock)alloc.block()).address() + alloc.offset(), (int)alloc.size());
   }

   public GpuBufferSlice.MappedView allocateStaging(final long size, final long alignment, final @GpuBuffer.Usage int usage, final long minimumAllocation, final long elementSize) {
      assert size <= 2147483647L;

      TransientBlockAllocator.Allocation<VulkanAllocation> alloc = this.stagingBlockAllocator.allocate(size, alignment, minimumAllocation, elementSize);
      TransientGpuBuffer apiBuffer = new TransientGpuBuffer((alloc.block()).vkBuffer, usage, (int)(alloc.block()).size, this.submitIndex);
      ByteBuffer cpuBuffer = MemoryUtil.memByteBuffer((alloc.block()).hostPtr + alloc.offset(), (int)alloc.size());
      return new GpuBufferSlice.MappedView(new GpuBufferSlice(apiBuffer, alloc.offset(), alloc.size()), cpuBuffer, () -> {
      });
   }

   public GpuBufferSlice allocateGpu(final long size, final long alignment, final @GpuBuffer.Usage int usage, final long minimumAllocation, final long elementSize) {
      assert size <= 2147483647L;

      TransientBlockAllocator.Allocation<VulkanAllocation> alloc = this.gpuBlockAllocator.allocate(size, alignment, minimumAllocation, elementSize);
      TransientGpuBuffer apiBuffer = new TransientGpuBuffer((alloc.block()).vkBuffer, usage, (int)(alloc.block()).size, this.submitIndex);
      return new GpuBufferSlice(apiBuffer, alloc.offset(), alloc.size());
   }

   public GpuBufferSlice.MappedView allocateGpuMapped(final long size, final long alignment, final @GpuBuffer.Usage int usage, final long minimumAllocation, final long elementSize) {
      assert size <= 2147483647L;

      TransientBlockAllocator.Allocation<TransferPair> alloc = this.gpuMappedBlockAllocator.allocate(size, alignment, minimumAllocation, elementSize);
      TransientGpuBuffer apiBuffer = new TransientGpuBuffer(((TransferPair)alloc.block()).gpu().vkBuffer, usage, (int)((TransferPair)alloc.block()).gpu().size, this.submitIndex);
      ByteBuffer cpuBuffer = MemoryUtil.memByteBuffer(((TransferPair)alloc.block()).cpu().hostPtr + alloc.offset(), (int)alloc.size());
      return new GpuBufferSlice.MappedView(new GpuBufferSlice(apiBuffer, alloc.offset(), alloc.size()), cpuBuffer, () -> {
      });
   }

   public GpuBufferSlice uploadStaging(final List<ByteBuffer> data, final long alignment, final @GpuBuffer.Usage int usage, final long minimumAllocation, final long elementSize) {
      return this.upload(data, alignment, usage, minimumAllocation, elementSize, true);
   }

   public GpuBufferSlice uploadGpu(final List<ByteBuffer> data, final long alignment, final @GpuBuffer.Usage int usage, final long minimumAllocation, final long elementSize) {
      return this.upload(data, alignment, usage, minimumAllocation, elementSize, false);
   }

   public GpuBufferSlice upload(final List<ByteBuffer> data, final long alignment, final @GpuBuffer.Usage int usage, final long minimumAllocation, final long elementSize, final boolean staging) {
      long totalSize = 0L;

      for(ByteBuffer buffer : data) {
         totalSize += (long)buffer.remaining();
         totalSize = Mth.roundToward(totalSize, alignment);
      }

      try (GpuBufferSlice.MappedView mapped = staging ? this.allocateStaging(totalSize, alignment, usage, minimumAllocation, elementSize) : this.allocateGpuMapped(totalSize, alignment, usage, minimumAllocation, elementSize)) {
         long mappedPtr = MemoryUtil.memAddress(mapped.data());
         long offset = 0L;

         for(ByteBuffer buffer : data) {
            MemoryUtil.memCopy(MemoryUtil.memAddress(buffer), mappedPtr + offset, Math.min(mapped.slice().length() - offset, (long)buffer.remaining()));
            offset += (long)buffer.remaining();
            offset = Mth.roundToward(offset, alignment);
            if (offset >= mapped.slice().length()) {
               break;
            }
         }

         return mapped.slice();
      }
   }

   public List<GpuBufferSlice> multiUploadStaging(final List<ByteBuffer> data, final long alignment, final @GpuBuffer.Usage int usage) {
      return this.multiUpload(data, alignment, usage, true);
   }

   public List<GpuBufferSlice> multiUploadGpu(final List<ByteBuffer> data, final long alignment, final @GpuBuffer.Usage int usage) {
      return this.multiUpload(data, alignment, usage, false);
   }

   public List<GpuBufferSlice> multiUpload(final List<ByteBuffer> data, final long alignment, final @GpuBuffer.Usage int usage, final boolean staging) {
      ReferenceArrayList<GpuBufferSlice> uploadedBuffers = new ReferenceArrayList();
      uploadedBuffers.size(data.size());
      TransientBlockAllocator<?> allocatorInUse = staging ? this.stagingBlockAllocator : this.gpuMappedBlockAllocator;
      IntArrayList sortedDataIndices = IntArrayList.toList(IntStream.range(0, data.size()));
      sortedDataIndices.sort(IntComparator.comparing((index) -> ((ByteBuffer)data.get(index)).remaining()));

      while(!sortedDataIndices.isEmpty()) {
         boolean allocatedAnything = false;

         for(int i = sortedDataIndices.size() - 1; i >= 0; --i) {
            int bufferIndex = sortedDataIndices.getInt(i);
            ByteBuffer currentBuffer = (ByteBuffer)data.get(bufferIndex);
            if (allocatorInUse.canAllocateInCurrentBlock((long)currentBuffer.remaining(), alignment)) {
               sortedDataIndices.removeInt(i);

               try (GpuBufferSlice.MappedView view = staging ? this.allocateStaging((long)currentBuffer.remaining(), alignment, usage) : this.allocateGpuMapped((long)currentBuffer.remaining(), alignment, usage)) {
                  MemoryUtil.memCopy(currentBuffer, view.data());
                  uploadedBuffers.set(bufferIndex, view.slice());
               }

               allocatedAnything = true;
               break;
            }
         }

         if (!allocatedAnything) {
            int bufferIndex = sortedDataIndices.popInt();
            ByteBuffer currentBuffer = (ByteBuffer)data.get(bufferIndex);

            try (GpuBufferSlice.MappedView view = this.allocateGpuMapped((long)currentBuffer.remaining(), alignment, usage)) {
               MemoryUtil.memCopy(currentBuffer, view.data());
               uploadedBuffers.set(bufferIndex, view.slice());
            }
         }
      }

      return uploadedBuffers;
   }

   private static record VulkanAllocation(long vkBuffer, long vmaAllocation, long hostPtr, long size, boolean suboptimal) implements TransientBlockAllocator.Allocator.Block {
      private VulkanAllocation {
         super();
      }
   }

   private static record TransferPair(VulkanAllocation cpu, VulkanAllocation gpu) implements TransientBlockAllocator.Allocator.Block {
      private TransferPair {
         super();
      }

      public boolean suboptimal() {
         return this.cpu.suboptimal() || this.gpu.suboptimal();
      }
   }

   private class TransientGpuBuffer extends VulkanGpuBuffer {
      private boolean closed;
      private final long bufferSubmitIndex;

      public TransientGpuBuffer(final @GpuBuffer.Usage long vkBuffer, final int usage, final int size, final long bufferSubmitIndex) {
         Objects.requireNonNull(VulkanTransientMemory.this);
         super(vkBuffer, usage, (long)size);
         this.closed = false;
         this.bufferSubmitIndex = bufferSubmitIndex;
      }

      public void destroy() {
      }

      public GpuBufferSlice.MappedView map(final long offset, final long length, final boolean read, final boolean write) {
         throw new IllegalStateException("Cannot map transient buffer");
      }

      public boolean isClosed() {
         if (this.closed) {
            return true;
         } else {
            this.closed = this.bufferSubmitIndex < VulkanTransientMemory.this.submitIndex;
            return this.closed;
         }
      }

      public void close() {
         this.closed = true;
      }

      public GpuBufferSlice slice(final long offset, final long length) {
         throw new IllegalStateException("Cannot slice transient buffer");
      }

      public GpuBufferSlice slice() {
         throw new IllegalStateException("Cannot slice transient buffer");
      }
   }
}
