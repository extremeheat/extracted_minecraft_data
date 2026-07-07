package com.mojang.renderpearl.backend.opengl;

import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.buffers.TransientMemory;
import com.mojang.renderpearl.backend.util.TransientBlockAllocator;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;
import org.lwjgl.opengl.ARBBufferStorage;
import org.lwjgl.opengl.GL33C;
import org.lwjgl.system.MemoryUtil;

public abstract class GlTransientMemory implements TransientMemory, AutoCloseable {
   private static final long BLOCK_SIZE = 524288L;
   private static final long MAX_CPU_ALIGNMENT = 16L;
   private static final long MAX_GPU_ALIGNMENT = Long.highestOneBit(9223372036854775807L);
   final GlCommandEncoder encoder;
   protected final DirectStateAccess dsa;
   protected final BufferStorage bufferStorage;
   protected final GlDebugLabel debugLabels;
   private final TransientBlockAllocator<Long> cpuBlockAllocator = new TransientBlockAllocator<Long>(524288L, 16L, TransientBlockAllocator.Allocator.create(MemoryUtil::nmemAlloc, MemoryUtil::nmemFree));

   GlTransientMemory(final GlDevice device, final GlCommandEncoder encoder) {
      super();
      this.encoder = encoder;
      this.dsa = device.directStateAccess();
      this.bufferStorage = device.getBufferStorage();
      this.debugLabels = device.debugLabels();
   }

   public void close() {
   }

   public ByteBuffer allocateCpu(final long size, final long alignment, final long minimumAllocation, final long elementSize) {
      assert size <= 2147483647L;

      TransientBlockAllocator.Allocation<Long> alloc = this.cpuBlockAllocator.allocate(size, alignment, minimumAllocation, elementSize);
      return MemoryUtil.memByteBuffer((Long)alloc.block() + alloc.offset(), (int)alloc.size());
   }

   public void rotate() {
      this.cpuBlockAllocator.rotate().run();
   }

   static class Fallback extends GlTransientMemory {
      private final TransientBlockAllocator<GlAllocation> blockAllocator;

      Fallback(final GlDevice device, final GlCommandEncoder encoder) {
         super(device, encoder);
         this.blockAllocator = new TransientBlockAllocator<GlAllocation>(524288L, GlTransientMemory.MAX_GPU_ALIGNMENT, TransientBlockAllocator.Allocator.create(this::allocateGlBlock, this::freeGlBlock));
      }

      public void close() {
         this.rotate();
      }

      public void rotate() {
         this.blockAllocator.rotate().run();
      }

      private GlAllocation allocateGlBlock(final long size) {
         GlBuffer buffer = this.bufferStorage.createBuffer(this.dsa, 40, size);
         this.debugLabels.applyLabel(buffer, () -> "OpenGL Transient Buffer");
         long hostPtr = MemoryUtil.nmemAlloc(size);
         return new GlAllocation(buffer, hostPtr);
      }

      private void freeGlBlock(final GlAllocation allocation) {
         allocation.glBuffer.close();
         MemoryUtil.nmemFree(allocation.hostBuffer);
      }

      public GpuBufferSlice.MappedView allocateStaging(final long size, final long alignment, final int usage, final long minimumAllocation, final long elementSize) {
         TransientBlockAllocator.Allocation<GlAllocation> allocation = this.blockAllocator.allocate(size, alignment, minimumAllocation, elementSize);
         TransientGpuBuffer transientBuffer = new TransientGpuBuffer(((GlAllocation)allocation.block()).glBuffer().handle(), usage, ((GlAllocation)allocation.block()).glBuffer().size());
         GpuBufferSlice slice = new GpuBufferSlice(transientBuffer, allocation.offset(), allocation.size());
         ByteBuffer hostBuffer = MemoryUtil.memByteBuffer((allocation.block()).hostBuffer + allocation.offset(), (int)allocation.size());
         return new GpuBufferSlice.MappedView(slice, hostBuffer, () -> this.dsa.bufferSubData(transientBuffer.handle(), slice.offset(), MemoryUtil.memByteBuffer((allocation.block()).hostBuffer + allocation.offset(), (int)allocation.size()), usage));
      }

      public GpuBufferSlice allocateGpu(final long size, final long alignment, final int usage, final long minimumAllocation, final long elementSize) {
         TransientBlockAllocator.Allocation<GlAllocation> allocation = this.blockAllocator.allocate(size, alignment, minimumAllocation, elementSize);
         TransientGpuBuffer transientBuffer = new TransientGpuBuffer(((GlAllocation)allocation.block()).glBuffer().handle(), usage, ((GlAllocation)allocation.block()).glBuffer().size());
         return new GpuBufferSlice(transientBuffer, allocation.offset(), allocation.size());
      }

      public GpuBufferSlice.MappedView allocateGpuMapped(final long size, final long alignment, final int usage, final long minimumAllocation, final long elementSize) {
         return this.allocateStaging(size, alignment, usage, minimumAllocation, elementSize);
      }

      public GpuBufferSlice uploadStaging(final List<ByteBuffer> data, final long alignment, final @GpuBuffer.Usage int usage, final long minimumAllocation, final long elementSize) {
         return this.uploadGpu(data, alignment, usage, minimumAllocation, elementSize);
      }

      public GpuBufferSlice uploadGpu(final List<ByteBuffer> data, final long alignment, final int usage, final long minimumAllocation, final long elementSize) {
         long totalSize = 0L;

         for(int i = 0; i < data.size(); ++i) {
            totalSize += (long)((ByteBuffer)data.get(i)).remaining();
            totalSize = Mth.roundToward(totalSize, alignment);
         }

         GpuBufferSlice bufferSlice = this.allocateGpu(totalSize, alignment, usage);
         int target = GlUtil.selectBufferBindTarget(usage);
         GlStateManager._glBindBuffer(target, ((GlBuffer)bufferSlice.buffer()).handle());
         long ptr = GL33C.nglMapBufferRange(target, bufferSlice.offset(), totalSize, 38);
         long offset = 0L;

         for(int i = 0; i < data.size(); ++i) {
            ByteBuffer buffer = (ByteBuffer)data.get(i);
            MemoryUtil.memCopy(MemoryUtil.memAddress(buffer), ptr + offset, (long)buffer.remaining());
            offset += (long)buffer.remaining();
            offset = Mth.roundToward(offset, alignment);
         }

         GL33C.glUnmapBuffer(target);
         GlStateManager._glBindBuffer(target, 0);
         return bufferSlice;
      }

      public List<GpuBufferSlice> multiUploadStaging(final List<ByteBuffer> data, final long alignment, final @GpuBuffer.Usage int usage) {
         return this.multiUploadGpu(data, alignment, usage);
      }

      public List<GpuBufferSlice> multiUploadGpu(final List<ByteBuffer> data, final long alignment, final int usage) {
         ReferenceArrayList<GpuBufferSlice> uploadedBuffers = new ReferenceArrayList();
         uploadedBuffers.size(data.size());
         IntArrayList sortedDataIndices = IntArrayList.toList(IntStream.range(0, data.size()));
         sortedDataIndices.sort(IntComparator.comparing((index) -> ((ByteBuffer)data.get(index)).remaining()));
         int target = GlUtil.selectBufferBindTarget(usage);

         while(!sortedDataIndices.isEmpty()) {
            boolean allocatedAnything = false;

            for(int i = sortedDataIndices.size() - 1; i >= 0; --i) {
               int bufferIndex = sortedDataIndices.getInt(i);
               ByteBuffer currentBuffer = (ByteBuffer)data.get(bufferIndex);
               if (this.blockAllocator.canAllocateInCurrentBlock((long)currentBuffer.remaining(), alignment)) {
                  sortedDataIndices.removeInt(i);
                  GpuBufferSlice bufferSlice = this.allocateGpu((long)currentBuffer.remaining(), alignment, usage);
                  uploadedBuffers.set(bufferIndex, bufferSlice);
                  GlStateManager._glBindBuffer(target, ((GlBuffer)bufferSlice.buffer()).handle());
                  long ptr = GL33C.nglMapBufferRange(target, bufferSlice.offset(), bufferSlice.length(), 38);
                  MemoryUtil.memCopy(MemoryUtil.memAddress(currentBuffer), ptr, bufferSlice.length());
                  GL33C.glUnmapBuffer(target);
                  allocatedAnything = true;
                  break;
               }
            }

            if (!allocatedAnything) {
               int bufferIndex = sortedDataIndices.popInt();
               ByteBuffer currentBuffer = (ByteBuffer)data.get(bufferIndex);
               GpuBufferSlice bufferSlice = this.allocateGpu((long)currentBuffer.remaining(), alignment, usage);
               uploadedBuffers.set(bufferIndex, bufferSlice);
               GlStateManager._glBindBuffer(target, ((GlBuffer)bufferSlice.buffer()).handle());
               long ptr = GL33C.nglMapBufferRange(target, bufferSlice.offset(), bufferSlice.length(), 38);
               MemoryUtil.memCopy(MemoryUtil.memAddress(currentBuffer), ptr, bufferSlice.length());
               GL33C.glUnmapBuffer(target);
            }
         }

         GlStateManager._glBindBuffer(target, 0);
         return uploadedBuffers;
      }

      private static record GlAllocation(GlBuffer glBuffer, long hostBuffer) {
         private GlAllocation {
            super();
         }
      }
   }

   static class PersistentMapping extends GlTransientMemory {
      private final TransientBlockAllocator<GlAllocation> stagingBlockAllocator;
      private final TransientBlockAllocator<GlAllocation> gpuBlockAllocator;
      private final TransientBlockAllocator<GlAllocation> gpuMappedBlockAllocator;
      private final @Nullable GlTransientMemory.Rotation[] rotations = new Rotation[2];

      PersistentMapping(final GlDevice device, final GlCommandEncoder encoder) {
         super(device, encoder);
         this.stagingBlockAllocator = new TransientBlockAllocator<GlAllocation>(524288L, GlTransientMemory.MAX_GPU_ALIGNMENT, TransientBlockAllocator.Allocator.create((size) -> this.allocateGlBlock(size, true, true), this::freeGlBlock));
         this.gpuBlockAllocator = new TransientBlockAllocator<GlAllocation>(524288L, GlTransientMemory.MAX_GPU_ALIGNMENT, TransientBlockAllocator.Allocator.create((size) -> this.allocateGlBlock(size, false, false), this::freeGlBlock));
         this.gpuMappedBlockAllocator = new TransientBlockAllocator<GlAllocation>(524288L, GlTransientMemory.MAX_GPU_ALIGNMENT, TransientBlockAllocator.Allocator.create((size) -> this.allocateGlBlock(size, false, true), this::freeGlBlock));
      }

      public void close() {
         GL33C.glFinish();

         for(Rotation rotation : this.rotations) {
            if (rotation != null) {
               rotation.run();
            }
         }

      }

      public void rotate() {
         Rotation previousRotation = this.rotations[this.encoder.currentSubmitSlot()];
         if (previousRotation != null) {
            previousRotation.run();
         }

         this.rotations[this.encoder.currentSubmitSlot()] = new Rotation(this.stagingBlockAllocator.rotate(), this.gpuBlockAllocator.rotate(), this.gpuMappedBlockAllocator.rotate());
         super.rotate();
      }

      private GlAllocation allocateGlBlock(final long size, final boolean host, final boolean mapped) {
         int glBuffer = this.dsa.createBuffer();
         int usageFlags = 0;
         if (host) {
            usageFlags |= 512;
         }

         if (mapped) {
            usageFlags |= 2;
            usageFlags |= 64;
            usageFlags |= 128;
         }

         GlStateManager._glBindBuffer(34962, glBuffer);
         ARBBufferStorage.glBufferStorage(34962, size, usageFlags);
         long hostPtr;
         if (mapped) {
            hostPtr = GL33C.nglMapBufferRange(34962, 0L, size, 226);
         } else {
            hostPtr = 0L;
         }

         GlStateManager._glBindBuffer(34962, 0);
         this.debugLabels.applyLabel(new TransientGpuBuffer(glBuffer, 0, size), () -> "OpenGL Transient Buffer");
         return new GlAllocation(glBuffer, hostPtr, size);
      }

      private void freeGlBlock(final GlAllocation allocation) {
         GlStateManager._glDeleteBuffers(allocation.glBuffer);
      }

      public GpuBufferSlice.MappedView allocateStaging(final long size, final long alignment, final @GpuBuffer.Usage int usage, final long minimumAllocation, final long elementSize) {
         assert size <= 2147483647L;

         TransientBlockAllocator.Allocation<GlAllocation> alloc = this.stagingBlockAllocator.allocate(size, alignment, minimumAllocation, elementSize);
         TransientGpuBuffer apiBuffer = new TransientGpuBuffer((alloc.block()).glBuffer, usage, (long)((int)(alloc.block()).size));
         ByteBuffer cpuBuffer = MemoryUtil.memByteBuffer((alloc.block()).hostPtr + alloc.offset(), (int)alloc.size());
         return new GpuBufferSlice.MappedView(new GpuBufferSlice(apiBuffer, alloc.offset(), alloc.size()), cpuBuffer, () -> {
         });
      }

      public GpuBufferSlice allocateGpu(final long size, final long alignment, final int usage, final @GpuBuffer.Usage long minimumAllocation, final long elementSize) {
         assert size <= 2147483647L;

         TransientBlockAllocator.Allocation<GlAllocation> alloc = this.gpuBlockAllocator.allocate(size, alignment, minimumAllocation, elementSize);
         TransientGpuBuffer apiBuffer = new TransientGpuBuffer((alloc.block()).glBuffer, usage, (long)((int)(alloc.block()).size));
         return new GpuBufferSlice(apiBuffer, alloc.offset(), alloc.size());
      }

      public GpuBufferSlice.MappedView allocateGpuMapped(final long size, final long alignment, final @GpuBuffer.Usage int usage, final long minimumAllocation, final long elementSize) {
         assert size <= 2147483647L;

         TransientBlockAllocator.Allocation<GlAllocation> alloc = this.gpuMappedBlockAllocator.allocate(size, alignment, minimumAllocation, elementSize);
         TransientGpuBuffer apiBuffer = new TransientGpuBuffer((alloc.block()).glBuffer, usage, (long)((int)(alloc.block()).size));
         ByteBuffer cpuBuffer = MemoryUtil.memByteBuffer((alloc.block()).hostPtr + alloc.offset(), (int)alloc.size());
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

      private static record GlAllocation(int glBuffer, long hostPtr, long size) {
         private GlAllocation {
            super();
         }
      }

      private static record Rotation(Runnable staging, Runnable gpu, Runnable gpuMapped) implements Runnable {
         private Rotation {
            super();
         }

         public void run() {
            this.staging.run();
            this.gpu.run();
            this.gpuMapped.run();
         }
      }
   }

   private class TransientGpuBuffer extends GlBuffer {
      private boolean closed;
      private final long bufferSubmitIndex;

      protected TransientGpuBuffer(final @GpuBuffer.Usage int handle, final int usage, final long size) {
         Objects.requireNonNull(GlTransientMemory.this);
         super(usage, size, handle, true);
         this.closed = false;
         this.bufferSubmitIndex = GlTransientMemory.this.encoder.currentSubmitIndex();
      }

      public boolean isClosed() {
         if (this.closed) {
            return true;
         } else {
            this.closed = this.bufferSubmitIndex < GlTransientMemory.this.encoder.currentSubmitIndex();
            return this.closed;
         }
      }

      public void close() {
         this.closed = true;
      }

      public GpuBufferSlice.MappedView map(final long offset, final long length, final boolean read, final boolean write) {
         throw new IllegalStateException("Cannot map Transient buffer");
      }

      public GpuBufferSlice slice(final long offset, final long length) {
         throw new IllegalStateException("Cannot slice transient buffer");
      }

      public GpuBufferSlice slice() {
         throw new IllegalStateException("Cannot slice transient buffer");
      }
   }
}
